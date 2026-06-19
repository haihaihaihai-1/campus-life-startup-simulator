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
 * 并购重组系统 - 收购企业+整合提升
 */
@Mod.EventBusSubscriber(modid = CampusLife.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MergerSystem {

    public static class Target {
        public String name; public int value; public int revenueBoost; public int requiredLevel; public String synergy;

        public Target(String n, int v, int r, int rl, String s) { name=n; value=v; revenueBoost=r; requiredLevel=rl; synergy=s; }
    }

    public static final Target[] TARGETS = {
        new Target("\u5c0f\u578b\u5496\u5561\u5e97", 3000, 50, 5, "\u996e\u54c1\u4e1a\u52a1+50"),
        new Target("\u6587\u5177\u8fde\u9501", 8000, 120, 8, "\u6587\u5177\u4e1a\u52a1+120"),
        new Target("\u79d1\u6280\u521d\u521b", 20000, 300, 12, "\u79d1\u6280\u4e1a\u52a1+300"),
        new Target("\u9910\u996e\u96c6\u56e2", 50000, 800, 18, "\u9910\u996e\u4e1a\u52a1+800"),
        new Target("\u4e92\u8054\u7f51\u516c\u53f8", 150000, 2500, 25, "\u79d1\u6280\u4e1a\u52a1+2500"),
        new Target("\u4e0a\u5e02\u516c\u53f8", 500000, 8000, 35, "\u5168\u9762\u4e1a\u52a1+8000"),
        new Target("\u8de8\u56fd\u96c6\u56e2", 2000000, 30000, 45, "\u5168\u7403\u4e1a\u52a1+30000")
    };

    private static final Map<UUID, List<Integer>> acquired = new HashMap<>();

    public static boolean acquire(ServerPlayerEntity player, int idx) {
        UUID uuid = player.getUUID();
        if (idx < 1 || idx > TARGETS.length) { player.sendMessage(new StringTextComponent("\u00a7c\u65e0\u6548\u7f16\u53f7!"), uuid); return false; }
        List<Integer> list = acquired.getOrDefault(uuid, new ArrayList<>());
        if (list.contains(idx - 1)) { player.sendMessage(new StringTextComponent("\u00a7c\u5df2\u6536\u8d2d!"), uuid); return false; }
        Target t = TARGETS[idx - 1];
        int level = player.getCapability(SkillCapability.SKILL_CAP).map(SkillCapability.ISkill::getLevel).orElse(1);
        if (level < t.requiredLevel) { player.sendMessage(new StringTextComponent("\u00a7c\u9700Lv." + t.requiredLevel), uuid); return false; }

        return player.getCapability(MoneyCapability.MONEY_CAP).map(m -> {
            if (m.spendMoney(t.value)) {
                list.add(idx - 1);
                acquired.put(uuid, list);
                player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u6536\u8d2d\u6210\u529f! " + t.name), uuid);
                player.sendMessage(new StringTextComponent("\u00a7e\u652f\u4ed8:" + t.value + "\u91d1\u5e01 | \u6536\u5165\u52a0\u6210:" + t.revenueBoost + "/\u5468 | " + t.synergy), uuid);
                return true;
            }
            player.sendMessage(new StringTextComponent("\u00a7c\u8d44\u91d1\u4e0d\u8db3! \u9700" + t.value), uuid); return false;
        }).orElse(false);
    }

    public static void show(ServerPlayerEntity player) {
        UUID uuid = player.getUUID();
        List<Integer> list = acquired.getOrDefault(uuid, new ArrayList<>());
        player.sendMessage(new StringTextComponent("\u00a76\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7a\ud83e\udd1d \u5e76\u8d2d\u91cd\u7ec4 (" + list.size() + "/" + TARGETS.length + ")  \u00a76\u2551"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u255a\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255d"), uuid);

        int totalBoost = 0;
        for (int i = 0; i < TARGETS.length; i++) {
            Target t = TARGETS[i];
            boolean has = list.contains(i);
            if (has) totalBoost += t.revenueBoost;
            player.sendMessage(new StringTextComponent("\u00a7e[" + (i+1) + "] " + t.name + " \u00a7f|\u00a76 " + t.value + "\u91d1\u5e01 \u00a7f|\u00a7a +" + t.revenueBoost + "/\u5468 \u00a7f|\u00a7b Lv." + t.requiredLevel + "+ \u00a7f| " + (has ? "\u00a7a\u2714" : "\u00a77\u2716")), uuid);
        }
        player.sendMessage(new StringTextComponent("\u00a7a\u603b\u6536\u8d2d\u52a0\u6210: \u00a76" + totalBoost + "\u91d1\u5e01/\u5468"), uuid);
        player.sendMessage(new StringTextComponent("\u00a7e\u8f93\u5165 /merger <1-7> \u6536\u8d2d"), uuid);
    }

    public static int getTotalBoost(UUID uuid) {
        List<Integer> list = acquired.getOrDefault(uuid, new ArrayList<>());
        int total = 0;
        for (int idx : list) total += TARGETS[idx].revenueBoost;
        return total;
    }
}
