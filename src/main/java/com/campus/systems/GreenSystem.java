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
 * 环保合规系统 - 碳配额+绿色认证
 */
@Mod.EventBusSubscriber(modid = CampusLife.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class GreenSystem {

    private static final Map<UUID, Integer> carbonCredits = new HashMap<>();
    private static final Map<UUID, Integer> carbonEmissions = new HashMap<>();
    private static final Map<UUID, Boolean> greenCertified = new HashMap<>();
    private static int tickCounter = 0;

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        tickCounter++;

        if (tickCounter % 12000 == 0 && ServerLifecycleHooks.getCurrentServer() != null) {
            for (ServerPlayerEntity player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
                UUID uuid = player.getUUID();
                int employees = EmployeeSystem.calculateTotalEmployees(uuid);
                int emissions = employees * 10 + 20;
                carbonEmissions.put(uuid, emissions);
                int credits = carbonCredits.getOrDefault(uuid, 100);
                if (emissions > credits) {
                    int penalty = (emissions - credits) * 5;
                    player.getCapability(MoneyCapability.MONEY_CAP).ifPresent(m -> m.spendMoney(Math.min(penalty, m.getMoney())));
                    player.sendMessage(new StringTextComponent("\u00a7c\u2718 \u78b3\u6392\u653e\u8d85\u6807! \u7f5a\u6b3e:" + penalty + "\u91d1\u5e01"), uuid);
                } else if (greenCertified.getOrDefault(uuid, false)) {
                    int bonus = credits - emissions;
                    player.getCapability(MoneyCapability.MONEY_CAP).ifPresent(m -> m.addMoney(bonus));
                    player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u7eff\u8272\u8ba4\u8bc1\u5956\u52b1: +" + bonus + "\u91d1\u5e01"), uuid);
                }
            }
        }
    }

    public static boolean buyCredits(ServerPlayerEntity player, int amount) {
        UUID uuid = player.getUUID();
        int cost = amount * 10;
        return player.getCapability(MoneyCapability.MONEY_CAP).map(m -> {
            if (m.spendMoney(cost)) {
                carbonCredits.put(uuid, carbonCredits.getOrDefault(uuid, 100) + amount);
                player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u8d2d\u4e70\u78b3\u914d\u989d +" + amount + " | \u6210\u672c:" + cost), uuid);
                return true;
            }
            player.sendMessage(new StringTextComponent("\u00a7c\u8d44\u91d1\u4e0d\u8db3!"), uuid); return false;
        }).orElse(false);
    }

    public static boolean certify(ServerPlayerEntity player) {
        UUID uuid = player.getUUID();
        if (greenCertified.getOrDefault(uuid, false)) { player.sendMessage(new StringTextComponent("\u00a7e\u5df2\u8ba4\u8bc1!"), uuid); return false; }
        return player.getCapability(MoneyCapability.MONEY_CAP).map(m -> {
            if (m.spendMoney(5000)) {
                greenCertified.put(uuid, true);
                carbonCredits.put(uuid, carbonCredits.getOrDefault(uuid, 100) + 50);
                player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u83b7\u5f97\u7eff\u8272\u8ba4\u8bc1! \u78b3\u914d\u989d+50 | \u6bcf\u5468\u83b7\u5f97\u5956\u52b1"), uuid);
                return true;
            }
            player.sendMessage(new StringTextComponent("\u00a7c\u8d44\u91d1\u4e0d\u8db3! \u97005000"), uuid); return false;
        }).orElse(false);
    }

    public static void show(ServerPlayerEntity player) {
        UUID uuid = player.getUUID();
        int credits = carbonCredits.getOrDefault(uuid, 100);
        int emissions = carbonEmissions.getOrDefault(uuid, 20);
        boolean certified = greenCertified.getOrDefault(uuid, false);
        player.sendMessage(new StringTextComponent("\u00a76\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7a\ud83c\udf31 \u73af\u4fdd\u5408\u89c4  \u00a76\u2551"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u255a\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255d"), uuid);
        player.sendMessage(new StringTextComponent("\u00a7e\u78b3\u914d\u989d: \u00a7a" + credits), uuid);
        player.sendMessage(new StringTextComponent("\u00a7e\u78b3\u6392\u653e: \u00a7c" + emissions), uuid);
        player.sendMessage(new StringTextComponent("\u00a7e\u7eff\u8272\u8ba4\u8bc1: " + (certified ? "\u00a7a\u2714\u5df2\u8ba4\u8bc1" : "\u00a7c\u672a\u8ba4\u8bc1")), uuid);
        player.sendMessage(new StringTextComponent("\u00a7e/green buy <\u6570\u91cf> | /green certify"), uuid);
    }
}
