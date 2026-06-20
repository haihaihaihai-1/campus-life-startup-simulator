package com.campus.systems;

import com.campus.economy.MoneyCapability;
import com.campus.economy.SkillCapability;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 孵化器系统 - 4 个孵化器，每个有不同的入驻费、加成、要求等级
 * 现已支持 GUI 容器的会话查询
 */
public class IncubatorSystem {

    public static class Incubator {
        public final String name;
        public final int fee;
        public final int requiredLevel;
        public final int bonusPercent;
        public final int durationTicks;

        public Incubator(String name, int fee, int reqLevel, int bonus, int duration) {
            this.name = name;
            this.fee = fee;
            this.requiredLevel = reqLevel;
            this.bonusPercent = bonus;
            this.durationTicks = duration;
        }
    }

    public static final Incubator[] INCUBATORS = {
        new Incubator("\u6821\u56ed\u5b75\u5316\u5668", 500, 1, 10, 24000),    // 校园孵化器
        new Incubator("\u533a\u521b\u4e1a\u5b75\u5316\u5668", 2000, 5, 25, 36000), // 区创业孵化器
        new Incubator("\u5e02\u521b\u4e1a\u5b75\u5316\u5668", 8000, 12, 50, 48000), // 市创业孵化器
        new Incubator("\u56fd\u5bb6\u7ea7\u5b75\u5316\u5668", 30000, 25, 100, 72000) // 国家级孵化器
    };

    public static class IncubatorSession {
        public final int incubatorIdx;
        public int remainingTicks;
        public IncubatorSession(int idx, int ticks) {
            this.incubatorIdx = idx;
            this.remainingTicks = ticks;
        }
    }

    private static final Map<UUID, IncubatorSession> sessions = new HashMap<>();

    /** 玩家入驻孵化器 */
    public static boolean join(ServerPlayerEntity player, int idx) {
        UUID uuid = player.getUUID();
        if (idx < 1 || idx > INCUBATORS.length) {
            player.sendMessage(new StringTextComponent("\u00a7c\u65e0\u6548\u7f16\u53f7"), uuid);
            return false;
        }
        if (sessions.containsKey(uuid)) {
            player.sendMessage(new StringTextComponent("\u00a7c\u4f60\u5df2\u5728\u5b75\u5316\u5668\u4e2d"), uuid);
            return false;
        }
        Incubator inc = INCUBATORS[idx - 1];

        int level = player.getCapability(SkillCapability.SKILL_CAP).map(s -> s.getLevel()).orElse(1);
        if (level < inc.requiredLevel) {
            player.sendMessage(new StringTextComponent("\u00a7c\u9700\u8981\u7b49\u7ea7 Lv." + inc.requiredLevel), uuid);
            return false;
        }

        return player.getCapability(MoneyCapability.MONEY_CAP).map(m -> {
            if (m.spendMoney(inc.fee)) {
                sessions.put(uuid, new IncubatorSession(idx - 1, inc.durationTicks));
                player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u5165\u9a7b: " + inc.name), uuid);
                player.sendMessage(new StringTextComponent("\u00a7e\u52a0\u6210: +" + inc.bonusPercent + "% | \u671f\u9650: " + (inc.durationTicks/1200) + "\u5206\u949f"), uuid);
                return true;
            }
            player.sendMessage(new StringTextComponent("\u00a7c\u8d44\u91d1\u4e0d\u8db3, \u9700 " + inc.fee), uuid);
            return false;
        }).orElse(false);
    }

    public static IncubatorSession getSession(UUID uuid) {
        return sessions.get(uuid);
    }

    public static int getBonus(UUID uuid) {
        IncubatorSession s = sessions.get(uuid);
        return s != null ? INCUBATORS[s.incubatorIdx].bonusPercent : 0;
    }

    /** 收入加成 - 兼容老 API */
    public static int getIncomeBonus(UUID uuid) { return getBonus(uuid); }

    /** 显示状态 - 兼容老 V5Commands */
    public static void show(ServerPlayerEntity player) {
        UUID uuid = player.getUUID();
        IncubatorSession s = sessions.get(uuid);
        if (s == null) {
            player.sendMessage(new StringTextComponent("\u00a77\u672a\u5165\u9a7b\u4efb\u4f55\u5b75\u5316\u5668"), uuid);
        } else {
            Incubator inc = INCUBATORS[s.incubatorIdx];
            player.sendMessage(new StringTextComponent("\u00a7a\u5f53\u524d: " + inc.name +
                " | \u52a0\u6210: +" + inc.bonusPercent + "% | \u5269\u4f59: " + (s.remainingTicks/1200) + "\u5206\u949f"), uuid);
        }
    }

    public static void tick() {
        sessions.entrySet().removeIf(e -> {
            e.getValue().remainingTicks--;
            return e.getValue().remainingTicks <= 0;
        });
    }
}
