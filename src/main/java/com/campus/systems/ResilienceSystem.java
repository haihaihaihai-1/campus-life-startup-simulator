package com.campus.systems;

import com.campus.CampusLife;
import com.campus.economy.MoneyCapability;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;

/**
 * 供应链韧性系统 - 抗风险能力
 */
@Mod.EventBusSubscriber(modid = CampusLife.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ResilienceSystem {

    private static final Map<UUID, int[]> resilience = new HashMap<>(); // [diversification, inventory, backup, monitoring]

    public static boolean invest(ServerPlayerEntity player, int type, int amount) {
        UUID uuid = player.getUUID();
        if (type < 1 || type > 4) return false;
        int cost = amount * 50;
        return player.getCapability(MoneyCapability.MONEY_CAP).map(m -> {
            if (m.spendMoney(cost)) {
                int[] r = resilience.getOrDefault(uuid, new int[]{0, 0, 0, 0});
                r[type - 1] = Math.min(100, r[type - 1] + amount);
                resilience.put(uuid, r);
                String[] names = {"\u4f9b\u5e94\u5546\u591a\u5143\u5316", "\u5e93\u5b58\u7f13\u51b2", "\u5907\u7528\u4f9b\u5e94\u5546", "\u98ce\u9669\u76d1\u63a7"};
                player.sendMessage(new StringTextComponent("\u00a7a\u2714 " + names[type-1] + " +" + amount + " | \u6210\u672c:" + cost), uuid);
                return true;
            }
            player.sendMessage(new StringTextComponent("\u00a7c\u8d44\u91d1\u4e0d\u8db3!"), uuid); return false;
        }).orElse(false);
    }

    public static int getTotal(UUID uuid) {
        int[] r = resilience.getOrDefault(uuid, new int[]{0, 0, 0, 0});
        return (r[0] + r[1] + r[2] + r[3]) / 4;
    }

    public static void show(ServerPlayerEntity player) {
        UUID uuid = player.getUUID();
        int[] r = resilience.getOrDefault(uuid, new int[]{0, 0, 0, 0});
        int total = getTotal(uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7b\ud83d\udee1\u00a7f \u4f9b\u5e94\u94fe\u97e7\u6027 " + total + "/100  \u00a76\u2551"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u255a\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255d"), uuid);
        player.sendMessage(new StringTextComponent("\u00a7e\u591a\u5143\u5316: \u00a7a" + r[0] + "/100"), uuid);
        player.sendMessage(new StringTextComponent("\u00a7e\u7f13\u51b2: \u00a7a" + r[1] + "/100"), uuid);
        player.sendMessage(new StringTextComponent("\u00a7e\u5907\u7528: \u00a7a" + r[2] + "/100"), uuid);
        player.sendMessage(new StringTextComponent("\u00a7e\u76d1\u63a7: \u00a7a" + r[3] + "/100"), uuid);
        player.sendMessage(new StringTextComponent("\u00a7e/resilience <1-4> <\u91d1\u989d> \u6295\u8d44"), uuid);
    }
}
