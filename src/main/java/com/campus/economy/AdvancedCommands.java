package com.campus.economy;

import com.campus.CampusLife;
import com.campus.systems.*;
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

/**
 * 高级命令系统拼图 - 投资人/研发/市场/员工/成就/排行
 */
@Mod.EventBusSubscriber(modid = CampusLife.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AdvancedCommands {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSource> d = event.getDispatcher();

        // === 投资人命令 ===
        d.register(Commands.literal("investor")
                .executes(ctx -> { InvestorSystem.showInvestors(ctx.getSource().getPlayerOrException()); return 1; })
                .then(Commands.literal("fund")
                        .then(Commands.argument("id", IntegerArgumentType.integer(1, 6))
                                .executes(ctx -> InvestorSystem.requestFunding(ctx.getSource().getPlayerOrException(),
                                        IntegerArgumentType.getInteger(ctx, "id")) ? 1 : 0))));

        // === 研发命令 ===
        d.register(Commands.literal("research")
                .executes(ctx -> { ResearchSystem.showTechTree(ctx.getSource().getPlayerOrException()); return 1; })
                .then(Commands.literal("unlock")
                        .then(Commands.argument("tech", StringArgumentType.string())
                                .executes(ctx -> ResearchSystem.unlockTech(ctx.getSource().getPlayerOrException(),
                                        StringArgumentType.getString(ctx, "tech")) ? 1 : 0))));

        // === 市场行情命令 ===
        d.register(Commands.literal("market")
                .then(Commands.literal("list")
                        .executes(ctx -> { MarketPriceSystem.showMarket(ctx.getSource().getPlayerOrException()); return 1; })));

        // === 雇佣命令 ===
        d.register(Commands.literal("hire")
                .executes(ctx -> { EmployeeSystem.showEmployees(ctx.getSource().getPlayerOrException()); return 1; })
                .then(Commands.argument("type", StringArgumentType.string())
                        .executes(ctx -> EmployeeSystem.hire(ctx.getSource().getPlayerOrException(),
                                StringArgumentType.getString(ctx, "type")) ? 1 : 0)));

        // === 成就命令 ===
        d.register(Commands.literal("achievements")
                .executes(ctx -> { AchievementSystem.showAchievements(ctx.getSource().getPlayerOrException()); return 1; }));

        // === 排行榜命令 ===
        d.register(Commands.literal("rank")
                .executes(ctx -> { LeaderboardSystem.showLeaderboard(ctx.getSource().getPlayerOrException(), "money"); return 1; })
                .then(Commands.argument("type", StringArgumentType.string())
                        .executes(ctx -> { LeaderboardSystem.showLeaderboard(ctx.getSource().getPlayerOrException(),
                                StringArgumentType.getString(ctx, "type")); return 1; })));

        CampusLife.LOGGER.info("Advanced commands registered! (investor/research/market/hire/achievements/rank)");
    }
}
