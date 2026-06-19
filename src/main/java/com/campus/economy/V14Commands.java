package com.campus.economy;

import com.campus.CampusLife;
import com.campus.systems.*;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CampusLife.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class V14Commands {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSource> d = event.getDispatcher();

        d.register(Commands.literal("agilefin").executes(ctx -> { AgileFinanceSystem.show(ctx.getSource().getPlayerOrException()); return 1; })
                .then(Commands.argument("id", IntegerArgumentType.integer(1, 5))
                        .executes(ctx -> AgileFinanceSystem.deploy(ctx.getSource().getPlayerOrException(), IntegerArgumentType.getInteger(ctx, "id")) ? 1 : 0)));

        d.register(Commands.literal("aics").executes(ctx -> { AICustomerServiceSystem.show(ctx.getSource().getPlayerOrException()); return 1; })
                .then(Commands.argument("id", IntegerArgumentType.integer(1, 5))
                        .executes(ctx -> AICustomerServiceSystem.deploy(ctx.getSource().getPlayerOrException(), IntegerArgumentType.getInteger(ctx, "id")) ? 1 : 0)));

        d.register(Commands.literal("emetaverse").executes(ctx -> { EnterpriseMetaverseSystem.show(ctx.getSource().getPlayerOrException()); return 1; })
                .then(Commands.argument("id", IntegerArgumentType.integer(1, 5))
                        .executes(ctx -> EnterpriseMetaverseSystem.purchase(ctx.getSource().getPlayerOrException(), IntegerArgumentType.getInteger(ctx, "id")) ? 1 : 0)));

        d.register(Commands.literal("blockchain").executes(ctx -> { BlockchainNotarySystem.show(ctx.getSource().getPlayerOrException()); return 1; })
                .then(Commands.argument("document", StringArgumentType.string())
                        .executes(ctx -> BlockchainNotarySystem.notarize(ctx.getSource().getPlayerOrException(), StringArgumentType.getString(ctx, "document")) ? 1 : 0)));

        d.register(Commands.literal("aidecision").executes(ctx -> { AIDecisionSystem.show(ctx.getSource().getPlayerOrException()); return 1; })
                .then(Commands.argument("id", IntegerArgumentType.integer(1, 5))
                        .executes(ctx -> AIDecisionSystem.acquire(ctx.getSource().getPlayerOrException(), IntegerArgumentType.getInteger(ctx, "id")) ? 1 : 0)));

        d.register(Commands.literal("igraph").executes(ctx -> { IndustryGraphSystem.show(ctx.getSource().getPlayerOrException()); return 1; })
                .then(Commands.argument("id", IntegerArgumentType.integer(1, 5))
                        .executes(ctx -> IndustryGraphSystem.connect(ctx.getSource().getPlayerOrException(), IntegerArgumentType.getInteger(ctx, "id")) ? 1 : 0)));

        d.register(Commands.literal("dtwin").executes(ctx -> { DigitalTwinSystem.show(ctx.getSource().getPlayerOrException()); return 1; })
                .then(Commands.argument("id", IntegerArgumentType.integer(1, 5))
                        .executes(ctx -> DigitalTwinSystem.upgrade(ctx.getSource().getPlayerOrException(), IntegerArgumentType.getInteger(ctx, "id")) ? 1 : 0)));

        d.register(Commands.literal("mrate").executes(ctx -> { MentorRatingSystem.show(ctx.getSource().getPlayerOrException()); return 1; })
                .then(Commands.argument("score", IntegerArgumentType.integer(1, 5))
                        .executes(ctx -> MentorRatingSystem.rate(ctx.getSource().getPlayerOrException(), IntegerArgumentType.getInteger(ctx, "score")) ? 1 : 0)));

        d.register(Commands.literal("health").executes(ctx -> { HealthIndexSystem.show(ctx.getSource().getPlayerOrException()); return 1; }));

        d.register(Commands.literal("ctrade").executes(ctx -> { CarbonTradeSystem.show(ctx.getSource().getPlayerOrException()); return 1; })
                .then(Commands.literal("buy").then(Commands.argument("amount", IntegerArgumentType.integer(1))
                        .executes(ctx -> CarbonTradeSystem.buy(ctx.getSource().getPlayerOrException(), IntegerArgumentType.getInteger(ctx, "amount")) ? 1 : 0)))
                .then(Commands.literal("sell").then(Commands.argument("amount", IntegerArgumentType.integer(1))
                        .executes(ctx -> CarbonTradeSystem.sell(ctx.getSource().getPlayerOrException(), IntegerArgumentType.getInteger(ctx, "amount")) ? 1 : 0))));

        CampusLife.LOGGER.info("V14 commands registered! (agilefin/aics/emetaverse/blockchain/aidecision/igraph/dtwin/mrate/health/ctrade)");
    }
}
