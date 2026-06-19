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
 * 培训教育系统 - 课程提升永久属性
 */
@Mod.EventBusSubscriber(modid = CampusLife.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TrainingSystem {

    public static class Course {
        public String id, name, description;
        public int cost, requiredLevel;
        public int expReward, permanentBoost;
        public String boostType;

        public Course(String id, String name, String desc, int cost, int reqLvl, int exp, int boost, String boostType) {
            this.id = id; this.name = name; this.description = desc; this.cost = cost;
            this.requiredLevel = reqLvl; this.expReward = exp; this.permanentBoost = boost; this.boostType = boostType;
        }
    }

    public static final Course[] COURSES = {
        new Course("biz_basics", "\u521b\u4e1a\u57fa\u7840", "\u521b\u4e1a\u5165\u95e8\u8bfe\u7a0b", 300, 1, 100, 5, "income"),
        new Course("marketing_101", "\u5e02\u573a\u8425\u9500", "\u8425\u9500\u57fa\u7840", 800, 3, 200, 10, "income"),
        new Course("finance_mgmt", "\u8d22\u52a1\u7ba1\u7406", "\u8d22\u52a1\u77e5\u8bc6", 1500, 5, 300, 15, "income"),
        new Course("leadership", "\u9886\u5bfc\u529b\u57f9\u8bad", "\u7ba1\u7406\u80fd\u529b\u63d0\u5347", 3000, 8, 500, 20, "income"),
        new Course("tech_advanced", "\u9ad8\u7ea7\u6280\u672f", "\u7814\u53d1\u80fd\u529b\u63d0\u5347", 5000, 12, 800, 25, "exp"),
        new Course("mba", "MBA\u8bfe\u7a0b", "\u5546\u5b66\u9662\u8bfe\u7a0b", 15000, 20, 2000, 50, "income"),
        new Course("phd", "\u535a\u58eb\u8bfe\u7a0b", "\u5b66\u672f\u6df1\u9020", 30000, 35, 3000, 100, "exp")
    };

    private static final Map<UUID, Set<String>> completed = new HashMap<>();
    private static final Map<UUID, Integer> totalIncomeBoost = new HashMap<>();
    private static final Map<UUID, Integer> totalExpBoost = new HashMap<>();

    public static void show(ServerPlayerEntity player) {
        UUID uuid = player.getUUID();
        Set<String> done = completed.getOrDefault(uuid, new HashSet<>());
        int level = player.getCapability(SkillCapability.SKILL_CAP).map(SkillCapability.ISkill::getLevel).orElse(1);
        int incomeBoost = totalIncomeBoost.getOrDefault(uuid, 0);
        int expBoost = totalExpBoost.getOrDefault(uuid, 0);

        player.sendMessage(new StringTextComponent("\u00a76\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7e\ud83c\udf93 \u57f9\u8bad\u6559\u80b2  \u00a76\u2551"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7a\u6c38\u4e45\u52a0\u6210: \u00a7a\u6536\u5165+" + incomeBoost + "% \u00a7b\u7ecf\u9a8c+" + expBoost + "%  \u00a76\u2551"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u255a\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255d"), uuid);

        for (int i = 0; i < COURSES.length; i++) {
            Course c = COURSES[i];
            boolean has = done.contains(c.id);
            boolean canTake = !has && level >= c.requiredLevel;
            String status = has ? "\u00a7a\u2714\u5df2\u5b8c\u6210" : (canTake ? "\u00a7e\u53ef\u5b66\u4e60" : "\u00a7c\u672a\u8fbe\u6807");
            String boostDesc = c.boostType.equals("income") ? "\u00a7a\u6536\u5165+" + c.permanentBoost + "%" : "\u00a7b\u7ecf\u9a8c+" + c.permanentBoost + "%";
            player.sendMessage(new StringTextComponent(
                    "\u00a7e[" + (i+1) + "] " + c.name +
                    " \u00a7f|\u00a76 " + c.cost + "\u91d1\u5e01" +
                    " \u00a7f|\u00a7b Lv." + c.requiredLevel + "+" +
                    " \u00a7f| " + boostDesc +
                    " \u00a7f| " + status), uuid);
        }
        player.sendMessage(new StringTextComponent("\u00a7e\u8f93\u5165 /train <1-7> \u5b66\u4e60\u8bfe\u7a0b"), uuid);
    }

    public static boolean enroll(ServerPlayerEntity player, int idx) {
        UUID uuid = player.getUUID();
        if (idx < 1 || idx > COURSES.length) { player.sendMessage(new StringTextComponent("\u00a7c\u65e0\u6548\u7f16\u53f7!"), uuid); return false; }

        Course c = COURSES[idx - 1];
        Set<String> done = completed.getOrDefault(uuid, new HashSet<>());
        if (done.contains(c.id)) { player.sendMessage(new StringTextComponent("\u00a7c\u5df2\u5b66\u4e60\u8fc7!"), uuid); return false; }

        int level = player.getCapability(SkillCapability.SKILL_CAP).map(SkillCapability.ISkill::getLevel).orElse(1);
        if (level < c.requiredLevel) { player.sendMessage(new StringTextComponent("\u00a7c\u9700Lv." + c.requiredLevel), uuid); return false; }

        return player.getCapability(MoneyCapability.MONEY_CAP).map(m -> {
            if (m.spendMoney(c.cost)) {
                done.add(c.id);
                completed.put(uuid, done);
                player.getCapability(SkillCapability.SKILL_CAP).ifPresent(s -> s.addExp(c.expReward));

                if (c.boostType.equals("income")) {
                    totalIncomeBoost.put(uuid, totalIncomeBoost.getOrDefault(uuid, 0) + c.permanentBoost);
                } else {
                    totalExpBoost.put(uuid, totalExpBoost.getOrDefault(uuid, 0) + c.permanentBoost);
                }

                player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u8bfe\u7a0b\u5b8c\u6210! " + c.name), uuid);
                player.sendMessage(new StringTextComponent("\u00a7e\u83b7\u5f97: \u00a7b" + c.expReward + "\u7ecf\u9a8c + \u00a7a\u6c38\u4e45" +
                    (c.boostType.equals("income") ? "\u6536\u5165+" : "\u7ecf\u9a8c+") + c.permanentBoost + "%"), uuid);
                return true;
            }
            player.sendMessage(new StringTextComponent("\u00a7c\u8d44\u91d1\u4e0d\u8db3! \u9700" + c.cost + "\u91d1\u5e01"), uuid);
            return false;
        }).orElse(false);
    }

    public static int getIncomeBoost(UUID uuid) { return totalIncomeBoost.getOrDefault(uuid, 0); }
    public static int getExpBoost(UUID uuid) { return totalExpBoost.getOrDefault(uuid, 0); }
}
