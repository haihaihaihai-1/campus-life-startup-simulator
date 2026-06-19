package com.campus.economy;

import com.campus.CampusLife;
import com.campus.systems.CompetitionSystem;
import com.campus.systems.LoanSystem;
import com.campus.systems.QuestSystem;
import com.campus.systems.TeamSystem;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CampusLife.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ExtendedCommands {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSource> d = event.getDispatcher();

        d.register(Commands.literal("team")
                .executes(ctx -> showTeamHelp(ctx.getSource()))
                .then(Commands.literal("create")
                        .then(Commands.argument("name", StringArgumentType.string())
                                .executes(ctx -> TeamSystem.createTeam(ctx.getSource().getPlayerOrException(),
                                        StringArgumentType.getString(ctx, "name")) ? 1 : 0)))
                .then(Commands.literal("invite")
                        .then(Commands.argument("player", StringArgumentType.string())
                                .executes(ctx -> invitePlayer(ctx.getSource(),
                                        StringArgumentType.getString(ctx, "player")))))
                .then(Commands.literal("donate")
                        .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                .executes(ctx -> TeamSystem.donateToTeam(ctx.getSource().getPlayerOrException(),
                                        IntegerArgumentType.getInteger(ctx, "amount")) ? 1 : 0)))
                .then(Commands.literal("info")
                        .executes(ctx -> { TeamSystem.showTeamInfo(ctx.getSource().getPlayerOrException()); return 1; }))
                .then(Commands.literal("leave")
                        .executes(ctx -> TeamSystem.leaveTeam(ctx.getSource().getPlayerOrException()) ? 1 : 0)));

        d.register(Commands.literal("loan")
                .executes(ctx -> { LoanSystem.showLoanInfo(ctx.getSource().getPlayerOrException()); return 1; })
                .then(Commands.literal("take")
                        .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                .executes(ctx -> LoanSystem.takeLoan(ctx.getSource().getPlayerOrException(),
                                        IntegerArgumentType.getInteger(ctx, "amount")) ? 1 : 0)))
                .then(Commands.literal("repay")
                        .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                .executes(ctx -> LoanSystem.repayLoan(ctx.getSource().getPlayerOrException(),
                                        IntegerArgumentType.getInteger(ctx, "amount")) ? 1 : 0))));

        d.register(Commands.literal("quest")
                .executes(ctx -> showQuest(ctx.getSource()))
                .then(Commands.literal("new")
                        .executes(ctx -> { QuestSystem.assignDailyQuest(ctx.getSource().getPlayerOrException()); return 1; })));

        d.register(Commands.literal("compete")
                .executes(ctx -> showCompetition(ctx.getSource()))
                .then(Commands.literal("join")
                        .executes(ctx -> { CompetitionSystem.joinCompetition(ctx.getSource().getPlayerOrException()); return 1; }))
                .then(Commands.literal("status")
                        .executes(ctx -> showCompetition(ctx.getSource()))));

        d.register(Commands.literal("startup")
                .executes(ctx -> showStartupHelp(ctx.getSource())));

        CampusLife.LOGGER.info("Extended commands registered! (team/loan/quest/compete)");
    }

    private static int showTeamHelp(CommandSource source) throws CommandSyntaxException {
        ServerPlayerEntity player = source.getPlayerOrException();
        player.sendMessage(new StringTextComponent("\u00a76\u2500\u2500\u2500 \u56e2\u961f\u5e2e\u52a9 \u2500\u2500\u2500"), player.getUUID());
        player.sendMessage(new StringTextComponent("\u00a7e/team create <\u540d\u79f0> \u00a7f- \u521b\u5efa\u56e2\u961f"), player.getUUID());
        player.sendMessage(new StringTextComponent("\u00a7e/team invite <\u73a9\u5bb6> \u00a7f- \u9080\u8bf7\u6210\u5458"), player.getUUID());
        player.sendMessage(new StringTextComponent("\u00a7e/team donate <\u91d1\u989d> \u00a7f- \u6350\u732e\u56e2\u961f\u8d44\u91d1"), player.getUUID());
        player.sendMessage(new StringTextComponent("\u00a7e/team info \u00a7f- \u67e5\u770b\u56e2\u961f\u4fe1\u606f"), player.getUUID());
        player.sendMessage(new StringTextComponent("\u00a7e/team leave \u00a7f- \u9000\u51fa\u56e2\u961f"), player.getUUID());
        return 1;
    }

    private static int invitePlayer(CommandSource source, String playerName) throws CommandSyntaxException {
        ServerPlayerEntity leader = source.getPlayerOrException();
        ServerPlayerEntity target = source.getServer().getPlayerList().getPlayerByName(playerName);
        if (target == null) {
            leader.sendMessage(new StringTextComponent("\u00a7c\u73a9\u5bb6\u4e0d\u5728\u7ebf!"), leader.getUUID());
            return 0;
        }
        String teamName = TeamSystem.getTeamName(leader.getUUID());
        if (teamName == null) {
            leader.sendMessage(new StringTextComponent("\u00a7c\u4f60\u6ca1\u6709\u56e2\u961f!"), leader.getUUID());
            return 0;
        }
        return TeamSystem.inviteMember(leader, target, teamName) ? 1 : 0;
    }

    private static int showQuest(CommandSource source) throws CommandSyntaxException {
        ServerPlayerEntity player = source.getPlayerOrException();
        QuestSystem.PlayerQuest quest = QuestSystem.getQuest(player.getUUID());
        if (quest == null) {
            player.sendMessage(new StringTextComponent("\u00a7e\u4f60\u6ca1\u6709\u6d3b\u8dc3\u4efb\u52a1! \u8f93\u5165 /quest new \u63a5\u53d6\u65b0\u4efb\u52a1"), player.getUUID());
        } else {
            player.sendMessage(new StringTextComponent("\u00a76\u2500\u2500\u2500 \u4efb\u52a1\u4fe1\u606f \u2500\u2500\u2500"), player.getUUID());
            player.sendMessage(new StringTextComponent("\u00a7e\u4efb\u52a1: \u00a7f" + quest.description + " x" + quest.targetAmount), player.getUUID());
            player.sendMessage(new StringTextComponent("\u00a7e\u8fdb\u5ea6: \u00a7f" + quest.currentProgress + "/" + quest.targetAmount), player.getUUID());
            player.sendMessage(new StringTextComponent("\u00a7e\u5956\u52b1: \u00a76" + quest.reward + " \u91d1\u5e01"), player.getUUID());
            if (quest.completed) {
                player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u5df2\u5b8c\u6210! \u8f93\u5165 /quest new \u63a5\u53d6\u65b0\u4efb\u52a1"), player.getUUID());
            }
        }
        return 1;
    }

    private static int showCompetition(CommandSource source) throws CommandSyntaxException {
        ServerPlayerEntity player = source.getPlayerOrException();
        if (CompetitionSystem.isCompetitionActive()) {
            player.sendMessage(new StringTextComponent("\u00a76\u2500\u2500\u2500 \u521b\u4e1a\u5927\u8d5b\u8fdb\u884c\u4e2d! \u2500\u2500\u2500"), player.getUUID());
            player.sendMessage(new StringTextComponent("\u00a7e\u8f93\u5165 /compete join \u62a5\u540d\u53c2\u8d5b!"), player.getUUID());
        } else {
            int mins = CompetitionSystem.getNextCompetitionIn() / 1200;
            player.sendMessage(new StringTextComponent(
                    "\u00a7e\u4e0b\u4e00\u573a\u5927\u8d5b: \u00a76" + mins + " \u5206\u949f\u540e"), player.getUUID());
        }
        return 1;
    }

    private static int showStartupHelp(CommandSource source) throws CommandSyntaxException {
        ServerPlayerEntity player = source.getPlayerOrException();
        player.sendMessage(new StringTextComponent("\u00a76\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557"), player.getUUID());
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7e\u26a1 \u6821\u56ed\u521b\u4e1a\u6a21\u62df\u5668 \u00a76\u2551"), player.getUUID());
        player.sendMessage(new StringTextComponent("\u00a76\u255a\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255d"), player.getUUID());
        player.sendMessage(new StringTextComponent("\u00a7e\u7ecf\u6d4e\u7cfb\u7edf:"), player.getUUID());
        player.sendMessage(new StringTextComponent("\u00a7f  /money \u00a77- \u67e5\u770b\u4f59\u989d"), player.getUUID());
        player.sendMessage(new StringTextComponent("\u00a7f  /money pay <\u73a9\u5bb6> <\u91d1\u989d> \u00a77- \u8f6c\u8d26"), player.getUUID());
        player.sendMessage(new StringTextComponent("\u00a7f  /business \u00a77- \u521b\u4e1a\u9762\u677f"), player.getUUID());
        player.sendMessage(new StringTextComponent("\u00a7f  /business invest <\u91d1\u989d> \u00a77- \u6295\u8d44\u521b\u4e1a"), player.getUUID());
        player.sendMessage(new StringTextComponent("\u00a7e\u56e2\u961f\u7cfb\u7edf:"), player.getUUID());
        player.sendMessage(new StringTextComponent("\u00a7f  /team create <\u540d\u79f0> \u00a77- \u521b\u5efa\u516c\u53f8"), player.getUUID());
        player.sendMessage(new StringTextComponent("\u00a7f  /team donate <\u91d1\u989d> \u00a77- \u6350\u732e\u56e2\u961f"), player.getUUID());
        player.sendMessage(new StringTextComponent("\u00a7e\u91d1\u878d\u7cfb\u7edf:"), player.getUUID());
        player.sendMessage(new StringTextComponent("\u00a7f  /loan take <\u91d1\u989d> \u00a77- \u94f6\u884c\u8d37\u6b3e"), player.getUUID());
        player.sendMessage(new StringTextComponent("\u00a7f  /loan repay <\u91d1\u989d> \u00a77- \u8fd8\u6b3e"), player.getUUID());
        player.sendMessage(new StringTextComponent("\u00a7e\u4efb\u52a1\u7cfb\u7edf:"), player.getUUID());
        player.sendMessage(new StringTextComponent("\u00a7f  /quest \u00a77- \u67e5\u770b\u4efb\u52a1"), player.getUUID());
        player.sendMessage(new StringTextComponent("\u00a7f  /quest new \u00a77- \u63a5\u53d6\u65b0\u4efb\u52a1"), player.getUUID());
        player.sendMessage(new StringTextComponent("\u00a7e\u5927\u8d5b\u7cfb\u7edf:"), player.getUUID());
        player.sendMessage(new StringTextComponent("\u00a7f  /compete join \u00a77- \u62a5\u540d\u53c2\u8d5b"), player.getUUID());
        player.sendMessage(new StringTextComponent("\u00a7f  /compete status \u00a77- \u67e5\u770b\u72b6\u6001"), player.getUUID());
        player.sendMessage(new StringTextComponent("\u00a7f  /shop \u00a77- \u5e02\u573a\u5546\u5e97"), player.getUUID());
        return 1;
    }
}
