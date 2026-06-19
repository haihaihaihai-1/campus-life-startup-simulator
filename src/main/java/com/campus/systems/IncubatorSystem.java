package com.campus.systems;

import com.campus.CampusLife;
import com.campus.economy.MoneyCapability;
import com.campus.economy.SkillCapability;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;

/**
 * 孵化器系统 - 入驻获取资源buff
 */
@Mod.EventBusSubscriber(modid = CampusLife.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class IncubatorSystem {

    public static class Incubator {
        public String name, description;
        public int fee, durationTicks;
        public int expBonus, incomeBonus, discountPercent;
        public int requiredLevel;

        public Incubator(String name, String desc, int fee, int dur, int exp, int income, int discount, int reqLvl) {
            this.name = name; this.description = desc; this.fee = fee; this.durationTicks = dur;
            this.expBonus = exp; this.incomeBonus = income; this.discountPercent = discount; this.requiredLevel = reqLvl;
        }
    }

    public static final Incubator[] INCUBATORS = {
        new Incubator("\u6821\u56ed\u521b\u4e1a\u56ed", "\u57fa\u7840\u5b75\u5316\u5668", 300, 12000, 20, 5, 5, 1),
        new Incubator("\u9ad8\u65b0\u533a\u521b\u4e1a\u57fa\u5730", "\u4e2d\u7ea7\u5b75\u5316\u5668", 1000, 24000, 50, 15, 10, 5),
        new Incubator("\u79d1\u6280\u56ed\u5b75\u5316\u5668", "\u9ad8\u7ea7\u79d1\u6280\u5b75\u5316", 3000, 36000, 100, 30, 15, 12),
        new Incubator("\u56fd\u5bb6\u7ea7\u5b75\u5316\u5668", "\u9876\u7ea7\u5b75\u5316\u5668", 10000, 72000, 200, 50, 25, 20)
    };

    private static final Map<UUID, IncubatorSession> sessions = new HashMap<>();
    private static int tickCounter = 0;

    public static class IncubatorSession {
        public int incubatorIdx;
        public int remainingTicks;

        public IncubatorSession(int idx, int ticks) { this.incubatorIdx = idx; this.remainingTicks = ticks; }
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        tickCounter++;
        if (tickCounter % 20 != 0) return;

        Iterator<Map.Entry<UUID, IncubatorSession>> it = sessions.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<UUID, IncubatorSession> entry = it.next();
            entry.getValue().remainingTicks -= 20;
            if (entry.getValue().remainingTicks <= 0) it.remove();
        }
    }

    public static boolean join(ServerPlayerEntity player, int idx) {
        UUID uuid = player.getUUID();
        if (idx < 1 || idx > INCUBATORS.length) { player.sendMessage(new StringTextComponent("\u00a7c\u65e0\u6548\u7f16\u53f7!"), uuid); return false; }
        if (sessions.containsKey(uuid)) { player.sendMessage(new StringTextComponent("\u00a7c\u5df2\u5728\u5b75\u5316\u5668\u4e2d!"), uuid); return false; }

        Incubator inc = INCUBATORS[idx - 1];
        int level = player.getCapability(SkillCapability.SKILL_CAP).map(SkillCapability.ISkill::getLevel).orElse(1);
        if (level < inc.requiredLevel) { player.sendMessage(new StringTextComponent("\u00a7c\u9700Lv." + inc.requiredLevel), uuid); return false; }

        return player.getCapability(MoneyCapability.MONEY_CAP).map(m -> {
            if (m.spendMoney(inc.fee)) {
                sessions.put(uuid, new IncubatorSession(idx - 1, inc.durationTicks));
                player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u5165\u9a7b" + inc.name + "!"), uuid);
                player.sendMessage(new StringTextComponent("\u00a7e" + inc.description), uuid);
                player.sendMessage(new StringTextComponent("\u00a7e\u7ecf\u9a8c+" + inc.expBonus + "% | \u6536\u5165+" + inc.incomeBonus + "% | \u6210\u672c-" + inc.discountPercent + "%"), uuid);
                player.sendMessage(new StringTextComponent("\u00a7e\u6301\u7eed: " + (inc.durationTicks/1200) + "\u5206\u949f"), uuid);
                return true;
            }
            player.sendMessage(new StringTextComponent("\u00a7c\u8d44\u91d1\u4e0d\u8db3! \u9700" + inc.fee + "\u91d1\u5e01"), uuid);
            return false;
        }).orElse(false);
    }

    public static void show(ServerPlayerEntity player) {
        UUID uuid = player.getUUID();
        player.sendMessage(new StringTextComponent("\u00a76\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7b\ud83c\udfe2 \u5b75\u5316\u5668  \u00a76\u2551"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u255a\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255d"), uuid);

        for (int i = 0; i < INCUBATORS.length; i++) {
            Incubator inc = INCUBATORS[i];
            player.sendMessage(new StringTextComponent(
                    "\u00a7e[" + (i+1) + "] " + inc.name +
                    " \u00a7f|\u00a76 " + inc.fee + "\u91d1\u5e01" +
                    " \u00a7f|\u00a7a \u7ecf\u9a8c+" + inc.expBonus + "% \u6536\u5165+" + inc.incomeBonus + "%" +
                    " \u00a7f|\u00a7b Lv." + inc.requiredLevel + "+" +
                    " \u00a7f|\u00a77 " + (inc.durationTicks/1200) + "\u5206\u949f"), uuid);
        }

        IncubatorSession s = sessions.get(uuid);
        if (s != null) {
            Incubator inc = INCUBATORS[s.incubatorIdx];
            player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u5f53\u524d: " + inc.name + " | \u5269\u4f59: " + (s.remainingTicks/1200) + "\u5206\u949f"), uuid);
        }
        player.sendMessage(new StringTextComponent("\u00a7e\u8f93\u5165 /incubator <1-4> \u5165\u9a7b"), uuid);
    }

    public static int getExpBonus(UUID uuid) { IncubatorSession s = sessions.get(uuid); return s != null ? INCUBATORS[s.incubatorIdx].expBonus : 0; }
    public static int getIncomeBonus(UUID uuid) { IncubatorSession s = sessions.get(uuid); return s != null ? INCUBATORS[s.incubatorIdx].incomeBonus : 0; }
    public static int getDiscount(UUID uuid) { IncubatorSession s = sessions.get(uuid); return s != null ? INCUBATORS[s.incubatorIdx].discountPercent : 0; }
}
