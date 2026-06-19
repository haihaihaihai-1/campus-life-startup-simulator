package com.campus.systems;

import com.campus.CampusLife;
import com.campus.economy.MoneyCapability;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;

/**
 * 团队/公司系统拼图 - 组队创业+共享资金
 * 参考: 公会系统模式
 */
@Mod.EventBusSubscriber(modid = CampusLife.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TeamSystem {

    private static final Map<String, Team> teams = new HashMap<>();
    private static final Map<UUID, String> playerTeams = new HashMap<>();

    public static class Team {
        public String name;
        public UUID leader;
        public Set<UUID> members = new HashSet<>();
        public int teamFunds;
        public int level;
        public String companyName;

        public Team(String name, UUID leader) {
            this.name = name;
            this.leader = leader;
            this.members.add(leader);
            this.teamFunds = 0;
            this.level = 1;
            this.companyName = "\u672a\u547d\u540d\u521b\u4e1a\u516c\u53f8";
        }
    }

    public static boolean createTeam(ServerPlayerEntity player, String teamName) {
        UUID uuid = player.getUUID();
        if (playerTeams.containsKey(uuid)) {
            player.sendMessage(new StringTextComponent("\u00a7c\u4f60\u5df2\u52a0\u5165\u5176\u4ed6\u56e2\u961f!"), uuid);
            return false;
        }
        if (teams.containsKey(teamName)) {
            player.sendMessage(new StringTextComponent("\u00a7c\u56e2\u961f\u540d\u79f0\u5df2\u5b58\u5728!"), uuid);
            return false;
        }
        teams.put(teamName, new Team(teamName, uuid));
        playerTeams.put(uuid, teamName);
        player.sendMessage(new StringTextComponent(
                "\u00a7a\u2714 \u521b\u5efa\u56e2\u961f\u6210\u529f! \u56e2\u961f: \u00a76" + teamName), uuid);
        return true;
    }

    public static boolean inviteMember(ServerPlayerEntity leader, ServerPlayerEntity target, String teamName) {
        Team team = teams.get(teamName);
        if (team == null || !team.leader.equals(leader.getUUID())) {
            leader.sendMessage(new StringTextComponent("\u00a7c\u65e0\u6743\u64cd\u4f5c!"), leader.getUUID());
            return false;
        }
        UUID targetUuid = target.getUUID();
        if (playerTeams.containsKey(targetUuid)) {
            leader.sendMessage(new StringTextComponent("\u00a7c\u8be5\u73a9\u5bb6\u5df2\u52a0\u5165\u5176\u4ed6\u56e2\u961f!"), leader.getUUID());
            return false;
        }
        team.members.add(targetUuid);
        playerTeams.put(targetUuid, teamName);
        leader.sendMessage(new StringTextComponent(
                "\u00a7a\u2714 \u5df2\u9080\u8bf7 " + target.getName().getString() + " \u52a0\u5165\u56e2\u961f!"), leader.getUUID());
        target.sendMessage(new StringTextComponent(
                "\u00a7a\u4f60\u5df2\u52a0\u5165\u56e2\u961f: \u00a76" + teamName), targetUuid);
        return true;
    }

    public static boolean donateToTeam(ServerPlayerEntity player, int amount) {
        UUID uuid = player.getUUID();
        String teamName = playerTeams.get(uuid);
        if (teamName == null) {
            player.sendMessage(new StringTextComponent("\u00a7c\u4f60\u6ca1\u6709\u56e2\u961f!"), uuid);
            return false;
        }
        Team team = teams.get(teamName);
        boolean success = player.getCapability(MoneyCapability.MONEY_CAP).map(m -> {
            if (m.spendMoney(amount)) {
                team.teamFunds += amount;
                player.sendMessage(new StringTextComponent(
                        "\u00a7a\u2714 \u5411\u56e2\u961f\u6350\u8d60 " + amount + " \u91d1\u5e01! \u56e2\u961f\u8d44\u91d1: " + team.teamFunds), uuid);
                return true;
            }
            player.sendMessage(new StringTextComponent("\u00a7c\u4f59\u989d\u4e0d\u8db3!"), uuid);
            return false;
        }).orElse(false);
        return success;
    }

    public static void showTeamInfo(ServerPlayerEntity player) {
        UUID uuid = player.getUUID();
        String teamName = playerTeams.get(uuid);
        if (teamName == null) {
            player.sendMessage(new StringTextComponent("\u00a7c\u4f60\u6ca1\u6709\u56e2\u961f! \u8f93\u5165 /team create <\u540d\u79f0> \u521b\u5efa"), uuid);
            return;
        }
        Team team = teams.get(teamName);
        player.sendMessage(new StringTextComponent("\u00a76\u2500\u2500\u2500 \u56e2\u961f\u4fe1\u606f \u2500\u2500\u2500"), uuid);
        player.sendMessage(new StringTextComponent("\u00a7e\u56e2\u961f: \u00a7f" + team.name), uuid);
        player.sendMessage(new StringTextComponent("\u00a7e\u516c\u53f8: \u00a7f" + team.companyName), uuid);
        player.sendMessage(new StringTextComponent("\u00a7e\u6210\u5458: \u00a7f" + team.members.size() + " \u4eba"), uuid);
        player.sendMessage(new StringTextComponent("\u00a7e\u56e2\u961f\u8d44\u91d1: \u00a76" + team.teamFunds + " \u91d1\u5e01"), uuid);
        player.sendMessage(new StringTextComponent("\u00a7e\u516c\u53f8\u7b49\u7ea7: \u00a7fLv." + team.level), uuid);
    }

    public static boolean leaveTeam(ServerPlayerEntity player) {
        UUID uuid = player.getUUID();
        String teamName = playerTeams.remove(uuid);
        if (teamName == null) return false;
        Team team = teams.get(teamName);
        if (team != null) {
            team.members.remove(uuid);
            if (team.members.isEmpty()) teams.remove(teamName);
        }
        player.sendMessage(new StringTextComponent("\u00a7a\u5df2\u9000\u51fa\u56e2\u961f!"), uuid);
        return true;
    }

    public static String getTeamName(UUID uuid) { return playerTeams.get(uuid); }
    public static Team getTeam(String name) { return teams.get(name); }
}
