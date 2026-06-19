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
 * 房地产系统 - 买卖物业收租
 */
@Mod.EventBusSubscriber(modid = CampusLife.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class RealEstateSystem {

    public static class Property {
        public String name, type;
        public int purchasePrice, weeklyRent, requiredLevel;

        public Property(String name, String type, int price, int rent, int reqLvl) {
            this.name = name; this.type = type; this.purchasePrice = price; this.weeklyRent = rent; this.requiredLevel = reqLvl;
        }
    }

    public static final Property[] PROPERTIES = {
        new Property("\u5b66\u751f\u5bbf\u820d", "\u4f4f\u5b85", 2000, 50, 1),
        new Property("\u6821\u56ed\u5546\u94fa", "\u5546\u4e1a", 5000, 150, 3),
        new Property("\u5199\u5b57\u697c\u529e\u516c\u533a", "\u529e\u516c", 15000, 500, 6),
        new Property("\u79d1\u6280\u56ed\u5382\u623f", "\u5de5\u4e1a", 30000, 1000, 10),
        new Property("\u5546\u4e1a\u7efc\u5408\u4f53", "\u7efc\u5408", 80000, 3000, 15),
        new Property("\u57ce\u5e02\u5730\u6807", "\u5730\u4ea7", 200000, 8000, 25)
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
            List<Integer> props = owned.get(uuid);
            if (props == null || props.isEmpty()) continue;
            int totalRent = 0;
            for (int idx : props) totalRent += PROPERTIES[idx].weeklyRent;
            final int rent = totalRent;
            player.getCapability(MoneyCapability.MONEY_CAP).ifPresent(m -> m.addMoney(rent));
            player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u7269\u4e1a\u79df\u91d1\u6536\u5165: \u00a76" + rent + "\u91d1\u5e01 (" + props.size() + "\u5957)"), uuid);
        }
    }

    public static boolean buy(ServerPlayerEntity player, int idx) {
        UUID uuid = player.getUUID();
        if (idx < 1 || idx > PROPERTIES.length) { player.sendMessage(new StringTextComponent("\u00a7c\u65e0\u6548\u7f16\u53f7!"), uuid); return false; }
        Property p = PROPERTIES[idx - 1];
        int level = player.getCapability(SkillCapability.SKILL_CAP).map(SkillCapability.ISkill::getLevel).orElse(1);
        if (level < p.requiredLevel) { player.sendMessage(new StringTextComponent("\u00a7c\u9700Lv." + p.requiredLevel), uuid); return false; }

        return player.getCapability(MoneyCapability.MONEY_CAP).map(m -> {
            if (m.spendMoney(p.purchasePrice)) {
                List<Integer> list = owned.getOrDefault(uuid, new ArrayList<>());
                list.add(idx - 1);
                owned.put(uuid, list);
                player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u8d2d\u4e70\u6210\u529f! " + p.name + " | \u6295\u8d44:" + p.purchasePrice + " | \u79df\u91d1:" + p.weeklyRent + "/\u5468"), uuid);
                return true;
            }
            player.sendMessage(new StringTextComponent("\u00a7c\u8d44\u91d1\u4e0d\u8db3! \u9700" + p.purchasePrice), uuid); return false;
        }).orElse(false);
    }

    public static boolean sell(ServerPlayerEntity player, int idx) {
        UUID uuid = player.getUUID();
        if (idx < 1 || idx > PROPERTIES.length) { player.sendMessage(new StringTextComponent("\u00a7c\u65e0\u6548\u7f16\u53f7!"), uuid); return false; }
        List<Integer> list = owned.getOrDefault(uuid, new ArrayList<>());
        if (!list.contains(idx - 1)) { player.sendMessage(new StringTextComponent("\u00a7c\u4f60\u6ca1\u6709\u6b64\u7269\u4e1a!"), uuid); return false; }
        Property p = PROPERTIES[idx - 1];
        int sellPrice = (int)(p.purchasePrice * 0.7);
        list.remove(Integer.valueOf(idx - 1));
        owned.put(uuid, list);
        player.getCapability(MoneyCapability.MONEY_CAP).ifPresent(m -> m.addMoney(sellPrice));
        player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u51fa\u552e\u6210\u529f! " + p.name + " | \u83b7\u5f97:" + sellPrice + "\u91d1\u5e01"), uuid);
        return true;
    }

    public static void show(ServerPlayerEntity player) {
        UUID uuid = player.getUUID();
        List<Integer> list = owned.getOrDefault(uuid, new ArrayList<>());
        player.sendMessage(new StringTextComponent("\u00a76\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7b\ud83c\udfe0 \u623f\u5730\u4ea7 (" + list.size() + "\u5957)  \u00a76\u2551"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u255a\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255d"), uuid);

        int totalRent = 0;
        for (int i = 0; i < PROPERTIES.length; i++) {
            Property p = PROPERTIES[i];
            boolean has = list.contains(i);
            if (has) totalRent += p.weeklyRent;
            player.sendMessage(new StringTextComponent(
                    "\u00a7e[" + (i+1) + "] " + p.name + " (" + p.type + ")" +
                    " \u00a7f|\u00a76 " + p.purchasePrice + "\u91d1\u5e01" +
                    " \u00a7f|\u00a7a \u79df:" + p.weeklyRent + "/\u5468" +
                    " \u00a7f|\u00a7b Lv." + p.requiredLevel + "+" +
                    " \u00a7f| " + (has ? "\u00a7a\u2714\u5df2\u6709" : "\u00a77\u672a\u8d2d\u4e70")), uuid);
        }
        player.sendMessage(new StringTextComponent("\u00a7a\u603b\u79df\u91d1\u6536\u5165: \u00a76" + totalRent + "\u91d1\u5e01/\u5468"), uuid);
        player.sendMessage(new StringTextComponent("\u00a7e/realestate buy <1-6> | /realestate sell <1-6>"), uuid);
    }
}
