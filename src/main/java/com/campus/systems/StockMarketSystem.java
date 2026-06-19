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
 * 股市交易系统 - 虚拟股票买卖
 */
@Mod.EventBusSubscriber(modid = CampusLife.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class StockMarketSystem {

    public static class Stock {
        public String symbol, name;
        public int currentPrice, previousPrice;
        public int totalShares;

        public Stock(String symbol, String name, int price) {
            this.symbol = symbol; this.name = name; this.currentPrice = price; this.previousPrice = price; this.totalShares = 100000;
        }
    }

    public static final Stock[] STOCKS = {
        new Stock("CAMP", "\u6821\u56ed\u79d1\u6280", 100),
        new Stock("FOOD", "\u98df\u5802\u96c6\u56e2", 50),
        new Stock("TECH", "\u521b\u65b0\u82af\u7247", 200),
        new Stock("EDU", "\u6559\u80b2\u96c6\u56e2", 150),
        new Stock("BANK", "\u6821\u56ed\u94f6\u884c", 300),
        new Stock("MART", "\u5e02\u573a\u63a7\u80a1", 80)
    };

    private static final Map<UUID, Map<String, Integer>> holdings = new HashMap<>();
    private static int tickCounter = 0;
    private static final Random RAND = new Random();

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        tickCounter++;

        // 每2分钟股价波动
        if (tickCounter % 2400 == 0) {
            for (Stock s : STOCKS) {
                s.previousPrice = s.currentPrice;
                int change = (int)(s.currentPrice * (RAND.nextDouble() * 0.3 - 0.15));
                s.currentPrice = Math.max(10, s.currentPrice + change);
            }

            if (ServerLifecycleHooks.getCurrentServer() != null) {
                for (ServerPlayerEntity p : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
                    Map<String, Integer> h = holdings.get(p.getUUID());
                    if (h != null && !h.isEmpty()) {
                        int portfolio = 0;
                        for (Map.Entry<String, Integer> e : h.entrySet()) {
                            for (Stock s : STOCKS) {
                                if (s.symbol.equals(e.getKey())) { portfolio += s.currentPrice * e.getValue(); break; }
                            }
                        }
                        p.sendMessage(new StringTextComponent("\u00a7b\u80a1\u5e02\u66f4\u65b0! \u6301\u4ed3\u5e02\u503c: \u00a76" + portfolio + "\u91d1\u5e01"), p.getUUID());
                    }
                }
            }
        }
    }

    public static boolean buy(ServerPlayerEntity player, int stockIdx, int shares) {
        UUID uuid = player.getUUID();
        if (stockIdx < 1 || stockIdx > STOCKS.length) { player.sendMessage(new StringTextComponent("\u00a7c\u65e0\u6548\u80a1\u7968!"), uuid); return false; }
        if (shares < 1 || shares > 10000) { player.sendMessage(new StringTextComponent("\u00a7c\u80a1\u6570: 1-10000"), uuid); return false; }

        Stock s = STOCKS[stockIdx - 1];
        int totalCost = s.currentPrice * shares;

        return player.getCapability(MoneyCapability.MONEY_CAP).map(m -> {
            if (m.spendMoney(totalCost)) {
                Map<String, Integer> h = holdings.getOrDefault(uuid, new HashMap<>());
                h.put(s.symbol, h.getOrDefault(s.symbol, 0) + shares);
                holdings.put(uuid, h);
                player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u4e70\u5165: " + s.name + "(" + s.symbol + ") x" + shares + " @ " + s.currentPrice + "\u91d1\u5e01"), uuid);
                player.sendMessage(new StringTextComponent("\u00a7e\u6210\u672c: " + totalCost + "\u91d1\u5e01"), uuid);
                return true;
            }
            player.sendMessage(new StringTextComponent("\u00a7c\u8d44\u91d1\u4e0d\u8db3! \u9700" + totalCost + "\u91d1\u5e01"), uuid);
            return false;
        }).orElse(false);
    }

    public static boolean sell(ServerPlayerEntity player, int stockIdx, int shares) {
        UUID uuid = player.getUUID();
        if (stockIdx < 1 || stockIdx > STOCKS.length) { player.sendMessage(new StringTextComponent("\u00a7c\u65e0\u6548\u80a1\u7968!"), uuid); return false; }

        Stock s = STOCKS[stockIdx - 1];
        Map<String, Integer> h = holdings.getOrDefault(uuid, new HashMap<>());
        int owned = h.getOrDefault(s.symbol, 0);
        if (owned < shares) { player.sendMessage(new StringTextComponent("\u00a7c\u6301\u4ed3\u4e0d\u8db3! \u5f53\u524d: " + owned), uuid); return false; }

        int revenue = s.currentPrice * shares;
        h.put(s.symbol, owned - shares);
        if (h.get(s.symbol) <= 0) h.remove(s.symbol);
        holdings.put(uuid, h);

        player.getCapability(MoneyCapability.MONEY_CAP).ifPresent(m -> m.addMoney(revenue));
        int profit = revenue - (s.previousPrice * shares);
        String profitStr = profit >= 0 ? "\u00a7a\u76c8\u5229" + profit : "\u00a7c\u4e8f\u635f" + (-profit);
        player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u5356\u51fa: " + s.name + " x" + shares + " @ " + s.currentPrice + "\u91d1\u5e01"), uuid);
        player.sendMessage(new StringTextComponent("\u00a7e\u6536\u5165: " + revenue + "\u91d1\u5e01 | " + profitStr), uuid);
        return true;
    }

    public static void show(ServerPlayerEntity player) {
        UUID uuid = player.getUUID();
        player.sendMessage(new StringTextComponent("\u00a76\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7a\ud83d\udcc8 \u80a1\u5e02\u884c\u60c5  \u00a76\u2551"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u255a\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255d"), uuid);

        Map<String, Integer> h = holdings.getOrDefault(uuid, new HashMap<>());
        int portfolio = 0;

        for (int i = 0; i < STOCKS.length; i++) {
            Stock s = STOCKS[i];
            int owned = h.getOrDefault(s.symbol, 0);
            int value = owned * s.currentPrice;
            portfolio += value;
            String trend = s.currentPrice > s.previousPrice ? "\u00a7a\u2191" : (s.currentPrice < s.previousPrice ? "\u00a7c\u2193" : "\u00a7e\u2192");
            player.sendMessage(new StringTextComponent(
                    "\u00a7e[" + (i+1) + "] " + s.symbol + " " + s.name +
                    " \u00a7f|\u00a76 " + s.currentPrice + "\u91d1\u5e01 " + trend +
                    " \u00a7f|\u00a7b \u6301\u4ed3:" + owned +
                    " \u00a7f|\u00a7a \u5e02\u503c:" + value), uuid);
        }
        player.sendMessage(new StringTextComponent("\u00a7a\u603b\u6301\u4ed3\u5e02\u503c: \u00a76" + portfolio + "\u91d1\u5e01"), uuid);
        player.sendMessage(new StringTextComponent("\u00a7e\u8f93\u5165 /stock buy <1-6> <\u80a1\u6570> | /stock sell <1-6> <\u80a1\u6570>"), uuid);
    }
}
