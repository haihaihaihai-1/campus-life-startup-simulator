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
 * 供应链金融系统 - 应收账款融资
 */
@Mod.EventBusSubscriber(modid = CampusLife.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SupplyChainFinanceSystem {

    private static final Map<UUID, List<Receivable>> receivables = new HashMap<>();
    private static final Map<UUID, Integer> creditLimit = new HashMap<>();
    private static int tickCounter = 0;

    public static class Receivable {
        public int amount; public int discountFee; public int remainingTicks;
        public Receivable(int amount) { this.amount = amount; this.discountFee = (int)(amount * 0.05); this.remainingTicks = 6000; }
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        tickCounter++;
        if (tickCounter % 6000 != 0) return;

        for (Map.Entry<UUID, List<Receivable>> entry : receivables.entrySet()) {
            Iterator<Receivable> it = entry.getValue().iterator();
            while (it.hasNext()) {
                it.next().remainingTicks -= 6000;
                if (it.next().remainingTicks <= 0) it.remove();
            }
        }
    }

    public static boolean finance(ServerPlayerEntity player, int amount) {
        UUID uuid = player.getUUID();
        int limit = creditLimit.getOrDefault(uuid, 5000);
        int currentFinanced = receivables.getOrDefault(uuid, new ArrayList<>()).stream().mapToInt(r -> r.amount).sum();
        if (currentFinanced + amount > limit) { player.sendMessage(new StringTextComponent("\u00a7c\u8d85\u51fa\u4fe1\u7528\u989d\u5ea6! \u53ef\u7528:" + (limit - currentFinanced)), uuid); return false; }

        int fee = (int)(amount * 0.05);
        return player.getCapability(MoneyCapability.MONEY_CAP).map(m -> {
            if (m.spendMoney(fee)) {
                receivables.computeIfAbsent(uuid, k -> new ArrayList<>()).add(new Receivable(amount));
                m.addMoney(amount);
                player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u5e94\u6536\u8d26\u6b3e\u878d\u8d44\u6210\u529f!"), uuid);
                player.sendMessage(new StringTextComponent("\u00a7e\u878d\u8d44\u989d:" + amount + " | \u8d34\u73b0\u8d39:" + fee + " | \u4fe1\u7528\u989d\u5ea6:" + (limit - currentFinanced - amount)), uuid);
                return true;
            }
            player.sendMessage(new StringTextComponent("\u00a7c\u8d34\u73b0\u8d39\u4e0d\u8db3!"), uuid); return false;
        }).orElse(false);
    }

    public static boolean upgradeCredit(ServerPlayerEntity player) {
        UUID uuid = player.getUUID();
        int current = creditLimit.getOrDefault(uuid, 5000);
        int cost = current / 2;
        return player.getCapability(MoneyCapability.MONEY_CAP).map(m -> {
            if (m.spendMoney(cost)) {
                creditLimit.put(uuid, current + 5000);
                player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u4fe1\u7528\u989d\u5ea6\u63d0\u5347! \u65b0\u989d\u5ea6:" + (current + 5000)), uuid);
                return true;
            }
            player.sendMessage(new StringTextComponent("\u00a7c\u8d44\u91d1\u4e0d\u8db3! \u9700" + cost), uuid); return false;
        }).orElse(false);
    }

    public static void show(ServerPlayerEntity player) {
        UUID uuid = player.getUUID();
        int limit = creditLimit.getOrDefault(uuid, 5000);
        int used = receivables.getOrDefault(uuid, new ArrayList<>()).stream().mapToInt(r -> r.amount).sum();
        player.sendMessage(new StringTextComponent("\u00a76\u2500\u2500\u2500 \u4f9b\u5e94\u94fe\u91d1\u878d \u2500\u2500\u2500"), uuid);
        player.sendMessage(new StringTextComponent("\u00a7e\u4fe1\u7528\u989d\u5ea6: \u00a76" + limit), uuid);
        player.sendMessage(new StringTextComponent("\u00a7e\u5df2\u7528\u989d\u5ea6: \u00a7c" + used), uuid);
        player.sendMessage(new StringTextComponent("\u00a7e\u53ef\u7528\u989d\u5ea6: \u00a7a" + (limit - used)), uuid);
        player.sendMessage(new StringTextComponent("\u00a7e\u8d34\u73b0\u8d39\u7387: 5%"), uuid);
        player.sendMessage(new StringTextComponent("\u00a7e/scf finance <\u91d1\u989d> | /scf upgrade"), uuid);
    }
}
