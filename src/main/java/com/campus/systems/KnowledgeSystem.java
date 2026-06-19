package com.campus.systems;

import com.campus.CampusLife;
import com.campus.economy.MoneyCapability;
import com.campus.economy.SkillCapability;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;

@Mod.EventBusSubscriber(modid = CampusLife.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class KnowledgeSystem {
    private static final Map<UUID, Integer> knowledgePoints = new HashMap<>();
    private static final Map<UUID, Set<String>> knowledgeBase = new HashMap<>();

    public static class KnowledgeItem {
        final String name;
        final String desc;
        final int cost;
        final int points;
        final int reqLevel;

        KnowledgeItem(String name, String desc, int cost, int points, int reqLevel) {
            this.name = name;
            this.desc = desc;
            this.cost = cost;
            this.points = points;
            this.reqLevel = reqLevel;
        }
    }

    public static final KnowledgeItem[] KNOWLEDGE = {
        new KnowledgeItem("市场分析报告", "沉淀市场洞察", 300, 15, 1),
        new KnowledgeItem("技术文档", "技术沉淀", 500, 20, 2),
        new KnowledgeItem("项目复盘", "项目经验复用", 1000, 30, 4),
        new KnowledgeItem("行业研究", "行业深度研究", 2000, 40, 6),
        new KnowledgeItem("竞争情报", "竞争对手分析", 3000, 50, 8),
        new KnowledgeItem("创新案例", "创新实践总结", 5000, 60, 10),
        new KnowledgeItem("组织智慧", "企业智慧汇编", 10000, 80, 15)
    };

    public static boolean create(ServerPlayerEntity player, int idx) {
        UUID uuid = player.getUUID();
        if (idx < 1 || idx > KNOWLEDGE.length) return false;
        KnowledgeItem item = KNOWLEDGE[idx - 1];
        Set<String> has = knowledgeBase.getOrDefault(uuid, new HashSet<>());
        if (has.contains(item.name)) {
            player.sendMessage(new StringTextComponent("§c已有!"), uuid);
            return false;
        }
        int cost = item.cost;
        int pts = item.points;
        int reqLvl = item.reqLevel;
        int level = player.getCapability(SkillCapability.SKILL_CAP).map(SkillCapability.ISkill::getLevel).orElse(1);
        if (level < reqLvl) {
            player.sendMessage(new StringTextComponent("§c需Lv." + reqLvl), uuid);
            return false;
        }
        return player.getCapability(MoneyCapability.MONEY_CAP).map(m -> {
            if (m.spendMoney(cost)) {
                has.add(item.name);
                knowledgeBase.put(uuid, has);
                knowledgePoints.merge(uuid, pts, Integer::sum);
                player.sendMessage(new StringTextComponent("§b✔ 沉淀:" + item.name + "|知识点+" + pts), uuid);
                return true;
            }
            player.sendMessage(new StringTextComponent("§c资金不足!"), uuid);
            return false;
        }).orElse(false);
    }

    public static void show(ServerPlayerEntity player) {
        UUID uuid = player.getUUID();
        int pts = knowledgePoints.getOrDefault(uuid, 0);
        Set<String> has = knowledgeBase.getOrDefault(uuid, new HashSet<>());
        player.sendMessage(new StringTextComponent("§6╔════════════════════════╗"), uuid);
        player.sendMessage(new StringTextComponent("§6║  §b🧠 知识管理 (" + has.size() + "/" + KNOWLEDGE.length + "|知识点:" + pts + ")  §6║"), uuid);
        player.sendMessage(new StringTextComponent("§6╚═════════════════════════╝"), uuid);
        for (int i = 0; i < KNOWLEDGE.length; i++) {
            KnowledgeItem k = KNOWLEDGE[i];
            boolean owned = has.contains(k.name);
            player.sendMessage(new StringTextComponent("§e[" + (i + 1) + "] " + k.name + "|§7 " + k.desc + "|§6 " + k.cost + "金币|§b +" + k.points + "点|§b Lv." + k.reqLevel + "+|" + (owned ? "§a✔" : "§7✖")), uuid);
        }
        player.sendMessage(new StringTextComponent("§e/knowledge <1-7> 沉淀"), uuid);
    }

    public static int getPoints(UUID uuid) {
        return knowledgePoints.getOrDefault(uuid, 0);
    }
}
