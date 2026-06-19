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
 * 元宇宙地产系统 - 虚拟土地买卖
 */
@Mod.EventBusSubscriber(modid = CampusLife.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MetaverseSystem {

    public static class VirtualLand {
        public String name, district;
        public int purchasePrice, weeklyRent, requiredLevel;

        public VirtualLand(String name, String district, int price, int rent, int reqLvl) {
            this.name = name; this.district = district; this.purchasePrice = price; this.weeklyRent = rent; this.requiredLevel = reqLvl;
        }
    }

    public static final VirtualLand[] LANDS = {
        new VirtualLand("\u5e73\u623f\u533aA", "\u57fa\u7840\u533a", 1000, 30, 1),
        new VirtualLand("\u5e73\u623f\u533aB", "\u57fa\u7840\u533a", 2000, 60, 2),
        new VirtualLand("\u5546\u4e1a\u533aC", "\u5546\u4e1a\u533a", 8000, 300, 5),
        new VirtualLand("\u91d1\u878d\u533aD", "\u91d1\u878d\u533a", 25000, 1000, 10),
        new VirtualLand("\u79d1\u6280\u533aE", "\u79d1\u6280\u533a", 50000, 2000, 15),
        new VirtualLand("\u6838\u5fc3\u533aF", "\u6838\u5fc3\u533a", 200000, 10000, 25),
        new VirtualLand("\u4f20\u5947\u533aG", "\u4f20\u5947\u533a", 1000000, 50000, 40)
    };

    private static final Map<UUID, List<Integer>> owned = new HashMap<>();
    private static int tickCounter = 0;

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        tickCounter++;
        if (tickCounter % 6000 != 0 || ServerLifecycleHooks.getCurrentServer() == null) return;

        for (ServerPlayerEntity player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
            UUID uuid = player.getUUID();
            List<Integer> lands = owned.get(uuid);
            if (lands == null || lands.isEmpty()) continue;
            int total = 0;
            for (int idx : lands) total += LANDS[idx].weeklyRent;
            final int rent = total;
            player.getCapability(MoneyCapability.MONEY_CAP).ifPresent(m -> m.addMoney(rent));
            player.sendMessage(new StringTextComponent("\u00a7d\u5143\u5b87\u5b99\u5730\u4ea7\u6536\u5165: \u00a76" + rent + "\u91d1\u5e01 (" + lands.size() + "\u5757)"), uuid);
        }
    }

    public static boolean buy(ServerPlayerEntity player, int idx) {
        UUID uuid = player.getUUID();
        if (idx < 1 || idx > LANDS.length) { player.sendMessage(new StringTextComponent("\u00a7c\u65e0\u6548\u7f16\u53f7!"), uuid); return false; }
        VirtualLand land = LANDS[idx - 1];
        int level = player.getCapability(SkillCapability.SKILL_CAP).map(SkillCapability.ISkill::getLevel).orElse(1);
        if (level < land.requiredLevel) { player.sendMessage(new StringTextComponent("\u00a7c\u9700Lv." + land.requiredLevel), uuid); return false; }
        List<Integer> list = owned.getOrDefault(uuid, new ArrayList<>());
        if (list.contains(idx - 1)) { player.sendMessage(new StringTextComponent("\u00a7c\u5df2\u6301\u6709!"), uuid); return false; }

        return player.getCapability(MoneyCapability.MONEY_CAP).map(m -> {
            if (m.spendMoney(land.purchasePrice)) {
                list.add(idx - 1);
                owned.put(uuid, list);
                player.sendMessage(new StringTextComponent("\u00a7d\u2714 \u8d2d\u4e70\u5143\u5b87\u5b99\u5730\u4ea7! " + land.name + " (" + land.district + ")"), uuid);
                player.sendMessage(new StringTextComponent("\u00a7e\u6295\u8d44:" + land.purchasePrice + " | \u79df\u91d1:" + land.weeklyRent + "/\u5468"), uuid);
                return true;
            }
            player.sendMessage(new StringTextComponent("\u00a7c\u8d44\u91d1\u4e0d\u8db3!"), uuid); return false;
        }).orElse(false);
    }

    public static void show(ServerPlayerEntity player) {
        UUID uuid = player.getUUID();
        List<Integer> list = owned.getOrDefault(uuid, new ArrayList<>());
        player.sendMessage(new StringTextComponent("\u00a7d\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557"), uuid);
        player.sendMessage(new StringTextComponent("\u00a7d\u2551  \u00a7d\ud83e\uudd8c \u5143\u5b87\u5b99\u5730\u4ea7 (" + list.size() + ")  \u00a7d\u2551"), uuid);
        player.sendMessage(new StringTextComponent("\u00a7d\u255a\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255d"), uuid);

        int totalRent = 0;
        for (int i = 0; i < LANDS.length; i++) {
            VirtualLand land = LANDS[i];
            boolean has = list.contains(i);
            if (has) totalRent += land.weeklyRent;
            player.sendMessage(new StringTextComponent(
                    "\u00a7d[" + (i+1) + "] " + land.name + " (" + land.district + ")" +
                    " \u00a7f|\u00a76 " + land.purchasePrice + "\u91d1\u5e01" +
                    " \u00a7f|\u00a7a \u79df:" + land.weeklyRent + "/\u5468" +
                    " \u00a7f|\u00a7b Lv." + land.requiredLevel + "+" +
                    " \u00a7f| " + (has ? "\u00a7a\u2714" : "\u00a77\u2716")), uuid);
        }
        player.sendMessage(new StringTextComponent("\u00a7d\u603b\u79df\u91d1: \u00a76" + totalRent + "\u91d1\u5e01/\u5468"), uuid);
        player.sendMessage(new StringTextComponent("\u00a7e\u8f93\u5165 /metaverse <1-7> \u8d2d\u4e70"), uuid);
    }
}
