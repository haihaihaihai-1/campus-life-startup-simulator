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
 * 创业任务系统拼图 - 日常任务+奖励
 * 参考: MMO 任务系统模式
 */
@Mod.EventBusSubscriber(modid = CampusLife.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class QuestSystem {

    private static final Random RAND = new Random();
    private static int tickCounter = 0;
    private static final Map<UUID, PlayerQuest> activeQuests = new HashMap<>();

    public static final String[] QUEST_TEMPLATES = {
            "\u6536\u96c6\u539f\u6750\u6599",   // 收集原材料
            "\u5236\u4f5c\u5496\u5561",          // 制作咖啡
            "\u64b0\u5199\u521b\u4e1a\u8ba1\u5212", // 撰写创业计划
            "\u7814\u53d1\u79d1\u6280\u4ea7\u54c1",   // 研发科技产品
            "\u53c2\u52a0\u5e02\u573a\u4ea4\u6613",   // 参加市场交易
            "\u62d3\u5c55\u4eba\u8109\u8d44\u6e90"    // 拓展人脉资源
    };

    public static final int[] QUEST_REWARDS = {100, 150, 200, 300, 250, 180};

    public static class PlayerQuest {
        public String description;
        public int targetAmount;
        public int currentProgress;
        public int reward;
        public boolean completed;

        public PlayerQuest(String desc, int target, int reward) {
            this.description = desc;
            this.targetAmount = target;
            this.reward = reward;
            this.currentProgress = 0;
            this.completed = false;
        }
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        tickCounter++;

        // 每5分钟给在线玩家检查/分配任务
        if (tickCounter % 6000 == 0) {
            for (ServerPlayerEntity player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
                assignDailyQuest(player);
            }
        }
    }

    public static void assignDailyQuest(ServerPlayerEntity player) {
        UUID uuid = player.getUUID();
        if (activeQuests.containsKey(uuid) && !activeQuests.get(uuid).completed) return;

        int idx = RAND.nextInt(QUEST_TEMPLATES.length);
        int target = 3 + RAND.nextInt(7);
        int reward = QUEST_REWARDS[idx] + RAND.nextInt(50);

        activeQuests.put(uuid, new PlayerQuest(QUEST_TEMPLATES[idx], target, reward));
        player.sendMessage(new StringTextComponent(
                "\u00a76\u2500\u2500\u2500 \u65b0\u4efb\u52a1! \u2500\u2500\u2500"), player.getUUID());
        player.sendMessage(new StringTextComponent(
                "\u00a7e\u4efb\u52a1: \u00a7f" + QUEST_TEMPLATES[idx] + " x" + target), player.getUUID());
        player.sendMessage(new StringTextComponent(
                "\u00a7e\u5956\u52b1: \u00a76" + reward + " \u91d1\u5e01 + \u00a7b" + (reward/20) + " \u7ecf\u9a8c"), player.getUUID());
    }

    public static void progressQuest(ServerPlayerEntity player, int amount) {
        UUID uuid = player.getUUID();
        PlayerQuest quest = activeQuests.get(uuid);
        if (quest == null || quest.completed) return;

        quest.currentProgress += amount;
        if (quest.currentProgress >= quest.targetAmount) {
            quest.completed = true;
            player.getCapability(MoneyCapability.MONEY_CAP).ifPresent(m -> m.addMoney(quest.reward));
            player.getCapability(SkillCapability.SKILL_CAP).ifPresent(s -> s.addExp(quest.reward / 20));
            player.sendMessage(new StringTextComponent(
                    "\u00a7a\u2714 \u4efb\u52a1\u5b8c\u6210! \u83b7\u5f97 " + quest.reward + " \u91d1\u5e01 + " + (quest.reward/20) + " \u7ecf\u9a8c!"), player.getUUID());
        } else {
            player.sendMessage(new StringTextComponent(
                    "\u00a7e\u4efb\u52a1\u8fdb\u5ea6: \u00a7f" + quest.currentProgress + "/" + quest.targetAmount), player.getUUID());
        }
    }

    public static PlayerQuest getQuest(UUID uuid) {
        return activeQuests.get(uuid);
    }
}
