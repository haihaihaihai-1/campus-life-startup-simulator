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
 * 跨境支付系统 - 汇率套利
 */
@Mod.EventBusSubscriber(modid = CampusLife.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CrossBorderSystem {

    public enum Currency {
        USD("\u7f8e\u5143", 7.2), EUR("\u6b27\u5143", 7.8), JPY("\u65e5\u5143", 0.05),
        GBP("\u82f1\u9551", 9.1), KRW("\u97e9\u5143", 0.005), SGD("\u65b0\u52a0\u5761\u5143", 5.3);

        public String name; public double baseRate;
        Currency(String n, double r) { name=n; baseRate=r; }
    }

    private static final Map<UUID, Map<Currency, Integer>> holdings = new HashMap<>();
    private static final Map<Currency, Double> currentRates = new HashMap<>();
    private static int tickCounter = 0;
    private static final Random RAND = new Random();

    static {
        for (Currency c : Currency.values()) currentRates.put(c, c.baseRate);
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        tickCounter++;
        if (tickCounter % 2400 == 0) {
            for (Currency c : Currency.values()) {
                double fluctuation = 1.0 + (RAND.nextDouble() * 0.2 - 0.1);
                currentRates.put(c, c.baseRate * fluctuation);
            }
        }
    }

    public static boolean exchange(ServerPlayerEntity player, int currencyIdx, int goldAmount) {
        UUID uuid = player.getUUID();
        Currency[] currencies = Currency.values();
        if (currencyIdx < 1 || currencyIdx > currencies.length) return false;
        Currency c = currencies[currencyIdx - 1];
        double rate = currentRates.get(c);
        int foreignAmount = (int)(goldAmount / rate);

        return player.getCapability(MoneyCapability.MONEY_CAP).map(m -> {
            if (m.spendMoney(goldAmount)) {
                holdings.computeIfAbsent(uuid, k -> new HashMap<>()).merge(c, foreignAmount, Integer::sum);
                player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u5151\u6362\u6210\u529f! \u00a76" + goldAmount + "\u91d1\u5e01 \u2192 " + foreignAmount + " " + c.name + " (\u6c47\u7387:" + String.format("%.2f", rate) + ")"), uuid);
                return true;
            }
            player.sendMessage(new StringTextComponent("\u00a7c\u8d44\u91d1\u4e0d\u8db3!"), uuid); return false;
        }).orElse(false);
    }

    public static boolean convertBack(ServerPlayerEntity player, int currencyIdx, int foreignAmount) {
        UUID uuid = player.getUUID();
        Currency[] currencies = Currency.values();
        if (currencyIdx < 1 || currencyIdx > currencies.length) return false;
        Currency c = currencies[currencyIdx - 1];
        Map<Currency, Integer> held = holdings.getOrDefault(uuid, new HashMap<>());
        int owned = held.getOrDefault(c, 0);
        if (owned < foreignAmount) { player.sendMessage(new StringTextComponent("\u00a7c\u5916\u5e01\u4e0d\u8db3! \u6301\u6709:" + owned), uuid); return false; }

        double rate = currentRates.get(c);
        int goldAmount = (int)(foreignAmount * rate);
        held.put(c, owned - foreignAmount);
        holdings.put(uuid, held);
        player.getCapability(MoneyCapability.MONEY_CAP).ifPresent(m -> m.addMoney(goldAmount));
        player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u56de\u5151\u6210\u529f! " + foreignAmount + " " + c.name + " \u2192 \u00a76" + goldAmount + "\u91d1\u5e01 (\u6c47\u7387:" + String.format("%.2f", rate) + ")"), uuid);
        return true;
    }

    public static void show(ServerPlayerEntity player) {
        UUID uuid = player.getUUID();
        player.sendMessage(new StringTextComponent("\u00a76\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7a\ud83c\udf0d \u8de8\u5883\u652f\u4ed8  \u00a76\u2551"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u255a\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255d"), uuid);

        Map<Currency, Integer> held = holdings.getOrDefault(uuid, new HashMap<>());
        Currency[] currencies = Currency.values();
        for (int i = 0; i < currencies.length; i++) {
            Currency c = currencies[i];
            double rate = currentRates.get(c);
            int owned = held.getOrDefault(c, 0);
            player.sendMessage(new StringTextComponent("\u00a7e[" + (i+1) + "] " + c.name + " \u00a7f|\u00a76 \u6c47\u7387:" + String.format("%.4f", rate) + " \u00a7f|\u00a7a \u6301\u6709:" + owned), uuid);
        }
        player.sendMessage(new StringTextComponent("\u00a7e/forex buy <1-6> <\u91d1\u5e01> | /forex sell <1-6> <\u5916\u5e01>"), uuid);
    }
}
