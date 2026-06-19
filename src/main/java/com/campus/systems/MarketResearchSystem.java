package com.campus.systems;

import com.campus.CampusLife;
import com.campus.economy.MoneyCapability;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;

/**
 * 市场调研系统 - 获取市场洞察buff
 */
@Mod.EventBusSubscriber(modid = CampusLife.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MarketResearchSystem {

    public enum Report {
        TREND_ANALYSIS("\u8d8b\u52bf\u5206\u6790\u62a5\u544a", 300, 6000, "\u5e02\u573a\u4ef7\u683c\u9884\u6d4b+10%"),
        COMPETITOR("\u7ade\u4e89\u5bf9\u624b\u62a5\u544a", 800, 6000, "\u5b9a\u4ef7\u4f18\u52bf+15%"),
        CONSUMER("\u6d88\u8d39\u8005\u884c\u4e3a\u62a5\u544a", 1500, 12000, "\u9500\u552e\u52a0\u6210+20%"),
        INDUSTRY("\u884c\u4e1a\u524d\u77bb\u62a5\u544a", 5000, 24000, "\u5168\u9762\u52a0\u6210+25%"),
        FORECAST("\u672a\u67655\u5e74\u9884\u6d4b", 15000, 72000, "\u9ad8\u7ea7\u6d1e\u5bdf+40%");

        public String name, effect; public int cost, duration;
        Report(String n, int c, int d, String e) { name=n; cost=c; duration=d; effect=e; }
    }

    private static final Map<UUID, Map<Report, Long>> active = new HashMap<>();

    public static boolean purchase(ServerPlayerEntity player, int idx) {
        UUID uuid = player.getUUID();
        Report[] reports = Report.values();
        if (idx < 1 || idx > reports.length) { player.sendMessage(new StringTextComponent("\u00a7c\u65e0\u6548\u7f16\u53f7!"), uuid); return false; }
        Report r = reports[idx - 1];

        return player.getCapability(MoneyCapability.MONEY_CAP).map(m -> {
            if (m.spendMoney(r.cost)) {
                Map<Report, Long> list = active.getOrDefault(uuid, new HashMap<>());
                list.put(r, System.currentTimeMillis() + (r.duration * 50L));
                active.put(uuid, list);
                player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u8d2d\u4e70\u62a5\u544a! " + r.name), uuid);
                player.sendMessage(new StringTextComponent("\u00a7e\u6548\u679c: " + r.effect + " | \u6301\u7eed:" + (r.duration/1200) + "\u5206\u949f"), uuid);
                return true;
            }
            player.sendMessage(new StringTextComponent("\u00a7c\u8d44\u91d1\u4e0d\u8db3! \u9700" + r.cost), uuid); return false;
        }).orElse(false);
    }

    public static void show(ServerPlayerEntity player) {
        UUID uuid = player.getUUID();
        Map<Report, Long> list = active.getOrDefault(uuid, new HashMap<>());
        long now = System.currentTimeMillis();

        player.sendMessage(new StringTextComponent("\u00a76\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7b\ud83d\udcc8 \u5e02\u573a\u8c03\u7814  \u00a76\u2551"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u255a\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255d"), uuid);

        Report[] reports = Report.values();
        for (int i = 0; i < reports.length; i++) {
            Report r = reports[i];
            boolean isActive = list.containsKey(r) && list.get(r) > now;
            player.sendMessage(new StringTextComponent(
                    "\u00a7e[" + (i+1) + "] " + r.name +
                    " \u00a7f|\u00a76 " + r.cost + "\u91d1\u5e01" +
                    " \u00a7f|\u00a7a " + r.effect +
                    " \u00a7f|\u00a7b " + (r.duration/1200) + "\u5206\u949f" +
                    " \u00a7f| " + (isActive ? "\u00a7a\u2714\u751f\u6548\u4e2d" : "\u00a77\u672a\u8d2d\u4e70")), uuid);
        }
        player.sendMessage(new StringTextComponent("\u00a7e\u8f93\u5165 /research_market <1-5> \u8d2d\u4e70"), uuid);
    }

    public static int getTotalBoost(UUID uuid) {
        Map<Report, Long> list = active.getOrDefault(uuid, new HashMap<>());
        long now = System.currentTimeMillis();
        int boost = 0;
        for (Map.Entry<Report, Long> e : list.entrySet()) {
            if (e.getValue() > now) {
                String effect = e.getKey().effect;
                if (effect.contains("+10%")) boost += 10;
                else if (effect.contains("+15%")) boost += 15;
                else if (effect.contains("+20%")) boost += 20;
                else if (effect.contains("+25%")) boost += 25;
                else if (effect.contains("+40%")) boost += 40;
            }
        }
        return boost;
    }
}
