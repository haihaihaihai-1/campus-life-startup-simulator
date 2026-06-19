package com.campus.systems;

import com.campus.CampusLife;
import com.campus.economy.MoneyCapability;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.*;

/**
 * ESG评级系统 - 环境/社会/治理三维评分
 */
@Mod.EventBusSubscriber(modid = CampusLife.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ESGSystem {

    private static final Map<UUID, int[]> esgScores = new HashMap<>(); // [E, S, G]
    private static int tickCounter = 0;

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        tickCounter++;
        if (tickCounter % 12000 == 0 && ServerLifecycleHooks.getCurrentServer() != null) {
            for (ServerPlayerEntity player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
                UUID uuid = player.getUUID();
                int[] scores = esgScores.getOrDefault(uuid, new int[]{50, 50, 50});
                int avg = (scores[0] + scores[1] + scores[2]) / 3;
                if (avg >= 80) {
                    int bonus = avg;
                    player.getCapability(MoneyCapability.MONEY_CAP).ifPresent(m -> m.addMoney(bonus));
                    player.sendMessage(new StringTextComponent("\u00a7a\u2714 ESG\u5956\u52b1: +" + bonus + "\u91d1\u5e01 (\u8bc4\u7ea7:" + getGrade(avg) + ")"), uuid);
                } else if (avg < 30) {
                    int penalty = (50 - avg) * 2;
                    player.getCapability(MoneyCapability.MONEY_CAP).ifPresent(m -> m.spendMoney(Math.min(penalty, m.getMoney())));
                    player.sendMessage(new StringTextComponent("\u00a7c\u2718 ESG\u60e9\u7f5a: -" + penalty + "\u91d1\u5e01 (\u8bc4\u7ea7:" + getGrade(avg) + ")"), uuid);
                }
                for (int i = 0; i < 3; i++) scores[i] = Math.max(0, Math.min(100, scores[i] + (scores[i] < 50 ? 1 : -1)));
                esgScores.put(uuid, scores);
            }
        }
    }

    public static boolean invest(ServerPlayerEntity player, int dimension, int amount) {
        UUID uuid = player.getUUID();
        if (dimension < 1 || dimension > 3) return false;
        int cost = amount * 10;
        return player.getCapability(MoneyCapability.MONEY_CAP).map(m -> {
            if (m.spendMoney(cost)) {
                int[] scores = esgScores.getOrDefault(uuid, new int[]{50, 50, 50});
                scores[dimension - 1] = Math.min(100, scores[dimension - 1] + amount);
                esgScores.put(uuid, scores);
                String[] dims = {"\u73af\u5883E", "\u793e\u4f1aS", "\u6cbb\u7406G"};
                player.sendMessage(new StringTextComponent("\u00a7a\u2714 " + dims[dimension-1] + " +" + amount + " | \u6210\u672c:" + cost), uuid);
                return true;
            }
            player.sendMessage(new StringTextComponent("\u00a7c\u8d44\u91d1\u4e0d\u8db3!"), uuid); return false;
        }).orElse(false);
    }

    private static String getGrade(int avg) {
        if (avg >= 90) return "\u00a7dAAA";
        if (avg >= 80) return "\u00a7aAA";
        if (avg >= 70) return "\u00a7bA";
        if (avg >= 60) return "\u00a7eBBB";
        if (avg >= 40) return "\u00a76BB";
        return "\u00a7cC";
    }

    public static void show(ServerPlayerEntity player) {
        UUID uuid = player.getUUID();
        int[] scores = esgScores.getOrDefault(uuid, new int[]{50, 50, 50});
        int avg = (scores[0] + scores[1] + scores[2]) / 3;
        player.sendMessage(new StringTextComponent("\u00a76\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7a\ud83c\udf31 ESG\u8bc4\u7ea7 " + getGrade(avg) + "  \u00a76\u2551"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u255a\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255d"), uuid);
        player.sendMessage(new StringTextComponent("\u00a7e\u73af\u5883(E): \u00a7a" + scores[0] + "/100"), uuid);
        player.sendMessage(new StringTextComponent("\u00a7e\u793e\u4f1a(S): \u00a7b" + scores[1] + "/100"), uuid);
        player.sendMessage(new StringTextComponent("\u00a7e\u6cbb\u7406(G): \u00a7d" + scores[2] + "/100"), uuid);
        player.sendMessage(new StringTextComponent("\u00a7e\u7efc\u5408: " + avg + "/100 " + getGrade(avg)), uuid);
        player.sendMessage(new StringTextComponent("\u00a7e/esg <1-3> <\u91d1\u989d> \u6295\u8d44ESG"), uuid);
    }

    public static int getRating(UUID uuid) {
        int[] scores = esgScores.getOrDefault(uuid, new int[]{50, 50, 50});
        return (scores[0] + scores[1] + scores[2]) / 3;
    }
}
