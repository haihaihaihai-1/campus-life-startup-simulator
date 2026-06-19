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
 * 数字货币系统 - 挖矿+交易
 */
@Mod.EventBusSubscriber(modid = CampusLife.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CryptoSystem {

    private static final Map<UUID, Integer> cryptoBalance = new HashMap<>();
    private static final Map<UUID, Integer> miningPower = new HashMap<>();
    private static int tickCounter = 0;
    private static int currentPrice = 500;
    private static final Random RAND = new Random();

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        tickCounter++;
        if (tickCounter % 2400 == 0) currentPrice = Math.max(100, currentPrice + RAND.nextInt(200) - 100);
        if (tickCounter % 3600 == 0) {
            for (Map.Entry<UUID, Integer> entry : miningPower.entrySet()) {
                int mined = entry.getValue() / 10;
                if (mined > 0) cryptoBalance.merge(entry.getKey(), mined, Integer::sum);
            }
        }
    }

    public static boolean buyMiner(ServerPlayerEntity player, int power) {
        UUID uuid = player.getUUID();
        int cost = power * 200;
        return player.getCapability(MoneyCapability.MONEY_CAP).map(m -> {
            if (m.spendMoney(cost)) {
                miningPower.merge(uuid, power, Integer::sum);
                player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u8d2d\u4e70\u6316\u77ff\u673a +" + power + " | \u6210\u672c:" + cost), uuid);
                return true;
            }
            player.sendMessage(new StringTextComponent("\u00a7c\u8d44\u91d1\u4e0d\u8db3!"), uuid); return false;
        }).orElse(false);
    }

    public static boolean sell(ServerPlayerEntity player, int amount) {
        UUID uuid = player.getUUID();
        int balance = cryptoBalance.getOrDefault(uuid, 0);
        if (balance < amount) { player.sendMessage(new StringTextComponent("\u00a7c\u4f59\u989d\u4e0d\u8db3! \u6301\u6709:" + balance), uuid); return false; }
        int revenue = amount * currentPrice;
        cryptoBalance.put(uuid, balance - amount);
        player.getCapability(MoneyCapability.MONEY_CAP).ifPresent(m -> m.addMoney(revenue));
        player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u5356\u51fa" + amount + "\u5e01 | \u83b7\u5f97" + revenue + "\u91d1\u5e01 (\u4ef7:" + currentPrice + ")"), uuid);
        return true;
    }

    public static void show(ServerPlayerEntity player) {
        UUID uuid = player.getUUID();
        int balance = cryptoBalance.getOrDefault(uuid, 0);
        int power = miningPower.getOrDefault(uuid, 0);
        int value = balance * currentPrice;
        player.sendMessage(new StringTextComponent("\u00a76\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7d\u20bf \u6570\u5b57\u8d27\u5e01  \u00a76\u2551"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u255a\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255d"), uuid);
        player.sendMessage(new StringTextComponent("\u00a7e\u5f53\u524d\u4ef7: \u00a76" + currentPrice + "\u91d1\u5e01/\u5e01"), uuid);
        player.sendMessage(new StringTextComponent("\u00a7e\u6301\u6709: \u00a7a" + balance + "\u5e01 (\u00a76" + value + "\u91d1\u5e01)"), uuid);
        player.sendMessage(new StringTextComponent("\u00a7e\u6316\u77ff\u529b: \u00a7b" + power), uuid);
        player.sendMessage(new StringTextComponent("\u00a7e/crypto mine <\u7b97\u529b> | /crypto sell <\u6570\u91cf>"), uuid);
    }
}
