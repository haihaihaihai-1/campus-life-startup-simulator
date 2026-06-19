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
 * 行业峰会系统 - 社交+资源获取
 */
@Mod.EventBusSubscriber(modid = CampusLife.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SummitSystem {

    private static int tickCounter = 0;
    private static boolean summitActive = false;
    private static int summitTimer = 0;
    private static int nextSummitIn = 18000; // 15分钟
    private static final Random RAND = new Random();
    private static final Map<UUID, Boolean> attended = new HashMap<>();

    public static final String[] SUMMITS = {
        "\u6821\u56ed\u521b\u4e1a\u5cf0\u4f1a", "\u79d1\u6280\u521b\u65b0\u8bba\u575b",
        "\u4e92\u8054\u7f51+\u5cf0\u4f1a", "\u9752\u5e74\u521b\u5ba2\u5927\u4f1a",
        "\u5168\u7403\u521b\u4e1a\u5cf0\u4f1a", "\u4ea7\u4e1a\u878d\u5408\u5927\u4f1a"
    };

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        tickCounter++;
        if (!summitActive) {
            nextSummitIn--;
            if (nextSummitIn <= 0) startSummit();
        } else {
            summitTimer++;
            if (summitTimer >= 4800) endSummit();
        }
    }

    private static void startSummit() {
        summitActive = true; summitTimer = 0; attended.clear();
        String name = SUMMITS[RAND.nextInt(SUMMITS.length)];
        if (ServerLifecycleHooks.getCurrentServer() != null) {
            for (ServerPlayerEntity p : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
                p.sendMessage(new StringTextComponent("\u00a7b\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557"), p.getUUID());
                p.sendMessage(new StringTextComponent("\u00a7b\u2551  \u00a7e\ud83c\udf9c " + name + " \u5f00\u59cb!  \u00a7b\u2551"), p.getUUID());
                p.sendMessage(new StringTextComponent("\u00a7b\u2551  \u00a7a\u8f93\u5165 /summit attend \u53c2\u52a0  \u00a7b\u2551"), p.getUUID());
                p.sendMessage(new StringTextComponent("\u00a7b\u255a\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255d"), p.getUUID());
            }
        }
    }

    public static void attend(ServerPlayerEntity player) {
        UUID uuid = player.getUUID();
        if (!summitActive) { player.sendMessage(new StringTextComponent("\u00a7c\u5cf0\u4f1a\u672a\u5f00\u59cb!"), uuid); return; }
        if (attended.getOrDefault(uuid, false)) { player.sendMessage(new StringTextComponent("\u00a7e\u5df2\u53c2\u52a0!"), uuid); return; }
        attended.put(uuid, true);

        int reward = 500 + RAND.nextInt(1500);
        int exp = 200 + RAND.nextInt(300);
        player.getCapability(MoneyCapability.MONEY_CAP).ifPresent(m -> m.addMoney(reward));
        player.getCapability(SkillCapability.SKILL_CAP).ifPresent(s -> s.addExp(exp));
        NetworkSystem.addNetworkPoint(player, 50 + RAND.nextInt(100));

        player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u53c2\u52a0\u5cf0\u4f1a\u6210\u529f!"), uuid);
        player.sendMessage(new StringTextComponent("\u00a7e\u83b7\u5f97: \u00a76" + reward + "\u91d1\u5e01 + \u00a7b" + exp + "\u7ecf\u9a8c + \u00a7d\u4eba\u8109\u70b9"), uuid);
    }

    private static void endSummit() {
        summitActive = false; summitTimer = 0;
        nextSummitIn = 18000 + RAND.nextInt(18000);
        if (ServerLifecycleHooks.getCurrentServer() != null) {
            for (ServerPlayerEntity p : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
                p.sendMessage(new StringTextComponent("\u00a7b\u5cf0\u4f1a\u5df2\u7ed3\u675f | \u4e0b\u4e00\u6b21: " + (nextSummitIn/1200) + "\u5206\u949f\u540e"), p.getUUID());
            }
        }
    }

    public static void show(ServerPlayerEntity player) {
        if (summitActive) {
            player.sendMessage(new StringTextComponent("\u00a7b\u5cf0\u4f1a\u8fdb\u884c\u4e2d! \u5269\u4f59:" + ((4800-summitTimer)/1200) + "\u5206\u949f | \u8f93\u5165 /summit attend"), player.getUUID());
        } else {
            player.sendMessage(new StringTextComponent("\u00a7e\u4e0b\u4e00\u6b21\u5cf0\u4f1a: " + (nextSummitIn/1200) + "\u5206\u949f\u540e"), player.getUUID());
        }
    }
}
