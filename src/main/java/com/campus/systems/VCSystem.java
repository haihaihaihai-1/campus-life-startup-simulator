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
 * 风险投资系统 - VC轮次融资+估值
 */
@Mod.EventBusSubscriber(modid = CampusLife.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class VCSystem {

    public enum Round {
        SEED("\u79cd\u5b50\u8f6e", 5000, 10, 1, 10000),
        ANGEL("\u5929\u4f7f\u8f6e", 20000, 15, 3, 50000),
        A("A\u8f6e", 100000, 20, 8, 300000),
        B("B\u8f6e", 500000, 25, 15, 1500000),
        C("C\u8f6e", 2000000, 30, 25, 8000000),
        PRE_IPO("Pre-IPO", 10000000, 35, 40, 50000000);

        public String name; public int amount; public int equity; public int reqLevel; public int valuation;
        Round(String n, int a, int e, int r, int v) { name=n; amount=a; equity=e; reqLevel=r; valuation=v; }
    }

    private static final Map<UUID, Integer> currentRound = new HashMap<>();
    private static final Map<UUID, Integer> valuation = new HashMap<>();

    public static boolean raiseRound(ServerPlayerEntity player, int roundIdx) {
        UUID uuid = player.getUUID();
        Round[] rounds = Round.values();
        if (roundIdx < 1 || roundIdx > rounds.length) { player.sendMessage(new StringTextComponent("\u00a7c\u65e0\u6548\u8f6e\u6b21!"), uuid); return false; }
        Round target = rounds[roundIdx - 1];
        int current = currentRound.getOrDefault(uuid, 0);
        if (roundIdx <= current) { player.sendMessage(new StringTextComponent("\u00a7c\u5df2\u5b8c\u6210\u6b64\u8f6e!"), uuid); return false; }
        if (roundIdx != current + 1) { player.sendMessage(new StringTextComponent("\u00a7c\u9700\u6309\u987a\u5e8f\u878d\u8d44! \u4e0b\u4e00\u8f6e: " + rounds[current].name), uuid); return false; }

        int level = player.getCapability(SkillCapability.SKILL_CAP).map(SkillCapability.ISkill::getLevel).orElse(1);
        if (level < target.reqLevel) { player.sendMessage(new StringTextComponent("\u00a7c\u9700Lv." + target.reqLevel), uuid); return false; }

        currentRound.put(uuid, roundIdx);
        valuation.put(uuid, target.valuation);
        player.getCapability(MoneyCapability.MONEY_CAP).ifPresent(m -> m.addMoney(target.amount));
        player.getCapability(SkillCapability.SKILL_CAP).ifPresent(s -> s.addExp(target.amount / 1000));

        player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u878d\u8d44\u6210\u529f! " + target.name), uuid);
        player.sendMessage(new StringTextComponent("\u00a7e\u878d\u8d44\u989d: \u00a76" + target.amount + "\u91d1\u5e01"), uuid);
        player.sendMessage(new StringTextComponent("\u00a7e\u8ba9\u6e21\u80a1\u6743: \u00a7c" + target.equity + "%"), uuid);
        player.sendMessage(new StringTextComponent("\u00a7e\u4f30\u503c: \u00a76" + target.valuation + "\u91d1\u5e01"), uuid);
        return true;
    }

    public static void show(ServerPlayerEntity player) {
        UUID uuid = player.getUUID();
        int current = currentRound.getOrDefault(uuid, 0);
        int val = valuation.getOrDefault(uuid, 0);
        int level = player.getCapability(SkillCapability.SKILL_CAP).map(SkillCapability.ISkill::getLevel).orElse(1);

        player.sendMessage(new StringTextComponent("\u00a76\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7a\ud83d\udcb0 \u98ce\u9669\u6295\u8d44 | \u4f30\u503c: \u00a76" + val + "  \u00a76\u2551"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u255a\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255d"), uuid);

        Round[] rounds = Round.values();
        for (int i = 0; i < rounds.length; i++) {
            Round r = rounds[i];
            boolean done = i < current;
            boolean next = i == current;
            boolean canRaise = next && level >= r.reqLevel;
            String status = done ? "\u00a7a\u2714\u5df2\u5b8c\u6210" : (canRaise ? "\u00a7e\u53ef\u878d\u8d44" : (next ? "\u00a7c\u7b49\u7ea7\u4e0d\u8db3" : "\u00a77\u672a\u89e3\u9501"));
            player.sendMessage(new StringTextComponent(
                    "\u00a7e[" + (i+1) + "] " + r.name +
                    " \u00a7f|\u00a76 " + r.amount + "\u91d1\u5e01" +
                    " \u00a7f|\u00a7c \u80a1\u6743" + r.equity + "%" +
                    " \u00a7f|\u00a7b Lv." + r.reqLevel + "+" +
                    " \u00a7f|\u00a76 \u4f30\u503c" + r.valuation +
                    " \u00a7f| " + status), uuid);
        }
        if (current < rounds.length) {
            player.sendMessage(new StringTextComponent("\u00a7e\u8f93\u5165 /vc <" + (current+1) + "> \u5f00\u542f\u4e0b\u4e00\u8f6e"), uuid);
        } else {
            player.sendMessage(new StringTextComponent("\u00a7d\u2605 \u5df2\u5b8c\u6210\u6240\u6709\u8f6e\u6b21! \u4f01\u4e1a\u4f30\u503c: " + val), uuid);
        }
    }

    public static int getValuation(UUID uuid) { return valuation.getOrDefault(uuid, 0); }
    public static int getRound(UUID uuid) { return currentRound.getOrDefault(uuid, 0); }
}
