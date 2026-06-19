package com.campus.systems;

import com.campus.CampusLife;
import com.campus.economy.MoneyCapability;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.*;

/**
 * 供应链系统 - 原材料→半成品→成品生产链
 */
@Mod.EventBusSubscriber(modid = CampusLife.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SupplyChainSystem {

    public static class ProductionLine {
        public String input, output, name;
        public int inputQty, outputQty, processingTime, cost, sellPrice;

        public ProductionLine(String name, String in, int inQ, String out, int outQ, int time, int cost, int sell) {
            this.name = name; this.input = in; this.inputQty = inQ; this.output = out; this.outputQty = outQ;
            this.processingTime = time; this.cost = cost; this.sellPrice = sell;
        }
    }

    public static final ProductionLine[] LINES = {
        new ProductionLine("\u5496\u5561\u751f\u4ea7\u7ebf", "raw_material", 2, "coffee_cup", 1, 1200, 30, 50),
        new ProductionLine("\u624b\u5de5\u827a\u54c1\u7ebf", "raw_material", 3, "handmade_craft", 1, 1800, 50, 80),
        new ProductionLine("\u79d1\u6280\u7ec4\u88c5\u7ebf", "innovation_chip", 2, "tech_gadget", 1, 3600, 200, 200),
        new ProductionLine("\u521b\u4e1a\u5957\u88c5\u7ec4\u88c5", "business_plan", 1, "startup_kit", 1, 2400, 100, 500)
    };

    private static final Map<UUID, ActiveLine> active = new HashMap<>();
    private static int tickCounter = 0;

    public static class ActiveLine {
        public int lineIdx;
        public int remainingTicks;
        public int batches;

        public ActiveLine(int idx, int batches) {
            this.lineIdx = idx; this.batches = batches;
            this.remainingTicks = LINES[idx].processingTime * batches;
        }
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        tickCounter++;

        if (tickCounter % 20 == 0 && ServerLifecycleHooks.getCurrentServer() != null) {
            for (ServerPlayerEntity player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
                UUID uuid = player.getUUID();
                ActiveLine line = active.get(uuid);
                if (line == null) continue;

                line.remainingTicks -= 20;
                if (line.remainingTicks <= 0) {
                    ProductionLine pl = LINES[line.lineIdx];
                    int revenue = pl.sellPrice * pl.outputQty * line.batches;
                    player.getCapability(MoneyCapability.MONEY_CAP).ifPresent(m -> m.addMoney(revenue));
                    player.sendMessage(new StringTextComponent(
                            "\u00a7a\u2714 \u751f\u4ea7\u5b8c\u6210! " + pl.name + " x" + (pl.outputQty * line.batches)), uuid);
                    player.sendMessage(new StringTextComponent(
                            "\u00a7e\u9500\u552e\u6536\u5165: \u00a76" + revenue + " \u91d1\u5e01"), uuid);
                    active.remove(uuid);
                }
            }
        }
    }

    public static boolean startProduction(ServerPlayerEntity player, int lineIdx, int batches) {
        UUID uuid = player.getUUID();
        if (lineIdx < 1 || lineIdx > LINES.length) {
            player.sendMessage(new StringTextComponent("\u00a7c\u65e0\u6548\u751f\u4ea7\u7ebf! (1-" + LINES.length + ")"), uuid);
            return false;
        }
        if (active.containsKey(uuid)) {
            player.sendMessage(new StringTextComponent("\u00a7c\u5df2\u6709\u751f\u4ea7\u8fdb\u884c\u4e2d!"), uuid);
            return false;
        }
        if (batches < 1 || batches > 20) {
            player.sendMessage(new StringTextComponent("\u00a7c\u6279\u6b21\u6570: 1-20"), uuid);
            return false;
        }

        ProductionLine pl = LINES[lineIdx - 1];
        int totalCost = pl.cost * batches;

        return player.getCapability(MoneyCapability.MONEY_CAP).map(m -> {
            if (m.spendMoney(totalCost)) {
                active.put(uuid, new ActiveLine(lineIdx - 1, batches));
                int timeMin = (pl.processingTime * batches) / 1200;
                player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u751f\u4ea7\u5f00\u59cb! " + pl.name), uuid);
                player.sendMessage(new StringTextComponent("\u00a7e\u6295\u5165: " + pl.input + " x" + (pl.inputQty * batches) + " + " + totalCost + "\u91d1\u5e01"), uuid);
                player.sendMessage(new StringTextComponent("\u00a7e\u4ea7\u51fa: " + pl.output + " x" + (pl.outputQty * batches)), uuid);
                player.sendMessage(new StringTextComponent("\u00a7e\u9884\u8ba1\u65f6\u95f4: " + timeMin + "\u5206\u949f"), uuid);
                return true;
            }
            player.sendMessage(new StringTextComponent("\u00a7c\u8d44\u91d1\u4e0d\u8db3! \u9700" + totalCost + "\u91d1\u5e01"), uuid);
            return false;
        }).orElse(false);
    }

    public static void showLines(ServerPlayerEntity player) {
        UUID uuid = player.getUUID();
        player.sendMessage(new StringTextComponent("\u00a76\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7e\ud83d\udd27 \u4f9b\u5e94\u94fe\u751f\u4ea7  \u00a76\u2551"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u255a\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255d"), uuid);

        for (int i = 0; i < LINES.length; i++) {
            ProductionLine pl = LINES[i];
            player.sendMessage(new StringTextComponent(
                    "\u00a7e[" + (i+1) + "] " + pl.name +
                    " \u00a7f|\u00a77 " + pl.input + "x" + pl.inputQty + " \u2192 " + pl.output + "x" + pl.outputQty +
                    " \u00a7f|\u00a7c \u6210\u672c:" + pl.cost +
                    " \u00a7f|\u00a7a \u552e\u4ef7:" + pl.sellPrice +
                    " \u00a7f|\u00a7b " + (pl.processingTime/1200) + "\u5206\u949f"), uuid);
        }

        ActiveLine al = active.get(uuid);
        if (al != null) {
            ProductionLine pl = LINES[al.lineIdx];
            player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u751f\u4ea7\u4e2d: " + pl.name + " x" + (pl.outputQty * al.batches) + " | \u5269\u4f59: " + (al.remainingTicks/1200) + "\u5206\u949f"), uuid);
        } else {
            player.sendMessage(new StringTextComponent("\u00a7e\u8f93\u5165 /produce <\u7ebf1-4> <\u6279\u6b21> \u5f00\u59cb\u751f\u4ea7"), uuid);
        }
    }
}
