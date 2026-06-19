package com.campus.systems;

import com.campus.economy.MoneyCapability;
import com.campus.economy.SkillCapability;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.*;

/**
 * 排行榜系统拼图 - 富豪榜+等级榜+企业榜
 * 参考: 游戏排行榜系统
 */
public class LeaderboardSystem {

    public static void showLeaderboard(ServerPlayerEntity viewer, String type) {
        if (ServerLifecycleHooks.getCurrentServer() == null) return;

        List<ServerPlayerEntity> players = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers();
        if (players.isEmpty()) {
            viewer.sendMessage(new StringTextComponent("\u00a7e\u5f53\u524d\u65e0\u5728\u7ebf\u73a9\u5bb6"), viewer.getUUID());
            return;
        }

        List<Map.Entry<ServerPlayerEntity, Integer>> sorted = new ArrayList<>();

        for (ServerPlayerEntity p : players) {
            int value = 0;
            switch (type) {
                case "money":
                case "rich":
                    value = p.getCapability(MoneyCapability.MONEY_CAP).map(MoneyCapability.IMoney::getMoney).orElse(0);
                    break;
                case "level":
                case "skill":
                    value = p.getCapability(SkillCapability.SKILL_CAP).map(SkillCapability.ISkill::getLevel).orElse(1);
                    break;
                case "employees":
                case "company":
                    value = EmployeeSystem.calculateTotalEmployees(p.getUUID());
                    break;
                case "income":
                    value = EmployeeSystem.calculateTotalIncome(p.getUUID());
                    break;
                case "tech":
                    value = ResearchSystem.getUnlockedTechs(p.getUUID()).size();
                    break;
                default:
                    value = p.getCapability(MoneyCapability.MONEY_CAP).map(MoneyCapability.IMoney::getMoney).orElse(0);
                    type = "money";
            }
            sorted.add(new AbstractMap.SimpleEntry<>(p, value));
        }

        sorted.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        String[] titles = {"money", "rich", "\u5bcc\u8c6a\u699c", "level", "skill", "\u7b49\u7ea7\u699c", "employees", "company", "\u4f01\u4e1a\u699c", "income", "\u6536\u5165\u699c", "tech", "\u79d1\u6280\u699c"};
        String titleName = "\u5bcc\u8c6a\u699c";
        String unit = "\u91d1\u5e01";
        for (int i = 0; i < titles.length; i += 2) {
            if (titles[i].equals(type)) { titleName = titles[i+1]; break; }
        }
        if (type.equals("level") || type.equals("skill")) unit = "Lv";
        if (type.equals("employees") || type.equals("company")) unit = "\u4eba";
        if (type.equals("tech")) unit = "\u9879";

        viewer.sendMessage(new StringTextComponent("\u00a76\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557"), viewer.getUUID());
        viewer.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7e\u26a1 " + titleName + "  \u00a76\u2551"), viewer.getUUID());
        viewer.sendMessage(new StringTextComponent("\u00a76\u255a\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255d"), viewer.getUUID());

        int rank = 1;
        for (Map.Entry<ServerPlayerEntity, Integer> entry : sorted) {
            String medal;
            if (rank == 1) medal = "\u00a7e\u2756 No.1";
            else if (rank == 2) medal = "\u00a7f\u2756 No.2";
            else if (rank == 3) medal = "\u00a76\u2756 No.3";
            else medal = "\u00a77 No." + rank;

            viewer.sendMessage(new StringTextComponent(
                    medal + " \u00a7a" + entry.getKey().getName().getString() +
                    " \u00a7f|\u00a76 " + entry.getValue() + " " + unit), viewer.getUUID());
            rank++;
            if (rank > 10) break;
        }

        viewer.sendMessage(new StringTextComponent("\u00a7e\u8f93\u5165 /rank <money|level|company|income|tech> \u5207\u6362\u699c\u5355"), viewer.getUUID());
    }
}
