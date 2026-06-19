package com.campus.systems;

import com.campus.CampusLife;
import com.campus.economy.MoneyCapability;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;

/**
 * 智能合约系统 - 自动执行协议
 */
@Mod.EventBusSubscriber(modid = CampusLife.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SmartContractSystem {

    private static final Map<UUID, List<Contract>> contracts = new HashMap<>();
    private static int tickCounter = 0;

    public enum Type {
        AUTO_PAYMENT("\u81ea\u52a8\u4ed8\u6b3e", 200, 100, 12000),
        AUTO_INVEST("\u81ea\u52a8\u6295\u8d44", 500, 200, 12000),
        AUTO_REINVEST("\u590d\u5229\u518d\u6295\u8d44", 1000, 500, 24000),
        AUTO_HEDGE("\u81ea\u52a8\u5957\u4fdd", 2000, 800, 36000);

        public String name; public int setupCost; public int perTickBenefit; public int duration;
        Type(String n, int c, int b, int d) { name=n; setupCost=c; perTickBenefit=b; duration=d; }
    }

    public static class Contract {
        public int typeIdx; public int remainingTicks;
        public Contract(int idx, int ticks) { typeIdx = idx; remainingTicks = ticks; }
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        tickCounter++;
        if (tickCounter % 1200 != 0) return;

        for (Map.Entry<UUID, List<Contract>> entry : contracts.entrySet()) {
            Iterator<Contract> it = entry.getValue().iterator();
            while (it.hasNext()) {
                Contract c = it.next();
                c.remainingTicks -= 1200;
                if (c.remainingTicks <= 0) { it.remove(); continue; }
            }
        }
    }

    public static boolean deploy(ServerPlayerEntity player, int typeIdx) {
        UUID uuid = player.getUUID();
        Type[] types = Type.values();
        if (typeIdx < 1 || typeIdx > types.length) return false;
        Type t = types[typeIdx - 1];
        return player.getCapability(MoneyCapability.MONEY_CAP).map(m -> {
            if (m.spendMoney(t.setupCost)) {
                contracts.computeIfAbsent(uuid, k -> new ArrayList<>()).add(new Contract(typeIdx - 1, t.duration));
                player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u90e8\u7f72\u667a\u80fd\u5408\u7ea6: " + t.name + " | \u6210\u672c:" + t.setupCost + " | \u6bcf\u5468+" + t.perTickBenefit), uuid);
                return true;
            }
            player.sendMessage(new StringTextComponent("\u00a7c\u8d44\u91d1\u4e0d\u8db3!"), uuid); return false;
        }).orElse(false);
    }

    public static int getBenefit(UUID uuid) {
        List<Contract> list = contracts.get(uuid);
        if (list == null) return 0;
        int total = 0;
        for (Contract c : list) total += Type.values()[c.typeIdx].perTickBenefit;
        return total;
    }

    public static void show(ServerPlayerEntity player) {
        UUID uuid = player.getUUID();
        List<Contract> list = contracts.getOrDefault(uuid, new ArrayList<>());
        player.sendMessage(new StringTextComponent("\u00a76\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7b\u2699 \u667a\u80fd\u5408\u7ea6 (" + list.size() + ")  \u00a76\u2551"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u255a\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255d"), uuid);
        Type[] types = Type.values();
        for (int i = 0; i < types.length; i++) {
            player.sendMessage(new StringTextComponent("\u00a7e[" + (i+1) + "] " + types[i].name + " \u00a7f|\u00a76 " + types[i].setupCost + "\u91d1\u5e01 \u00a7f|\u00a7a +" + types[i].perTickBenefit + "/\u5468 \u00a7f|\u00a7b " + (types[i].duration/1200) + "\u5206\u949f"), uuid);
        }
        player.sendMessage(new StringTextComponent("\u00a7a\u603b\u6536\u76ca: \u00a76" + getBenefit(uuid) + "\u91d1\u5e01/\u5468"), uuid);
        player.sendMessage(new StringTextComponent("\u00a7e/smart <1-4> \u90e8\u7f72"), uuid);
    }
}
