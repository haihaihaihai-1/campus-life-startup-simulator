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
 * 税务筹划系统 - 合法避税+优化
 */
@Mod.EventBusSubscriber(modid = CampusLife.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TaxPlanningSystem {

    public enum Strategy {
        OFFSHORE("\u79bb\u5cb8\u67b6\u6784", 5000, 15, 10),
        RD_DEDUCTION("R&D\u52a0\u8ba1\u6263\u9664", 3000, 10, 5),
        LOSS_CARRYFORWARD("\u4e8f\u635f\u7ed3\u8f6c", 2000, 8, 3),
        ACCELERATED_DEPRECIATION("\u52a0\u901f\u6298\u65e7", 4000, 12, 8),
        TAX_HAVEN("\u7a0e\u52a1\u907f\u98ce\u6e2f", 15000, 30, 20);

        public String name; public int cost; public int taxReduction; public int reqLevel;
        Strategy(String n, int c, int r, int rl) { name=n; cost=c; taxReduction=r; reqLevel=rl; }
    }

    private static final Map<UUID, Set<Strategy>> applied = new HashMap<>();

    public static boolean apply(ServerPlayerEntity player, int idx) {
        UUID uuid = player.getUUID();
        Strategy[] strategies = Strategy.values();
        if (idx < 1 || idx > strategies.length) return false;
        Strategy s = strategies[idx - 1];
        Set<Strategy> has = applied.getOrDefault(uuid, new HashSet<>());
        if (has.contains(s)) { player.sendMessage(new StringTextComponent("\u00a7c\u5df2\u5e94\u7528!"), uuid); return false; }
        int level = player.getCapability(SkillCapability.SKILL_CAP).map(SkillCapability.ISkill::getLevel).orElse(1);
        if (level < s.reqLevel) { player.sendMessage(new StringTextComponent("\u00a7c\u9700Lv." + s.reqLevel), uuid); return false; }

        return player.getCapability(MoneyCapability.MONEY_CAP).map(m -> {
            if (m.spendMoney(s.cost)) {
                has.add(s); applied.put(uuid, has);
                player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u5e94\u7528\u7a0e\u52a1\u7b79\u5212: " + s.name + " | \u7a0e\u7387\u964d\u4f4e-" + s.taxReduction + "%"), uuid);
                return true;
            }
            player.sendMessage(new StringTextComponent("\u00a7c\u8d44\u91d1\u4e0d\u8db3!"), uuid); return false;
        }).orElse(false);
    }

    public static void show(ServerPlayerEntity player) {
        UUID uuid = player.getUUID();
        Set<Strategy> has = applied.getOrDefault(uuid, new HashSet<>());
        int totalReduction = 0;
        for (Strategy s : has) totalReduction += s.taxReduction;
        player.sendMessage(new StringTextComponent("\u00a76\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7e\ud83d\udcb4 \u7a0e\u52a1\u7b79\u5212 (\u964d\u7a0e-" + totalReduction + "%)  \u00a76\u2551"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u255a\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255d"), uuid);
        Strategy[] strategies = Strategy.values();
        for (int i = 0; i < strategies.length; i++) {
            Strategy s = strategies[i];
            boolean owned = has.contains(s);
            player.sendMessage(new StringTextComponent("\u00a7e[" + (i+1) + "] " + s.name + " \u00a7f|\u00a76 " + s.cost + "\u91d1\u5e01 \u00a7f|\u00a7a \u964d\u7a0e-" + s.taxReduction + "% \u00a7f|\u00a7b Lv." + s.reqLevel + "+ \u00a7f| " + (owned ? "\u00a7a\u2714" : "\u00a77\u2716")), uuid);
        }
        player.sendMessage(new StringTextComponent("\u00a7e/taxplan <1-5> \u5e94\u7528"), uuid);
    }

    public static int getReduction(UUID uuid) {
        Set<Strategy> has = applied.getOrDefault(uuid, new HashSet<>());
        int total = 0;
        for (Strategy s : has) total += s.taxReduction;
        return total;
    }
}
