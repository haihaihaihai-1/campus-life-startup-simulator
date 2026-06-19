package com.campus.economy;

import com.campus.CampusLife;
import com.campus.systems.*;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CampusLife.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class V5Commands {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSource> d = event.getDispatcher();

        d.register(Commands.literal("grant")
                .executes(ctx -> { GrantSystem.showGrants(ctx.getSource().getPlayerOrException()); return 1; })
                .then(Commands.argument("id", IntegerArgumentType.integer(1, 7))
                        .executes(ctx -> GrantSystem.applyGrant(ctx.getSource().getPlayerOrException(),
                                IntegerArgumentType.getInteger(ctx, "id")) ? 1 : 0)));

        d.register(Commands.literal("produce")
                .executes(ctx -> { SupplyChainSystem.showLines(ctx.getSource().getPlayerOrException()); return 1; })
                .then(Commands.argument("line", IntegerArgumentType.integer(1, 4))
                        .then(Commands.argument("batches", IntegerArgumentType.integer(1, 20))
                                .executes(ctx -> SupplyChainSystem.startProduction(ctx.getSource().getPlayerOrException(),
                                        IntegerArgumentType.getInteger(ctx, "line"),
                                        IntegerArgumentType.getInteger(ctx, "batches")) ? 1 : 0))));

        d.register(Commands.literal("incubator")
                .executes(ctx -> { IncubatorSystem.show(ctx.getSource().getPlayerOrException()); return 1; })
                .then(Commands.argument("id", IntegerArgumentType.integer(1, 4))
                        .executes(ctx -> IncubatorSystem.join(ctx.getSource().getPlayerOrException(),
                                IntegerArgumentType.getInteger(ctx, "id")) ? 1 : 0)));

        d.register(Commands.literal("crisis")
                .executes(ctx -> { CrisisSystem.show(ctx.getSource().getPlayerOrException()); return 1; })
                .then(Commands.literal("solve")
                        .then(Commands.argument("action", IntegerArgumentType.integer(1, 3))
                                .executes(ctx -> CrisisSystem.solve(ctx.getSource().getPlayerOrException(),
                                        IntegerArgumentType.getInteger(ctx, "action")) ? 1 : 0))));

        d.register(Commands.literal("network")
                .executes(ctx -> { NetworkSystem.show(ctx.getSource().getPlayerOrException()); return 1; })
                .then(Commands.argument("id", IntegerArgumentType.integer(1, 8))
                        .executes(ctx -> NetworkSystem.buildConnection(ctx.getSource().getPlayerOrException(),
                                IntegerArgumentType.getInteger(ctx, "id")) ? 1 : 0)));

        d.register(Commands.literal("train")
                .executes(ctx -> { TrainingSystem.show(ctx.getSource().getPlayerOrException()); return 1; })
                .then(Commands.argument("id", IntegerArgumentType.integer(1, 7))
                        .executes(ctx -> TrainingSystem.enroll(ctx.getSource().getPlayerOrException(),
                                IntegerArgumentType.getInteger(ctx, "id")) ? 1 : 0)));

        d.register(Commands.literal("stock")
                .executes(ctx -> { StockMarketSystem.show(ctx.getSource().getPlayerOrException()); return 1; })
                .then(Commands.literal("buy")
                        .then(Commands.argument("idx", IntegerArgumentType.integer(1, 6))
                                .then(Commands.argument("shares", IntegerArgumentType.integer(1, 10000))
                                        .executes(ctx -> StockMarketSystem.buy(ctx.getSource().getPlayerOrException(),
                                                IntegerArgumentType.getInteger(ctx, "idx"),
                                                IntegerArgumentType.getInteger(ctx, "shares")) ? 1 : 0))))
                .then(Commands.literal("sell")
                        .then(Commands.argument("idx", IntegerArgumentType.integer(1, 6))
                                .then(Commands.argument("shares", IntegerArgumentType.integer(1, 10000))
                                        .executes(ctx -> StockMarketSystem.sell(ctx.getSource().getPlayerOrException(),
                                                IntegerArgumentType.getInteger(ctx, "idx"),
                                                IntegerArgumentType.getInteger(ctx, "shares")) ? 1 : 0)))));

        d.register(Commands.literal("franchise")
                .executes(ctx -> { FranchiseSystem.show(ctx.getSource().getPlayerOrException()); return 1; })
                .then(Commands.argument("id", IntegerArgumentType.integer(1, 6))
                        .executes(ctx -> FranchiseSystem.open(ctx.getSource().getPlayerOrException(),
                                IntegerArgumentType.getInteger(ctx, "id")) ? 1 : 0)));

        CampusLife.LOGGER.info("V5 commands registered! (grant/produce/incubator/crisis/network/train/stock/franchise)");
    }
}
