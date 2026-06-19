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
 * 客户满意度系统 - 声誉影响销售
 */
@Mod.EventBusSubscriber(modid = CampusLife.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ReputationSystem {

    private static final Map<UUID, Integer> reputation = new HashMap<>();
    private static final Map<UUID, Integer> totalSales = new HashMap<>();
    private static int tickCounter = 0;

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        tickCounter++;

        // 每5分钟根据声誉发放奖励/惩罚
        if (tickCounter % 6000 == 0 && ServerLifecycleHooks.getCurrentServer() != null) {
            for (ServerPlayerEntity player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
                UUID uuid = player.getUUID();
                int rep = reputation.getOrDefault(uuid, 50);
                if (rep >= 80) {
                    int bonus = rep - 50;
                    player.getCapability(MoneyCapability.MONEY_CAP).ifPresent(m -> m.addMoney(bonus));
                    player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u53e3\u7891\u5956\u52b1: +" + bonus + "\u91d1\u5e01 (\u58f0\u8a89:" + rep + ")"), uuid);
                } else if (rep < 30) {
                    int penalty = (50 - rep) / 2;
                    player.getCapability(MoneyCapability.MONEY_CAP).ifPresent(m -> m.spendMoney(Math.min(penalty, m.getMoney())));
                    player.sendMessage(new StringTextComponent("\u00a7c\u2718 \u53e3\u7891\u60e9\u7f5a: -" + penalty + "\u91d1\u5e01 (\u58f0\u8a89:" + rep + ")"), uuid);
                }
                // 声誉自然衰减/恢复
                reputation.put(uuid, Math.max(0, Math.min(100, rep + (rep < 50 ? 1 : -1))));
            }
        }
    }

    public static void addReputation(UUID uuid, int amount) {
        int current = reputation.getOrDefault(uuid, 50);
        reputation.put(uuid, Math.max(0, Math.min(100, current + amount)));
    }

    public static int getReputation(UUID uuid) {
        return reputation.getOrDefault(uuid, 50);
    }

    public static void recordSale(UUID uuid, int amount) {
        totalSales.put(uuid, totalSales.getOrDefault(uuid, 0) + amount);
        addReputation(uuid, 1);
    }

    public static void show(ServerPlayerEntity player) {
        UUID uuid = player.getUUID();
        int rep = reputation.getOrDefault(uuid, 50);
        int sales = totalSales.getOrDefault(uuid, 0);
        String rank;
        if (rep >= 90) rank = "\u00a7d\u4f20\u8bf4\u7ea7\u53e3\u7891";
        else if (rep >= 75) rank = "\u00a7a\u4f18\u79c0\u53e3\u7891";
        else if (rep >= 50) rank = "\u00a7e\u826f\u597d\u53e3\u7891";
        else if (rep >= 30) rank = "\u00a76\u4e00\u822c\u53e3\u7891";
        else rank = "\u00a7c\u5dee\u8bc4";

        player.sendMessage(new StringTextComponent("\u00a76\u2500\u2500\u2500 \u53e3\u7891\u7cfb\u7edf \u2500\u2500\u2500"), uuid);
        player.sendMessage(new StringTextComponent("\u00a7e\u58f0\u8a89\u503c: \u00a7f" + rep + "/100 " + rank), uuid);
        player.sendMessage(new StringTextComponent("\u00a7e\u7d2f\u8ba1\u9500\u552e: \u00a76" + sales + "\u91d1\u5e01"), uuid);

        String bar = "\u00a7a";
        for (int i = 0; i < 20; i++) bar += (i < rep / 5 ? "\u2588" : "\u00a77\u2588");
        player.sendMessage(new StringTextComponent(bar + "\u00a7f " + rep + "%"), uuid);

        player.sendMessage(new StringTextComponent("\u00a7e\u63d0\u5347\u65b9\u5f0f: \u9500\u552e\u5546\u54c1 | \u5e7f\u544a\u6295\u653e | \u53c2\u52a0\u5927\u8d5b"), uuid);
    }
}
