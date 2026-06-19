package com.campus.systems;

import com.campus.economy.MoneyCapability;
import com.campus.economy.SkillCapability;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;

import java.util.UUID;

/**
 * 统计面板系统拼图 - 详细创业数据汇总
 * 参考: 数据分析仪表盘
 */
public class StatsSystem {

    public static void showDashboard(ServerPlayerEntity player) {
        UUID uuid = player.getUUID();

        int money = player.getCapability(MoneyCapability.MONEY_CAP).map(MoneyCapability.IMoney::getMoney).orElse(0);
        int level = player.getCapability(SkillCapability.SKILL_CAP).map(SkillCapability.ISkill::getLevel).orElse(1);
        int exp = player.getCapability(SkillCapability.SKILL_CAP).map(SkillCapability.ISkill::getExp).orElse(0);
        String rank = player.getCapability(SkillCapability.SKILL_CAP).map(SkillCapability.ISkill::getRank).orElse("");

        int employeeCount = EmployeeSystem.calculateTotalEmployees(uuid);
        int employeeIncome = EmployeeSystem.calculateTotalIncome(uuid);
        int techCount = ResearchSystem.getUnlockedTechs(uuid).size();
        int fundedLevel = InvestorSystem.getFundedLevel(uuid);
        int adBoost = MarketingSystem.getBoostPercent(uuid);
        int expBoost = MentorSystem.getExpBoost(uuid);
        int incomeBoost = MentorSystem.getIncomeBoost(uuid);
        TaxSystem.TaxRecord tax = TaxSystem.getRecord(uuid);
        int patents = PatentSystem.getPatentCount(uuid);

        player.sendMessage(new StringTextComponent("\u00a76\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551      \u00a7e\u26a1 \u521b\u4e1a\u6570\u636e\u9762\u677f      \u00a76\u2551"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u255a\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255d"), uuid);

        // 基础数据
        player.sendMessage(new StringTextComponent("\u00a7e\u3010\u57fa\u7840\u6570\u636e\u3011"), uuid);
        player.sendMessage(new StringTextComponent("  \u00a7f\u8d44\u91d1: \u00a76" + money + " \u91d1\u5e01"), uuid);
        player.sendMessage(new StringTextComponent("  \u00a7f\u7b49\u7ea7: \u00a7bLv." + level + " " + rank), uuid);
        player.sendMessage(new StringTextComponent("  \u00a7f\u7ecf\u9a8c: \u00a7a" + exp + "/" + (level * 100)), uuid);

        // 经营数据
        player.sendMessage(new StringTextComponent("\u00a7e\u3010\u7ecf\u8425\u6570\u636e\u3011"), uuid);
        player.sendMessage(new StringTextComponent("  \u00a7f\u5458\u5de5: \u00a7b" + employeeCount + " \u4eba"), uuid);
        player.sendMessage(new StringTextComponent("  \u00a7f\u5458\u5de5\u6536\u5165: \u00a7a" + employeeIncome + " \u91d1\u5e01/\u5468"), uuid);
        player.sendMessage(new StringTextComponent("  \u00a7f\u5e7f\u544a\u52a0\u6210: \u00a7a+" + adBoost + "%"), uuid);
        player.sendMessage(new StringTextComponent("  \u00a7f\u4e13\u5229: \u00a7d" + patents + " \u9879"), uuid);

        // 科技数据
        player.sendMessage(new StringTextComponent("\u00a7e\u3010\u7814\u53d1\u6570\u636e\u3011"), uuid);
        player.sendMessage(new StringTextComponent("  \u00a7f\u5df2\u89e3\u9501\u6280\u672f: \u00a7b" + techCount + "/8"), uuid);
        player.sendMessage(new StringTextComponent("  \u00a7f\u878d\u8d44\u7b49\u7ea7: \u00a76" + (fundedLevel + 1) + "/6"), uuid);

        // 导师加成
        player.sendMessage(new StringTextComponent("\u00a7e\u3010Buff\u52a0\u6210\u3011"), uuid);
        if (expBoost > 0 || incomeBoost > 0) {
            player.sendMessage(new StringTextComponent("  \u00a7f\u7ecf\u9a8c\u52a0\u6210: \u00a7a+" + expBoost + "%"), uuid);
            player.sendMessage(new StringTextComponent("  \u00a7f\u6536\u5165\u52a0\u6210: \u00a7a+" + incomeBoost + "%"), uuid);
        } else {
            player.sendMessage(new StringTextComponent("  \u00a77\u65e0\u6d3b\u8dc3\u52a0\u6210"), uuid);
        }

        // 税务数据
        player.sendMessage(new StringTextComponent("\u00a7e\u3010\u7a0e\u52a1\u6570\u636e\u3011"), uuid);
        player.sendMessage(new StringTextComponent("  \u00a7f\u7d2f\u8ba1\u7eb3\u7a0e: \u00a7c" + tax.getTotalPaid() + " \u91d1\u5e01"), uuid);
        player.sendMessage(new StringTextComponent("  \u00a7f\u4e0a\u6b21\u7a0e\u6b3e: \u00a7c" + tax.lastTaxAmount + " \u91d1\u5e01"), uuid);

        // 当前事件
        RandomEventSystem.GameEvent evt = RandomEventSystem.getCurrentEvent();
        player.sendMessage(new StringTextComponent("\u00a7e\u3010\u73af\u5883\u4e8b\u4ef6\u3011"), uuid);
        if (evt != null) {
            player.sendMessage(new StringTextComponent("  \u00a7f\u5f53\u524d: \u00a76" + evt.name), uuid);
        } else {
            player.sendMessage(new StringTextComponent("  \u00a77\u65e0\u6d3b\u8dc3\u4e8b\u4ef6"), uuid);
        }

        player.sendMessage(new StringTextComponent("\u00a76\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500"), uuid);
    }
}
