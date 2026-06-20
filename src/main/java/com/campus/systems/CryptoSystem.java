package com.campus.systems;

import com.campus.CampusLife;
import com.campus.economy.MoneyCapability;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;

/**
 * 数字货币系统 - 挖矿 + 多币种交易（GUI 集成版）
 */
@Mod.EventBusSubscriber(modid = CampusLife.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CryptoSystem {

    // 旧版统一 balance（保留兼容）
    private static final Map<UUID, Integer> cryptoBalance = new HashMap<>();
    private static final Map<UUID, Integer> miningPower = new HashMap<>();

    // 新版：分币种持仓
    private static final Map<UUID, Map<String, Integer>> holdings = new HashMap<>();
    private static final Map<String, Integer> prices = new HashMap<>();
    static {
        prices.put("BTC", 5000);
        prices.put("ETH", 1500);
        prices.put("DOGE", 50);
    }

    private static int tickCounter = 0;
    private static int currentPrice = 500;  // 旧版价格（保留兼容）
    private static final Random RAND = new Random();

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        tickCounter++;
        // 旧版价格波动
        if (tickCounter % 2400 == 0) {
            currentPrice = Math.max(100, currentPrice + RAND.nextInt(200) - 100);
            // 新版多币种价格波动 ±15%
            for (String coin : prices.keySet()) {
                int p = prices.get(coin);
                int delta = (int) (p * (RAND.nextDouble() * 0.3 - 0.15));
                prices.put(coin, Math.max(10, p + delta));
            }
        }
        // 挖矿产出（旧版）
        if (tickCounter % 3600 == 0) {
            for (Map.Entry<UUID, Integer> entry : miningPower.entrySet()) {
                int mined = entry.getValue() / 10;
                if (mined > 0) cryptoBalance.merge(entry.getKey(), mined, Integer::sum);
            }
        }
    }

    /* ==================== 新版多币种 API（GUI 用） ==================== */

    public static int getPrice(String coin) {
        return prices.getOrDefault(coin, 0);
    }

    public static int getHolding(UUID uuid, String coin) {
        Map<String, Integer> h = holdings.get(uuid);
        return h == null ? 0 : h.getOrDefault(coin, 0);
    }

    public static boolean buy(ServerPlayerEntity player, String coin, int amount) {
        UUID uuid = player.getUUID();
        int price = getPrice(coin);
        if (price <= 0) {
            player.sendMessage(new StringTextComponent("\u00a7c\u672a\u77e5\u5e01\u79cd"), uuid);
            return false;
        }
        int cost = price * amount;
        return player.getCapability(MoneyCapability.MONEY_CAP).map(m -> {
            if (m.spendMoney(cost)) {
                holdings.computeIfAbsent(uuid, k -> new HashMap<>()).merge(coin, amount, Integer::sum);
                player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u4e70\u5165 " + amount + " " + coin + " @" + price), uuid);
                return true;
            }
            player.sendMessage(new StringTextComponent("\u00a7c\u8d44\u91d1\u4e0d\u8db3, \u9700 " + cost), uuid);
            return false;
        }).orElse(false);
    }

    public static boolean sell(ServerPlayerEntity player, String coin, int amount) {
        UUID uuid = player.getUUID();
        int held = getHolding(uuid, coin);
        if (held < amount) {
            player.sendMessage(new StringTextComponent("\u00a7c\u6301\u4ed3\u4e0d\u8db3, \u4ec5\u6709 " + held), uuid);
            return false;
        }
        int price = getPrice(coin);
        int revenue = price * amount;
        holdings.get(uuid).merge(coin, -amount, Integer::sum);
        player.getCapability(MoneyCapability.MONEY_CAP).ifPresent(m -> m.addMoney(revenue));
        player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u5356\u51fa " + amount + " " + coin + ", \u83b7\u5f97 " + revenue), uuid);
        return true;
    }

    /* ==================== 旧版兼容 API（V14Commands 用） ==================== */

    public static boolean buyMiner(ServerPlayerEntity player, int power) {
        UUID uuid = player.getUUID();
        int cost = power * 200;
        return player.getCapability(MoneyCapability.MONEY_CAP).map(m -> {
            if (m.spendMoney(cost)) {
                miningPower.merge(uuid, power, Integer::sum);
                player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u8d2d\u4e70\u6316\u77ff\u673a +" + power + " | \u6210\u672c:" + cost), uuid);
                return true;
            }
            player.sendMessage(new StringTextComponent("\u00a7c\u8d44\u91d1\u4e0d\u8db3!"), uuid);
            return false;
        }).orElse(false);
    }

    /** 旧版兼容：卖出统一 cryptoBalance */
    public static boolean sell(ServerPlayerEntity player, int amount) {
        UUID uuid = player.getUUID();
        int balance = cryptoBalance.getOrDefault(uuid, 0);
        if (balance < amount) {
            player.sendMessage(new StringTextComponent("\u00a7c\u4f59\u989d\u4e0d\u8db3! \u6301\u6709:" + balance), uuid);
            return false;
        }
        int revenue = amount * currentPrice;
        cryptoBalance.put(uuid, balance - amount);
        player.getCapability(MoneyCapability.MONEY_CAP).ifPresent(m -> m.addMoney(revenue));
        player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u5356\u51fa" + amount + "\u5e01 | \u83b7\u5f97" + revenue + "\u91d1\u5e01"), uuid);
        return true;
    }

    public static void show(ServerPlayerEntity player) {
        UUID uuid = player.getUUID();
        int balance = cryptoBalance.getOrDefault(uuid, 0);
        int power = miningPower.getOrDefault(uuid, 0);
        int value = balance * currentPrice;
        player.sendMessage(new StringTextComponent("\u00a76=== \u6570\u5b57\u8d27\u5e01 ==="), uuid);
        player.sendMessage(new StringTextComponent("\u00a7e\u4ef7\u683c: \u00a76" + currentPrice + " \u91d1/\u5e01"), uuid);
        player.sendMessage(new StringTextComponent("\u00a7e\u6301\u6709: \u00a7a" + balance + " (\u00a76" + value + "\u91d1\u5e01)"), uuid);
        player.sendMessage(new StringTextComponent("\u00a7e\u7b97\u529b: \u00a7b" + power), uuid);
        player.sendMessage(new StringTextComponent("\u00a7e\u591a\u5e01\u79cd\u4ef7: BTC " + getPrice("BTC") + " | ETH " + getPrice("ETH") + " | DOGE " + getPrice("DOGE")), uuid);
    }
}
