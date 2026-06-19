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
 * 数据资产系统 - 数据变现
 */
@Mod.EventBusSubscriber(modid = CampusLife.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DataAssetSystem {

    private static final Map<UUID, Integer> dataPoints = new HashMap<>();
    private static final Map<UUID, Set<String>> dataSources = new HashMap<>();
    private static int tickCounter = 0;

    public static final String[] SOURCES = {
        "\u7528\u6237\u884c\u4e3a\u6570\u636e", "\u4ea4\u6613\u8bb0\u5f55\u6570\u636e", "\u5e02\u573a\u8c03\u7814\u6570\u636e",
        "\u5ba2\u6237\u753b\u50cf\u6570\u636e", "\u4f9b\u5e94\u94fe\u6570\u636e", "\u793e\u4ea4\u8209\u52a8\u6570\u636e"
    };

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        tickCounter++;

        if (tickCounter % 3600 == 0 && ServerLifecycleHooks.getCurrentServer() != null) {
            for (ServerPlayerEntity player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
                UUID uuid = player.getUUID();
                int sources = dataSources.getOrDefault(uuid, new HashSet<>()).size();
                if (sources > 0) {
                    int generated = sources * 10 + new Random().nextInt(sources * 5);
                    dataPoints.put(uuid, dataPoints.getOrDefault(uuid, 0) + generated);
                }
            }
        }
    }

    public static boolean acquire(ServerPlayerEntity player, int sourceIdx) {
        UUID uuid = player.getUUID();
        if (sourceIdx < 1 || sourceIdx > SOURCES.length) { player.sendMessage(new StringTextComponent("\u00a7c\u65e0\u6548\u7f16\u53f7!"), uuid); return false; }
        Set<String> sources = dataSources.getOrDefault(uuid, new HashSet<>());
        String source = SOURCES[sourceIdx - 1];
        if (sources.contains(source)) { player.sendMessage(new StringTextComponent("\u00a7c\u5df2\u6388\u6743!"), uuid); return false; }
        int cost = 1000 * sourceIdx;
        return player.getCapability(MoneyCapability.MONEY_CAP).map(m -> {
            if (m.spendMoney(cost)) {
                sources.add(source);
                dataSources.put(uuid, sources);
                player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u83b7\u53d6\u6570\u636e\u6e90: " + source), uuid);
                return true;
            }
            player.sendMessage(new StringTextComponent("\u00a7c\u8d44\u91d1\u4e0d\u8db3! \u9700" + cost), uuid); return false;
        }).orElse(false);
    }

    public static boolean monetize(ServerPlayerEntity player) {
        UUID uuid = player.getUUID();
        int points = dataPoints.getOrDefault(uuid, 0);
        if (points < 100) { player.sendMessage(new StringTextComponent("\u00a7c\u6570\u636e\u70b9\u4e0d\u8db3! \u9700\u8981100+"), uuid); return false; }
        int revenue = points / 10;
        dataPoints.put(uuid, 0);
        player.getCapability(MoneyCapability.MONEY_CAP).ifPresent(m -> m.addMoney(revenue));
        player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u6570\u636e\u53d8\u73b0! \u6d88\u8017" + points + "\u70b9 | \u83b7\u5f97" + revenue + "\u91d1\u5e01"), uuid);
        return true;
    }

    public static void show(ServerPlayerEntity player) {
        UUID uuid = player.getUUID();
        int points = dataPoints.getOrDefault(uuid, 0);
        Set<String> sources = dataSources.getOrDefault(uuid, new HashSet<>());
        player.sendMessage(new StringTextComponent("\u00a76\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7b\ud83d\udcca \u6570\u636e\u8d44\u4ea7  \u00a76\u2551"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u255a\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255d"), uuid);
        player.sendMessage(new StringTextComponent("\u00a7e\u6570\u636e\u70b9: \u00a7a" + points), uuid);
        player.sendMessage(new StringTextComponent("\u00a7e\u6570\u636e\u6e90: " + sources.size() + "/" + SOURCES.length), uuid);
        for (int i = 0; i < SOURCES.length; i++) {
            boolean has = sources.contains(SOURCES[i]);
            player.sendMessage(new StringTextComponent("  " + (has ? "\u00a7a\u2714" : "\u00a77\u2716") + " [" + (i+1) + "] " + SOURCES[i] + " \u00a7f|\u00a76 " + (1000*(i+1)) + "\u91d1\u5e01"), uuid);
        }
        player.sendMessage(new StringTextComponent("\u00a7e/data acquire <1-6> | /data monetize"), uuid);
    }
}
