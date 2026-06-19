package com.campus.systems;

import com.campus.CampusLife;
import com.campus.economy.MoneyCapability;
import com.campus.economy.SkillCapability;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.*;

/**
 * 专利知识产权系统拼图 - 申请专利收版税
 * 参考: 知识产权商业化模型
 */
@Mod.EventBusSubscriber(modid = CampusLife.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PatentSystem {

    private static int tickCounter = 0;
    private static final Map<UUID, List<Patent>> patents = new HashMap<>();
    private static int patentCounter = 1000;

    public static class Patent {
        public int id;
        public String name;
        public String field;
        public int filingCost;
        public int royaltyRate;
        public int durationTicks;
        public int totalEarned;

        public Patent(String name, String field, int filingCost, int royaltyRate) {
            this.id = ++patentCounter;
            this.name = name;
            this.field = field;
            this.filingCost = filingCost;
            this.royaltyRate = royaltyRate;
            this.durationTicks = 72000; // 1小时
            this.totalEarned = 0;
        }
    }

    public static final String[] PATENT_FIELDS = {
        "\u667a\u80fd\u7a7f\u6234", "\u65e0\u4eba\u673a", "\u533a\u5757\u94fe", "AI\u7b97\u6cd5",
        "\u7eff\u8272\u80fd\u6e90", "\u751f\u7269\u533b\u836f", "\u6750\u6599\u79d1\u5b66", "\u7269\u6d41\u7cfb\u7edf"
    };

    public static final String[] PATENT_NAMES = {
        "\u8d85\u7ea7\u5145\u7535\u6280\u672f", "\u6298\u53e0\u5c4f\u5e55\u8bbe\u8ba1", "\u91cf\u5b50\u52a0\u5bc6\u65b9\u6cd5",
        "\u81ea\u52a8\u9a7e\u9a76\u7b97\u6cd5", "\u690d\u5165\u5f0f\u4f20\u611f\u5668", "\u7eb3\u7c73\u6750\u6599\u5408\u91d1",
        "\u5168\u81ea\u52a8\u751f\u4ea7\u7ebf", "\u667a\u80fd\u7269\u6d41\u7f51", "\u8d85\u5bfc\u4f53\u7ba1",
        "\u57fa\u56e0\u7f16\u8f91\u6280\u672f", "\u8fb9\u7f18\u8ba1\u7b97\u82af\u7247", "\u592a\u9633\u80fd\u50a8\u80fd"
    };

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        tickCounter++;

        // 每3分钟结算专利版税
        if (tickCounter % 3600 == 0 && ServerLifecycleHooks.getCurrentServer() != null) {
            for (ServerPlayerEntity player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
                UUID uuid = player.getUUID();
                List<Patent> list = patents.get(uuid);
                if (list == null || list.isEmpty()) continue;

                Iterator<Patent> it = list.iterator();
                int totalRoyalty = 0;
                while (it.hasNext()) {
                    Patent p = it.next();
                    p.durationTicks -= 3600;
                    if (p.durationTicks <= 0) {
                        it.remove();
                        player.sendMessage(new StringTextComponent(
                                "\u00a7e\u4e13\u5229\u300c" + p.name + "\u300d\u5df2\u5230\u671f"), uuid);
                        continue;
                    }
                    int royalty = p.royaltyRate + new Random().nextInt(p.royaltyRate);
                    totalRoyalty += royalty;
                    p.totalEarned += royalty;
                }

                if (totalRoyalty > 0) {
                    final int royalty = totalRoyalty;
                    player.getCapability(MoneyCapability.MONEY_CAP).ifPresent(m -> m.addMoney(royalty));
                    player.sendMessage(new StringTextComponent(
                            "\u00a7a\u2714 \u4e13\u5229\u7248\u7a0e\u6536\u5165: \u00a76" + royalty + " \u91d1\u5e01"), uuid);
                }
            }
        }
    }

    public static boolean filePatent(ServerPlayerEntity player) {
        UUID uuid = player.getUUID();
        int level = player.getCapability(SkillCapability.SKILL_CAP).map(SkillCapability.ISkill::getLevel).orElse(1);

        if (level < 5) {
            player.sendMessage(new StringTextComponent("\u00a7c\u9700\u8981Lv.5\u624d\u80fd\u7533\u8bf7\u4e13\u5229!"), uuid);
            return false;
        }

        List<Patent> list = patents.getOrDefault(uuid, new ArrayList<>());
        if (list.size() >= 5) {
            player.sendMessage(new StringTextComponent("\u00a7c\u6700\u591a\u540c\u65f6\u6301\u67095\u9879\u4e13\u5229!"), uuid);
            return false;
        }

        int cost = 1000 + level * 200;
        Random rand = new Random();
        String name = PATENT_NAMES[rand.nextInt(PATENT_NAMES.length)];
        String field = PATENT_FIELDS[rand.nextInt(PATENT_FIELDS.length)];
        int royalty = 50 + level * 20 + rand.nextInt(100);

        return player.getCapability(MoneyCapability.MONEY_CAP).map(m -> {
            if (m.spendMoney(cost)) {
                Patent p = new Patent(name, field, cost, royalty);
                list.add(p);
                patents.put(uuid, list);
                player.getCapability(SkillCapability.SKILL_CAP).ifPresent(s -> s.addExp(cost / 50));

                player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u4e13\u5229\u7533\u8bf7\u6210\u529f!"), uuid);
                player.sendMessage(new StringTextComponent("\u00a7e\u4e13\u5229\u53f7: #CL" + p.id), uuid);
                player.sendMessage(new StringTextComponent("\u00a7e\u540d\u79f0: \u00a7f" + p.name), uuid);
                player.sendMessage(new StringTextComponent("\u00a7e\u9886\u57df: \u00a7b" + p.field), uuid);
                player.sendMessage(new StringTextComponent("\u00a7e\u7533\u8bf7\u8d39: \u00a7c" + p.filingCost + " \u91d1\u5e01"), uuid);
                player.sendMessage(new StringTextComponent("\u00a7e\u7248\u7a0e\u7387: \u00a7a" + p.royaltyRate + "+ \u91d1\u5e01/\u5468"), uuid);
                player.sendMessage(new StringTextComponent("\u00a7e\u6709\u6548\u671f: \u00a7f1\u5c0f\u65f6"), uuid);
                return true;
            }
            player.sendMessage(new StringTextComponent("\u00a7c\u8d44\u91d1\u4e0d\u8db3! \u9700\u8981 " + cost + " \u91d1\u5e01"), uuid);
            return false;
        }).orElse(false);
    }

    public static int getPatentCount(UUID uuid) {
        List<Patent> list = patents.get(uuid);
        return list != null ? list.size() : 0;
    }

    public static void showPatents(ServerPlayerEntity player) {
        UUID uuid = player.getUUID();
        List<Patent> list = patents.getOrDefault(uuid, new ArrayList<>());

        player.sendMessage(new StringTextComponent("\u00a76\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7b\u2139 \u4e13\u5229\u77e5\u8bc6\u4ea7\u6743 (" + list.size() + "/5)  \u00a76\u2551"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u255a\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255d"), uuid);

        if (list.isEmpty()) {
            player.sendMessage(new StringTextComponent("\u00a77\u65e0\u4e13\u5229 | \u8f93\u5165 /patent file \u7533\u8bf7"), uuid);
        } else {
            for (Patent p : list) {
                player.sendMessage(new StringTextComponent(
                        "\u00a7e#CL" + p.id + " " + p.name +
                        " \u00a7f|\u00a7b " + p.field +
                        " \u00a7f|\u00a7a \u7248\u7a0e:" + p.royaltyRate + "+" +
                        " \u00a7f|\u00a76 \u5df2\u8d5a:" + p.totalEarned +
                        " \u00a7f|\u00a77 " + (p.durationTicks/1200) + "\u5206\u949f"), uuid);
            }
        }
        player.sendMessage(new StringTextComponent("\u00a7e\u8f93\u5165 /patent file \u7533\u8bf7\u65b0\u4e13\u5229 (Lv.5+)"), uuid);
    }
}
