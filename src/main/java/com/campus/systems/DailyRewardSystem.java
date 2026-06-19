package com.campus.systems;

import com.campus.CampusLife;
import com.campus.economy.MoneyCapability;
import com.campus.economy.SkillCapability;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;

/**
 * 每日签到奖励系统拼图 - 连续签到递增奖励
 * 参考: 手游签到系统
 */
@Mod.EventBusSubscriber(modid = CampusLife.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DailyRewardSystem {

    private static final Map<UUID, LoginRecord> records = new HashMap<>();

    // 签到奖励表(连续天数 → 奖励)
    public static final int[][] REWARD_TABLE = {
        {1, 100, 10},    // 第1天: 100金币 + 10经验
        {2, 150, 15},
        {3, 200, 20},
        {4, 300, 30},
        {5, 500, 50},
        {6, 800, 80},
        {7, 1500, 150},   // 第7天: 大奖
    };

    public static class LoginRecord {
        public int consecutiveDays;
        public int totalDays;
        public long lastLoginDate;

        public LoginRecord() {
            this.consecutiveDays = 0;
            this.totalDays = 0;
            this.lastLoginDate = 0;
        }
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getPlayer() instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) event.getPlayer();
            checkDailyReward(player);
        }
    }

    public static void checkDailyReward(ServerPlayerEntity player) {
        UUID uuid = player.getUUID();
        LoginRecord record = records.getOrDefault(uuid, new LoginRecord());

        long today = getTodayMillis();
        long lastLogin = record.lastLoginDate;

        // 如果今天已经签过到
        if (isSameDay(today, lastLogin)) {
            player.sendMessage(new StringTextComponent(
                    "\u00a7e\u4eca\u65e5\u5df2\u7b7e\u5230 | \u8fde\u7eed\u7b7e\u5230: \u00a76" + record.consecutiveDays + " \u5929"), uuid);
            return;
        }

        // 检查是否连续
        long yesterday = today - 86400000L;
        if (isSameDay(lastLogin, yesterday)) {
            record.consecutiveDays++;
        } else {
            record.consecutiveDays = 1; // 断签重置
        }

        record.totalDays++;
        record.lastLoginDate = today;

        // 获取奖励
        int day = Math.min(record.consecutiveDays, 7);
        int[] reward = REWARD_TABLE[day - 1];
        int money = reward[1];
        int exp = reward[2];

        // 第7天额外奖励
        if (record.consecutiveDays % 7 == 0) {
            money *= 2;
            exp *= 2;
        }

        final int finalMoney = money;
        final int finalExp = exp;
        player.getCapability(MoneyCapability.MONEY_CAP).ifPresent(m -> m.addMoney(finalMoney));
        player.getCapability(SkillCapability.SKILL_CAP).ifPresent(s -> s.addExp(finalExp));
        records.put(uuid, record);

        player.sendMessage(new StringTextComponent("\u00a76\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7e\u2705 \u6bcf\u65e5\u7b7e\u5230  \u00a76\u2551"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7f\u8fde\u7eed\u7b7e\u5230: \u00a76" + record.consecutiveDays + " \u5929  \u00a76\u2551"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7f\u7d2f\u8ba1\u7b7e\u5230: \u00a7b" + record.totalDays + " \u5929  \u00a76\u2551"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7a\u5956\u52b1: \u00a76" + finalMoney + " \u91d1\u5e01 + \u00a7b" + finalExp + " \u7ecf\u9a8c  \u00a76\u2551"), uuid);
        if (record.consecutiveDays % 7 == 0) {
            player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7d\u2605 \u5468\u5927\u5956\u53cc\u500d!  \u00a76\u2551"), uuid);
        }
        player.sendMessage(new StringTextComponent("\u00a76\u255a\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255d"), uuid);

        // 检查成就
        if (record.consecutiveDays >= 7) {
            AchievementSystem.awardManual(player, "first_money");
        }
    }

    public static void showStatus(ServerPlayerEntity player) {
        UUID uuid = player.getUUID();
        LoginRecord record = records.getOrDefault(uuid, new LoginRecord());

        player.sendMessage(new StringTextComponent("\u00a76\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7e\u2705 \u7b7e\u5230\u72b6\u6001  \u00a76\u2551"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u255a\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255d"), uuid);
        player.sendMessage(new StringTextComponent("\u00a7e\u8fde\u7eed\u7b7e\u5230: \u00a76" + record.consecutiveDays + " \u5929"), uuid);
        player.sendMessage(new StringTextComponent("\u00a7e\u7d2f\u8ba1\u7b7e\u5230: \u00a7b" + record.totalDays + " \u5929"), uuid);

        player.sendMessage(new StringTextComponent("\u00a7e\u5956\u52b1\u8868:"), uuid);
        for (int[] r : REWARD_TABLE) {
            String bonus = (r[0] == 7) ? "\u00a7d(\u5468\u5927\u5956\u53cc\u500d)" : "";
            player.sendMessage(new StringTextComponent(
                    "  \u00a7f\u7b2c" + r[0] + "\u5929: \u00a76" + r[1] + "\u91d1\u5e01 + \u00a7b" + r[2] + "\u7ecf\u9a8c " + bonus), uuid);
        }
    }

    private static long getTodayMillis() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    private static boolean isSameDay(long t1, long t2) {
        return Math.abs(t1 - t2) < 1000 && t1 > 0;
    }
}
