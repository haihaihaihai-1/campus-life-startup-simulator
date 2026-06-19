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
 * 创新挑战系统 - 限时挑战+排名
 */
@Mod.EventBusSubscriber(modid = CampusLife.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class InnovationChallengeSystem {

    private static int tickCounter = 0;
    private static boolean challengeActive = false;
    private static int challengeTimer = 0;
    private static int nextChallengeIn = 12000; // 10分钟后
    private static final Map<UUID, Integer> challengeScores = new HashMap<>();
    private static final Random RAND = new Random();

    public static final String[] CHALLENGES = {
        "\u6781\u901f\u878d\u8d44\u6311\u6218", "\u4ea7\u54c1\u8fed\u4ee3\u6311\u6218",
        "\u5e02\u573a\u62d3\u5c55\u6311\u6218", "\u6210\u672c\u63a7\u5236\u6311\u6218",
        "\u56e2\u961f\u5efa\u8bbe\u6311\u6218", "\u6280\u672f\u7a81\u7834\u6311\u6218"
    };

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        tickCounter++;

        if (!challengeActive) {
            nextChallengeIn--;
            if (nextChallengeIn <= 0) startChallenge();
        } else {
            challengeTimer++;
            if (challengeTimer >= 3600) endChallenge(); // 3分钟
        }
    }

    private static void startChallenge() {
        challengeActive = true;
        challengeTimer = 0;
        challengeScores.clear();
        String challenge = CHALLENGES[RAND.nextInt(CHALLENGES.length)];

        if (ServerLifecycleHooks.getCurrentServer() != null) {
            for (ServerPlayerEntity p : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
                p.sendMessage(new StringTextComponent("\u00a7d\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557"), p.getUUID());
                p.sendMessage(new StringTextComponent("\u00a7d\u2551  \u00a7e\u26a1 \u521b\u65b0\u6311\u6218: " + challenge + "  \u00a7d\u2551"), p.getUUID());
                p.sendMessage(new StringTextComponent("\u00a7d\u2551  \u00a7f\u9650\u65f63\u5206\u949f | \u53d1\u6325\u521b\u610f\u62ff\u9ad8\u5206  \u00a7d\u2551"), p.getUUID());
                p.sendMessage(new StringTextComponent("\u00a7d\u2551  \u00a7a\u8f93\u5165 /challenge join \u53c2\u52a0  \u00a7d\u2551"), p.getUUID());
                p.sendMessage(new StringTextComponent("\u00a7d\u255a\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255d"), p.getUUID());
            }
        }
    }

    public static void join(ServerPlayerEntity player) {
        UUID uuid = player.getUUID();
        if (!challengeActive) { player.sendMessage(new StringTextComponent("\u00a7c\u6311\u6218\u672a\u5f00\u59cb!"), uuid); return; }
        if (challengeScores.containsKey(uuid)) { player.sendMessage(new StringTextComponent("\u00a7e\u5df2\u53c2\u52a0!"), uuid); return; }
        challengeScores.put(uuid, 0);
        player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u5df2\u53c2\u52a0\u521b\u65b0\u6311\u6218! \u6295\u8d44/\u7814\u53d1/\u96c7\u4f63\u83b7\u5f97\u5206\u6570"), uuid);
    }

    public static void addScore(ServerPlayerEntity player, int amount) {
        if (!challengeActive) return;
        UUID uuid = player.getUUID();
        if (challengeScores.containsKey(uuid)) {
            challengeScores.put(uuid, challengeScores.get(uuid) + amount);
        }
    }

    private static void endChallenge() {
        challengeActive = false;
        challengeTimer = 0;
        nextChallengeIn = 12000 + RAND.nextInt(12000);

        if (challengeScores.isEmpty() || ServerLifecycleHooks.getCurrentServer() == null) return;

        List<Map.Entry<UUID, Integer>> sorted = new ArrayList<>(challengeScores.entrySet());
        sorted.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        int[] rewards = {2000, 1000, 500};
        for (int i = 0; i < Math.min(3, sorted.size()); i++) {
            UUID uuid = sorted.get(i).getKey();
            int score = sorted.get(i).getValue();
            int reward = rewards[i];
            ServerPlayerEntity p = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(uuid);
            if (p != null) {
                p.getCapability(MoneyCapability.MONEY_CAP).ifPresent(m -> m.addMoney(reward));
                p.getCapability(SkillCapability.SKILL_CAP).ifPresent(s -> s.addExp(reward / 10));
                String medal = i == 0 ? "\u00a7e\u1f947" : (i == 1 ? "\u00a7f\u1f948" : "\u00a76\u1f949");
                p.sendMessage(new StringTextComponent(medal + " \u6311\u6218\u6392\u540d\u7b2c" + (i+1) + "! \u5206\u6570:" + score + " | \u5956\u52b1:" + reward + "\u91d1\u5e01"), uuid);
            }
        }
        challengeScores.clear();
    }

    public static void show(ServerPlayerEntity player) {
        if (challengeActive) {
            int remaining = (3600 - challengeTimer) / 1200;
            player.sendMessage(new StringTextComponent("\u00a7d\u521b\u65b0\u6311\u6218\u8fdb\u884c\u4e2d! \u5269\u4f59:" + remaining + "\u5206\u949f | \u53c2\u4e0e:" + challengeScores.size() + "\u4eba"), player.getUUID());
        } else {
            player.sendMessage(new StringTextComponent("\u00a7e\u4e0b\u4e00\u6b21\u6311\u6218: " + (nextChallengeIn/1200) + "\u5206\u949f\u540e"), player.getUUID());
        }
    }
}
