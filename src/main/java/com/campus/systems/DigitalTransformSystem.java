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
 * 数字化转型系统 - 技术升级+效率提升
 */
@Mod.EventBusSubscriber(modid = CampusLife.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DigitalTransformSystem {

    public enum Tech {
        ERP("\u4f01\u4e1a\u8d44\u6e90\u89c4\u5212", 2000, 8, 2),
        CLOUD("\u4e91\u8ba1\u7b97", 5000, 12, 5),
        AI("\u4eba\u5de5\u667a\u80fd", 15000, 20, 8),
        BIGDATA("\u5927\u6570\u636e", 30000, 25, 12),
        IOT("\u7269\u8054\u7f51", 50000, 30, 15),
        BLOCKCHAIN("\u533a\u5757\u94fe", 100000, 40, 20),
        METAVERSE("\u5143\u5b87\u5b99", 300000, 60, 30);

        public String name; public int cost; public int efficiency; public int reqLevel;
        Tech(String n, int c, int e, int r) { name=n; cost=c; efficiency=e; reqLevel=r; }
    }

    private static final Map<UUID, Set<Tech>> transformed = new HashMap<>();

    public static boolean transform(ServerPlayerEntity player, int idx) {
        UUID uuid = player.getUUID();
        Tech[] techs = Tech.values();
        if (idx < 1 || idx > techs.length) return false;
        Tech t = techs[idx - 1];
        Set<Tech> has = transformed.getOrDefault(uuid, new HashSet<>());
        if (has.contains(t)) { player.sendMessage(new StringTextComponent("\u00a7c\u5df2\u5b8c\u6210!"), uuid); return false; }
        int level = player.getCapability(SkillCapability.SKILL_CAP).map(SkillCapability.ISkill::getLevel).orElse(1);
        if (level < t.reqLevel) { player.sendMessage(new StringTextComponent("\u00a7c\u9700Lv." + t.reqLevel), uuid); return false; }

        return player.getCapability(MoneyCapability.MONEY_CAP).map(m -> {
            if (m.spendMoney(t.cost)) {
                has.add(t); transformed.put(uuid, has);
                player.getCapability(SkillCapability.SKILL_CAP).ifPresent(s -> s.addExp(t.cost / 100));
                player.sendMessage(new StringTextComponent("\u00a7b\u2714 \u6570\u5b57\u5316\u5347\u7ea7: " + t.name + " | \u6548\u7387+" + t.efficiency + "%"), uuid);
                return true;
            }
            player.sendMessage(new StringTextComponent("\u00a7c\u8d44\u91d1\u4e0d\u8db3!"), uuid); return false;
        }).orElse(false);
    }

    public static void show(ServerPlayerEntity player) {
        UUID uuid = player.getUUID();
        Set<Tech> has = transformed.getOrDefault(uuid, new HashSet<>());
        int totalEff = 0;
        for (Tech t : has) totalEff += t.efficiency;
        player.sendMessage(new StringTextComponent("\u00a76\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7b\ud83d\udcbb \u6570\u5b57\u5316\u8f6c\u578b (\u6548\u7387+" + totalEff + "%)  \u00a76\u2551"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u255a\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255d"), uuid);
        Tech[] techs = Tech.values();
        for (int i = 0; i < techs.length; i++) {
            Tech t = techs[i];
            boolean owned = has.contains(t);
            player.sendMessage(new StringTextComponent("\u00a7e[" + (i+1) + "] " + t.name + " \u00a7f|\u00a76 " + t.cost + "\u91d1\u5e01 \u00a7f|\u00a7a \u6548\u7387+" + t.efficiency + "% \u00a7f|\u00a7b Lv." + t.reqLevel + "+ \u00a7f| " + (owned ? "\u00a7a\u2714" : "\u00a77\u2716")), uuid);
        }
        player.sendMessage(new StringTextComponent("\u00a7e/digital <1-7> \u5347\u7ea7"), uuid);
    }

    public static int getEfficiency(UUID uuid) {
        Set<Tech> has = transformed.getOrDefault(uuid, new HashSet<>());
        int total = 0;
        for (Tech t : has) total += t.efficiency;
        return total;
    }
}
