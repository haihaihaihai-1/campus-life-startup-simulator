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
 * CRM客户关系管理系统 - 客户分级+忠诚度
 */
@Mod.EventBusSubscriber(modid = CampusLife.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CRMSystem {

    private static final Map<UUID, int[]> customers = new HashMap<>(); // [bronze, silver, gold, platinum]
    private static final Map<UUID, Integer> loyalty = new HashMap<>();
    private static int tickCounter = 0;

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        tickCounter++;
        if (tickCounter % 6000 == 0 && ServerLifecycleHooks.getCurrentServer() != null) {
            for (ServerPlayerEntity player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
                UUID uuid = player.getUUID();
                int[] custs = customers.getOrDefault(uuid, new int[]{0, 0, 0, 0});
                int[] rates = {10, 30, 80, 200};
                int total = 0;
                for (int i = 0; i < 4; i++) total += custs[i] * rates[i];
                if (total > 0) {
                    final int income = total;
                    player.getCapability(MoneyCapability.MONEY_CAP).ifPresent(m -> m.addMoney(income));
                    player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u5ba2\u6237\u6536\u5165: \u00a76" + income + "\u91d1\u5e01"), uuid);
                }
            }
        }
    }

    public static boolean acquire(ServerPlayerEntity player, int tier) {
        UUID uuid = player.getUUID();
        if (tier < 1 || tier > 4) return false;
        int[] costs = {50, 200, 800, 3000};
        int cost = costs[tier - 1];
        return player.getCapability(MoneyCapability.MONEY_CAP).map(m -> {
            if (m.spendMoney(cost)) {
                int[] custs = customers.getOrDefault(uuid, new int[]{0, 0, 0, 0});
                custs[tier - 1]++;
                customers.put(uuid, custs);
                loyalty.merge(uuid, 5, Integer::sum);
                String[] names = {"\u9752\u94dc", "\u767d\u94f6", "\u9ec4\u91d1", "\u94c2\u91d1"};
                player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u83b7\u5f97" + names[tier-1] + "\u5ba2\u6237 | \u6210\u672c:" + cost), uuid);
                return true;
            }
            player.sendMessage(new StringTextComponent("\u00a7c\u8d44\u91d1\u4e0d\u8db3!"), uuid); return false;
        }).orElse(false);
    }

    public static void show(ServerPlayerEntity player) {
        UUID uuid = player.getUUID();
        int[] custs = customers.getOrDefault(uuid, new int[]{0, 0, 0, 0});
        int totalCusts = custs[0] + custs[1] + custs[2] + custs[3];
        int loyaltyVal = loyalty.getOrDefault(uuid, 0);
        int[] rates = {10, 30, 80, 200};
        int totalIncome = 0;
        for (int i = 0; i < 4; i++) totalIncome += custs[i] * rates[i];
        String[] names = {"\u9752\u94dc", "\u767d\u94f6", "\u9ec4\u91d1", "\u94c2\u91d1"};
        player.sendMessage(new StringTextComponent("\u00a76\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7b\ud83e\udd1d CRM\u5ba2\u6237\u7ba1\u7406  \u00a76\u2551"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u255a\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255d"), uuid);
        for (int i = 0; i < 4; i++) {
            player.sendMessage(new StringTextComponent("\u00a7e[" + (i+1) + "] " + names[i] + ": " + custs[i] + "\u4eba | \u00a7a" + rates[i] + "\u91d1\u5e01/\u5468 | \u00a76\u83b7\u53d6\u8d39" + new int[]{50,200,800,3000}[i]), uuid);
        }
        player.sendMessage(new StringTextComponent("\u00a7e\u603b\u5ba2\u6237: " + totalCusts + " | \u00a7a\u603b\u6536\u5165: " + totalIncome + "/\u5468 | \u00a7b\u5fe0\u8bda\u5ea6: " + loyaltyVal), uuid);
        player.sendMessage(new StringTextComponent("\u00a7e/crm <1-4> \u83b7\u53d6\u5ba2\u6237"), uuid);
    }
}
