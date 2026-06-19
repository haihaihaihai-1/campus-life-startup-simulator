package com.campus.systems;

import com.campus.CampusLife;
import com.campus.economy.MoneyCapability;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;

/**
 * 竞业协议系统 - 防止人才流失+保护商业机密
 */
@Mod.EventBusSubscriber(modid = CampusLife.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class NonCompeteSystem {

    public enum Protection {
        BASIC("\u57fa\u7840\u7ade\u4e1a\u534f\u8bae", 200, 5, 36000),
        STANDARD("\u6807\u51c6\u7ade\u4e1a\u534f\u8bae", 600, 10, 72000),
        PREMIUM("\u9ad8\u7ea7\u7ade\u4e1a\u534f\u8bae", 1500, 20, 144000),
        EXECUTIVE("\u9ad8\u7ba1\u7ade\u4e1a\u534f\u8bae", 5000, 35, 288000);

        public String name; public int cost; public int protectionPercent; public int duration;
        Protection(String n, int c, int p, int d) { name=n; cost=c; protectionPercent=p; duration=d; }
    }

    private static final Map<UUID, ActiveProtection> active = new HashMap<>();

    public static class ActiveProtection {
        public int protectionIdx; public int remainingTicks;
        public ActiveProtection(int idx, int ticks) { protectionIdx = idx; remainingTicks = ticks; }
    }

    public static boolean sign(ServerPlayerEntity player, int idx) {
        UUID uuid = player.getUUID();
        Protection[] types = Protection.values();
        if (idx < 1 || idx > types.length) { player.sendMessage(new StringTextComponent("\u00a7c\u65e0\u6548\u7c7b\u578b!"), uuid); return false; }
        if (active.containsKey(uuid)) { player.sendMessage(new StringTextComponent("\u00a7c\u5df2\u6709\u751f\u6548\u4e2d\u7684\u534f\u8bae!"), uuid); return false; }
        Protection p = types[idx - 1];

        return player.getCapability(MoneyCapability.MONEY_CAP).map(m -> {
            if (m.spendMoney(p.cost)) {
                active.put(uuid, new ActiveProtection(idx - 1, p.duration));
                player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u7b7e\u7f72" + p.name + "!"), uuid);
                player.sendMessage(new StringTextComponent("\u00a7e\u4fdd\u62a4\u5f3a\u5ea6: " + p.protectionPercent + "% | \u6301\u7eed: " + (p.duration/1200) + "\u5206\u949f"), uuid);
                return true;
            }
            player.sendMessage(new StringTextComponent("\u00a7c\u8d44\u91d1\u4e0d\u8db3!"), uuid); return false;
        }).orElse(false);
    }

    public static void show(ServerPlayerEntity player) {
        UUID uuid = player.getUUID();
        ActiveProtection ap = active.get(uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7e\ud83d\udcdc \u7ade\u4e1a\u534f\u8bae  \u00a76\u2551"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u255a\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255d"), uuid);

        Protection[] types = Protection.values();
        for (int i = 0; i < types.length; i++) {
            Protection p = types[i];
            player.sendMessage(new StringTextComponent(
                    "\u00a7e[" + (i+1) + "] " + p.name +
                    " \u00a7f|\u00a76 " + p.cost + "\u91d1\u5e01" +
                    " \u00a7f|\u00a7a \u4fdd\u62a4" + p.protectionPercent + "%" +
                    " \u00a7f|\u00a7b " + (p.duration/1200) + "\u5206\u949f"), uuid);
        }
        if (ap != null) {
            player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u5f53\u524d: " + types[ap.protectionIdx].name + " | \u5269\u4f59:" + (ap.remainingTicks/1200) + "\u5206\u949f"), uuid);
        }
        player.sendMessage(new StringTextComponent("\u00a7e\u8f93\u5165 /noncompete <1-4> \u7b7e\u7f72"), uuid);
    }

    public static int getProtection(UUID uuid) {
        ActiveProtection ap = active.get(uuid);
        return ap != null ? Protection.values()[ap.protectionIdx].protectionPercent : 0;
    }
}
