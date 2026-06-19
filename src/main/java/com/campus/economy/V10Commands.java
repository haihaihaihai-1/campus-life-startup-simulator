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
public class V10Commands {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSource> d = event.getDispatcher();

        d.register(Commands.literal("audit").executes(ctx -> { AuditSystem.show(ctx.getSource().getPlayerOrException()); return 1; })
                .then(Commands.literal("run").executes(ctx -> AuditSystem.manualAudit(ctx.getSource().getPlayerOrException()) ? 1 : 0)));

        d.register(Commands.literal("talent").executes(ctx -> { TalentMarketSystem.show(ctx.getSource().getPlayerOrException()); return 1; })
                .then(Commands.argument("id", IntegerArgumentType.integer(1, 7))
                        .executes(ctx -> TalentMarketSystem.recruit(ctx.getSource().getPlayerOrException(), IntegerArgumentType.getInteger(ctx, "id")) ? 1 : 0)));

        d.register(Commands.literal("crm").executes(ctx -> { CRMSystem.show(ctx.getSource().getPlayerOrException()); return 1; })
                .then(Commands.argument("tier", IntegerArgumentType.integer(1, 4))
                        .executes(ctx -> CRMSystem.acquire(ctx.getSource().getPlayerOrException(), IntegerArgumentType.getInteger(ctx, "tier")) ? 1 : 0)));

        d.register(Commands.literal("trace").executes(ctx -> { TraceabilitySystem.show(ctx.getSource().getPlayerOrException()); return 1; })
                .then(Commands.argument("id", IntegerArgumentType.integer(1, 5))
                        .executes(ctx -> TraceabilitySystem.certify(ctx.getSource().getPlayerOrException(), IntegerArgumentType.getInteger(ctx, "id")) ? 1 : 0)));

        d.register(Commands.literal("fund").executes(ctx -> { IndustryFundSystem.show(ctx.getSource().getPlayerOrException()); return 1; })
                .then(Commands.literal("create").then(Commands.argument("capital", IntegerArgumentType.integer(10000))
                        .executes(ctx -> IndustryFundSystem.create(ctx.getSource().getPlayerOrException(), IntegerArgumentType.getInteger(ctx, "capital")) ? 1 : 0)))
                .then(Commands.literal("withdraw").executes(ctx -> IndustryFundSystem.withdraw(ctx.getSource().getPlayerOrException()) ? 1 : 0)));

        d.register(Commands.literal("bankruptcy").executes(ctx -> { BankruptcySystem.show(ctx.getSource().getPlayerOrException()); return 1; })
                .then(Commands.literal("restructure").executes(ctx -> BankruptcySystem.restructure(ctx.getSource().getPlayerOrException()) ? 1 : 0)));

        d.register(Commands.literal("taxplan").executes(ctx -> { TaxPlanningSystem.show(ctx.getSource().getPlayerOrException()); return 1; })
                .then(Commands.argument("id", IntegerArgumentType.integer(1, 5))
                        .executes(ctx -> TaxPlanningSystem.apply(ctx.getSource().getPlayerOrException(), IntegerArgumentType.getInteger(ctx, "id")) ? 1 : 0)));

        d.register(Commands.literal("digital").executes(ctx -> { DigitalTransformSystem.show(ctx.getSource().getPlayerOrException()); return 1; })
                .then(Commands.argument("id", IntegerArgumentType.integer(1, 7))
                        .executes(ctx -> DigitalTransformSystem.transform(ctx.getSource().getPlayerOrException(), IntegerArgumentType.getInteger(ctx, "id")) ? 1 : 0)));

        d.register(Commands.literal("csr").executes(ctx -> { CSRSystem.show(ctx.getSource().getPlayerOrException()); return 1; })
                .then(Commands.argument("id", IntegerArgumentType.integer(1, 6))
                        .executes(ctx -> CSRSystem.donate(ctx.getSource().getPlayerOrException(), IntegerArgumentType.getInteger(ctx, "id")) ? 1 : 0)));

        d.register(Commands.literal("mnet").executes(ctx -> { MentorNetworkSystem.show(ctx.getSource().getPlayerOrException()); return 1; })
                .then(Commands.literal("consult").executes(ctx -> MentorNetworkSystem.consult(ctx.getSource().getPlayerOrException()) ? 1 : 0))
                .then(Commands.argument("id", IntegerArgumentType.integer(1, 5))
                        .executes(ctx -> MentorNetworkSystem.addMentor(ctx.getSource().getPlayerOrException(), IntegerArgumentType.getInteger(ctx, "id")) ? 1 : 0)));

        CampusLife.LOGGER.info("V10 commands registered! (audit/talent/crm/trace/fund/bankruptcy/taxplan/digital/csr/mnet)");
    }
}
