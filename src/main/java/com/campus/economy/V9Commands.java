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
public class V9Commands {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSource> d = event.getDispatcher();

        d.register(Commands.literal("ipo").executes(ctx -> { IPOSystem.show(ctx.getSource().getPlayerOrException()); return 1; })
                .then(Commands.literal("go").executes(ctx -> IPOSystem.goIPO(ctx.getSource().getPlayerOrException()) ? 1 : 0)));

        d.register(Commands.literal("merger").executes(ctx -> { MergerSystem.show(ctx.getSource().getPlayerOrException()); return 1; })
                .then(Commands.argument("id", IntegerArgumentType.integer(1, 7))
                        .executes(ctx -> MergerSystem.acquire(ctx.getSource().getPlayerOrException(), IntegerArgumentType.getInteger(ctx, "id")) ? 1 : 0)));

        d.register(Commands.literal("esg").executes(ctx -> { ESGSystem.show(ctx.getSource().getPlayerOrException()); return 1; })
                .then(Commands.argument("dim", IntegerArgumentType.integer(1, 3)).then(Commands.argument("amount", IntegerArgumentType.integer(1))
                        .executes(ctx -> ESGSystem.invest(ctx.getSource().getPlayerOrException(), IntegerArgumentType.getInteger(ctx, "dim"), IntegerArgumentType.getInteger(ctx, "amount")) ? 1 : 0))));

        d.register(Commands.literal("crypto").executes(ctx -> { CryptoSystem.show(ctx.getSource().getPlayerOrException()); return 1; })
                .then(Commands.literal("mine").then(Commands.argument("power", IntegerArgumentType.integer(1))
                        .executes(ctx -> CryptoSystem.buyMiner(ctx.getSource().getPlayerOrException(), IntegerArgumentType.getInteger(ctx, "power")) ? 1 : 0)))
                .then(Commands.literal("sell").then(Commands.argument("amount", IntegerArgumentType.integer(1))
                        .executes(ctx -> CryptoSystem.sell(ctx.getSource().getPlayerOrException(), IntegerArgumentType.getInteger(ctx, "amount")) ? 1 : 0))));

        d.register(Commands.literal("plan").executes(ctx -> { BusinessPlanSystem.show(ctx.getSource().getPlayerOrException()); return 1; })
                .then(Commands.literal("submit").executes(ctx -> BusinessPlanSystem.submit(ctx.getSource().getPlayerOrException()) ? 1 : 0)));

        d.register(Commands.literal("summit").executes(ctx -> { SummitSystem.show(ctx.getSource().getPlayerOrException()); return 1; })
                .then(Commands.literal("attend").executes(ctx -> { SummitSystem.attend(ctx.getSource().getPlayerOrException()); return 1; })));

        d.register(Commands.literal("resilience").executes(ctx -> { ResilienceSystem.show(ctx.getSource().getPlayerOrException()); return 1; })
                .then(Commands.argument("type", IntegerArgumentType.integer(1, 4)).then(Commands.argument("amount", IntegerArgumentType.integer(1))
                        .executes(ctx -> ResilienceSystem.invest(ctx.getSource().getPlayerOrException(), IntegerArgumentType.getInteger(ctx, "type"), IntegerArgumentType.getInteger(ctx, "amount")) ? 1 : 0))));

        d.register(Commands.literal("smart").executes(ctx -> { SmartContractSystem.show(ctx.getSource().getPlayerOrException()); return 1; })
                .then(Commands.argument("type", IntegerArgumentType.integer(1, 4))
                        .executes(ctx -> SmartContractSystem.deploy(ctx.getSource().getPlayerOrException(), IntegerArgumentType.getInteger(ctx, "type")) ? 1 : 0)));

        d.register(Commands.literal("hatchery").executes(ctx -> { HatcheryCupSystem.show(ctx.getSource().getPlayerOrException()); return 1; })
                .then(Commands.literal("join").executes(ctx -> { HatcheryCupSystem.join(ctx.getSource().getPlayerOrException()); return 1; })));

        d.register(Commands.literal("legacy").executes(ctx -> { LegacySystem.show(ctx.getSource().getPlayerOrException()); return 1; })
                .then(Commands.argument("id", IntegerArgumentType.integer(1, 5))
                        .executes(ctx -> LegacySystem.unlock(ctx.getSource().getPlayerOrException(), IntegerArgumentType.getInteger(ctx, "id")) ? 1 : 0)));

        CampusLife.LOGGER.info("V9 commands registered! (ipo/merger/esg/crypto/plan/summit/resilience/smart/hatchery/legacy)");
    }
}
