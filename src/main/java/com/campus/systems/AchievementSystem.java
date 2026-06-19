package com.campus.systems;

import com.campus.CampusLife;
import com.campus.economy.MoneyCapability;
import com.campus.economy.SkillCapability;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;

/**
 * 成就里程碑系统拼图 - 创业成就追踪
 * 参考: Minecraft Advancement + RPG成就系统
 */
@Mod.EventBusSubscriber(modid = CampusLife.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AchievementSystem {

    public static class Achievement {
        public String id;
        public String name;
        public String description;
        public int reward;
        public String category;

        public Achievement(String id, String name, String desc, int reward, String category) {
            this.id = id;
            this.name = name;
            this.description = desc;
            this.reward = reward;
            this.category = category;
        }
    }

    public static final Achievement[] ACHIEVEMENTS = {
        new Achievement("first_money", "\u7b2c\u4e00\u6876\u91d1", "\u6311\u6218\u7ecf\u6d4e\u7cfb\u7edf", 50, "\u5165\u95e8"),
        new Achievement("level5", "\u5b66\u751f\u521b\u5ba2", "\u8fbe\u5230Lv.5", 100, "\u7b49\u7ea7"),
        new Achievement("level15", "\u521d\u9636\u521b\u4e1a\u8005", "\u8fbe\u5230Lv.15", 300, "\u7b49\u7ea7"),
        new Achievement("level30", "\u8d44\u6df1\u521b\u5ba2", "\u8fbe\u5230Lv.30", 800, "\u7b49\u7ea7"),
        new Achievement("level50", "\u521b\u4e1a\u5927\u5e08", "\u8fbe\u5230Lv.50", 2000, "\u7b49\u7ea7"),
        new Achievement("first_invest", "\u521d\u6b21\u6295\u8d44", "\u5b8c\u6210\u9996\u6b21\u521b\u4e1a\u6295\u8d44", 100, "\u7ecf\u6d4e"),
        new Achievement("team_created", "\u7ec4\u5efa\u56e2\u961f", "\u521b\u5efa\u81ea\u5df1\u7684\u56e2\u961f", 200, "\u793e\u4ea4"),
        new Achievement("loan_master", "\u8d37\u6b3e\u9ad8\u624b", "\u6210\u529f\u501f\u6b3e\u5e76\u8fd8\u6e05", 300, "\u91d1\u878d"),
        new Achievement("rich_1k", "\u5c0f\u6709\u8d44\u4ea7", "\u62e5\u67091000\u91d1\u5e01", 100, "\u8d22\u5bcc"),
        new Achievement("rich_10k", "\u4e2d\u4ea7\u9636\u7ea7", "\u62e5\u670910000\u91d1\u5e01", 500, "\u8d22\u5bcc"),
        new Achievement("rich_100k", "\u5bcc\u8c6a", "\u62e5\u6709100000\u91d1\u5e01", 5000, "\u8d22\u5bcc"),
        new Achievement("competition_win", "\u5927\u8d5b\u51a0\u519b", "\u8d62\u5f97\u521b\u4e1a\u5927\u8d5b", 1000, "\u7ade\u6280"),
        new Achievement("first_tech", "\u79d1\u6280\u542f\u822a", "\u89e3\u9501\u7b2c\u4e00\u4e2a\u6280\u672f", 150, "\u7814\u53d1"),
        new Achievement("all_tech", "\u79d1\u6280\u6811\u6ee1\u7ea7", "\u89e3\u9501\u5168\u90e8\u6280\u672f", 10000, "\u7814\u53d1"),
        new Achievement("employer", "\u4f01\u4e1a\u5bb6", "\u96c7\u4f63\u7b2c\u4e00\u4e2a\u5458\u5de5", 200, "\u7ecf\u8425"),
        new Achievement("mega_corp", "\u5546\u4e1a\u5e1d\u56fd", "\u96c7\u4f6310\u540d\u5458\u5de5", 2000, "\u7ecf\u8425")
    };

    private static final Map<UUID, Set<String>> unlocked = new HashMap<>();

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getPlayer() instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) event.getPlayer();
            checkAchievements(player);
        }
    }

    public static void checkAchievements(ServerPlayerEntity player) {
        UUID uuid = player.getUUID();
        Set<String> done = unlocked.getOrDefault(uuid, new HashSet<>());

        int money = player.getCapability(MoneyCapability.MONEY_CAP).map(MoneyCapability.IMoney::getMoney).orElse(0);
        int level = player.getCapability(SkillCapability.SKILL_CAP).map(SkillCapability.ISkill::getLevel).orElse(1);

        checkAndAward(player, "first_money", done);
        if (level >= 5) checkAndAward(player, "level5", done);
        if (level >= 15) checkAndAward(player, "level15", done);
        if (level >= 30) checkAndAward(player, "level30", done);
        if (level >= 50) checkAndAward(player, "level50", done);
        if (money >= 1000) checkAndAward(player, "rich_1k", done);
        if (money >= 10000) checkAndAward(player, "rich_10k", done);
        if (money >= 100000) checkAndAward(player, "rich_100k", done);
    }

    private static void checkAndAward(ServerPlayerEntity player, String id, Set<String> done) {
        if (done.contains(id)) return;

        final Achievement ach;
        Achievement found = null;
        for (Achievement a : ACHIEVEMENTS) {
            if (a.id.equals(id)) { found = a; break; }
        }
        ach = found;
        if (ach == null) return;

        done.add(id);
        unlocked.put(player.getUUID(), done);
        player.getCapability(MoneyCapability.MONEY_CAP).ifPresent(m -> m.addMoney(ach.reward));

        player.sendMessage(new StringTextComponent("\u00a76\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557"), player.getUUID());
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7e\u2605 \u6210\u5c31\u89e3\u9501!  \u00a76\u2551"), player.getUUID());
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7a" + ach.name + "  \u00a76\u2551"), player.getUUID());
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a77" + ach.description + "  \u00a76\u2551"), player.getUUID());
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a76\u5956\u52b1: " + ach.reward + " \u91d1\u5e01  \u00a76\u2551"), player.getUUID());
        player.sendMessage(new StringTextComponent("\u00a76\u255a\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255d"), player.getUUID());
    }

    public static void showAchievements(ServerPlayerEntity player) {
        UUID uuid = player.getUUID();
        Set<String> done = unlocked.getOrDefault(uuid, new HashSet<>());

        player.sendMessage(new StringTextComponent("\u00a76\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7e\u2605 \u521b\u4e1a\u6210\u5c31 (" + done.size() + "/" + ACHIEVEMENTS.length + ")  \u00a76\u2551"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u255a\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255d"), uuid);

        Map<String, List<Achievement>> byCategory = new LinkedHashMap<>();
        for (Achievement a : ACHIEVEMENTS) {
            byCategory.computeIfAbsent(a.category, k -> new ArrayList<>()).add(a);
        }

        for (Map.Entry<String, List<Achievement>> entry : byCategory.entrySet()) {
            player.sendMessage(new StringTextComponent("\u00a7e\u3010" + entry.getKey() + "\u3011"), uuid);
            for (Achievement a : entry.getValue()) {
                boolean has = done.contains(a.id);
                String icon = has ? "\u00a7a\u2714" : "\u00a77\u2716";
                String color = has ? "\u00a7a" : "\u00a77";
                player.sendMessage(new StringTextComponent(
                        "  " + icon + " " + color + a.name +
                        " \u00a7f|\u00a76 " + a.reward + "\u91d1\u5e01" +
                        " \u00a7f|\u00a77 " + a.description), uuid);
            }
        }
    }

    public static void awardManual(ServerPlayerEntity player, String id) {
        Set<String> done = unlocked.getOrDefault(player.getUUID(), new HashSet<>());
        checkAndAward(player, id, done);
    }
}
