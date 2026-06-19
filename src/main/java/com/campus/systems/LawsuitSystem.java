package com.campus.systems;

import com.campus.CampusLife;
import com.campus.economy.MoneyCapability;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;

/**
 * 知识产权诉讼系统 - 专利维权
 */
@Mod.EventBusSubscriber(modid = CampusLife.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class LawsuitSystem {

    private static final Map<UUID, List<Lawsuit>> lawsuits = new HashMap<>();
    private static final Random RAND = new Random();

    public static class Lawsuit {
        public String target; public int claimAmount; public int legalFee;
        public int remainingTicks; public boolean resolved; public boolean won;

        public Lawsuit(String target, int claim, int fee) {
            this.target = target; this.claimAmount = claim; this.legalFee = fee;
            this.remainingTicks = 2400; this.resolved = false; this.won = false;
        }
    }

    public static boolean file(ServerPlayerEntity player, String target, int claimAmount) {
        UUID uuid = player.getUUID();
        int patentCount = PatentSystem.getPatentCount(uuid);
        if (patentCount == 0) { player.sendMessage(new StringTextComponent("\u00a7c\u9700\u8981\u81f3\u5c111\u9879\u4e13\u5229\u624d\u80fd\u8d77\u8bc9!"), uuid); return false; }

        int legalFee = claimAmount / 5;
        return player.getCapability(MoneyCapability.MONEY_CAP).map(m -> {
            if (m.spendMoney(legalFee)) {
                lawsuits.computeIfAbsent(uuid, k -> new ArrayList<>()).add(new Lawsuit(target, claimAmount, legalFee));
                player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u5df2\u63d0\u8d77\u8bc9\u8bbc! \u76ee\u6807:" + target + " | \u7d22\u8d54:" + claimAmount + " | \u5f8b\u5e08\u8d39:" + legalFee), uuid);
                return true;
            }
            player.sendMessage(new StringTextComponent("\u00a7c\u5f8b\u5e08\u8d39\u4e0d\u8db3! \u9700" + legalFee), uuid); return false;
        }).orElse(false);
    }

    public static void tickLawsuits() {
        for (Map.Entry<UUID, List<Lawsuit>> entry : lawsuits.entrySet()) {
            Iterator<Lawsuit> it = entry.getValue().iterator();
            while (it.hasNext()) {
                Lawsuit l = it.next();
                if (!l.resolved) {
                    l.remainingTicks -= 20;
                    if (l.remainingTicks <= 0) {
                        l.resolved = true;
                        l.won = RAND.nextDouble() > 0.4;
                    }
                } else {
                    it.remove();
                }
            }
        }
    }

    public static void show(ServerPlayerEntity player) {
        UUID uuid = player.getUUID();
        List<Lawsuit> list = lawsuits.getOrDefault(uuid, new ArrayList<>());
        player.sendMessage(new StringTextComponent("\u00a76\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7c\u2696 \u77e5\u8bc6\u4ea7\u6743\u8bc9\u8bbc (" + list.size() + ")  \u00a76\u2551"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u255a\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255d"), uuid);

        for (int i = 0; i < list.size(); i++) {
            Lawsuit l = list.get(i);
            String status = l.resolved ? (l.won ? "\u00a7a\u2714\u80dc\u8bc9" : "\u00a7c\u2718\u8d25\u8bc9") : "\u00a7e\u5ba1\u7406\u4e2d(" + (l.remainingTicks/1200) + "\u5206\u949f)";
            player.sendMessage(new StringTextComponent("\u00a7e[" + (i+1) + "] \u8bc9\u8bbc:" + l.target + " \u00a7f|\u00a76 \u7d22\u8d54:" + l.claimAmount + " \u00a7f| " + status), uuid);
        }
        player.sendMessage(new StringTextComponent("\u00a7e/lawsuit file <\u76ee\u6807> <\u91d1\u989d> \u63d0\u8d77\u8bc9\u8bbc"), uuid);
    }
}
