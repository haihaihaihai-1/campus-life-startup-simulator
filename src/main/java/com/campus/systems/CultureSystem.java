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
 * 企业文化系统 - 员工满意度+效率
 */
@Mod.EventBusSubscriber(modid = CampusLife.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CultureSystem {

    public enum Culture {
        OPEN("\u5f00\u653e\u5305\u5bb9", 500, 10, "\u521b\u65b0\u529b+\u5458\u5de5\u6ee1\u610f\u5ea6"),
        EFFICIENT("\u9ad8\u6548\u6267\u884c", 1000, 15, "\u5458\u5de5\u4ea7\u51fa+\u6267\u884c\u529b"),
        INNOVATIVE("\u521b\u65b0\u9a71\u52a8", 2000, 20, "\u7814\u53d1\u6548\u7387+\u521b\u610f\u4ea7\u51fa"),
        CARING("\u5173\u6000\u5458\u5de5", 3000, 25, "\u5458\u5de5\u5fe0\u8bda\u5ea6+\u79bb\u804c\u7387\u964d\u4f4e"),
        EXCELLENCE("\u8ffd\u6c42\u5353\u8d8a", 5000, 30, "\u5168\u9762\u6548\u7387\u63d0\u5347"),
        LEGENDARY("\u4f20\u5947\u6587\u5316", 15000, 50, "\u9876\u7ea7\u4f01\u4e1a\u6587\u5316\uff0c\u5168\u9762\u52a0\u6210");

        public String name, effect; public int cost, boost;
        Culture(String n, int c, int b, String e) { name=n; cost=c; boost=b; effect=e; }
    }

    private static final Map<UUID, Set<Culture>> established = new HashMap<>();
    private static final Map<UUID, Integer> satisfaction = new HashMap<>();
    private static int tickCounter = 0;

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        tickCounter++;

        if (tickCounter % 6000 == 0 && ServerLifecycleHooks.getCurrentServer() != null) {
            for (ServerPlayerEntity player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
                UUID uuid = player.getUUID();
                Set<Culture> cultures = established.getOrDefault(uuid, new HashSet<>());
                if (cultures.isEmpty()) continue;
                int boost = 0;
                for (Culture c : cultures) boost += c.boost;
                int bonus = boost * 5;
                player.getCapability(MoneyCapability.MONEY_CAP).ifPresent(m -> m.addMoney(bonus));
                player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u4f01\u4e1a\u6587\u5316\u52a0\u6210\u6536\u5165: +" + bonus + "\u91d1\u5e01"), uuid);
                int sat = satisfaction.getOrDefault(uuid, 50) + 1;
                satisfaction.put(uuid, Math.min(100, sat));
            }
        }
    }

    public static boolean establish(ServerPlayerEntity player, int idx) {
        UUID uuid = player.getUUID();
        Culture[] cultures = Culture.values();
        if (idx < 1 || idx > cultures.length) { player.sendMessage(new StringTextComponent("\u00a7c\u65e0\u6548\u7f16\u53f7!"), uuid); return false; }
        Culture c = cultures[idx - 1];
        Set<Culture> has = established.getOrDefault(uuid, new HashSet<>());
        if (has.contains(c)) { player.sendMessage(new StringTextComponent("\u00a7c\u5df2\u5efa\u7acb!"), uuid); return false; }

        return player.getCapability(MoneyCapability.MONEY_CAP).map(m -> {
            if (m.spendMoney(c.cost)) {
                has.add(c);
                established.put(uuid, has);
                satisfaction.put(uuid, Math.min(100, satisfaction.getOrDefault(uuid, 50) + 10));
                player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u5efa\u7acb\u6587\u5316: " + c.name), uuid);
                player.sendMessage(new StringTextComponent("\u00a7e\u6548\u679c: " + c.effect + " (+" + c.boost + "%)"), uuid);
                return true;
            }
            player.sendMessage(new StringTextComponent("\u00a7c\u8d44\u91d1\u4e0d\u8db3! \u9700" + c.cost), uuid); return false;
        }).orElse(false);
    }

    public static void show(ServerPlayerEntity player) {
        UUID uuid = player.getUUID();
        Set<Culture> has = established.getOrDefault(uuid, new HashSet<>());
        int sat = satisfaction.getOrDefault(uuid, 50);

        player.sendMessage(new StringTextComponent("\u00a76\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7d\ud83c\udf0a \u4f01\u4e1a\u6587\u5316 (" + has.size() + "/" + Culture.values().length + ")  \u00a76\u2551"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7e\u5458\u5de5\u6ee1\u610f\u5ea6: " + sat + "/100  \u00a76\u2551"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u255a\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255d"), uuid);

        Culture[] cultures = Culture.values();
        for (int i = 0; i < cultures.length; i++) {
            Culture c = cultures[i];
            boolean owned = has.contains(c);
            player.sendMessage(new StringTextComponent(
                    "\u00a7e[" + (i+1) + "] " + c.name +
                    " \u00a7f|\u00a76 " + c.cost + "\u91d1\u5e01" +
                    " \u00a7f|\u00a7a +" + c.boost + "%" +
                    " \u00a7f|\u00a77 " + c.effect +
                    " \u00a7f| " + (owned ? "\u00a7a\u2714" : "\u00a77\u2716")), uuid);
        }
        player.sendMessage(new StringTextComponent("\u00a7e\u8f93\u5165 /culture <1-6> \u5efa\u7acb"), uuid);
    }

    public static int getTotalBoost(UUID uuid) {
        Set<Culture> has = established.getOrDefault(uuid, new HashSet<>());
        int total = 0;
        for (Culture c : has) total += c.boost;
        return total;
    }

    public static int getSatisfaction(UUID uuid) { return satisfaction.getOrDefault(uuid, 50); }
}
