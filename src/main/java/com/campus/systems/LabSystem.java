package com.campus.systems;

import com.campus.CampusLife;
import com.campus.economy.MoneyCapability;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;

/**
 * 研发实验室系统 - 高级研发+组合效果
 */
@Mod.EventBusSubscriber(modid = CampusLife.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class LabSystem {

    public static class Experiment {
        public String name, description;
        public int cost, requiredLevel;
        public String[] synergy; // 组合加成需要的其他实验
        public int incomeBoost, expBoost;

        public Experiment(String name, String desc, int cost, int reqLvl, int incBoost, int expBoost, String... synergy) {
            this.name = name; this.description = desc; this.cost = cost; this.requiredLevel = reqLvl;
            this.incomeBoost = incBoost; this.expBoost = expBoost; this.synergy = synergy;
        }
    }

    public static final Experiment[] EXPERIMENTS = {
        new Experiment("\u7eb3\u7c73\u5b9e\u9a8c", "\u7eb3\u7c73\u6750\u6599\u7814\u7a76", 1000, 5, 5, 10),
        new Experiment("\u91cf\u5b50\u8ba1\u7b97", "\u91cf\u5b50\u8ba1\u7b97\u673a\u5b9e\u9a8c", 5000, 10, 10, 20, "\u7eb3\u7c73\u5b9e\u9a8c"),
        new Experiment("\u57fa\u56e0\u7f16\u8f91", "\u57fa\u56e0\u7ec4\u5b66\u5b9e\u9a8c", 15000, 18, 20, 40, "\u91cf\u5b50\u8ba1\u7b97"),
        new Experiment("\u4eba\u5de5\u667a\u80fd", "AI\u7b97\u6cd5\u5b9e\u9a8c", 30000, 25, 30, 60, "\u57fa\u56e0\u7f16\u8f91"),
        new Experiment("\u8111\u673a\u63a5\u53e3", "\u8111\u673a\u5bf9\u63a5\u5b9e\u9a8c", 80000, 35, 50, 100, "\u4eba\u5de5\u667a\u80fd"),
        new Experiment("\u91cf\u5b50\u901a\u4fe1", "\u91cf\u5b50\u7ea0\u7f20\u5b9e\u9a8c", 200000, 45, 80, 200, "\u8111\u673a\u63a5\u53e3")
    };

    private static final Map<UUID, Set<String>> completed = new HashMap<>();

    public static void show(ServerPlayerEntity player) {
        UUID uuid = player.getUUID();
        Set<String> done = completed.getOrDefault(uuid, new HashSet<>());

        player.sendMessage(new StringTextComponent("\u00a76\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7d\ud83e\udd7c \u7814\u53d1\u5b9e\u9a8c\u5ba4  \u00a76\u2551"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u255a\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255d"), uuid);

        for (int i = 0; i < EXPERIMENTS.length; i++) {
            Experiment e = EXPERIMENTS[i];
            boolean has = done.contains(e.name);
            boolean synergy = true;
            for (String s : e.synergy) if (!done.contains(s)) synergy = false;
            String synTag = synergy && !has && e.synergy.length > 0 ? " \u00a7d[\u7ec4\u5408\u52a0\u6210x2]" : "";
            player.sendMessage(new StringTextComponent(
                    "\u00a7e[" + (i+1) + "] " + e.name +
                    " \u00a7f|\u00a76 " + e.cost + "\u91d1\u5e01" +
                    " \u00a7f|\u00a7b Lv." + e.requiredLevel + "+" +
                    " \u00a7f|\u00a7a \u6536\u5165+" + e.incomeBoost + "% \u7ecf\u9a8c+" + e.expBoost + "%" +
                    synTag +
                    " \u00a7f| " + (has ? "\u00a7a\u2714" : "\u00a77\u2716")), uuid);
        }
        player.sendMessage(new StringTextComponent("\u00a7e\u8f93\u5165 /lab <1-6> \u8fdb\u884c\u5b9e\u9a8c"), uuid);
    }

    public static boolean run(ServerPlayerEntity player, int idx) {
        UUID uuid = player.getUUID();
        if (idx < 1 || idx > EXPERIMENTS.length) { player.sendMessage(new StringTextComponent("\u00a7c\u65e0\u6548\u7f16\u53f7!"), uuid); return false; }
        Experiment e = EXPERIMENTS[idx - 1];
        Set<String> done = completed.getOrDefault(uuid, new HashSet<>());
        if (done.contains(e.name)) { player.sendMessage(new StringTextComponent("\u00a7c\u5df2\u5b8c\u6210!"), uuid); return false; }

        for (String s : e.synergy) {
            if (!done.contains(s)) { player.sendMessage(new StringTextComponent("\u00a7c\u9700\u5148\u5b8c\u6210: " + s), uuid); return false; }
        }

        return player.getCapability(MoneyCapability.MONEY_CAP).map(m -> {
            if (m.spendMoney(e.cost)) {
                done.add(e.name);
                completed.put(uuid, done);
                boolean synergy = e.synergy.length > 0;
                int incB = synergy ? e.incomeBoost * 2 : e.incomeBoost;
                int expB = synergy ? e.expBoost * 2 : e.expBoost;
                player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u5b9e\u9a8c\u6210\u529f! " + e.name), uuid);
                player.sendMessage(new StringTextComponent("\u00a7e\u6536\u5165+" + incB + "% | \u7ecf\u9a8c+" + expB + "%" + (synergy ? " \u00a7d[\u7ec4\u5408\u52a0\u6210]" : "")), uuid);
                return true;
            }
            player.sendMessage(new StringTextComponent("\u00a7c\u8d44\u91d1\u4e0d\u8db3! \u9700" + e.cost), uuid); return false;
        }).orElse(false);
    }

    public static int getTotalIncomeBoost(UUID uuid) {
        Set<String> done = completed.getOrDefault(uuid, new HashSet<>());
        int total = 0;
        for (Experiment e : EXPERIMENTS) {
            if (done.contains(e.name)) {
                boolean synergy = true;
                for (String s : e.synergy) if (!done.contains(s)) synergy = false;
                total += synergy ? e.incomeBoost * 2 : e.incomeBoost;
            }
        }
        return total;
    }

    public static int getTotalExpBoost(UUID uuid) {
        Set<String> done = completed.getOrDefault(uuid, new HashSet<>());
        int total = 0;
        for (Experiment e : EXPERIMENTS) {
            if (done.contains(e.name)) {
                boolean synergy = true;
                for (String s : e.synergy) if (!done.contains(s)) synergy = false;
                total += synergy ? e.expBoost * 2 : e.expBoost;
            }
        }
        return total;
    }
}
