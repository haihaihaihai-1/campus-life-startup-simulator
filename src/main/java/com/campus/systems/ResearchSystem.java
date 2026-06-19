package com.campus.systems;

import com.campus.CampusLife;
import com.campus.economy.MoneyCapability;
import com.campus.economy.SkillCapability;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;

/**
 * 研发科技树系统拼图 - 解锁新产品和技术
 * 参考: 科技树/研发升级模式
 */
@Mod.EventBusSubscriber(modid = CampusLife.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ResearchSystem {

    public static class TechNode {
        public String id;
        public String name;
        public String description;
        public int cost;
        public int requiredLevel;
        public String[] prerequisites;

        public TechNode(String id, String name, String desc, int cost, int reqLevel, String... prereqs) {
            this.id = id;
            this.name = name;
            this.description = desc;
            this.cost = cost;
            this.requiredLevel = reqLevel;
            this.prerequisites = prereqs;
        }
    }

    public static final TechNode[] TECH_TREE = {
        new TechNode("basic_craft", "\u57fa\u7840\u624b\u5de5\u827a", "\u89e3\u9501\u624b\u5de5\u827a\u54c1\u5236\u4f5c", 200, 1),
        new TechNode("coffee_brewing", "\u5496\u5561\u51b2\u6ce1\u6280\u672f", "\u89e3\u9501\u5496\u5561\u676f\u5236\u4f5c", 500, 2, "basic_craft"),
        new TechNode("market_research", "\u5e02\u573a\u8c03\u7814", "\u63d0\u5347\u4ea7\u54c1\u552e\u4ef7 10%", 1000, 3, "basic_craft"),
        new TechNode("tech_assembly", "\u79d1\u6280\u7ec4\u88c5", "\u89e3\u9501\u79d1\u6280\u5c0f\u73a9\u5177\u5236\u4f5c", 2000, 5, "coffee_brewing"),
        new TechNode("mass_production", "\u6d41\u6c34\u7ebf\u751f\u4ea7", "\u964d\u4f4e\u539f\u6750\u6599\u6d88\u8017 20%", 5000, 8, "market_research"),
        new TechNode("innovation_lab", "\u521b\u65b0\u5b9e\u9a8c\u5ba4", "\u89e3\u9501\u521b\u65b0\u82af\u7247\u5236\u4f5c", 10000, 12, "tech_assembly", "mass_production"),
        new TechNode("brand_building", "\u54c1\u724c\u5efa\u8bbe", "\u63d0\u5347\u4ea7\u54c1\u552e\u4ef7 30%", 20000, 20, "innovation_lab"),
        new TechNode("global_expansion", "\u5168\u7403\u6269\u5f20", "\u89e3\u9501\u6240\u6709\u9ad8\u7ea7\u4ea7\u54c1", 50000, 35, "brand_building")
    };

    private static final Map<UUID, Set<String>> researched = new HashMap<>();

    public static void showTechTree(ServerPlayerEntity player) {
        UUID uuid = player.getUUID();
        Set<String> unlocked = researched.getOrDefault(uuid, new HashSet<>());
        int level = player.getCapability(SkillCapability.SKILL_CAP).map(SkillCapability.ISkill::getLevel).orElse(1);

        player.sendMessage(new StringTextComponent("\u00a76\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7b\u269b \u7814\u53d1\u79d1\u6280\u6811 (Lv." + level + ")  \u00a76\u2551"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u255a\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255d"), uuid);

        for (TechNode node : TECH_TREE) {
            boolean has = unlocked.contains(node.id);
            boolean canResearch = !has && level >= node.requiredLevel && hasPrerequisites(uuid, node);
            String icon = has ? "\u00a7a\u2714" : (canResearch ? "\u00a7e\u25b6" : "\u00a7c\u2716");
            String color = has ? "\u00a7a" : (canResearch ? "\u00a7e" : "\u00a77");
            player.sendMessage(new StringTextComponent(
                    icon + " " + color + node.name +
                    " \u00a7f|\u00a76 \u8d39\u7528:" + node.cost +
                    " \u00a7f|\u00a7b \u9700Lv." + node.requiredLevel +
                    " \u00a7f|\u00a77 " + node.description), uuid);
        }
        player.sendMessage(new StringTextComponent("\u00a7e\u8f93\u5165 /research unlock <\u540d\u79f0> \u7814\u53d1"), uuid);
    }

    private static boolean hasPrerequisites(UUID uuid, TechNode node) {
        if (node.prerequisites == null || node.prerequisites.length == 0) return true;
        Set<String> unlocked = researched.getOrDefault(uuid, new HashSet<>());
        for (String pre : node.prerequisites) {
            if (!unlocked.contains(pre)) return false;
        }
        return true;
    }

    public static boolean unlockTech(ServerPlayerEntity player, String techName) {
        UUID uuid = player.getUUID();
        Set<String> unlocked = researched.getOrDefault(uuid, new HashSet<>());

        TechNode target = null;
        for (TechNode node : TECH_TREE) {
            if (node.name.equals(techName) || node.id.equals(techName)) {
                target = node;
                break;
            }
        }

        if (target == null) {
            player.sendMessage(new StringTextComponent("\u00a7c\u672a\u627e\u5230\u6280\u672f: " + techName), uuid);
            return false;
        }

        final TechNode finalTarget = target;

        if (unlocked.contains(finalTarget.id)) {
            player.sendMessage(new StringTextComponent("\u00a7e\u5df2\u89e3\u9501\u6b64\u6280\u672f!"), uuid);
            return false;
        }

        int level = player.getCapability(SkillCapability.SKILL_CAP).map(SkillCapability.ISkill::getLevel).orElse(1);
        if (level < finalTarget.requiredLevel) {
            player.sendMessage(new StringTextComponent(
                    "\u00a7c\u7b49\u7ea7\u4e0d\u8db3! \u9700\u8981 Lv." + finalTarget.requiredLevel), uuid);
            return false;
        }

        if (!hasPrerequisites(uuid, finalTarget)) {
            player.sendMessage(new StringTextComponent(
                    "\u00a7c\u524d\u7f6e\u6280\u672f\u672a\u89e3\u9501!"), uuid);
            return false;
        }

        boolean success = player.getCapability(MoneyCapability.MONEY_CAP).map(m -> {
            if (m.spendMoney(finalTarget.cost)) {
                unlocked.add(finalTarget.id);
                researched.put(uuid, unlocked);
                player.getCapability(SkillCapability.SKILL_CAP).ifPresent(s -> s.addExp(finalTarget.cost / 100));
                player.sendMessage(new StringTextComponent(
                        "\u00a7a\u2714 \u7814\u53d1\u6210\u529f! \u89e3\u9501: " + finalTarget.name), uuid);
                player.sendMessage(new StringTextComponent(
                        "\u00a7b" + finalTarget.description), uuid);
                return true;
            }
            player.sendMessage(new StringTextComponent("\u00a7c\u8d44\u91d1\u4e0d\u8db3! \u9700\u8981 " + finalTarget.cost + " \u91d1\u5e01"), uuid);
            return false;
        }).orElse(false);
        return success;
    }

    public static boolean hasTech(UUID uuid, String techId) {
        return researched.getOrDefault(uuid, new HashSet<>()).contains(techId);
    }

    public static Set<String> getUnlockedTechs(UUID uuid) {
        return researched.getOrDefault(uuid, new HashSet<>());
    }
}
