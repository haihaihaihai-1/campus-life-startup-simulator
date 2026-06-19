package com.campus.systems;

import com.campus.CampusLife;
import com.campus.economy.MoneyCapability;
import com.campus.economy.SkillCapability;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;

/**
 * 导师系统拼图 - NPC导师提供buff加成
 * 参考: RPG导师/师傅系统
 */
@Mod.EventBusSubscriber(modid = CampusLife.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MentorSystem {

    public static class Mentor {
        public String name;
        public String title;
        public int consultationFee;
        public int expBoost;
        public int incomeBoost;
        public int durationTicks;
        public String effectDesc;

        public Mentor(String name, String title, int fee, int expBoost, int incomeBoost, int duration, String effect) {
            this.name = name;
            this.title = title;
            this.consultationFee = fee;
            this.expBoost = expBoost;
            this.incomeBoost = incomeBoost;
            this.durationTicks = duration;
            this.effectDesc = effect;
        }
    }

    public static final Mentor[] MENTORS = {
        new Mentor("\u9648\u6559\u6388", "\u521b\u4e1a\u5bfc\u5e08", 200, 50, 0, 12000, "\u521b\u4e1a\u7ecf\u9a8c+50%"),
        new Mentor("\u6797\u603b\u88c1", "\u4e92\u8054\u7f51\u524d\u8f88", 500, 100, 10, 12000, "\u7ecf\u9a8c+100% \u6536\u5165+10%"),
        new Mentor("\u5f20\u6559\u6388", "\u91d1\u878d\u5b66\u6559\u6388", 1000, 0, 25, 18000, "\u6536\u5165+25%"),
        new Mentor("\u5218\u9662\u58eb", "\u9662\u58eb/\u79d1\u5b66\u5bb6", 3000, 200, 20, 24000, "\u7ecf\u9a8c+200% \u6536\u5165+20%"),
        new Mentor("\u8d75\u4f01\u4e1a\u5bb6", "\u798f\u5e03\u65af\u5bcc\u8c6a", 10000, 300, 50, 36000, "\u7ecf\u9a8c+300% \u6536\u5165+50%")
    };

    private static final Map<UUID, MentorSession> activeSessions = new HashMap<>();

    public static class MentorSession {
        public Mentor mentor;
        public int remainingTicks;

        public MentorSession(Mentor mentor) {
            this.mentor = mentor;
            this.remainingTicks = mentor.durationTicks;
        }
    }

    public static void showMentors(ServerPlayerEntity player) {
        UUID uuid = player.getUUID();
        MentorSession session = activeSessions.get(uuid);

        player.sendMessage(new StringTextComponent("\u00a76\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7d\u2605 \u5bfc\u5e08\u540d\u5355  \u00a76\u2551"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u255a\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255d"), uuid);

        for (int i = 0; i < MENTORS.length; i++) {
            Mentor m = MENTORS[i];
            player.sendMessage(new StringTextComponent(
                    "\u00a7d[" + (i+1) + "] " + m.title + " " + m.name +
                    " \u00a7f|\u00a76 \u8d39\u7528:" + m.consultationFee +
                    " \u00a7f|\u00a7a " + m.effectDesc +
                    " \u00a7f|\u00a7b " + (m.durationTicks/1200) + "\u5206\u949f"), uuid);
        }

        if (session != null) {
            player.sendMessage(new StringTextComponent(
                    "\u00a7a\u2714 \u5f53\u524d\u5bfc\u5e08: " + session.mentor.title + " " + session.mentor.name), uuid);
            player.sendMessage(new StringTextComponent(
                    "\u00a7e\u5269\u4f59: " + (session.remainingTicks/1200) + " \u5206\u949f | " + session.mentor.effectDesc), uuid);
        } else {
            player.sendMessage(new StringTextComponent("\u00a7e\u8f93\u5165 /mentor <1-5> \u8bf7\u6559\u5bfc\u5e08"), uuid);
        }
    }

    public static boolean consultMentor(ServerPlayerEntity player, int mentorIdx) {
        UUID uuid = player.getUUID();
        if (mentorIdx < 1 || mentorIdx > MENTORS.length) {
            player.sendMessage(new StringTextComponent("\u00a7c\u65e0\u6548\u7f16\u53f7! (1-" + MENTORS.length + ")"), uuid);
            return false;
        }

        if (activeSessions.containsKey(uuid)) {
            player.sendMessage(new StringTextComponent("\u00a7c\u4f60\u5df2\u6709\u8fdb\u884c\u4e2d\u7684\u5bfc\u5e08\u8f85\u5bfc!"), uuid);
            return false;
        }

        Mentor mentor = MENTORS[mentorIdx - 1];
        return player.getCapability(MoneyCapability.MONEY_CAP).map(m -> {
            if (m.spendMoney(mentor.consultationFee)) {
                activeSessions.put(uuid, new MentorSession(mentor));

                // 给予药水效果作为buff可视化
                player.addEffect(new EffectInstance(Effects.LUCK, mentor.durationTicks, 0));
                if (mentor.incomeBoost > 0) {
                    player.addEffect(new EffectInstance(Effects.MOVEMENT_SPEED, mentor.durationTicks, 0));
                }

                player.sendMessage(new StringTextComponent(
                        "\u00a7a\u2714 \u5bfc\u5e08\u8f85\u5bfc\u5f00\u59cb! " + mentor.title + " " + mentor.name), uuid);
                player.sendMessage(new StringTextComponent(
                        "\u00a7e\u6548\u679c: " + mentor.effectDesc), uuid);
                player.sendMessage(new StringTextComponent(
                        "\u00a7e\u6301\u7eed: " + (mentor.durationTicks/1200) + " \u5206\u949f"), uuid);
                return true;
            }
            player.sendMessage(new StringTextComponent("\u00a7c\u8d44\u91d1\u4e0d\u8db3! \u9700\u8981 " + mentor.consultationFee + " \u91d1\u5e01"), uuid);
            return false;
        }).orElse(false);
    }

    public static int getExpBoost(UUID uuid) {
        MentorSession s = activeSessions.get(uuid);
        return s != null ? s.mentor.expBoost : 0;
    }

    public static int getIncomeBoost(UUID uuid) {
        MentorSession s = activeSessions.get(uuid);
        return s != null ? s.mentor.incomeBoost : 0;
    }

    public static void tickSessions() {
        Iterator<Map.Entry<UUID, MentorSession>> it = activeSessions.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<UUID, MentorSession> entry = it.next();
            entry.getValue().remainingTicks -= 20;
            if (entry.getValue().remainingTicks <= 0) {
                it.remove();
            }
        }
    }
}
