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
 * 商业计划评审系统 - 评分+融资加成
 */
@Mod.EventBusSubscriber(modid = CampusLife.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BusinessPlanSystem {

    private static final Map<UUID, Integer> planScore = new HashMap<>();
    private static final Map<UUID, Integer> reviewCount = new HashMap<>();
    private static final Random RAND = new Random();

    public static boolean submit(ServerPlayerEntity player) {
        UUID uuid = player.getUUID();
        int level = player.getCapability(SkillCapability.SKILL_CAP).map(SkillCapability.ISkill::getLevel).orElse(1);
        int money = player.getCapability(MoneyCapability.MONEY_CAP).map(MoneyCapability.IMoney::getMoney).orElse(0);
        int emp = EmployeeSystem.calculateTotalEmployees(uuid);
        int tech = ResearchSystem.getUnlockedTechs(uuid).size();
        int prev = planScore.getOrDefault(uuid, 0);

        int baseScore = 30 + level * 2 + Math.min(20, emp * 2) + tech * 5 + Math.min(20, money / 1000);
        int bonus = RAND.nextInt(20);
        int score = Math.min(100, baseScore + bonus);

        planScore.put(uuid, score);
        reviewCount.merge(uuid, 1, Integer::sum);

        if (score > prev) {
            int reward = (score - prev) * 50;
            player.getCapability(MoneyCapability.MONEY_CAP).ifPresent(m -> m.addMoney(reward));
            player.getCapability(SkillCapability.SKILL_CAP).ifPresent(s -> s.addExp(score));
            player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u8bc4\u5206\u63d0\u5347! " + prev + "\u2192" + score + " | \u5956\u52b1:" + reward + "\u91d1\u5e01"), uuid);
        } else {
            player.sendMessage(new StringTextComponent("\u00a7e\u8bc4\u5206: " + score + " (\u672a\u63d0\u5347)"), uuid);
        }

        String grade;
        if (score >= 90) grade = "\u00a7dS\u7ea7 \u00a7f- \u9876\u7ea7\u5546\u4e1a\u8ba1\u5212";
        else if (score >= 75) grade = "\u00a7aA\u7ea7 \u00a7f- \u4f18\u79c0\u5546\u4e1a\u8ba1\u5212";
        else if (score >= 60) grade = "\u00a7bB\u7ea7 \u00a7f- \u826f\u597d\u5546\u4e1a\u8ba1\u5212";
        else if (score >= 40) grade = "\u00a7eC\u7ea7 \u00a7f- \u9700\u6539\u8fdb";
        else grade = "\u00a7cD\u7ea7 \u00a7f- \u9700\u91cd\u5199";
        player.sendMessage(new StringTextComponent("\u00a7e\u8bc4\u7ea7: " + grade), uuid);
        player.sendMessage(new StringTextComponent("\u00a7e\u878d\u8d44\u52a0\u6210: \u00a7a+" + score + "%"), uuid);
        return true;
    }

    public static void show(ServerPlayerEntity player) {
        UUID uuid = player.getUUID();
        int score = planScore.getOrDefault(uuid, 0);
        int reviews = reviewCount.getOrDefault(uuid, 0);
        player.sendMessage(new StringTextComponent("\u00a76\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7e\ud83d\udcc4 \u5546\u4e1a\u8ba1\u5212\u8bc4\u5ba1  \u00a76\u2551"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u255a\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255d"), uuid);
        player.sendMessage(new StringTextComponent("\u00a7e\u8bc4\u5206: \u00a76" + score + "/100"), uuid);
        player.sendMessage(new StringTextComponent("\u00a7e\u8bc4\u5ba1\u6b21\u6570: " + reviews), uuid);
        player.sendMessage(new StringTextComponent("\u00a7e\u878d\u8d44\u52a0\u6210: \u00a7a+" + score + "%"), uuid);
        player.sendMessage(new StringTextComponent("\u00a7e\u8f93\u5165 /plan submit \u63d0\u4ea4\u8bc4\u5ba1"), uuid);
    }

    public static int getScore(UUID uuid) { return planScore.getOrDefault(uuid, 0); }
}
