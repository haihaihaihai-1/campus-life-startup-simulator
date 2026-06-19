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
 * 产业基金系统 - 设立基金+LP投资
 */
@Mod.EventBusSubscriber(modid = CampusLife.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class IndustryFundSystem {

    private static final Map<UUID, Fund> funds = new HashMap<>();
    private static int tickCounter = 0;
    private static final Random RAND = new Random();

    public static class Fund {
        public int totalCapital; public int investorCount; public int nav; public long createdTime;
        public Fund(int capital) { totalCapital = capital; investorCount = 1; nav = capital; createdTime = System.currentTimeMillis(); }
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        tickCounter++;
        if (tickCounter % 6000 == 0 && ServerLifecycleHooks.getCurrentServer() != null) {
            for (Map.Entry<UUID, Fund> entry : funds.entrySet()) {
                double growth = 1.0 + (RAND.nextDouble() * 0.1 - 0.03);
                entry.getValue().nav = (int)(entry.getValue().nav * growth);
            }
        }
    }

    public static boolean create(ServerPlayerEntity player, int capital) {
        UUID uuid = player.getUUID();
        if (funds.containsKey(uuid)) { player.sendMessage(new StringTextComponent("\u00a7c\u5df2\u6709\u57fa\u91d1!"), uuid); return false; }
        if (capital < 10000) { player.sendMessage(new StringTextComponent("\u00a7c\u6700\u4f4e10000\u91d1\u5e01!"), uuid); return false; }
        return player.getCapability(MoneyCapability.MONEY_CAP).map(m -> {
            if (m.spendMoney(capital)) {
                funds.put(uuid, new Fund(capital));
                player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u8bbe\u7acb\u4ea7\u4e1a\u57fa\u91d1! \u89c4\u6a21:" + capital), uuid);
                return true;
            }
            player.sendMessage(new StringTextComponent("\u00a7c\u8d44\u91d1\u4e0d\u8db3!"), uuid); return false;
        }).orElse(false);
    }

    public static boolean withdraw(ServerPlayerEntity player) {
        UUID uuid = player.getUUID();
        Fund f = funds.get(uuid);
        if (f == null) { player.sendMessage(new StringTextComponent("\u00a7c\u65e0\u57fa\u91d1!"), uuid); return false; }
        player.getCapability(MoneyCapability.MONEY_CAP).ifPresent(m -> m.addMoney(f.nav));
        player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u6e05\u7b97\u57fa\u91d1: \u00a76" + f.nav + "\u91d1\u5e01"), uuid);
        funds.remove(uuid);
        return true;
    }

    public static void show(ServerPlayerEntity player) {
        UUID uuid = player.getUUID();
        Fund f = funds.get(uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7d\ud83d\udcb0 \u4ea7\u4e1a\u57fa\u91d1  \u00a76\u2551"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u255a\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255d"), uuid);
        if (f == null) {
            player.sendMessage(new StringTextComponent("\u00a77\u672a\u8bbe\u7acb\u57fa\u91d1"), uuid);
            player.sendMessage(new StringTextComponent("\u00a7e/fund create <\u91d1\u989d> | \u6700\u4f4e10000"), uuid);
        } else {
            int profit = f.nav - f.totalCapital;
            String profitStr = profit >= 0 ? "\u00a7a+" + profit : "\u00a7c" + profit;
            player.sendMessage(new StringTextComponent("\u00a7e\u521d\u59cb\u8d44\u672c: \u00a76" + f.totalCapital), uuid);
            player.sendMessage(new StringTextComponent("\u00a7e\u5f53\u524d\u51c0\u503c: \u00a76" + f.nav), uuid);
            player.sendMessage(new StringTextComponent("\u00a7e\u6536\u76ca: " + profitStr), uuid);
            player.sendMessage(new StringTextComponent("\u00a7e/fund withdraw \u6e05\u7b97"), uuid);
        }
    }
}
