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
 * 银行理财系统 - 存款赚利息+理财产品
 */
@Mod.EventBusSubscriber(modid = CampusLife.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SavingsSystem {

    public enum Product {
        CURRENT("\u6d3b\u671f\u5b58\u6b3e", 0.02, 0, 0),
        FIXED_7D("7\u65e5\u7406\u8d22", 0.05, 8400, 1000),
        FIXED_30D("30\u65e5\u5b9a\u5b58", 0.12, 36000, 5000),
        FUND("\u521b\u4e1a\u57fa\u91d1", 0.20, 72000, 20000),
        STOCK_INDEX("\u80a1\u7968\u6307\u6570", 0.35, 144000, 50000);

        public String name; public double interestRate; public int lockPeriod; public int minAmount;
        Product(String n, double r, int l, int m) { name=n; interestRate=r; lockPeriod=l; minAmount=m; }
    }

    private static final Map<UUID, Deposit> deposits = new HashMap<>();
    private static int tickCounter = 0;

    public static class Deposit {
        public int amount; public int productIdx; public int remainingTicks; public int accruedInterest;

        public Deposit(int amount, int idx) {
            this.amount = amount; this.productIdx = idx;
            this.remainingTicks = Product.values()[idx].lockPeriod;
            this.accruedInterest = 0;
        }
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        tickCounter++;

        if (tickCounter % 600 == 0 && ServerLifecycleHooks.getCurrentServer() != null) {
            for (ServerPlayerEntity player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
                UUID uuid = player.getUUID();
                Deposit dep = deposits.get(uuid);
                if (dep == null) continue;

                Product p = Product.values()[dep.productIdx];
                int interest = (int)(dep.amount * p.interestRate / 20);
                dep.accruedInterest += interest;

                if (p.lockPeriod > 0) {
                    dep.remainingTicks -= 600;
                    if (dep.remainingTicks <= 0) {
                        int total = dep.amount + dep.accruedInterest;
                        player.getCapability(MoneyCapability.MONEY_CAP).ifPresent(m -> m.addMoney(total));
                        player.sendMessage(new StringTextComponent(
                                "\u00a7a\u2714 \u7406\u8d22\u5230\u671f! \u8fd4\u8fd8\u672c\u606f: \u00a76" + total + "\u91d1\u5e01 (\u5229\u606f:" + dep.accruedInterest + ")"), uuid);
                        deposits.remove(uuid);
                    }
                } else {
                    player.getCapability(MoneyCapability.MONEY_CAP).ifPresent(m -> m.addMoney(interest));
                }
            }
        }
    }

    public static boolean deposit(ServerPlayerEntity player, int productIdx, int amount) {
        UUID uuid = player.getUUID();
        Product[] products = Product.values();
        if (productIdx < 1 || productIdx > products.length) { player.sendMessage(new StringTextComponent("\u00a7c\u65e0\u6548\u7c7b\u578b!"), uuid); return false; }
        Product p = products[productIdx - 1];
        if (amount < p.minAmount) { player.sendMessage(new StringTextComponent("\u00a7c\u6700\u4f4e\u5b58\u5165: " + p.minAmount + "\u91d1\u5e01"), uuid); return false; }
        if (deposits.containsKey(uuid) && p.lockPeriod > 0) { player.sendMessage(new StringTextComponent("\u00a7c\u5df2\u6709\u5b9a\u671f\u5b58\u6b3e!"), uuid); return false; }

        return player.getCapability(MoneyCapability.MONEY_CAP).map(m -> {
            if (m.spendMoney(amount)) {
                deposits.put(uuid, new Deposit(amount, productIdx - 1));
                player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u5b58\u5165\u6210\u529f! " + p.name + " | \u91d1\u989d:" + amount + " | \u5e74\u5316:" + (int)(p.interestRate*100*20) + "%"), uuid);
                return true;
            }
            player.sendMessage(new StringTextComponent("\u00a7c\u8d44\u91d1\u4e0d\u8db3!"), uuid); return false;
        }).orElse(false);
    }

    public static boolean withdraw(ServerPlayerEntity player) {
        UUID uuid = player.getUUID();
        Deposit dep = deposits.get(uuid);
        if (dep == null) { player.sendMessage(new StringTextComponent("\u00a7c\u65e0\u5b58\u6b3e!"), uuid); return false; }
        if (dep.remainingTicks > 0) { player.sendMessage(new StringTextComponent("\u00a7c\u5b9a\u671f\u672a\u5230\u671f! \u5269\u4f59:" + (dep.remainingTicks/1200) + "\u5206\u949f"), uuid); return false; }

        int total = dep.amount + dep.accruedInterest;
        player.getCapability(MoneyCapability.MONEY_CAP).ifPresent(m -> m.addMoney(total));
        player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u53d6\u51fa\u6210\u529f! \u00a76" + total + "\u91d1\u5e01"), uuid);
        deposits.remove(uuid);
        return true;
    }

    public static void show(ServerPlayerEntity player) {
        UUID uuid = player.getUUID();
        Deposit dep = deposits.get(uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7a\ud83c\udfe6 \u94f6\u884c\u7406\u8d22  \u00a76\u2551"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u255a\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255d"), uuid);

        Product[] products = Product.values();
        for (int i = 0; i < products.length; i++) {
            Product p = products[i];
            player.sendMessage(new StringTextComponent(
                    "\u00a7e[" + (i+1) + "] " + p.name +
                    " \u00a7f|\u00a7a \u5e74\u5316:" + (int)(p.interestRate*100*20) + "%" +
                    " \u00a7f|\u00a7b \u6700\u4f4e:" + p.minAmount +
                    " \u00a7f|\u00a77 " + (p.lockPeriod > 0 ? (p.lockPeriod/1200) + "\u5206\u949f\u5b9a\u671f" : "\u968f\u5b58\u968f\u53d6")), uuid);
        }
        if (dep != null) {
            Product p = products[dep.productIdx];
            player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u5f53\u524d: " + p.name + " | \u672c\u91d1:" + dep.amount + " | \u5229\u606f:" + dep.accruedInterest + (dep.remainingTicks > 0 ? " | \u5269\u4f59:" + (dep.remainingTicks/1200) + "\u5206\u949f" : " | \u53ef\u53d6\u51fa")), uuid);
        }
        player.sendMessage(new StringTextComponent("\u00a7e/savings deposit <1-5> <\u91d1\u989d> | /savings withdraw"), uuid);
    }
}
