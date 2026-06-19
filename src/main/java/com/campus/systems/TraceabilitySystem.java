package com.campus.systems;

import com.campus.CampusLife;
import com.campus.economy.MoneyCapability;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;

/**
 * 产品溯源系统 - 质量保证+溢价
 */
@Mod.EventBusSubscriber(modid = CampusLife.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TraceabilitySystem {

    public enum Cert {
        BASIC("\u57fa\u7840\u6eaf\u6e90", 500, 5, 1),
        QUALITY("\u8d28\u91cf\u8ba4\u8bc1", 2000, 12, 3),
        PREMIUM("\u9ad8\u7aef\u8ba4\u8bc1", 5000, 20, 6),
        BLOCKCHAIN("\u533a\u5757\u94fe\u6eaf\u6e90", 15000, 35, 12),
        FULL_TRACE("\u5168\u94fe\u6761\u6eaf\u6e90", 50000, 50, 20);

        public String name; public int cost; public int premiumPercent; public int reqLevel;
        Cert(String n, int c, int p, int r) { name=n; cost=c; premiumPercent=p; reqLevel=r; }
    }

    private static final Map<UUID, Set<Cert>> certified = new HashMap<>();

    public static boolean certify(ServerPlayerEntity player, int idx) {
        UUID uuid = player.getUUID();
        Cert[] certs = Cert.values();
        if (idx < 1 || idx > certs.length) return false;
        Cert c = certs[idx - 1];
        Set<Cert> has = certified.getOrDefault(uuid, new HashSet<>());
        if (has.contains(c)) { player.sendMessage(new StringTextComponent("\u00a7c\u5df2\u8ba4\u8bc1!"), uuid); return false; }
        return player.getCapability(MoneyCapability.MONEY_CAP).map(m -> {
            if (m.spendMoney(c.cost)) {
                has.add(c); certified.put(uuid, has);
                player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u83b7\u5f97\u8ba4\u8bc1: " + c.name + " | \u4ea7\u54c1\u6ea2\u4ef7+" + c.premiumPercent + "%"), uuid);
                ReputationSystem.addReputation(uuid, 5);
                return true;
            }
            player.sendMessage(new StringTextComponent("\u00a7c\u8d44\u91d1\u4e0d\u8db3!"), uuid); return false;
        }).orElse(false);
    }

    public static void show(ServerPlayerEntity player) {
        UUID uuid = player.getUUID();
        Set<Cert> has = certified.getOrDefault(uuid, new HashSet<>());
        int totalPremium = 0;
        for (Cert c : has) totalPremium += c.premiumPercent;
        player.sendMessage(new StringTextComponent("\u00a76\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7a\ud83d\udd0d \u4ea7\u54c1\u6eaf\u6e90 (" + has.size() + "/" + Cert.values().length + ")  \u00a76\u2551"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u255a\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255d"), uuid);
        Cert[] certs = Cert.values();
        for (int i = 0; i < certs.length; i++) {
            Cert c = certs[i];
            boolean owned = has.contains(c);
            player.sendMessage(new StringTextComponent("\u00a7e[" + (i+1) + "] " + c.name + " \u00a7f|\u00a76 " + c.cost + "\u91d1\u5e01 \u00a7f|\u00a7a \u6ea2\u4ef7+" + c.premiumPercent + "% \u00a7f|\u00a7b Lv." + c.reqLevel + "+ \u00a7f| " + (owned ? "\u00a7a\u2714" : "\u00a77\u2716")), uuid);
        }
        player.sendMessage(new StringTextComponent("\u00a7a\u603b\u6ea2\u4ef7: \u00a76+" + totalPremium + "%"), uuid);
        player.sendMessage(new StringTextComponent("\u00a7e/trace <1-5> \u8ba4\u8bc1"), uuid);
    }

    public static int getPremium(UUID uuid) {
        Set<Cert> has = certified.getOrDefault(uuid, new HashSet<>());
        int total = 0;
        for (Cert c : has) total += c.premiumPercent;
        return total;
    }
}
