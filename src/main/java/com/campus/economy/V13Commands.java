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
public class V13Commands {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSource> d = event.getDispatcher();

        d.register(Commands.literal("pricing").executes(ctx -> { SmartPricingSystem.show(ctx.getSource().getPlayerOrException()); return 1; })
                .then(Commands.argument("id", IntegerArgumentType.integer(1, 5))
                        .executes(ctx -> SmartPricingSystem.upgrade(ctx.getSource().getPlayerOrException(), IntegerArgumentType.getInteger(ctx, "id")) ? 1 : 0)));

        d.register(Commands.literal("vip").executes(ctx -> { MembershipSystem.show(ctx.getSource().getPlayerOrException()); return 1; })
                .then(Commands.literal("upgrade").executes(ctx -> MembershipSystem.upgrade(ctx.getSource().getPlayerOrException()) ? 1 : 0))
                .then(Commands.literal("redeem").executes(ctx -> MembershipSystem.redeem(ctx.getSource().getPlayerOrException()) ? 1 : 0)));

        d.register(Commands.literal("media").executes(ctx -> { MediaSystem.show(ctx.getSource().getPlayerOrException()); return 1; })
                .then(Commands.argument("id", IntegerArgumentType.integer(1, 5))
                        .executes(ctx -> MediaSystem.acquire(ctx.getSource().getPlayerOrException(), IntegerArgumentType.getInteger(ctx, "id")) ? 1 : 0)));

        d.register(Commands.literal("schedule").executes(ctx -> { SmartSchedulingSystem.show(ctx.getSource().getPlayerOrException()); return 1; })
                .then(Commands.argument("complexity", IntegerArgumentType.integer(1, 5))
                        .executes(ctx -> SmartSchedulingSystem.start(ctx.getSource().getPlayerOrException(), IntegerArgumentType.getInteger(ctx, "complexity")) ? 1 : 0)));

        d.register(Commands.literal("community").executes(ctx -> { CommunitySystem.show(ctx.getSource().getPlayerOrException()); return 1; })
                .then(Commands.literal("post").executes(ctx -> CommunitySystem.post(ctx.getSource().getPlayerOrException(), "post") ? 1 : 0))
                .then(Commands.literal("help").executes(ctx -> CommunitySystem.help(ctx.getSource().getPlayerOrException()) ? 1 : 0)));

        d.register(Commands.literal("foundation").executes(ctx -> { FoundationSystem.show(ctx.getSource().getPlayerOrException()); return 1; })
                .then(Commands.literal("create").then(Commands.argument("amount", IntegerArgumentType.integer(5000))
                        .executes(ctx -> FoundationSystem.create(ctx.getSource().getPlayerOrException(), IntegerArgumentType.getInteger(ctx, "amount")) ? 1 : 0)))
                .then(Commands.literal("donate").then(Commands.argument("amount", IntegerArgumentType.integer(1))
                        .executes(ctx -> FoundationSystem.donate(ctx.getSource().getPlayerOrException(), IntegerArgumentType.getInteger(ctx, "amount")) ? 1 : 0))));

        d.register(Commands.literal("logistics").executes(ctx -> { SmartLogisticsSystem.show(ctx.getSource().getPlayerOrException()); return 1; })
                .then(Commands.argument("id", IntegerArgumentType.integer(1, 5))
                        .executes(ctx -> SmartLogisticsSystem.build(ctx.getSource().getPlayerOrException(), IntegerArgumentType.getInteger(ctx, "id")) ? 1 : 0)));

        d.register(Commands.literal("carbon").executes(ctx -> { CarbonTraceSystem.show(ctx.getSource().getPlayerOrException()); return 1; })
                .then(Commands.argument("id", IntegerArgumentType.integer(1, 5))
                        .executes(ctx -> CarbonTraceSystem.implement(ctx.getSource().getPlayerOrException(), IntegerArgumentType.getInteger(ctx, "id")) ? 1 : 0)));

        d.register(Commands.literal("cockpit").executes(ctx -> { CockpitSystem.show(ctx.getSource().getPlayerOrException()); return 1; }));

        d.register(Commands.literal("factory").executes(ctx -> { FutureFactorySystem.show(ctx.getSource().getPlayerOrException()); return 1; })
                .then(Commands.argument("id", IntegerArgumentType.integer(1, 6))
                        .executes(ctx -> FutureFactorySystem.install(ctx.getSource().getPlayerOrException(), IntegerArgumentType.getInteger(ctx, "id")) ? 1 : 0)));

        CampusLife.LOGGER.info("V13 commands registered! (pricing/vip/media/schedule/community/foundation/logistics/carbon/cockpit/factory)");
    }
}
