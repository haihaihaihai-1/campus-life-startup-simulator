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
 * 加盟连锁系统 - 开设分店扩张
 */
@Mod.EventBusSubscriber(modid = CampusLife.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class FranchiseSystem {

    public static class FranchiseType {
        public String name, description;
        public int openCost, dailyRevenue, requiredLevel;

        public FranchiseType(String name, String desc, int cost, int revenue, int reqLvl) {
            this.name = name; this.description = desc; this.openCost = cost; this.dailyRevenue = revenue; this.requiredLevel = reqLvl;
        }
    }

    public static final FranchiseType[] TYPES = {
        new FranchiseType("\u5496\u5561\u5e97", "\u57fa\u7840\u5496\u5561\u5e97", 1000, 50, 2),
        new FranchiseType("\u6587\u5177\u5e97", "\u6821\u56ed\u6587\u5177\u5e97", 2000, 80, 4),
        new FranchiseType("\u5feb\u9910\u5e97", "\u5feb\u9910\u8fde\u9501\u5e97", 5000, 200, 6),
        new FranchiseType("\u79d1\u6280\u5e97", "\u79d1\u6280\u4ea7\u54c1\u5e97", 10000, 500, 10),
        new FranchiseType("\u65d7\u8230\u5e97", "\u54c1\u724c\u65d7\u8230\u5e97", 30000, 1500, 15),
        new FranchiseType("\u57ce\u5e02\u603b\u5e97", "\u57ce\u5e02\u7ea7\u65d7\u8230\u603b\u5e97", 100000, 5000, 25)
    };

    private static final Map<UUID, List<Franchise>> franchises = new HashMap<>();
    private static int tickCounter = 0;

    public static class Franchise {
        public int typeIdx;
        public long openTime;

        public Franchise(int idx) { this.typeIdx = idx; this.openTime = System.currentTimeMillis(); }
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        tickCounter++;

        // 每5分钟结算加盟店收入
        if (tickCounter % 6000 == 0 && ServerLifecycleHooks.getCurrentServer() != null) {
            for (ServerPlayerEntity player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
                UUID uuid = player.getUUID();
                List<Franchise> list = franchises.get(uuid);
                if (list == null || list.isEmpty()) continue;

                int total = 0;
                for (Franchise f : list) {
                    total += TYPES[f.typeIdx].dailyRevenue;
                }

                final int totalRevenue = total;
                player.getCapability(MoneyCapability.MONEY_CAP).ifPresent(m -> m.addMoney(totalRevenue));
                player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u52a0\u76df\u5e97\u6536\u5165: \u00a76" + totalRevenue + "\u91d1\u5e01 (" + list.size() + "\u5bb6\u5e97)"), uuid);
            }
        }
    }

    public static boolean open(ServerPlayerEntity player, int typeIdx) {
        UUID uuid = player.getUUID();
        if (typeIdx < 1 || typeIdx > TYPES.length) { player.sendMessage(new StringTextComponent("\u00a7c\u65e0\u6548\u7c7b\u578b!"), uuid); return false; }

        FranchiseType ft = TYPES[typeIdx - 1];
        int level = player.getCapability(SkillCapability.SKILL_CAP).map(SkillCapability.ISkill::getLevel).orElse(1);
        if (level < ft.requiredLevel) { player.sendMessage(new StringTextComponent("\u00a7c\u9700Lv." + ft.requiredLevel), uuid); return false; }

        List<Franchise> list = franchises.getOrDefault(uuid, new ArrayList<>());
        int total = list.size();
        int maxShops = 3 + level / 5;
        if (total >= maxShops) { player.sendMessage(new StringTextComponent("\u00a7c\u5e97\u94fa\u4e0a\u9650: " + maxShops + "\u5bb6 (\u6bcf5\u7ea7+1)"), uuid); return false; }

        return player.getCapability(MoneyCapability.MONEY_CAP).map(m -> {
            if (m.spendMoney(ft.openCost)) {
                list.add(new Franchise(typeIdx - 1));
                franchises.put(uuid, list);
                player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u5f00\u4e1a! " + ft.name + " (" + (total+1) + "/" + maxShops + ")"), uuid);
                player.sendMessage(new StringTextComponent("\u00a7e\u6295\u8d44: " + ft.openCost + "\u91d1\u5e01 | \u9884\u8ba1\u6536\u5165: " + ft.dailyRevenue + "\u91d1\u5e01/\u5468"), uuid);
                return true;
            }
            player.sendMessage(new StringTextComponent("\u00a7c\u8d44\u91d1\u4e0d\u8db3! \u9700" + ft.openCost + "\u91d1\u5e01"), uuid);
            return false;
        }).orElse(false);
    }

    public static void show(ServerPlayerEntity player) {
        UUID uuid = player.getUUID();
        List<Franchise> list = franchises.getOrDefault(uuid, new ArrayList<>());
        int level = player.getCapability(SkillCapability.SKILL_CAP).map(SkillCapability.ISkill::getLevel).orElse(1);
        int maxShops = 3 + level / 5;

        player.sendMessage(new StringTextComponent("\u00a76\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7e\ud83c\udfe2 \u52a0\u76df\u8fde\u9501 (" + list.size() + "/" + maxShops + ")  \u00a76\u2551"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u255a\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255d"), uuid);

        for (int i = 0; i < TYPES.length; i++) {
            FranchiseType ft = TYPES[i];
            int count = 0;
            for (Franchise f : list) if (f.typeIdx == i) count++;
            player.sendMessage(new StringTextComponent(
                    "\u00a7e[" + (i+1) + "] " + ft.name +
                    " \u00a7f|\u00a76 " + ft.openCost + "\u91d1\u5e01" +
                    " \u00a7f|\u00a7a " + ft.dailyRevenue + "/\u5468" +
                    " \u00a7f|\u00a7b Lv." + ft.requiredLevel + "+" +
                    " \u00a7f|\u00a77 \u5df2\u5f00:" + count), uuid);
        }

        int totalIncome = 0;
        for (Franchise f : list) totalIncome += TYPES[f.typeIdx].dailyRevenue;
        player.sendMessage(new StringTextComponent("\u00a7a\u603b\u6536\u5165: \u00a76" + totalIncome + "\u91d1\u5e01/\u5468"), uuid);
        player.sendMessage(new StringTextComponent("\u00a7e\u8f93\u5165 /franchise <1-6> \u5f00\u8bbe\u65b0\u5e97"), uuid);
    }

    public static int getShopCount(UUID uuid) {
        List<Franchise> list = franchises.get(uuid);
        return list != null ? list.size() : 0;
    }
}
