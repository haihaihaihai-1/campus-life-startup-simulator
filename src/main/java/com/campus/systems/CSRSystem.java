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
 * 社会责任系统 - 公益投入+品牌价值提升
 */
@Mod.EventBusSubscriber(modid = CampusLife.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CSRSystem {

    private static final Map<UUID, Integer> csrPoints = new HashMap<>();
    private static final Map<UUID, Integer> totalDonated = new HashMap<>();
    private static int tickCounter = 0;

    public enum Project {
        SCHOLARSHIP("\u5e0c\u671b\u5de5\u7a0b\u5956\u5b66\u91d1", 1000, 10),
        ENVIRONMENT("\u73af\u4fdd\u516c\u76ca", 2000, 15),
        POVERTY("\u6276\u8d2b\u9879\u76ee", 5000, 25),
        EDUCATION("\u6559\u80b2\u652f\u63f4", 10000, 40),
        MEDICAL("\u533b\u7597\u6350\u52a9", 20000, 60),
        DISASTER("\u707e\u540e\u91cd\u5efa", 50000, 100);

        public String name; public int cost; public int points;
        Project(String n, int c, int p) { name=n; cost=c; points=p; }
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        tickCounter++;
        if (tickCounter % 12000 == 0 && ServerLifecycleHooks.getCurrentServer() != null) {
            for (ServerPlayerEntity player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
                UUID uuid = player.getUUID();
                int points = csrPoints.getOrDefault(uuid, 0);
                if (points > 0) {
                    int bonus = points * 2;
                    player.getCapability(MoneyCapability.MONEY_CAP).ifPresent(m -> m.addMoney(bonus));
                    player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u793e\u4f1a\u8d23\u4efb\u56de\u62a5: +" + bonus + "\u91d1\u5e01 (CSR:" + points + ")"), uuid);
                }
            }
        }
    }

    public static boolean donate(ServerPlayerEntity player, int idx) {
        UUID uuid = player.getUUID();
        Project[] projects = Project.values();
        if (idx < 1 || idx > projects.length) return false;
        Project p = projects[idx - 1];
        return player.getCapability(MoneyCapability.MONEY_CAP).map(m -> {
            if (m.spendMoney(p.cost)) {
                csrPoints.merge(uuid, p.points, Integer::sum);
                totalDonated.merge(uuid, p.cost, Integer::sum);
                ReputationSystem.addReputation(uuid, p.points / 5);
                ESGSystem.invest(player, 2, p.points / 10);
                player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u516c\u76ca\u6350\u8d60: " + p.name + " | CSR+" + p.points + " | \u58f0\u8a89+" + (p.points/5)), uuid);
                return true;
            }
            player.sendMessage(new StringTextComponent("\u00a7c\u8d44\u91d1\u4e0d\u8db3!"), uuid); return false;
        }).orElse(false);
    }

    public static void show(ServerPlayerEntity player) {
        UUID uuid = player.getUUID();
        int points = csrPoints.getOrDefault(uuid, 0);
        int donated = totalDonated.getOrDefault(uuid, 0);
        player.sendMessage(new StringTextComponent("\u00a76\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7a\ud83c\udf0a \u793e\u4f1a\u8d23\u4efb (CSR:" + points + ")  \u00a76\u2551"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u255a\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255d"), uuid);
        player.sendMessage(new StringTextComponent("\u00a7e\u7d2f\u8ba1\u6350\u8d60: \u00a76" + donated + "\u91d1\u5e01"), uuid);
        player.sendMessage(new StringTextComponent("\u00a7eCSR\u79ef\u5206: \u00a7a" + points), uuid);
        Project[] projects = Project.values();
        for (int i = 0; i < projects.length; i++) {
            player.sendMessage(new StringTextComponent("\u00a7e[" + (i+1) + "] " + projects[i].name + " \u00a7f|\u00a76 " + projects[i].cost + "\u91d1\u5e01 \u00a7f|\u00a7a CSR+" + projects[i].points), uuid);
        }
        player.sendMessage(new StringTextComponent("\u00a7e/csr <1-6> \u6350\u8d60"), uuid);
    }

    public static int getPoints(UUID uuid) { return csrPoints.getOrDefault(uuid, 0); }
}
