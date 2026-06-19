package com.campus.systems;

import com.campus.CampusLife;
import com.campus.economy.MoneyCapability;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;

/**
 * 品牌联名系统 - 跨界合作buff
 */
@Mod.EventBusSubscriber(modid = CampusLife.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BrandCollabSystem {

    public enum Collab {
        CAFE("\u5496\u5561\u54c1\u724c\u8054\u540d", 1000, 15, 18000, "\u996e\u54c1\u9500\u552e+15%"),
        TECH("\u79d1\u6280\u54c1\u724c\u8054\u540d", 3000, 20, 24000, "\u79d1\u6280\u9500\u552e+20%"),
        FASHION("\u65f6\u5c1a\u54c1\u724c\u8054\u540d", 5000, 25, 36000, "\u624b\u5de5\u827a\u54c1+25%"),
        CELEBRITY("\u660e\u661f\u4ee3\u8a00\u8054\u540d", 10000, 35, 36000, "\u5168\u9762\u9500\u552e+35%"),
        GLOBAL("\u56fd\u9645\u54c1\u724c\u8054\u540d", 30000, 50, 72000, "\u5168\u9762\u9500\u552e+50%");

        public String name, effect; public int cost, boost, duration;
        Collab(String n, int c, int b, int d, String e) { name=n; cost=c; boost=b; duration=d; effect=e; }
    }

    private static final Map<UUID, ActiveCollab> active = new HashMap<>();

    public static class ActiveCollab {
        public int collabIdx; public int remainingTicks;
        public ActiveCollab(int idx, int ticks) { collabIdx = idx; remainingTicks = ticks; }
    }

    public static boolean launch(ServerPlayerEntity player, int idx) {
        UUID uuid = player.getUUID();
        Collab[] collabs = Collab.values();
        if (idx < 1 || idx > collabs.length) return false;
        if (active.containsKey(uuid)) { player.sendMessage(new StringTextComponent("\u00a7c\u5df2\u6709\u8054\u540d\u8fdb\u884c\u4e2d!"), uuid); return false; }
        Collab c = collabs[idx - 1];
        return player.getCapability(MoneyCapability.MONEY_CAP).map(m -> {
            if (m.spendMoney(c.cost)) {
                active.put(uuid, new ActiveCollab(idx - 1, c.duration));
                player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u54c1\u724c\u8054\u540d\u542f\u52a8! " + c.name), uuid);
                player.sendMessage(new StringTextComponent("\u00a7e" + c.effect + " | " + (c.duration/1200) + "\u5206\u949f"), uuid);
                return true;
            }
            player.sendMessage(new StringTextComponent("\u00a7c\u8d44\u91d1\u4e0d\u8db3!"), uuid); return false;
        }).orElse(false);
    }

    public static void show(ServerPlayerEntity player) {
        UUID uuid = player.getUUID();
        ActiveCollab ac = active.get(uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7d\ud83e\udd1d \u54c1\u724c\u8054\u540d  \u00a76\u2551"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u255a\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255d"), uuid);
        Collab[] collabs = Collab.values();
        for (int i = 0; i < collabs.length; i++) {
            Collab c = collabs[i];
            player.sendMessage(new StringTextComponent("\u00a7e[" + (i+1) + "] " + c.name + " \u00a7f|\u00a76 " + c.cost + "\u91d1\u5e01 \u00a7f|\u00a7a +" + c.boost + "% \u00a7f|\u00a7b " + (c.duration/1200) + "\u5206\u949f \u00a7f|\u00a77 " + c.effect), uuid);
        }
        if (ac != null) {
            player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u5f53\u524d: " + collabs[ac.collabIdx].name + " | \u5269\u4f59:" + (ac.remainingTicks/1200) + "\u5206\u949f"), uuid);
        }
        player.sendMessage(new StringTextComponent("\u00a7e\u8f93\u5165 /collab <1-5> \u542f\u52a8\u8054\u540d"), uuid);
    }

    public static int getBoost(UUID uuid) {
        ActiveCollab ac = active.get(uuid);
        return ac != null ? Collab.values()[ac.collabIdx].boost : 0;
    }
}
