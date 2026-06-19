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
 * 退税系统 - 年终退税返还
 */
@Mod.EventBusSubscriber(modid = CampusLife.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TaxReturnSystem {

    private static int tickCounter = 0;
    private static final Map<UUID, Integer> accumulatedTax = new HashMap<>();
    private static final Map<UUID, Long> lastReturn = new HashMap<>();
    private static final double RETURN_RATE = 0.15; // 退回15%的已纳税款

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        tickCounter++;

        // 每30分钟自动退税一次
        if (tickCounter % 36000 == 0 && ServerLifecycleHooks.getCurrentServer() != null) {
            for (ServerPlayerEntity player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
                processTaxReturn(player);
            }
        }
    }

    public static void recordTaxPaid(UUID uuid, int amount) {
        accumulatedTax.put(uuid, accumulatedTax.getOrDefault(uuid, 0) + amount);
    }

    public static void processTaxReturn(ServerPlayerEntity player) {
        UUID uuid = player.getUUID();
        int totalPaid = accumulatedTax.getOrDefault(uuid, 0);
        if (totalPaid <= 0) {
            player.sendMessage(new StringTextComponent("\u00a7e\u672c\u671f\u65e0\u53ef\u9000\u7a0e\u989d"), uuid);
            return;
        }

        int returnAmount = (int)(totalPaid * RETURN_RATE);
        player.getCapability(MoneyCapability.MONEY_CAP).ifPresent(m -> m.addMoney(returnAmount));
        accumulatedTax.put(uuid, 0);
        lastReturn.put(uuid, System.currentTimeMillis());

        player.sendMessage(new StringTextComponent("\u00a76\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7a\u2705 \u7a0e\u52a1\u9000\u8fd4  \u00a76\u2551"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7f\u7d2f\u8ba1\u7eb3\u7a0e: \u00a7c" + totalPaid + "\u91d1\u5e01  \u00a76\u2551"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7a\u9000\u7a0e\u6bd4\u4f8b: " + (int)(RETURN_RATE*100) + "%  \u00a76\u2551"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7a\u9000\u7a0e\u91d1\u989d: \u00a76" + returnAmount + "\u91d1\u5e01  \u00a76\u2551"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u255a\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255d"), uuid);
    }

    public static void show(ServerPlayerEntity player) {
        UUID uuid = player.getUUID();
        int accumulated = accumulatedTax.getOrDefault(uuid, 0);
        int estimated = (int)(accumulated * RETURN_RATE);
        player.sendMessage(new StringTextComponent("\u00a76\u2500\u2500\u2500 \u9000\u7a0e\u4fe1\u606f \u2500\u2500\u2500"), uuid);
        player.sendMessage(new StringTextComponent("\u00a7e\u7d2f\u8ba1\u5df2\u7eb3\u7a0e: \u00a7c" + accumulated + "\u91d1\u5e01"), uuid);
        player.sendMessage(new StringTextComponent("\u00a7e\u9884\u8ba1\u53ef\u9000: \u00a7a" + estimated + "\u91d1\u5e01 (15%)"), uuid);
        player.sendMessage(new StringTextComponent("\u00a7e\u9000\u7a0e\u5468\u671f: \u00a7f\u6bcf30\u5206\u949f\u81ea\u52a8\u9000\u7a0e"), uuid);
    }
}
