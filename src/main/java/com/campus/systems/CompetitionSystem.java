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
 * 创业大赛系统拼图 - 定期事件+奖金
 * 参考: 服务器定时活动模式
 */
@Mod.EventBusSubscriber(modid = CampusLife.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CompetitionSystem {

    private static int tickCounter = 0;
    private static boolean competitionActive = false;
    private static int competitionTimer = 0;
    private static final Map<UUID, Integer> scores = new HashMap<>();
    private static int nextCompetitionIn = 24000; // 20分钟后开始(1天tick)

    public static final String[] COMPETITION_TYPES = {
            "\u6700\u4f73\u521b\u610f\u5956",       // 最佳创意奖
            "\u6700\u5177\u5546\u4e1a\u4ef7\u503c\u5956",  // 最具商业价值奖
            "\u6700\u4f73\u56e2\u961f\u5956",       // 最佳团队奖
            "\u521b\u65b0\u6280\u672f\u5956"        // 创新技术奖
    };

    public static final int[] PRIZES = {1000, 800, 1500, 600};

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        tickCounter++;

        if (!competitionActive) {
            nextCompetitionIn--;
            if (nextCompetitionIn <= 0) {
                startCompetition();
            }
        } else {
            competitionTimer++;
            // 大赛持续5分钟(6000 ticks)
            if (competitionTimer >= 6000) {
                endCompetition();
            }
        }
    }

    private static void startCompetition() {
        competitionActive = true;
        competitionTimer = 0;
        scores.clear();

        int typeIdx = new Random().nextInt(COMPETITION_TYPES.length);
        String type = COMPETITION_TYPES[typeIdx];

        for (ServerPlayerEntity player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
            player.sendMessage(new StringTextComponent(
                    "\u00a76\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557"), player.getUUID());
            player.sendMessage(new StringTextComponent(
                    "\u00a76\u2551    \u00a7e\u26a1 \u521b\u4e1a\u5927\u8d5b\u5f00\u59cb! \u00a7f" + type + "    \u00a76\u2551"), player.getUUID());
            player.sendMessage(new StringTextComponent(
                    "\u00a76\u2551    \u00a7a\u5956\u91d1: \u00a76" + PRIZES[typeIdx] + " \u91d1\u5e01    \u00a76\u2551"), player.getUUID());
            player.sendMessage(new StringTextComponent(
                    "\u00a76\u2551    \u00a7b\u8f93\u5165 /compete join \u62a5\u540d\u53c2\u8d5b    \u00a76\u2551"), player.getUUID());
            player.sendMessage(new StringTextComponent(
                    "\u00a76\u2551    \u00a7e\u65f6\u95f4: 5\u5206\u949f    \u00a76\u2551"), player.getUUID());
            player.sendMessage(new StringTextComponent(
                    "\u00a76\u255a\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255d"), player.getUUID());
        }
        CampusLife.LOGGER.info("Startup competition started: " + type);
    }

    public static void joinCompetition(ServerPlayerEntity player) {
        if (!competitionActive) {
            player.sendMessage(new StringTextComponent(
                    "\u00a7c\u521b\u4e1a\u5927\u8d5b\u672a\u5f00\u59cb!"), player.getUUID());
            return;
        }
        UUID uuid = player.getUUID();
        if (scores.containsKey(uuid)) {
            player.sendMessage(new StringTextComponent("\u00a7e\u4f60\u5df2\u62a5\u540d!"), uuid);
            return;
        }
        scores.put(uuid, 0);
        player.sendMessage(new StringTextComponent(
                "\u00a7a\u2714 \u62a5\u540d\u6210\u529f! \u4f7f\u7528 /business invest \u79ef\u7d2f\u5206\u6570!"), uuid);
    }

    public static void addScore(ServerPlayerEntity player, int amount) {
        if (!competitionActive) return;
        UUID uuid = player.getUUID();
        if (scores.containsKey(uuid)) {
            scores.put(uuid, scores.get(uuid) + amount);
        }
    }

    private static void endCompetition() {
        competitionActive = false;
        competitionTimer = 0;
        nextCompetitionIn = 24000; // 20分钟后再次开始

        if (scores.isEmpty()) {
            for (ServerPlayerEntity player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
                player.sendMessage(new StringTextComponent(
                        "\u00a7e\u521b\u4e1a\u5927\u8d5b\u7ed3\u675f! \u65e0\u4eba\u53c2\u52a0\u3002"), player.getUUID());
            }
            return;
        }

        UUID winner = Collections.max(scores.entrySet(), Map.Entry.comparingByValue()).getKey();
        int maxScore = scores.get(winner);
        int prizeIdx = new Random().nextInt(PRIZES.length);
        int prize = PRIZES[prizeIdx];

        ServerPlayerEntity winnerPlayer = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(winner);
        if (winnerPlayer != null) {
            winnerPlayer.getCapability(MoneyCapability.MONEY_CAP).ifPresent(m -> m.addMoney(prize));
            winnerPlayer.getCapability(SkillCapability.SKILL_CAP).ifPresent(s -> s.addExp(prize / 10));

            for (ServerPlayerEntity player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
                player.sendMessage(new StringTextComponent(
                        "\u00a76\u2500\u2500\u2500 \u521b\u4e1a\u5927\u8d5b\u7ed3\u675c! \u2500\u2500\u2500"), player.getUUID());
                player.sendMessage(new StringTextComponent(
                        "\u00a7e\u51a0\u519b: \u00a76" + winnerPlayer.getName().getString() + " \u00a7f(\u5206\u6570: " + maxScore + ")"), player.getUUID());
                player.sendMessage(new StringTextComponent(
                        "\u00a7e\u5956\u91d1: \u00a76" + prize + " \u91d1\u5e01 + \u00a7b" + (prize/10) + " \u7ecf\u9a8c"), player.getUUID());
                player.sendMessage(new StringTextComponent(
                        "\u00a77\u4e0b\u4e00\u573a\u5927\u8d5b: 20\u5206\u949f\u540e"), player.getUUID());
            }
        }
        scores.clear();
    }

    public static boolean isCompetitionActive() { return competitionActive; }
    public static int getNextCompetitionIn() { return nextCompetitionIn; }
}
