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
 * 创业孵化大赛系统 - 多轮淘汰赛
 */
@Mod.EventBusSubscriber(modid = CampusLife.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class HatcheryCupSystem {

    private static int tickCounter = 0;
    private static int cupPhase = 0; // 0=idle, 1=signup, 2=round1, 3=round2, 4=final
    private static int phaseTimer = 0;
    private static int nextCupIn = 30000; // 25分钟后
    private static final Map<UUID, Integer> scores = new HashMap<>();
    private static final Random RAND = new Random();
    private static final int[] REWARDS = {0, 0, 1000, 3000, 10000};

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        tickCounter++;
        if (cupPhase == 0) {
            nextCupIn--;
            if (nextCupIn <= 0) { cupPhase = 1; phaseTimer = 2400; scores.clear(); broadcast("\u00a7d\ud83c\udfc6 \u521b\u4e1a\u5b75\u5316\u5927\u8d5b\u62a5\u540d\u5f00\u59cb! \u8f93\u5165 /hatchery join \u62a5\u540d"); }
        } else {
            phaseTimer--;
            if (phaseTimer <= 0) advanceRound();
        }
    }

    private static void advanceRound() {
        if (scores.isEmpty()) { cupPhase = 0; nextCupIn = 30000; broadcast("\u00a7e\u5927\u8d5b\u56e0\u65e0\u4eba\u53c2\u52a0\u53d6\u6d88"); return; }
        cupPhase++;
        if (cupPhase > 4) { endCup(); return; }
        phaseTimer = 1800;

        List<UUID> sorted = new ArrayList<>(scores.keySet());
        sorted.sort((a, b) -> scores.get(b).compareTo(scores.get(a)));
        int survivors = Math.max(1, sorted.size() / 2);
        List<UUID> eliminated = new ArrayList<>();

        for (int i = 0; i < sorted.size(); i++) {
            UUID uuid = sorted.get(i);
            ServerPlayerEntity p = ServerLifecycleHooks.getCurrentServer() != null ? ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(uuid) : null;
            if (p == null) continue;
            if (i >= survivors) {
                eliminated.add(uuid);
                p.sendMessage(new StringTextComponent("\u00a7c\u6dd8\u6c70! \u6392\u540d\u7b2c" + (i+1) + " | \u5956\u52b1:" + REWARDS[cupPhase-1] + "\u91d1\u5e01"), uuid);
                p.getCapability(MoneyCapability.MONEY_CAP).ifPresent(m -> m.addMoney(REWARDS[cupPhase-1]));
            } else {
                int roundScore = 50 + RAND.nextInt(100);
                scores.put(uuid, scores.get(uuid) + roundScore);
                String roundName = cupPhase == 2 ? "\u521d\u8d5b" : (cupPhase == 3 ? "\u534a\u51b3\u8d5b" : "\u51b3\u8d5b");
                p.sendMessage(new StringTextComponent("\u00a7a\u2714 " + roundName + "\u664b\u7ea7! +\u5206" + roundScore + " | \u603b\u5206:" + scores.get(uuid)), uuid);
            }
        }
        scores.keySet().removeAll(eliminated);
    }

    private static void endCup() {
        if (!scores.isEmpty()) {
            UUID winner = scores.entrySet().stream().max(Map.Entry.comparingByValue()).get().getKey();
            ServerPlayerEntity p = ServerLifecycleHooks.getCurrentServer() != null ? ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(winner) : null;
            if (p != null) {
                p.getCapability(MoneyCapability.MONEY_CAP).ifPresent(m -> m.addMoney(REWARDS[4]));
                p.getCapability(SkillCapability.SKILL_CAP).ifPresent(s -> s.addExp(2000));
                p.sendMessage(new StringTextComponent("\u00a7d\ud83c\udfc6 \u5b75\u5316\u5927\u8d5b\u51a0\u519b! \u5956\u52b1:" + REWARDS[4] + "\u91d1\u5e01 + 2000\u7ecf\u9a8c"), winner);
                broadcast("\u00a7d\ud83c\udfc6 \u5b75\u5316\u5927\u8d5b\u51a0\u519b: " + p.getName().getString());
            }
        }
        cupPhase = 0; nextCupIn = 30000; scores.clear();
    }

    public static void join(ServerPlayerEntity player) {
        UUID uuid = player.getUUID();
        if (cupPhase != 1) { player.sendMessage(new StringTextComponent("\u00a7c\u672a\u5728\u62a5\u540d\u9636\u6bb5!"), uuid); return; }
        if (scores.containsKey(uuid)) { player.sendMessage(new StringTextComponent("\u00a7e\u5df2\u62a5\u540d!"), uuid); return; }
        scores.put(uuid, 0);
        player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u62a5\u540d\u6210\u529f! \u591a\u8f6e\u6dd8\u6c70\u5236"), uuid);
    }

    private static void broadcast(String msg) {
        if (ServerLifecycleHooks.getCurrentServer() != null) {
            for (ServerPlayerEntity p : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
                p.sendMessage(new StringTextComponent(msg), p.getUUID());
            }
        }
    }

    public static void show(ServerPlayerEntity player) {
        String[] phases = {"\u7b49\u5f85\u5f00\u59cb", "\u62a5\u540d\u4e2d", "\u521d\u8d5b", "\u534a\u51b3\u8d5b", "\u51b3\u8d5b"};
        if (cupPhase == 0) {
            player.sendMessage(new StringTextComponent("\u00a7e\u4e0b\u4e00\u6b21\u5927\u8d5b: " + (nextCupIn/1200) + "\u5206\u949f\u540e"), player.getUUID());
        } else {
            player.sendMessage(new StringTextComponent("\u00a7d\u5927\u8d5b\u8fdb\u884c\u4e2d: " + phases[cupPhase] + " | \u53c2\u4e0e:" + scores.size() + "\u4eba | \u5269\u4f59:" + (phaseTimer/1200) + "\u5206\u949f"), player.getUUID());
        }
    }
}
