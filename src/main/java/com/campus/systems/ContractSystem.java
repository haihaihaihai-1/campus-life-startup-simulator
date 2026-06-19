package com.campus.systems;

import com.campus.CampusLife;
import com.campus.economy.MoneyCapability;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;

/**
 * 供应合同系统 - 长期供应协议+折扣
 */
@Mod.EventBusSubscriber(modid = CampusLife.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ContractSystem {

    public static class Supplier {
        public String name, material;
        public int contractFee, discountPercent, durationTicks, requiredLevel;

        public Supplier(String name, String material, int fee, int discount, int duration, int reqLvl) {
            this.name = name; this.material = material; this.contractFee = fee;
            this.discountPercent = discount; this.durationTicks = duration; this.requiredLevel = reqLvl;
        }
    }

    public static final Supplier[] SUPPLIERS = {
        new Supplier("\u672c\u5730\u519c\u573a", "raw_material", 500, 10, 72000, 1),
        new Supplier("\u57ce\u5e02\u6279\u53d1\u5546", "raw_material", 1500, 20, 72000, 3),
        new Supplier("\u8fdb\u53e3\u4f9b\u5e94\u5546", "innovation_chip", 3000, 15, 72000, 5),
        new Supplier("\u5de5\u5382\u76f4\u4f9b", "innovation_chip", 5000, 25, 72000, 8),
        new Supplier("\u5168\u7403\u4f9b\u5e94\u94fe", "\u5168\u7c7b\u578b", 15000, 35, 144000, 15)
    };

    private static final Map<UUID, ActiveContract> contracts = new HashMap<>();

    public static class ActiveContract {
        public int supplierIdx; public int remainingTicks;
        public ActiveContract(int idx, int ticks) { supplierIdx = idx; remainingTicks = ticks; }
    }

    public static boolean sign(ServerPlayerEntity player, int idx) {
        UUID uuid = player.getUUID();
        if (idx < 1 || idx > SUPPLIERS.length) { player.sendMessage(new StringTextComponent("\u00a7c\u65e0\u6548\u7f16\u53f7!"), uuid); return false; }
        if (contracts.containsKey(uuid)) { player.sendMessage(new StringTextComponent("\u00a7c\u5df2\u6709\u5408\u540c!"), uuid); return false; }
        Supplier s = SUPPLIERS[idx - 1];

        return player.getCapability(MoneyCapability.MONEY_CAP).map(m -> {
            if (m.spendMoney(s.contractFee)) {
                contracts.put(uuid, new ActiveContract(idx - 1, s.durationTicks));
                player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u7b7e\u7f72\u5408\u540c! " + s.name), uuid);
                player.sendMessage(new StringTextComponent("\u00a7e\u6750\u6599: " + s.material + " | \u6298\u6263: -" + s.discountPercent + "% | \u671f\u9650: " + (s.durationTicks/1200) + "\u5206\u949f"), uuid);
                return true;
            }
            player.sendMessage(new StringTextComponent("\u00a7c\u8d44\u91d1\u4e0d\u8db3! \u9700" + s.contractFee), uuid); return false;
        }).orElse(false);
    }

    public static int getDiscount(UUID uuid) {
        ActiveContract c = contracts.get(uuid);
        return c != null ? SUPPLIERS[c.supplierIdx].discountPercent : 0;
    }

    public static void show(ServerPlayerEntity player) {
        UUID uuid = player.getUUID();
        player.sendMessage(new StringTextComponent("\u00a76\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7e\ud83d\udcdc \u4f9b\u5e94\u5408\u540c  \u00a76\u2551"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u255a\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255d"), uuid);

        for (int i = 0; i < SUPPLIERS.length; i++) {
            Supplier s = SUPPLIERS[i];
            player.sendMessage(new StringTextComponent(
                    "\u00a7e[" + (i+1) + "] " + s.name +
                    " \u00a7f|\u00a77 " + s.material +
                    " \u00a7f|\u00a76 " + s.contractFee + "\u91d1\u5e01" +
                    " \u00a7f|\u00a7a \u6298\u6263-" + s.discountPercent + "%" +
                    " \u00a7f|\u00a7b Lv." + s.requiredLevel + "+"), uuid);
        }

        ActiveContract c = contracts.get(uuid);
        if (c != null) {
            player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u5f53\u524d: " + SUPPLIERS[c.supplierIdx].name + " | \u6298\u6263:-" + SUPPLIERS[c.supplierIdx].discountPercent + "% | \u5269\u4f59:" + (c.remainingTicks/1200) + "\u5206\u949f"), uuid);
        }
        player.sendMessage(new StringTextComponent("\u00a7e\u8f93\u5165 /contract <1-5> \u7b7e\u7f72"), uuid);
    }

    public static void tickContracts() {
        Iterator<Map.Entry<UUID, ActiveContract>> it = contracts.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<UUID, ActiveContract> e = it.next();
            e.getValue().remainingTicks -= 20;
            if (e.getValue().remainingTicks <= 0) it.remove();
        }
    }
}
