package com.campus.systems;

import com.campus.CampusLife;
import com.campus.economy.MoneyCapability;
import com.campus.economy.SkillCapability;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;

/**
 * 投资人系统拼图 - NPC投资者按等级提供融资
 * 参考: 风险投资/天使投资模式
 */
@Mod.EventBusSubscriber(modid = CampusLife.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class InvestorSystem {

    public static class Investor {
        public String name;
        public int minLevel;
        public int maxInvestment;
        public double equityRate;
        public String title;

        public Investor(String name, int minLevel, int maxInvestment, double equityRate, String title) {
            this.name = name;
            this.minLevel = minLevel;
            this.maxInvestment = maxInvestment;
            this.equityRate = equityRate;
            this.title = title;
        }
    }

    public static final Investor[] INVESTORS = {
        new Investor("\u674e\u6559\u6388", 1, 500, 0.05, "\u5929\u4f7f\u6295\u8d44\u4eba"),
        new Investor("\u738b\u603b\u88c1", 5, 2000, 0.10, "\u98ce\u6295\u5408\u4f19\u4eba"),
        new Investor("\u5f20\u8463\u4e8b\u957f", 10, 5000, 0.15, "\u4ea7\u4e1a\u57fa\u91d1"),
        new Investor("\u5218\u8463\u4e8b\u5c40\u957f", 20, 10000, 0.20, "\u79d1\u6280\u56ed\u533a"),
        new Investor("\u8d75\u4f01\u4e1a\u5bb6", 35, 30000, 0.25, "\u4e92\u8054\u7f51\u5de8\u5934"),
        new Investor("\u9648\u6295\u8d44\u9ad8\u7ba1", 50, 100000, 0.30, "\u9876\u7ea7\u98ce\u6295")
    };

    private static final Map<UUID, Integer> fundedPlayers = new HashMap<>();
    private static final Map<UUID, Long> lastFundingTime = new HashMap<>();

    public static void showInvestors(ServerPlayerEntity player) {
        UUID uuid = player.getUUID();
        int level = player.getCapability(SkillCapability.SKILL_CAP).map(SkillCapability.ISkill::getLevel).orElse(1);

        player.sendMessage(new StringTextComponent("\u00a76\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7e\u2605 \u6295\u8d44\u4eba\u540d\u5355 (Lv." + level + ")  \u00a76\u2551"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u255a\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255d"), uuid);

        for (int i = 0; i < INVESTORS.length; i++) {
            Investor inv = INVESTORS[i];
            boolean unlocked = level >= inv.minLevel;
            boolean funded = fundedPlayers.containsKey(uuid) && fundedPlayers.get(uuid) >= i;
            String status = funded ? "\u00a7a\u2714\u5df2\u878d\u8d44" : (unlocked ? "\u00a7e\u53ef\u878d\u8d44" : "\u00a7c\u672a\u89e3\u9501");
            String color = unlocked ? "\u00a7a" : "\u00a77";
            player.sendMessage(new StringTextComponent(
                    color + "[" + (i+1) + "] " + inv.title + " " + inv.name +
                    " \u00a7f|\u00a76 \u6700\u5927:" + inv.maxInvestment +
                    " \u00a7f|\u00a7c \u80a1\u6743:" + (int)(inv.equityRate*100) + "%" +
                    " \u00a7f| " + status), uuid);
        }
        player.sendMessage(new StringTextComponent("\u00a7e\u8f93\u5165 /investor fund <\u7f16\u53f7> \u8fdb\u884c\u878d\u8d44"), uuid);
    }

    public static boolean requestFunding(ServerPlayerEntity player, int investorIdx) {
        UUID uuid = player.getUUID();
        if (investorIdx < 1 || investorIdx > INVESTORS.length) {
            player.sendMessage(new StringTextComponent("\u00a7c\u65e0\u6548\u7f16\u53f7! (1-" + INVESTORS.length + ")"), uuid);
            return false;
        }

        Investor inv = INVESTORS[investorIdx - 1];
        int level = player.getCapability(SkillCapability.SKILL_CAP).map(SkillCapability.ISkill::getLevel).orElse(1);

        if (level < inv.minLevel) {
            player.sendMessage(new StringTextComponent(
                    "\u00a7c" + inv.name + ": \u4f60\u7684\u7b49\u7ea7\u4e0d\u8db3! \u9700\u8981 Lv." + inv.minLevel), uuid);
            return false;
        }

        long now = System.currentTimeMillis();
        long lastTime = lastFundingTime.getOrDefault(uuid, 0L);
        if (now - lastTime < 300000) {
            long wait = (300000 - (now - lastTime)) / 1000;
            player.sendMessage(new StringTextComponent(
                    "\u00a7c\u51b7\u5374\u4e2d... \u8bf7\u7b49\u5f85 " + wait + " \u79d2"), uuid);
            return false;
        }

        int funded = fundedPlayers.getOrDefault(uuid, -1);
        if (funded >= investorIdx - 1) {
            player.sendMessage(new StringTextComponent("\u00a7c\u4f60\u5df2\u83b7\u5f97\u6b64\u7ea7\u522b\u6216\u66f4\u9ad8\u7ea7\u522b\u7684\u878d\u8d44!"), uuid);
            return false;
        }

        int amount = inv.maxInvestment;
        player.getCapability(MoneyCapability.MONEY_CAP).ifPresent(m -> m.addMoney(amount));
        player.getCapability(SkillCapability.SKILL_CAP).ifPresent(s -> s.addExp(amount / 50));
        fundedPlayers.put(uuid, investorIdx - 1);
        lastFundingTime.put(uuid, now);

        player.sendMessage(new StringTextComponent(
                "\u00a7a\u2714 \u878d\u8d44\u6210\u529f! " + inv.name + " \u6295\u8d44 " + amount + " \u91d1\u5e01!"), uuid);
        player.sendMessage(new StringTextComponent(
                "\u00a7e\u83b7\u5f97 " + (amount/50) + " \u521b\u4e1a\u7ecf\u9a8c"), uuid);
        player.sendMessage(new StringTextComponent(
                "\u00a7c\u63d0\u793a: \u80a1\u6743 " + (int)(inv.equityRate*100) + "% \u5c06\u5728\u540e\u7eed\u7ed3\u7b97"), uuid);
        return true;
    }

    public static int getFundedLevel(UUID uuid) {
        return fundedPlayers.getOrDefault(uuid, -1);
    }
}
