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
 * 人才市场系统 - 猎头招聘高级人才
 */
@Mod.EventBusSubscriber(modid = CampusLife.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TalentMarketSystem {

    public enum Talent {
        JUNIOR_DEV("\u521d\u7ea7\u5f00\u53d1", 500, 30, 1),
        SENIOR_DEV("\u9ad8\u7ea7\u5f00\u53d1", 2000, 80, 3),
        ARCHITECT("\u67b6\u6784\u5e08", 5000, 200, 6),
        PRODUCT_MGR("\u4ea7\u54c1\u7ecf\u7406", 3000, 120, 5),
        DATA_SCIENTIST("\u6570\u636e\u79d1\u5b66\u5bb6", 8000, 300, 8),
        CFO("CFO", 20000, 800, 15),
        CTO("CTO", 50000, 2000, 20);

        public String name; public int cost; public int income; public int reqLevel;
        Talent(String n, int c, int i, int r) { name=n; cost=c; income=i; reqLevel=r; }
    }

    private static final Map<UUID, Map<String, Integer>> hired = new HashMap<>();

    public static boolean recruit(ServerPlayerEntity player, int idx) {
        UUID uuid = player.getUUID();
        Talent[] talents = Talent.values();
        if (idx < 1 || idx > talents.length) return false;
        Talent t = talents[idx - 1];
        int level = player.getCapability(SkillCapability.SKILL_CAP).map(SkillCapability.ISkill::getLevel).orElse(1);
        if (level < t.reqLevel) { player.sendMessage(new StringTextComponent("\u00a7c\u9700Lv." + t.reqLevel), uuid); return false; }

        return player.getCapability(MoneyCapability.MONEY_CAP).map(m -> {
            if (m.spendMoney(t.cost)) {
                hired.computeIfAbsent(uuid, k -> new HashMap<>()).merge(t.name, 1, Integer::sum);
                player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u62db\u8058: " + t.name + " | \u6210\u672c:" + t.cost + " | \u6536\u5165+" + t.income + "/\u5468"), uuid);
                return true;
            }
            player.sendMessage(new StringTextComponent("\u00a7c\u8d44\u91d1\u4e0d\u8db3!"), uuid); return false;
        }).orElse(false);
    }

    public static void show(ServerPlayerEntity player) {
        UUID uuid = player.getUUID();
        Map<String, Integer> list = hired.getOrDefault(uuid, new HashMap<>());
        player.sendMessage(new StringTextComponent("\u00a76\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7b\ud83d\udc65 \u4eba\u624d\u5e02\u573a  \u00a76\u2551"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u255a\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255d"), uuid);
        int totalIncome = 0;
        Talent[] talents = Talent.values();
        for (int i = 0; i < talents.length; i++) {
            Talent t = talents[i];
            int count = list.getOrDefault(t.name, 0);
            totalIncome += count * t.income;
            player.sendMessage(new StringTextComponent("\u00a7e[" + (i+1) + "] " + t.name + " \u00a7f|\u00a76 " + t.cost + "\u91d1\u5e01 \u00a7f|\u00a7a +" + t.income + "/\u5468 \u00a7f|\u00a7b Lv." + t.reqLevel + "+ \u00a7f|\u00a77 \u5df2\u8058:" + count), uuid);
        }
        player.sendMessage(new StringTextComponent("\u00a7a\u603b\u6536\u5165: \u00a76" + totalIncome + "\u91d1\u5e01/\u5468"), uuid);
        player.sendMessage(new StringTextComponent("\u00a7e/talent <1-7> \u62db\u8058"), uuid);
    }

    public static int getTotalIncome(UUID uuid) {
        Map<String, Integer> list = hired.getOrDefault(uuid, new HashMap<>());
        int total = 0;
        for (Talent t : Talent.values()) total += list.getOrDefault(t.name, 0) * t.income;
        return total;
    }
}
