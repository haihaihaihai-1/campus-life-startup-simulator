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
 * 政府补贴系统 - 条件申请+补贴发放
 */
@Mod.EventBusSubscriber(modid = CampusLife.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class GrantSystem {

    public static class Grant {
        public String id, name, description, condition;
        public int amount, requiredLevel, requiredEmployees;
        public boolean oneTime;

        public Grant(String id, String name, String desc, String cond, int amount, int reqLevel, int reqEmp, boolean oneTime) {
            this.id = id; this.name = name; this.description = desc; this.condition = cond;
            this.amount = amount; this.requiredLevel = reqLevel; this.requiredEmployees = reqEmp; this.oneTime = oneTime;
        }
    }

    public static final Grant[] GRANTS = {
        new Grant("startup_subsidy", "\u521b\u4e1a\u542f\u52a8\u8865\u8d34", "\u65b0\u624b\u521b\u4e1a\u8865\u8d34", "Lv.1+", 500, 1, 0, true),
        new Grant("employment_grant", "\u5c31\u4e1a\u8865\u8d34", "\u96c7\u4f63\u5458\u5de5\u8865\u8d34", "Lv.3+ \u96c7\u4f63\u81f3\u5c111\u4eba", 1000, 3, 1, true),
        new Grant("tech_grant", "\u79d1\u6280\u521b\u65b0\u8865\u8d34", "\u7814\u53d1\u8865\u8d34", "Lv.8+ \u89e3\u9501\u81f3\u5c112\u6280\u672f", 3000, 8, 0, true),
        new Grant("green_grant", "\u7eff\u8272\u53d1\u5c55\u8865\u8d34", "\u73af\u4fdd\u521b\u4e1a\u8865\u8d34", "Lv.12+ \u96c7\u4f63\u81f3\u5c113\u4eba", 5000, 12, 3, true),
        new Grant("expansion_grant", "\u6269\u5f20\u8865\u8d34", "\u4f01\u4e1a\u6269\u5f20\u8865\u8d34", "Lv.20+ \u96c7\u4f63\u81f3\u5c115\u4eba", 10000, 20, 5, true),
        new Grant("excellence_grant", "\u4f18\u79c0\u4f01\u4e1a\u8865\u8d34", "\u5927\u578b\u4f01\u4e1a\u8865\u8d34", "Lv.35+ \u96c7\u4f63\u81f3\u5c1110\u4eba", 30000, 35, 10, true),
        new Grant("monthly_allowance", "\u6708\u5ea6\u8fd0\u8425\u8865\u8d34", "\u6bcf\u6708\u53ef\u7533\u8bf7", "Lv.5+", 800, 5, 0, false)
    };

    private static final Map<UUID, Set<String>> claimed = new HashMap<>();
    private static final Map<UUID, Long> lastMonthlyClaim = new HashMap<>();

    public static void showGrants(ServerPlayerEntity player) {
        UUID uuid = player.getUUID();
        Set<String> done = claimed.getOrDefault(uuid, new HashSet<>());
        int level = player.getCapability(SkillCapability.SKILL_CAP).map(SkillCapability.ISkill::getLevel).orElse(1);
        int emp = EmployeeSystem.calculateTotalEmployees(uuid);
        int tech = ResearchSystem.getUnlockedTechs(uuid).size();

        player.sendMessage(new StringTextComponent("\u00a76\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7b\ud83c\udfe2 \u653f\u5e9c\u8865\u8d34\u5217\u8868  \u00a76\u2551"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u255a\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255d"), uuid);

        for (int i = 0; i < GRANTS.length; i++) {
            Grant g = GRANTS[i];
            boolean has = done.contains(g.id);
            boolean canClaim = !has && level >= g.requiredLevel && emp >= g.requiredEmployees;
            String status = has ? "\u00a7a\u2714\u5df2\u9886\u53d6" : (canClaim ? "\u00a7e\u53ef\u7533\u8bf7" : "\u00a7c\u672a\u8fbe\u6807");
            player.sendMessage(new StringTextComponent(
                    "\u00a7e[" + (i+1) + "] " + g.name +
                    " \u00a7f|\u00a76 " + g.amount + "\u91d1\u5e01" +
                    " \u00a7f| " + status +
                    " \u00a7f|\u00a77 " + g.condition), uuid);
        }
        player.sendMessage(new StringTextComponent("\u00a7e\u8f93\u5165 /grant <\u7f16\u53f7> \u7533\u8bf7\u8865\u8d34"), uuid);
    }

    public static boolean applyGrant(ServerPlayerEntity player, int idx) {
        UUID uuid = player.getUUID();
        if (idx < 1 || idx > GRANTS.length) { player.sendMessage(new StringTextComponent("\u00a7c\u65e0\u6548\u7f16\u53f7!"), uuid); return false; }

        Grant g = GRANTS[idx - 1];
        Set<String> done = claimed.getOrDefault(uuid, new HashSet<>());

        if (g.oneTime && done.contains(g.id)) {
            player.sendMessage(new StringTextComponent("\u00a7c\u5df2\u9886\u53d6\u8fc7\u6b64\u8865\u8d34!"), uuid);
            return false;
        }

        if (!g.oneTime) {
            long last = lastMonthlyClaim.getOrDefault(uuid, 0L);
            if (System.currentTimeMillis() - last < 86400000L * 30) {
                player.sendMessage(new StringTextComponent("\u00a7c\u672c\u6708\u5df2\u9886\u53d6\uff0c\u4e0b\u6708\u53ef\u518d\u7533\u8bf7!"), uuid);
                return false;
            }
        }

        int level = player.getCapability(SkillCapability.SKILL_CAP).map(SkillCapability.ISkill::getLevel).orElse(1);
        int emp = EmployeeSystem.calculateTotalEmployees(uuid);

        if (level < g.requiredLevel) {
            player.sendMessage(new StringTextComponent("\u00a7c\u7b49\u7ea7\u4e0d\u8db3! \u9700Lv." + g.requiredLevel), uuid);
            return false;
        }
        if (emp < g.requiredEmployees) {
            player.sendMessage(new StringTextComponent("\u00a7c\u5458\u5de5\u4e0d\u8db3! \u9700" + g.requiredEmployees + "\u4eba"), uuid);
            return false;
        }

        done.add(g.id);
        claimed.put(uuid, done);
        if (!g.oneTime) lastMonthlyClaim.put(uuid, System.currentTimeMillis());

        player.getCapability(MoneyCapability.MONEY_CAP).ifPresent(m -> m.addMoney(g.amount));
        player.getCapability(SkillCapability.SKILL_CAP).ifPresent(s -> s.addExp(g.amount / 100));
        player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u8865\u8d34\u7533\u8bf7\u6210\u529f! " + g.name), uuid);
        player.sendMessage(new StringTextComponent("\u00a7e\u83b7\u5f97: \u00a76" + g.amount + " \u91d1\u5e01 + \u00a7b" + (g.amount/100) + " \u7ecf\u9a8c"), uuid);
        return true;
    }
}
