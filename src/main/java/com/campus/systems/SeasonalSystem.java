package com.campus.systems;

import com.campus.CampusLife;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.*;

/**
 * 季节活动系统 - 春夏秋冬不同加成
 */
@Mod.EventBusSubscriber(modid = CampusLife.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SeasonalSystem {

    public enum Season {
        SPRING("\u6625\u5b63", "\u751f\u673a\u52c3\u52c3\uff0c\u98df\u54c1\u9500\u552e+20%", 0.2, 0, 0, 0.1),
        SUMMER("\u590f\u5b63", "\u70ed\u60c5\u6d0b\u6ea2\uff0c\u79d1\u6280\u9500\u552e+30%", 0, 0.3, 0.1, 0),
        AUTUMN("\u79cb\u5b63", "\u6536\u83b7\u5b63\u8282\uff0c\u5168\u4ea7\u54c1+10%", 0.1, 0.1, 0.1, 0.1),
        WINTER("\u51ac\u5b63", "\u5e74\u7ec8\u4fc3\u9500\uff0c\u6587\u5177+40% \u4f46\u6210\u672c+15%", 0, 0, 0.4, 0);

        public String name, description;
        public double foodBoost, techBoost, stationeryBoost, costChange;
        Season(String n, String d, double f, double t, double s, double c) {
            name=n; description=d; foodBoost=f; techBoost=t; stationeryBoost=s; costChange=c;
        }
    }

    private static int tickCounter = 0;
    private static Season currentSeason = Season.SPRING;
    private static int seasonTimer = 24000; // 20分钟一季

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        tickCounter++;
        seasonTimer--;

        if (seasonTimer <= 0) {
            Season[] seasons = Season.values();
            currentSeason = seasons[(currentSeason.ordinal() + 1) % seasons.length];
            seasonTimer = 24000;

            if (ServerLifecycleHooks.getCurrentServer() != null) {
                for (ServerPlayerEntity p : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
                    p.sendMessage(new StringTextComponent("\u00a76\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557"), p.getUUID());
                    p.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7b\u2728 \u5b63\u8282\u53d8\u5316: " + currentSeason.name + "  \u00a76\u2551"), p.getUUID());
                    p.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7f" + currentSeason.description + "  \u00a76\u2551"), p.getUUID());
                    p.sendMessage(new StringTextComponent("\u00a76\u255a\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255d"), p.getUUID());
                }
            }
        }
    }

    public static void show(ServerPlayerEntity player) {
        player.sendMessage(new StringTextComponent("\u00a76\u2500\u2500\u2500 \u5b63\u8282\u4fe1\u606f \u2500\u2500\u2500"), player.getUUID());
        player.sendMessage(new StringTextComponent("\u00a7e\u5f53\u524d\u5b63\u8282: \u00a7b" + currentSeason.name), player.getUUID());
        player.sendMessage(new StringTextComponent("\u00a7e\u6548\u679c: \u00a7f" + currentSeason.description), player.getUUID());
        player.sendMessage(new StringTextComponent("\u00a7e\u4e0b\u4e00\u5b63: \u00a76" + (seasonTimer/1200) + "\u5206\u949f\u540e"), player.getUUID());

        Season[] seasons = Season.values();
        for (Season s : seasons) {
            String active = s == currentSeason ? "\u00a7a\u2714\u5f53\u524d" : "\u00a77";
            player.sendMessage(new StringTextComponent(active + " " + s.name + " \u00a7f|\u00a77 " + s.description), player.getUUID());
        }
    }

    public static Season getCurrentSeason() { return currentSeason; }
    public static double getFoodBoost() { return currentSeason.foodBoost; }
    public static double getTechBoost() { return currentSeason.techBoost; }
    public static double getStationeryBoost() { return currentSeason.stationeryBoost; }
    public static double getCostChange() { return currentSeason.costChange; }
}
