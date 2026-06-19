package com.campus.systems;

import com.campus.CampusLife;
import com.campus.economy.MoneyCapability;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;

/**
 * 保险系统 - 购买保险抵御随机损失
 */
@Mod.EventBusSubscriber(modid = CampusLife.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class InsuranceSystem {

    public enum InsuranceType {
        PROPERTY("\u8d22\u4ea7\u4fdd\u9669", 200, 0.3, 72000),
        BUSINESS("\u8425\u4e1a\u4fdd\u9669", 500, 0.5, 72000),
        LIABILITY("\u8d23\u4efb\u4fdd\u9669", 1000, 0.7, 72000),
        COMPREHENSIVE("\u7efc\u5408\u4fdd\u9669", 3000, 1.0, 144000);

        public String name; public int premium; public double coverage; public int duration;
        InsuranceType(String n, int p, double c, int d) { name=n; premium=p; coverage=c; duration=d; }
    }

    private static final Map<UUID, Map<InsuranceType, Long>> policies = new HashMap<>();

    public static boolean buy(ServerPlayerEntity player, int idx) {
        UUID uuid = player.getUUID();
        InsuranceType[] types = InsuranceType.values();
        if (idx < 1 || idx > types.length) { player.sendMessage(new StringTextComponent("\u00a7c\u65e0\u654f\u7c7b\u578b!"), uuid); return false; }
        InsuranceType type = types[idx - 1];
        Map<InsuranceType, Long> existing = policies.getOrDefault(uuid, new HashMap<>());
        if (existing.containsKey(type) && existing.get(type) > System.currentTimeMillis()) {
            player.sendMessage(new StringTextComponent("\u00a7e\u5df2\u6709\u6709\u6548\u4fdd\u9669!"), uuid); return false;
        }
        return player.getCapability(MoneyCapability.MONEY_CAP).map(m -> {
            if (m.spendMoney(type.premium)) {
                existing.put(type, System.currentTimeMillis() + (type.duration * 50L));
                policies.put(uuid, existing);
                player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u8d2d\u4e70" + type.name + "! \u8d54\u8d39:" + type.premium + " | \u8d54\u7387:" + (int)(type.coverage*100) + "%"), uuid);
                return true;
            }
            player.sendMessage(new StringTextComponent("\u00a7c\u8d44\u91d1\u4e0d\u8db3!"), uuid); return false;
        }).orElse(false);
    }

    public static int claim(ServerPlayerEntity player, int lossAmount) {
        UUID uuid = player.getUUID();
        Map<InsuranceType, Long> existing = policies.get(uuid);
        if (existing == null) return 0;
        int totalClaim = 0;
        for (Map.Entry<InsuranceType, Long> e : existing.entrySet()) {
            if (e.getValue() > System.currentTimeMillis()) {
                totalClaim += (int)(lossAmount * e.getKey().coverage);
            }
        }
        if (totalClaim > 0) {
            final int claim = totalClaim;
            player.getCapability(MoneyCapability.MONEY_CAP).ifPresent(m -> m.addMoney(claim));
            player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u4fdd\u9669\u7406\u8d54: " + claim + "\u91d1\u5e01"), uuid);
        }
        return totalClaim;
    }

    public static void show(ServerPlayerEntity player) {
        UUID uuid = player.getUUID();
        Map<InsuranceType, Long> existing = policies.getOrDefault(uuid, new HashMap<>());
        player.sendMessage(new StringTextComponent("\u00a76\u2500\u2500\u2500 \u4fdd\u9669\u7cfb\u7edf \u2500\u2500\u2500"), uuid);
        InsuranceType[] types = InsuranceType.values();
        for (int i = 0; i < types.length; i++) {
            InsuranceType t = types[i];
            boolean active = existing.containsKey(t) && existing.get(t) > System.currentTimeMillis();
            String status = active ? "\u00a7a\u2714\u751f\u6548\u4e2d" : "\u00a7c\u672a\u8d2d\u4e70";
            player.sendMessage(new StringTextComponent("\u00a7e[" + (i+1) + "] " + t.name + " \u00a7f|\u00a76 " + t.premium + "\u91d1\u5e01 \u00a7f|\u00a7a \u8d54\u7387:" + (int)(t.coverage*100) + "% \u00a7f| " + status), uuid);
        }
        player.sendMessage(new StringTextComponent("\u00a7e\u8f93\u5165 /insurance <1-4> \u8d2d\u4e70"), uuid);
    }
}
