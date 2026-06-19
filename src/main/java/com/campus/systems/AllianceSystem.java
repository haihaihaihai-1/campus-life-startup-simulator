package com.campus.systems;

import com.campus.CampusLife;
import com.campus.economy.MoneyCapability;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;

/**
 * 产业联盟系统 - 跨玩家合作
 */
@Mod.EventBusSubscriber(modid = CampusLife.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AllianceSystem {

    public static class Alliance {
        public String name;
        public UUID leader;
        public Set<UUID> members = new HashSet<>();
        public int allianceLevel;
        public int sharedFund;
        public String industry;

        public Alliance(String name, UUID leader, String industry) {
            this.name = name; this.leader = leader; this.industry = industry;
            this.members.add(leader); this.allianceLevel = 1; this.sharedFund = 0;
        }
    }

    private static final Map<String, Alliance> alliances = new HashMap<>();
    private static final Map<UUID, String> playerAlliances = new HashMap<>();

    public static boolean create(ServerPlayerEntity player, String name, String industry) {
        UUID uuid = player.getUUID();
        if (playerAlliances.containsKey(uuid)) { player.sendMessage(new StringTextComponent("\u00a7c\u5df2\u52a0\u5165\u8054\u76df!"), uuid); return false; }
        if (alliances.containsKey(name)) { player.sendMessage(new StringTextComponent("\u00a7c\u8054\u76df\u540d\u5df2\u5b58\u5728!"), uuid); return false; }
        alliances.put(name, new Alliance(name, uuid, industry));
        playerAlliances.put(uuid, name);
        player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u521b\u5efa\u4ea7\u4e1a\u8054\u76df: " + name + " [" + industry + "]"), uuid);
        return true;
    }

    public static boolean invite(ServerPlayerEntity leader, ServerPlayerEntity target) {
        String allyName = playerAlliances.get(leader.getUUID());
        if (allyName == null) { leader.sendMessage(new StringTextComponent("\u00a7c\u4f60\u6ca1\u6709\u8054\u76df!"), leader.getUUID()); return false; }
        Alliance ally = alliances.get(allyName);
        if (!ally.leader.equals(leader.getUUID())) { leader.sendMessage(new StringTextComponent("\u00a7c\u4ec5\u76df\u4e3b\u53ef\u9080\u8bf7!"), leader.getUUID()); return false; }
        if (playerAlliances.containsKey(target.getUUID())) { leader.sendMessage(new StringTextComponent("\u00a7c\u5bf9\u65b9\u5df2\u52a0\u5165\u8054\u76df!"), leader.getUUID()); return false; }

        ally.members.add(target.getUUID());
        playerAlliances.put(target.getUUID(), allyName);
        leader.sendMessage(new StringTextComponent("\u00a7a\u2714 \u5df2\u9080\u8bf7 " + target.getName().getString()), leader.getUUID());
        target.sendMessage(new StringTextComponent("\u00a7a\u4f60\u52a0\u5165\u4e86\u4ea7\u4e1a\u8054\u76df: " + allyName), target.getUUID());
        return true;
    }

    public static boolean contribute(ServerPlayerEntity player, int amount) {
        UUID uuid = player.getUUID();
        String name = playerAlliances.get(uuid);
        if (name == null) { player.sendMessage(new StringTextComponent("\u00a7c\u6ca1\u6709\u8054\u76df!"), uuid); return false; }
        Alliance ally = alliances.get(name);
        return player.getCapability(MoneyCapability.MONEY_CAP).map(m -> {
            if (m.spendMoney(amount)) {
                ally.sharedFund += amount;
                player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u6350\u732e " + amount + "\u91d1\u5e01 | \u8054\u76df\u57fa\u91d1: " + ally.sharedFund), uuid);
                return true;
            }
            player.sendMessage(new StringTextComponent("\u00a7c\u8d44\u91d1\u4e0d\u8db3!"), uuid); return false;
        }).orElse(false);
    }

    public static void show(ServerPlayerEntity player) {
        UUID uuid = player.getUUID();
        String name = playerAlliances.get(uuid);
        if (name == null) {
            player.sendMessage(new StringTextComponent("\u00a7e\u672a\u52a0\u5165\u8054\u76df | \u8f93\u5165 /alliance create <\u540d\u79f0> <\u884c\u4e1a>"), uuid);
            return;
        }
        Alliance ally = alliances.get(name);
        player.sendMessage(new StringTextComponent("\u00a76\u2500\u2500\u2500 \u4ea7\u4e1a\u8054\u76df \u2500\u2500\u2500"), uuid);
        player.sendMessage(new StringTextComponent("\u00a7e\u8054\u76df: \u00a7f" + ally.name + " [" + ally.industry + "]"), uuid);
        player.sendMessage(new StringTextComponent("\u00a7e\u6210\u5458: \u00a7b" + ally.members.size() + "\u4eba"), uuid);
        player.sendMessage(new StringTextComponent("\u00a7e\u8054\u76df\u7b49\u7ea7: \u00a7aLv." + ally.allianceLevel), uuid);
        player.sendMessage(new StringTextComponent("\u00a7e\u8054\u76df\u57fa\u91d1: \u00a76" + ally.sharedFund + "\u91d1\u5e01"), uuid);
        player.sendMessage(new StringTextComponent("\u00a7e/alliance invite <\u73a9\u5bb6> | /alliance contribute <\u91d1\u989d>"), uuid);
    }

    public static Set<UUID> getAllianceMembers(UUID uuid) {
        String name = playerAlliances.get(uuid);
        if (name == null) return Collections.emptySet();
        Alliance ally = alliances.get(name);
        return ally != null ? ally.members : Collections.emptySet();
    }
}
