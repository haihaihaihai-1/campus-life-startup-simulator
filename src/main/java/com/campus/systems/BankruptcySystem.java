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
 * 破产保护系统 - 财务危机+重组
 */
@Mod.EventBusSubscriber(modid = CampusLife.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BankruptcySystem {

    private static final Map<UUID, Boolean> inProtection = new HashMap<>();
    private static final Map<UUID, Integer> protectionTicks = new HashMap<>();

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (ServerLifecycleHooks.getCurrentServer() == null) return;

        for (ServerPlayerEntity player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
            UUID uuid = player.getUUID();
            int money = player.getCapability(MoneyCapability.MONEY_CAP).map(MoneyCapability.IMoney::getMoney).orElse(0);

            if (money <= 0 && !inProtection.getOrDefault(uuid, false)) {
                inProtection.put(uuid, true);
                protectionTicks.put(uuid, 12000); // 10分钟保护期
                player.getCapability(MoneyCapability.MONEY_CAP).ifPresent(m -> m.addMoney(500));
                player.sendMessage(new StringTextComponent("\u00a7c\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557"), uuid);
                player.sendMessage(new StringTextComponent("\u00a7c\u2551  \u26a0 \u7834\u4ea7\u4fdd\u62a4\u542f\u52a8!  \u00a7c\u2551"), uuid);
                player.sendMessage(new StringTextComponent("\u00a7c\u2551  \u00a7e\u83b7\u5f97\u7d27\u6025\u8865\u52a9 500\u91d1\u5e01  \u00a7c\u2551"), uuid);
                player.sendMessage(new StringTextComponent("\u00a7c\u2551  \u00a7e\u4fdd\u62a4\u671f10\u5206\u949f\u514d\u7a0e  \u00a7c\u2551"), uuid);
                player.sendMessage(new StringTextComponent("\u00a7c\u255a\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255d"), uuid);
            }

            if (inProtection.getOrDefault(uuid, false)) {
                int ticks = protectionTicks.getOrDefault(uuid, 0) - 20;
                protectionTicks.put(uuid, ticks);
                if (ticks <= 0) {
                    inProtection.put(uuid, false);
                    player.sendMessage(new StringTextComponent("\u00a7e\u7834\u4ea7\u4fdd\u62a4\u671f\u7ed3\u675f! \u8bf7\u52aa\u529b\u7ecf\u8425"), uuid);
                }
            }
        }
    }

    public static boolean restructure(ServerPlayerEntity player) {
        UUID uuid = player.getUUID();
        if (!inProtection.getOrDefault(uuid, false)) { player.sendMessage(new StringTextComponent("\u00a7c\u672a\u5728\u4fdd\u62a4\u671f!"), uuid); return false; }
        int level = player.getCapability(SkillCapability.SKILL_CAP).map(SkillCapability.ISkill::getLevel).orElse(1);
        int bailout = level * 200;
        player.getCapability(MoneyCapability.MONEY_CAP).ifPresent(m -> m.addMoney(bailout));
        inProtection.put(uuid, false);
        player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u91cd\u7ec4\u6210\u529f! \u83b7\u5f97\u91cd\u7ec4\u8d44\u91d1:" + bailout), uuid);
        return true;
    }

    public static void show(ServerPlayerEntity player) {
        UUID uuid = player.getUUID();
        if (inProtection.getOrDefault(uuid, false)) {
            player.sendMessage(new StringTextComponent("\u00a7c\u5904\u4e8e\u7834\u4ea7\u4fdd\u62a4\u4e2d! \u5269\u4f59:" + (protectionTicks.getOrDefault(uuid, 0)/1200) + "\u5206\u949f | /bankruptcy restructure \u91cd\u7ec4"), uuid);
        } else {
            player.sendMessage(new StringTextComponent("\u00a7a\u8d22\u52a1\u72b6\u51b5\u6b63\u5e38"), uuid);
        }
    }

    public static boolean isInProtection(UUID uuid) { return inProtection.getOrDefault(uuid, false); }
}
