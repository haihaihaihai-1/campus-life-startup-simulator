package com.campus.systems;

import com.campus.CampusLife;
import com.campus.economy.MoneyCapability;
import com.campus.economy.SkillCapability;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.*;

/**
 * 员工期权系统 - 股权激励
 */
@Mod.EventBusSubscriber(modid = CampusLife.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class StockOptionSystem {

    private static final Map<UUID, Integer> optionPool = new HashMap<>();
    private static final Map<UUID, Integer> vestedOptions = new HashMap<>();
    private static int tickCounter = 0;

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        tickCounter++;

        if (tickCounter % 6000 == 0 && ServerLifecycleHooks.getCurrentServer() != null) {
            for (ServerPlayerEntity player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
                UUID uuid = player.getUUID();
                int pool = optionPool.getOrDefault(uuid, 0);
                int vested = vestedOptions.getOrDefault(uuid, 0);
                if (pool > vested) {
                    vestedOptions.put(uuid, vested + 1);
                    player.sendMessage(new StringTextComponent("\u00a7b\u671f\u6743\u5f52\u5c5e +1 | \u5df2\u5f52\u5c5e:" + (vested + 1) + "/" + pool), uuid);
                }
            }
        }
    }

    public static boolean grant(ServerPlayerEntity player, int amount) {
        UUID uuid = player.getUUID();
        int level = player.getCapability(SkillCapability.SKILL_CAP).map(SkillCapability.ISkill::getLevel).orElse(1);
        if (level < 5) { player.sendMessage(new StringTextComponent("\u00a7c\u9700Lv.5+"), uuid); return false; }
        int cost = amount * 100;
        return player.getCapability(MoneyCapability.MONEY_CAP).map(m -> {
            if (m.spendMoney(cost)) {
                optionPool.put(uuid, optionPool.getOrDefault(uuid, 0) + amount);
                player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u53d1\u653e\u671f\u6743 +" + amount + " | \u6210\u672c:" + cost), uuid);
                return true;
            }
            player.sendMessage(new StringTextComponent("\u00a7c\u8d44\u91d1\u4e0d\u8db3!"), uuid); return false;
        }).orElse(false);
    }

    public static boolean exercise(ServerPlayerEntity player) {
        UUID uuid = player.getUUID();
        int vested = vestedOptions.getOrDefault(uuid, 0);
        if (vested == 0) { player.sendMessage(new StringTextComponent("\u00a7c\u65e0\u53ef\u884c\u6743\u671f\u6743!"), uuid); return false; }
        int value = vested * 200;
        vestedOptions.put(uuid, 0);
        optionPool.put(uuid, optionPool.getOrDefault(uuid, 0) - vested);
        player.getCapability(MoneyCapability.MONEY_CAP).ifPresent(m -> m.addMoney(value));
        player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u884c\u6743\u6210\u529f! \u83b7\u5f97:" + value + "\u91d1\u5e01 (" + vested + "\u80a1)"), uuid);
        return true;
    }

    public static void show(ServerPlayerEntity player) {
        UUID uuid = player.getUUID();
        int pool = optionPool.getOrDefault(uuid, 0);
        int vested = vestedOptions.getOrDefault(uuid, 0);
        player.sendMessage(new StringTextComponent("\u00a76\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7b\ud83d\udcc8 \u5458\u5de5\u671f\u6743  \u00a76\u2551"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u255a\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255d"), uuid);
        player.sendMessage(new StringTextComponent("\u00a7e\u671f\u6743\u6c60: " + pool), uuid);
        player.sendMessage(new StringTextComponent("\u00a7e\u5df2\u5f52\u5c5e: \u00a7a" + vested), uuid);
        player.sendMessage(new StringTextComponent("\u00a7e\u53ef\u884c\u6743\u4ef7\u503c: \u00a76" + (vested * 200) + "\u91d1\u5e01"), uuid);
        player.sendMessage(new StringTextComponent("\u00a7e/option grant <\u6570\u91cf> | /option exercise"), uuid);
    }
}
