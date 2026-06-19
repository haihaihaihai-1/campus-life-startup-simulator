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
 * 广告营销系统拼图 - 投入广告提升销量
 * 参考: 数字营销投放模型
 */
@Mod.EventBusSubscriber(modid = CampusLife.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MarketingSystem {

    private static int tickCounter = 0;
    private static final Map<UUID, AdCampaign> campaigns = new HashMap<>();

    public static class AdCampaign {
        public int budget;
        public int remainingBudget;
        public int boostPercent;
        public int durationTicks;
        public AdType type;

        public AdCampaign(int budget, AdType type) {
            this.budget = budget;
            this.remainingBudget = budget;
            this.type = type;
            this.boostPercent = Math.min(type.maxBoost, budget / type.costPerPercent);
            this.durationTicks = type.duration;
        }
    }

    public enum AdType {
        SOCIAL_MEDIA("\u793e\u4ea4\u5a92\u4f53", 50, 2, 100, 6000),
        CAMPUS_POSTER("\u6821\u56ed\u6d77\u62a5", 200, 5, 50, 12000),
        ONLINE_ADS("\u7f51\u7edc\u5e7f\u544a", 500, 10, 80, 18000),
        KOL_ENDORSE("KOL\u4ee3\u8a00", 2000, 25, 60, 24000),
        TV_COMMERCIAL("\u7535\u89c6\u5e7f\u544a", 10000, 50, 40, 36000);

        public String name;
        public int costPerPercent;
        public int maxBoost;
        public int maxBudget;
        public int duration;

        AdType(String name, int costPerPercent, int maxBoost, int maxBudget, int duration) {
            this.name = name;
            this.costPerPercent = costPerPercent;
            this.maxBoost = maxBoost;
            this.maxBudget = maxBudget;
            this.duration = duration;
        }
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        tickCounter++;

        // 每30秒结算广告效果
        if (tickCounter % 600 == 0 && ServerLifecycleHooks.getCurrentServer() != null) {
            for (ServerPlayerEntity player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
                UUID uuid = player.getUUID();
                AdCampaign camp = campaigns.get(uuid);
                if (camp == null) continue;

                camp.durationTicks -= 600;
                if (camp.durationTicks <= 0) {
                    campaigns.remove(uuid);
                    player.sendMessage(new StringTextComponent(
                            "\u00a7e\u5e7f\u544a\u6d3b\u52a8\u5df2\u7ed3\u675f! \u9500\u552e\u52a0\u6210\u5df2\u53d6\u6d88"), uuid);
                    continue;
                }

                // 广告带来被动收入
                int adIncome = camp.boostPercent * 10;
                player.getCapability(MoneyCapability.MONEY_CAP).ifPresent(m -> m.addMoney(adIncome));
                player.sendMessage(new StringTextComponent(
                        "\u00a7a\u5e7f\u544a\u6536\u76ca: +" + adIncome + " \u91d1\u5e01 (" + camp.type.name + " +" + camp.boostPercent + "%)"), uuid);
            }
        }
    }

    public static boolean launchCampaign(ServerPlayerEntity player, AdType type, int budget) {
        UUID uuid = player.getUUID();

        if (budget > type.maxBudget) {
            player.sendMessage(new StringTextComponent(
                    "\u00a7c" + type.name + "\u6700\u5927\u9884\u7b97: " + type.maxBudget + " \u91d1\u5e01"), uuid);
            return false;
        }

        if (campaigns.containsKey(uuid)) {
            player.sendMessage(new StringTextComponent(
                    "\u00a7c\u4f60\u5df2\u6709\u8fdb\u884c\u4e2d\u7684\u5e7f\u544a\u6d3b\u52a8!"), uuid);
            return false;
        }

        return player.getCapability(MoneyCapability.MONEY_CAP).map(m -> {
            if (m.spendMoney(budget)) {
                AdCampaign camp = new AdCampaign(budget, type);
                campaigns.put(uuid, camp);
                player.sendMessage(new StringTextComponent(
                        "\u00a7a\u2714 \u5e7f\u544a\u6295\u653e\u6210\u529f!"), uuid);
                player.sendMessage(new StringTextComponent(
                        "\u00a7e\u7c7b\u578b: \u00a7f" + type.name), uuid);
                player.sendMessage(new StringTextComponent(
                        "\u00a7e\u9884\u7b97: \u00a76" + budget + " \u91d1\u5e01"), uuid);
                player.sendMessage(new StringTextComponent(
                        "\u00a7e\u9500\u552e\u52a0\u6210: \u00a7a+" + camp.boostPercent + "%"), uuid);
                player.sendMessage(new StringTextComponent(
                        "\u00a7e\u6301\u7eed\u65f6\u95f4: \u00a7f" + (type.duration/1200) + " \u5206\u949f"), uuid);
                player.getCapability(SkillCapability.SKILL_CAP).ifPresent(s -> s.addExp(budget / 100));
                return true;
            }
            player.sendMessage(new StringTextComponent("\u00a7c\u8d44\u91d1\u4e0d\u8db3!"), uuid);
            return false;
        }).orElse(false);
    }

    public static void showCampaigns(ServerPlayerEntity player) {
        UUID uuid = player.getUUID();
        player.sendMessage(new StringTextComponent("\u00a76\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7e\ud83d\udce1 \u5e7f\u544a\u8425\u9500  \u00a76\u2551"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u255a\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255d"), uuid);

        for (AdType type : AdType.values()) {
            player.sendMessage(new StringTextComponent(
                    "\u00a7e" + type.name +
                    " \u00a7f|\u00a76 \u6210\u672c:" + type.costPerPercent + "/%" +
                    " \u00a7f|\u00a7a \u6700\u9ad8:+" + type.maxBoost + "%" +
                    " \u00a7f|\u00a7b \u6700\u5927\u9884\u7b97:" + type.maxBudget +
                    " \u00a7f|\u00a77 " + (type.duration/1200) + "\u5206\u949f"), uuid);
        }

        AdCampaign camp = campaigns.get(uuid);
        if (camp != null) {
            player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u5f53\u524d\u6d3b\u52a8: " + camp.type.name + " +" + camp.boostPercent + "%"), uuid);
            player.sendMessage(new StringTextComponent("\u00a7e\u5269\u4f59\u65f6\u95f4: " + (camp.durationTicks/1200) + " \u5206\u949f"), uuid);
        } else {
            player.sendMessage(new StringTextComponent("\u00a77\u65e0\u8fdb\u884c\u4e2d\u7684\u5e7f\u544a"), uuid);
        }
        player.sendMessage(new StringTextComponent("\u00a7e\u8f93\u5165 /ad <\u7c7b\u578b1-5> <\u91d1\u989d> \u6295\u653e\u5e7f\u544a"), uuid);
    }

    public static int getBoostPercent(UUID uuid) {
        AdCampaign camp = campaigns.get(uuid);
        return camp != null ? camp.boostPercent : 0;
    }

    public static AdType getTypeByIndex(int idx) {
        AdType[] types = AdType.values();
        if (idx < 1 || idx > types.length) return null;
        return types[idx - 1];
    }
}
