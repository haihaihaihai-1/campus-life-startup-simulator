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
 * 跨区域扩张系统 - 开分校+多区域经营
 */
@Mod.EventBusSubscriber(modid = CampusLife.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ExpansionSystem {

    public static class Region {
        public String name, description;
        public int setupCost, dailyRevenue, requiredLevel;

        public Region(String name, String desc, int cost, int revenue, int reqLvl) {
            this.name = name; this.description = desc; this.setupCost = cost; this.dailyRevenue = revenue; this.requiredLevel = reqLvl;
        }
    }

    public static final Region[] REGIONS = {
        new Region("\u672c\u6821\u533a", "\u8d77\u6b65\u533a\u57df", 0, 0, 1),
        new Region("\u4e1c\u6821\u533a", "\u62d3\u5c55\u4e1c\u6821\u533a", 3000, 100, 3),
        new Region("\u897f\u6821\u533a", "\u62d3\u5c55\u897f\u6821\u533a", 5000, 200, 5),
        new Region("\u5357\u6821\u533a", "\u62d3\u5c55\u5357\u6821\u533a", 10000, 400, 8),
        new Region("\u5317\u6821\u533a", "\u62d3\u5c55\u5317\u6821\u533a", 20000, 800, 12),
        new Region("\u5916\u6821\u533a", "\u62d3\u5c55\u5916\u6821\u533a", 50000, 2000, 18),
        new Region("\u5916\u57ce\u5206\u6821", "\u5f00\u8bbe\u5916\u57ce\u5206\u6821", 150000, 6000, 25),
        new Region("\u5916\u7701\u5206\u6821", "\u5f00\u8bbe\u5916\u7701\u5206\u6821", 500000, 20000, 35)
    };

    private static final Map<UUID, Set<Integer>> expanded = new HashMap<>();
    private static int tickCounter = 0;

    static {
        // 每个玩家默认拥有本校区
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        tickCounter++;

        if (tickCounter % 6000 == 0 && ServerLifecycleHooks.getCurrentServer() != null) {
            for (ServerPlayerEntity player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
                UUID uuid = player.getUUID();
                Set<Integer> regions = expanded.getOrDefault(uuid, new HashSet<>());
                if (regions.isEmpty()) { regions.add(0); expanded.put(uuid, regions); }
                int total = 0;
                for (int idx : regions) total += REGIONS[idx].dailyRevenue;
                if (total > 0) {
                    final int revenue = total;
                    player.getCapability(MoneyCapability.MONEY_CAP).ifPresent(m -> m.addMoney(revenue));
                    player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u533a\u57df\u7ecf\u8425\u6536\u5165: \u00a76" + revenue + "\u91d1\u5e01 (" + regions.size() + "\u4e2a\u533a\u57df)"), uuid);
                }
            }
        }
    }

    public static boolean expand(ServerPlayerEntity player, int idx) {
        UUID uuid = player.getUUID();
        if (idx < 1 || idx > REGIONS.length) { player.sendMessage(new StringTextComponent("\u00a7c\u65e0\u6548\u7f16\u53f7!"), uuid); return false; }
        Set<Integer> regions = expanded.getOrDefault(uuid, new HashSet<>());
        if (regions.isEmpty()) regions.add(0);
        if (regions.contains(idx - 1)) { player.sendMessage(new StringTextComponent("\u00a7c\u5df2\u62d3\u5c55\u8be5\u533a\u57df!"), uuid); return false; }

        Region r = REGIONS[idx - 1];
        int level = player.getCapability(SkillCapability.SKILL_CAP).map(SkillCapability.ISkill::getLevel).orElse(1);
        if (level < r.requiredLevel) { player.sendMessage(new StringTextComponent("\u00a7c\u9700Lv." + r.requiredLevel), uuid); return false; }

        return player.getCapability(MoneyCapability.MONEY_CAP).map(m -> {
            if (m.spendMoney(r.setupCost)) {
                regions.add(idx - 1);
                expanded.put(uuid, regions);
                player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u62d3\u5c55\u6210\u529f! " + r.name), uuid);
                player.sendMessage(new StringTextComponent("\u00a7e\u6295\u8d44:" + r.setupCost + " | \u9884\u8ba1\u6536\u5165:" + r.dailyRevenue + "/\u5468"), uuid);
                return true;
            }
            player.sendMessage(new StringTextComponent("\u00a7c\u8d44\u91d1\u4e0d\u8db3! \u9700" + r.setupCost), uuid); return false;
        }).orElse(false);
    }

    public static void show(ServerPlayerEntity player) {
        UUID uuid = player.getUUID();
        Set<Integer> regions = expanded.getOrDefault(uuid, new HashSet<>());
        if (regions.isEmpty()) { regions.add(0); expanded.put(uuid, regions); }

        player.sendMessage(new StringTextComponent("\u00a76\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7b\ud83c\udf0d \u533a\u57df\u6269\u5f20 (" + regions.size() + "/" + REGIONS.length + ")  \u00a76\u2551"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u255a\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255d"), uuid);

        int totalRevenue = 0;
        for (int i = 0; i < REGIONS.length; i++) {
            Region r = REGIONS[i];
            boolean has = regions.contains(i);
            if (has) totalRevenue += r.dailyRevenue;
            player.sendMessage(new StringTextComponent(
                    "\u00a7e[" + (i+1) + "] " + r.name +
                    " \u00a7f|\u00a76 " + (r.setupCost > 0 ? r.setupCost + "\u91d1\u5e01" : "\u514d\u8d39") +
                    " \u00a7f|\u00a7a " + r.dailyRevenue + "/\u5468" +
                    " \u00a7f|\u00a7b Lv." + r.requiredLevel + "+" +
                    " \u00a7f| " + (has ? "\u00a7a\u2714\u5df2\u62d3\u5c55" : "\u00a77\u672a\u62d3\u5c55")), uuid);
        }
        player.sendMessage(new StringTextComponent("\u00a7a\u603b\u533a\u57df\u6536\u5165: \u00a76" + totalRevenue + "\u91d1\u5e01/\u5468"), uuid);
        player.sendMessage(new StringTextComponent("\u00a7e\u8f93\u5165 /expand <1-8> \u62d3\u5c55\u65b0\u533a\u57df"), uuid);
    }

    public static int getRegionCount(UUID uuid) {
        Set<Integer> regions = expanded.getOrDefault(uuid, new HashSet<>());
        return regions.isEmpty() ? 1 : regions.size();
    }
}
