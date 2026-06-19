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
 * 企业传承系统 - 创始人遗产+传承加成
 */
@Mod.EventBusSubscriber(modid = CampusLife.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class LegacySystem {

    public enum Legacy {
        FOUNDING("\u521b\u59cb\u4f01\u4e1a", 1000, 5, "\u521b\u59cb\u52a0\u6210"),
        GROWTH("\u5feb\u901f\u6210\u957f", 5000, 10, "\u6210\u957f\u52a0\u6210"),
        INNOVATION("\u521b\u65b0\u4f20\u627f", 15000, 20, "\u7814\u53d1\u52a0\u6210"),
        SCALE("\u89c4\u6a21\u4f20\u627f", 50000, 30, "\u7ecf\u8425\u52a0\u6210"),
        LEGEND("\u4f20\u5947\u9057\u4ea7", 200000, 50, "\u5168\u9762\u52a0\u6210");

        public String name, effect; public int cost, boost;
        Legacy(String n, int c, int b, String e) { name=n; cost=c; boost=b; effect=e; }
    }

    private static final Map<UUID, Set<Legacy>> unlocked = new HashMap<>();

    public static boolean unlock(ServerPlayerEntity player, int idx) {
        UUID uuid = player.getUUID();
        Legacy[] legacies = Legacy.values();
        if (idx < 1 || idx > legacies.length) return false;
        Legacy l = legacies[idx - 1];
        Set<Legacy> has = unlocked.getOrDefault(uuid, new HashSet<>());
        if (has.contains(l)) { player.sendMessage(new StringTextComponent("\u00a7c\u5df2\u89e3\u9501!"), uuid); return false; }
        int level = player.getCapability(SkillCapability.SKILL_CAP).map(SkillCapability.ISkill::getLevel).orElse(1);
        if (level < idx * 10) { player.sendMessage(new StringTextComponent("\u00a7c\u9700Lv." + (idx*10)), uuid); return false; }

        return player.getCapability(MoneyCapability.MONEY_CAP).map(m -> {
            if (m.spendMoney(l.cost)) {
                has.add(l);
                unlocked.put(uuid, has);
                player.sendMessage(new StringTextComponent("\u00a7d\u2714 \u89e3\u9501\u4f20\u627f: " + l.name + " | " + l.effect + "+" + l.boost + "%"), uuid);
                return true;
            }
            player.sendMessage(new StringTextComponent("\u00a7c\u8d44\u91d1\u4e0d\u8db3! \u9700" + l.cost), uuid); return false;
        }).orElse(false);
    }

    public static void show(ServerPlayerEntity player) {
        UUID uuid = player.getUUID();
        Set<Legacy> has = unlocked.getOrDefault(uuid, new HashSet<>());
        player.sendMessage(new StringTextComponent("\u00a76\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7d\ud83c\udfe0 \u4f01\u4e1a\u4f20\u627f (" + has.size() + "/" + Legacy.values().length + ")  \u00a76\u2551"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u255a\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255d"), uuid);
        Legacy[] legacies = Legacy.values();
        for (int i = 0; i < legacies.length; i++) {
            Legacy l = legacies[i];
            boolean owned = has.contains(l);
            player.sendMessage(new StringTextComponent("\u00a7e[" + (i+1) + "] " + l.name + " \u00a7f|\u00a76 " + l.cost + "\u91d1\u5e01 \u00a7f|\u00a7a " + l.effect + "+" + l.boost + "% \u00a7f|\u00a7b Lv." + (i+1)*10 + "+ \u00a7f| " + (owned ? "\u00a7a\u2714" : "\u00a77\u2716")), uuid);
        }
        player.sendMessage(new StringTextComponent("\u00a7e\u8f93\u5165 /legacy <1-5> \u89e3\u9501"), uuid);
    }

    public static int getTotalBoost(UUID uuid) {
        Set<Legacy> has = unlocked.getOrDefault(uuid, new HashSet<>());
        int total = 0;
        for (Legacy l : has) total += l.boost;
        return total;
    }
}
