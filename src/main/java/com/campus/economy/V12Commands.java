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
public class V12Commands {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSource> d = event.getDispatcher();

        d.register(Commands.literal("scsync").executes(ctx -> { SupplyChainSyncSystem.show(ctx.getSource().getPlayerOrException()); return 1; })
                .then(Commands.argument("id", IntegerArgumentType.integer(1, 5))
                        .executes(ctx -> SupplyChainSyncSystem.partner(ctx.getSource().getPlayerOrException(), IntegerArgumentType.getInteger(ctx, "id")) ? 1 : 0)));

        d.register(Commands.literal("iptrade").executes(ctx -> { IPTradeSystem.show(ctx.getSource().getPlayerOrException()); return 1; })
                .then(Commands.argument("id", IntegerArgumentType.integer(1, 6))
                        .executes(ctx -> IPTradeSystem.buy(ctx.getSource().getPlayerOrException(), IntegerArgumentType.getInteger(ctx, "id")) ? 1 : 0)));

        d.register(Commands.literal("ecosystem").executes(ctx -> { EcosystemSystem.show(ctx.getSource().getPlayerOrException()); return 1; })
                .then(Commands.argument("id", IntegerArgumentType.integer(1, 7))
                        .executes(ctx -> EcosystemSystem.build(ctx.getSource().getPlayerOrException(), IntegerArgumentType.getInteger(ctx, "id")) ? 1 : 0)));

        d.register(Commands.literal("pipeline").executes(ctx -> { TalentPipelineSystem.show(ctx.getSource().getPlayerOrException()); return 1; })
                .then(Commands.argument("id", IntegerArgumentType.integer(1, 5))
                        .executes(ctx -> TalentPipelineSystem.enroll(ctx.getSource().getPlayerOrException(), IntegerArgumentType.getInteger(ctx, "id")) ? 1 : 0)));

        d.register(Commands.literal("risk").executes(ctx -> { RiskWarningSystem.show(ctx.getSource().getPlayerOrException()); return 1; })
                .then(Commands.literal("upgrade").executes(ctx -> RiskWarningSystem.upgrade(ctx.getSource().getPlayerOrException()) ? 1 : 0)));

        d.register(Commands.literal("museum").executes(ctx -> { MuseumSystem.show(ctx.getSource().getPlayerOrException()); return 1; })
                .then(Commands.literal("add").executes(ctx -> MuseumSystem.addExhibit(ctx.getSource().getPlayerOrException()) ? 1 : 0)));

        d.register(Commands.literal("scviz").executes(ctx -> { SupplyChainVizSystem.show(ctx.getSource().getPlayerOrException()); return 1; })
                .then(Commands.argument("id", IntegerArgumentType.integer(1, 5))
                        .executes(ctx -> SupplyChainVizSystem.deploy(ctx.getSource().getPlayerOrException(), IntegerArgumentType.getInteger(ctx, "id")) ? 1 : 0)));

        d.register(Commands.literal("workshop").executes(ctx -> { InnovationWorkshopSystem.show(ctx.getSource().getPlayerOrException()); return 1; })
                .then(Commands.literal("brainstorm").executes(ctx -> InnovationWorkshopSystem.brainstorm(ctx.getSource().getPlayerOrException()) ? 1 : 0)));

        d.register(Commands.literal("cocreate").executes(ctx -> { ValueCoCreationSystem.show(ctx.getSource().getPlayerOrException()); return 1; })
                .then(Commands.argument("id", IntegerArgumentType.integer(1, 6))
                        .executes(ctx -> ValueCoCreationSystem.engage(ctx.getSource().getPlayerOrException(), IntegerArgumentType.getInteger(ctx, "id")) ? 1 : 0)));

        d.register(Commands.literal("archive").executes(ctx -> { ArchiveSystem.show(ctx.getSource().getPlayerOrException()); return 1; })
                .then(Commands.argument("category", StringArgumentType.string())
                        .executes(ctx -> ArchiveSystem.archive(ctx.getSource().getPlayerOrException(), StringArgumentType.getString(ctx, "category")) ? 1 : 0)));

        CampusLife.LOGGER.info("V12 commands registered! (scsync/iptrade/ecosystem/pipeline/risk/museum/scviz/workshop/cocreate/archive)");
    }
}
