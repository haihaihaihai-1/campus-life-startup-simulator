package com.campus.economy;

import com.campus.CampusLife;
import com.campus.systems.*;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CampusLife.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class V11Commands {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSource> d = event.getDispatcher();

        d.register(Commands.literal("logins").executes(ctx -> { LogisticsInsuranceSystem.show(ctx.getSource().getPlayerOrException()); return 1; })
                .then(Commands.argument("type", IntegerArgumentType.integer(1, 3))
                        .executes(ctx -> LogisticsInsuranceSystem.buy(ctx.getSource().getPlayerOrException(), IntegerArgumentType.getInteger(ctx, "type")) ? 1 : 0)));

        d.register(Commands.literal("quality").executes(ctx -> { QualityControlSystem.show(ctx.getSource().getPlayerOrException()); return 1; })
                .then(Commands.argument("id", IntegerArgumentType.integer(1, 5))
                        .executes(ctx -> QualityControlSystem.certify(ctx.getSource().getPlayerOrException(), IntegerArgumentType.getInteger(ctx, "id")) ? 1 : 0)));

        d.register(Commands.literal("brand").executes(ctx -> { BrandValueSystem.show(ctx.getSource().getPlayerOrException()); return 1; })
                .then(Commands.argument("id", IntegerArgumentType.integer(1, 6))
                        .executes(ctx -> BrandValueSystem.invest(ctx.getSource().getPlayerOrException(), IntegerArgumentType.getInteger(ctx, "id")) ? 1 : 0)));

        d.register(Commands.literal("knowledge").executes(ctx -> { KnowledgeSystem.show(ctx.getSource().getPlayerOrException()); return 1; })
                .then(Commands.argument("id", IntegerArgumentType.integer(1, 7))
                        .executes(ctx -> KnowledgeSystem.create(ctx.getSource().getPlayerOrException(), IntegerArgumentType.getInteger(ctx, "id")) ? 1 : 0)));

        d.register(Commands.literal("agile").executes(ctx -> { AgileSystem.show(ctx.getSource().getPlayerOrException()); return 1; })
                .then(Commands.argument("complexity", IntegerArgumentType.integer(1, 5))
                        .executes(ctx -> AgileSystem.startSprint(ctx.getSource().getPlayerOrException(), IntegerArgumentType.getInteger(ctx, "complexity")) ? 1 : 0)));

        d.register(Commands.literal("feedback").executes(ctx -> { FeedbackSystem.show(ctx.getSource().getPlayerOrException()); return 1; })
                .then(Commands.literal("collect").executes(ctx -> FeedbackSystem.collect(ctx.getSource().getPlayerOrException()) ? 1 : 0))
                .then(Commands.literal("improve").executes(ctx -> FeedbackSystem.improve(ctx.getSource().getPlayerOrException()) ? 1 : 0)));

        d.register(Commands.literal("competitor").executes(ctx -> { CompetitorSystem.show(ctx.getSource().getPlayerOrException()); return 1; })
                .then(Commands.argument("id", IntegerArgumentType.integer(1, 5))
                        .executes(ctx -> CompetitorSystem.gatherIntel(ctx.getSource().getPlayerOrException(), IntegerArgumentType.getInteger(ctx, "id")) ? 1 : 0)));

        d.register(Commands.literal("macro").executes(ctx -> { MacroEconomySystem.show(ctx.getSource().getPlayerOrException()); return 1; }));

        d.register(Commands.literal("gov").executes(ctx -> { GovRelationSystem.show(ctx.getSource().getPlayerOrException()); return 1; })
                .then(Commands.argument("id", IntegerArgumentType.integer(1, 5))
                        .executes(ctx -> GovRelationSystem.act(ctx.getSource().getPlayerOrException(), IntegerArgumentType.getInteger(ctx, "id")) ? 1 : 0)));

        d.register(Commands.literal("uni").executes(ctx -> { CorporateUniversitySystem.show(ctx.getSource().getPlayerOrException()); return 1; })
                .then(Commands.argument("id", IntegerArgumentType.integer(1, 5))
                        .executes(ctx -> CorporateUniversitySystem.enroll(ctx.getSource().getPlayerOrException(), IntegerArgumentType.getInteger(ctx, "id")) ? 1 : 0)));

        CampusLife.LOGGER.info("V11 commands registered! (logins/quality/brand/knowledge/agile/feedback/competitor/macro/gov/uni)");
    }
}
