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
 * 导师网络系统 - 建立导师库+智能匹配
 */
@Mod.EventBusSubscriber(modid = CampusLife.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MentorNetworkSystem {

    public enum MentorType {
        ACADEMIC("\u5b66\u672f\u5bfc\u5e08", 300, 30, 1, "\u7814\u53d1\u7ecf\u9a8c+30%"),
        INDUSTRY("\u4e1a\u754c\u5bfc\u5e08", 1000, 50, 3, "\u6536\u5165+50%"),
        INVESTOR("\u6295\u8d44\u5bfc\u5e08", 3000, 100, 6, "\u878d\u8d44\u989d+20%"),
        GLOBAL("\u5168\u7403\u5bfc\u5e08", 10000, 200, 12, "\u5168\u9762\u52a0\u6210+20%"),
        LEGEND("\u4f20\u5947\u5bfc\u5e08", 50000, 500, 25, "\u8d85\u7ea7\u52a0\u6210+50%");

        public String name, effect; public int cost, boost, reqLevel;
        MentorType(String n, int c, int b, int r, String e) { name=n; cost=c; boost=b; reqLevel=r; effect=e; }
    }

    private static final Map<UUID, Set<MentorType>> network = new HashMap<>();
    private static final Map<UUID, Long> lastConsult = new HashMap<>();

    public static boolean addMentor(ServerPlayerEntity player, int idx) {
        UUID uuid = player.getUUID();
        MentorType[] types = MentorType.values();
        if (idx < 1 || idx > types.length) return false;
        MentorType m = types[idx - 1];
        Set<MentorType> has = network.getOrDefault(uuid, new HashSet<>());
        if (has.contains(m)) { player.sendMessage(new StringTextComponent("\u00a7c\u5df2\u52a0\u5165\u7f51\u7edc!"), uuid); return false; }
        int level = player.getCapability(SkillCapability.SKILL_CAP).map(SkillCapability.ISkill::getLevel).orElse(1);
        if (level < m.reqLevel) { player.sendMessage(new StringTextComponent("\u00a7c\u9700Lv." + m.reqLevel), uuid); return false; }

        return player.getCapability(MoneyCapability.MONEY_CAP).map(money -> {
            if (money.spendMoney(m.cost)) {
                has.add(m); network.put(uuid, has);
                player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u52a0\u5165\u5bfc\u5e08\u7f51\u7edc: " + m.name + " | " + m.effect), uuid);
                return true;
            }
            player.sendMessage(new StringTextComponent("\u00a7c\u8d44\u91d1\u4e0d\u8db3!"), uuid); return false;
        }).orElse(false);
    }

    public static boolean consult(ServerPlayerEntity player) {
        UUID uuid = player.getUUID();
        Set<MentorType> has = network.get(uuid);
        if (has == null || has.isEmpty()) { player.sendMessage(new StringTextComponent("\u00a7c\u5bfc\u5e08\u7f51\u7edc\u4e3a\u7a7a!"), uuid); return false; }
        long now = System.currentTimeMillis();
        if (now - lastConsult.getOrDefault(uuid, 0L) < 300000) { player.sendMessage(new StringTextComponent("\u00a7c\u51b7\u5374\u4e2d... \u8bf7\u7b49\u5f855\u5206\u949f"), uuid); return false; }

        int totalBoost = 0;
        for (MentorType m : has) totalBoost += m.boost;
        int expGain = totalBoost * 2;
        int moneyGain = totalBoost * 5;
        player.getCapability(MoneyCapability.MONEY_CAP).ifPresent(m -> m.addMoney(moneyGain));
        player.getCapability(SkillCapability.SKILL_CAP).ifPresent(s -> s.addExp(expGain));
        lastConsult.put(uuid, now);
        player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u5bfc\u5e08\u54a8\u8be2! \u83b7\u5f97: \u00a76" + moneyGain + "\u91d1\u5e01 + \u00a7b" + expGain + "\u7ecf\u9a8c (" + has.size() + "\u4f4d\u5bfc\u5e08)"), uuid);
        return true;
    }

    public static void show(ServerPlayerEntity player) {
        UUID uuid = player.getUUID();
        Set<MentorType> has = network.getOrDefault(uuid, new HashSet<>());
        int totalBoost = 0;
        for (MentorType m : has) totalBoost += m.boost;
        player.sendMessage(new StringTextComponent("\u00a76\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7d\ud83e\uddd1\u200d\ud83c\udfeb \u5bfc\u5e08\u7f51\u7edc (" + has.size() + "/" + MentorType.values().length + ")  \u00a76\u2551"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u255a\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255d"), uuid);
        MentorType[] types = MentorType.values();
        for (int i = 0; i < types.length; i++) {
            MentorType m = types[i];
            boolean owned = has.contains(m);
            player.sendMessage(new StringTextComponent("\u00a7e[" + (i+1) + "] " + m.name + " \u00a7f|\u00a76 " + m.cost + "\u91d1\u5e01 \u00a7f|\u00a7a +" + m.boost + " \u00a7f|\u00a7b Lv." + m.reqLevel + "+ \u00a7f| " + (owned ? "\u00a7a\u2714" : "\u00a77\u2716")), uuid);
        }
        player.sendMessage(new StringTextComponent("\u00a7a\u603b\u52a0\u6210: \u00a76+" + totalBoost), uuid);
        player.sendMessage(new StringTextComponent("\u00a7e/mnet <1-5> \u52a0\u5165 | /mnet consult \u54a8\u8be2"), uuid);
    }

    public static int getTotalBoost(UUID uuid) {
        Set<MentorType> has = network.getOrDefault(uuid, new HashSet<>());
        int total = 0;
        for (MentorType m : has) total += m.boost;
        return total;
    }
}
