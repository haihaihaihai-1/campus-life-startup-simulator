package com.campus.systems;

import com.campus.economy.MoneyCapability;
import com.campus.economy.SkillCapability;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;

import java.util.UUID;

/**
 * 数据分析系统 - 预测+趋势分析
 */
public class AnalyticsSystem {

    public static void showReport(ServerPlayerEntity player) {
        UUID uuid = player.getUUID();
        int money = player.getCapability(MoneyCapability.MONEY_CAP).map(MoneyCapability.IMoney::getMoney).orElse(0);
        int level = player.getCapability(SkillCapability.SKILL_CAP).map(SkillCapability.ISkill::getLevel).orElse(1);

        int empIncome = EmployeeSystem.calculateTotalIncome(uuid);
        int franchiseCount = FranchiseSystem.getShopCount(uuid);
        int rep = ReputationSystem.getReputation(uuid);
        int techCount = ResearchSystem.getUnlockedTechs(uuid).size();
        int labIncBoost = LabSystem.getTotalIncomeBoost(uuid);
        int trainIncBoost = TrainingSystem.getIncomeBoost(uuid);
        int mentorIncBoost = MentorSystem.getIncomeBoost(uuid);
        int incubatorIncBoost = IncubatorSystem.getIncomeBonus(uuid);

        int totalIncBoost = labIncBoost + trainIncBoost + mentorIncBoost + incubatorIncBoost;
        int projectedDailyIncome = (int)((empIncome + franchiseCount * 200) * (1 + totalIncBoost / 100.0));
        int projectedWeeklyIncome = projectedDailyIncome * 4;

        player.sendMessage(new StringTextComponent("\u00a76\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551     \u00a7b\ud83d\udcca \u6570\u636e\u5206\u6790\u62a5\u544a     \u00a76\u2551"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u255a\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255d"), uuid);

        // 收入预测
        player.sendMessage(new StringTextComponent("\u00a7e\u3010\u6536\u5165\u9884\u6d4b\u3011"), uuid);
        player.sendMessage(new StringTextComponent("  \u00a7f\u5458\u5de5\u6536\u5165: \u00a7a" + empIncome + "/\u5468"), uuid);
        player.sendMessage(new StringTextComponent("  \u00a7f\u52a0\u76df\u5e97\u9884\u4f30: \u00a7a" + (franchiseCount * 200) + "/\u5468"), uuid);
        player.sendMessage(new StringTextComponent("  \u00a7f\u603b\u52a0\u6210: \u00a7b+" + totalIncBoost + "%"), uuid);
        player.sendMessage(new StringTextComponent("  \u00a7f\u9884\u8ba1\u5468\u6536\u5165: \u00a76" + projectedWeeklyIncome + "\u91d1\u5e01"), uuid);

        // 声誉分析
        player.sendMessage(new StringTextComponent("\u00a7e\u3010\u58f0\u8a89\u5206\u6790\u3011"), uuid);
        String repAdvice;
        if (rep >= 80) repAdvice = "\u00a7a\u4f18\u79c0\u72b6\u6001\uff0c\u4fdd\u6301\u8fd0\u8425";
        else if (rep >= 50) repAdvice = "\u00a7e\u72b6\u6001\u826f\u597d\uff0c\u53ef\u52a0\u5927\u8425\u9500";
        else repAdvice = "\u00a7c\u9700\u8981\u6539\u5584\uff0c\u5efa\u8bae\u6295\u653e\u5e7f\u544a";
        player.sendMessage(new StringTextComponent("  \u00a7f\u58f0\u8a89: " + rep + "/100 | " + repAdvice), uuid);

        // 科技进度
        player.sendMessage(new StringTextComponent("\u00a7e\u3010\u7814\u53d1\u5206\u6790\u3011"), uuid);
        player.sendMessage(new StringTextComponent("  \u00a7f\u5df2\u89e3\u9501\u6280\u672f: " + techCount + "/8"), uuid);
        int labCount = LabSystem.getTotalIncomeBoost(uuid) > 0 ? 1 : 0;
        player.sendMessage(new StringTextComponent("  \u00a7f\u5b9e\u9a8c\u5ba4\u52a0\u6210: \u00a7a+" + LabSystem.getTotalIncomeBoost(uuid) + "%\u6536\u5165"), uuid);

        // 财务健康度
        player.sendMessage(new StringTextComponent("\u00a7e\u3010\u8d22\u52a1\u5065\u5eb7\u5ea6\u3011"), uuid);
        int healthScore = 50;
        if (money > 10000) healthScore += 20;
        if (money > 50000) healthScore += 15;
        if (projectedWeeklyIncome > 500) healthScore += 15;
        if (rep >= 70) healthScore += 10;
        if (level >= 20) healthScore += 10;
        healthScore = Math.min(100, healthScore);
        String healthBar = "";
        for (int i = 0; i < 20; i++) healthBar += (i < healthScore / 5 ? "\u00a7a\u2588" : "\u00a77\u2588");
        player.sendMessage(new StringTextComponent("  " + healthBar + "\u00a7f " + healthScore + "/100"), uuid);

        // 建议
        player.sendMessage(new StringTextComponent("\u00a7e\u3010\u667a\u80fd\u5efa\u8bae\u3011"), uuid);
        if (money < 1000) {
            player.sendMessage(new StringTextComponent("  \u00a7c\u8d44\u91d1\u7d27\u5f20! \u5efa\u8bae: \u8d37\u6b3e/\u8865\u8d34/\u7b7e\u5230"), uuid);
        } else if (level < 10) {
            player.sendMessage(new StringTextComponent("  \u00a7e\u7b49\u7ea7\u504f\u4f4e! \u5efa\u8bae: \u6295\u8d44/\u4efb\u52a1/\u57f9\u8bad"), uuid);
        } else if (techCount < 4) {
            player.sendMessage(new StringTextComponent("  \u00a7e\u7814\u53d1\u4e0d\u8db3! \u5efa\u8bae: \u89e3\u9501\u79d1\u6280\u6811"), uuid);
        } else if (franchiseCount < 3) {
            player.sendMessage(new StringTextComponent("  \u00a7a\u53ef\u4ee5\u6269\u5f20! \u5efa\u8bae: \u5f00\u8bbe\u52a0\u76df\u5e97/\u8d2d\u4e70\u7269\u4e1a"), uuid);
        } else {
            player.sendMessage(new StringTextComponent("  \u00a7a\u8fd0\u8425\u826f\u597d! \u53ef\u8003\u8651\u53c2\u52a0\u5927\u8d5b/\u6295\u8d44\u80a1\u5e02"), uuid);
        }
    }
}
