package com.campus.systems;

import com.campus.CampusLife;
import com.campus.economy.MoneyCapability;
import com.campus.economy.SkillCapability;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;

/**
 * 社交人脉系统 - 人脉值解锁机会
 */
@Mod.EventBusSubscriber(modid = CampusLife.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class NetworkSystem {

    private static final Map<UUID, Integer> networkPoints = new HashMap<>();
    private static final Map<UUID, Set<String>> unlockedContacts = new HashMap<>();

    public static final String[][] CONTACTS = {
        {"\u5b66\u751f\u4f1a\u4e3b\u5e2d", "50", "\u89e3\u9501\u6821\u56ed\u4efb\u52a1\u52a0\u6210"},
        {"\u521b\u4e1a\u793e\u793e\u957f", "200", "\u89e3\u9501\u6295\u8d44\u4eba\u6298\u6263"},
        {"\u9ad8\u7ba1\u5b66\u9662\u9662\u957f", "500", "\u89e3\u9501\u5bfc\u5e08\u6298\u6263"},
        {"\u91d1\u878d\u754c\u4eba\u58eb", "1000", "\u89e3\u9501\u8d37\u6b3e\u5229\u7387\u4e0b\u8c03"},
        {"\u653f\u5e9c\u5b98\u5458", "2000", "\u89e3\u9501\u8865\u8d34\u989d\u7ffb\u500d"},
        {"\u5a92\u4f53\u8bb0\u8005", "800", "\u89e3\u9501\u5e7f\u544a\u6548\u679c\u52a0\u500d"},
        {"\u98ce\u6295\u5408\u4f19\u4eba", "1500", "\u89e3\u9501\u878d\u8d44\u989d\u7ffb\u500d"},
        {"\u4e92\u8054\u7f51\u5927\u4f6c", "3000", "\u89e3\u9501\u79d1\u6280\u7814\u53d1\u6298\u6263"}
    };

    public static void addNetworkPoint(ServerPlayerEntity player, int amount) {
        UUID uuid = player.getUUID();
        networkPoints.put(uuid, networkPoints.getOrDefault(uuid, 0) + amount);
    }

    public static int getPoints(UUID uuid) { return networkPoints.getOrDefault(uuid, 0); }

    public static boolean buildConnection(ServerPlayerEntity player, int contactIdx) {
        UUID uuid = player.getUUID();
        if (contactIdx < 1 || contactIdx > CONTACTS.length) { player.sendMessage(new StringTextComponent("\u00a7c\u65e0\u6548\u7f16\u53f7!"), uuid); return false; }

        Set<String> unlocked = unlockedContacts.getOrDefault(uuid, new HashSet<>());
        String name = CONTACTS[contactIdx - 1][0];
        if (unlocked.contains(name)) { player.sendMessage(new StringTextComponent("\u00a7e\u5df2\u5efa\u7acb\u8054\u7cfb!"), uuid); return false; }

        int cost = Integer.parseInt(CONTACTS[contactIdx - 1][1]);
        int points = getPoints(uuid);
        if (points < cost) { player.sendMessage(new StringTextComponent("\u00a7c\u4eba\u8109\u503c\u4e0d\u8db3! \u9700" + cost + " | \u5f53\u524d: " + points), uuid); return false; }

        networkPoints.put(uuid, points - cost);
        unlocked.add(name);
        unlockedContacts.put(uuid, unlocked);
        player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u5efa\u7acb\u8054\u7cfb: " + name), uuid);
        player.sendMessage(new StringTextComponent("\u00a7e\u6548\u679c: " + CONTACTS[contactIdx - 1][2]), uuid);
        return true;
    }

    public static void show(ServerPlayerEntity player) {
        UUID uuid = player.getUUID();
        int points = getPoints(uuid);
        Set<String> unlocked = unlockedContacts.getOrDefault(uuid, new HashSet<>());

        player.sendMessage(new StringTextComponent("\u00a76\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7d\ud83e\udd1d \u793e\u4ea4\u4eba\u8109 | \u4eba\u8109\u503c: \u00a7b" + points + "  \u00a76\u2551"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u255a\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255d"), uuid);

        for (int i = 0; i < CONTACTS.length; i++) {
            String[] c = CONTACTS[i];
            boolean has = unlocked.contains(c[0]);
            String status = has ? "\u00a7a\u2714\u5df2\u5efa\u7acb" : "\u00a7e\u53ef\u5efa\u7acb";
            player.sendMessage(new StringTextComponent(
                    "\u00a7e[" + (i+1) + "] " + c[0] +
                    " \u00a7f|\u00a7b \u6d88\u8017:" + c[1] +
                    " \u00a7f|\u00a77 " + c[2] +
                    " \u00a7f| " + status), uuid);
        }
        player.sendMessage(new StringTextComponent("\u00a7e\u8f93\u5165 /network <1-8> \u5efa\u7acb\u8054\u7cfb"), uuid);
        player.sendMessage(new StringTextComponent("\u00a7e\u6295\u8d44/\u6d88\u8d39\u83b7\u5f97\u4eba\u8109\u503c"), uuid);
    }

    public static boolean hasContact(UUID uuid, String name) {
        return unlockedContacts.getOrDefault(uuid, new HashSet<>()).contains(name);
    }
}
