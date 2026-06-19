package com.campus.economy;

import com.campus.CampusLife;
import com.campus.systems.*;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CampusLife.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class V6Commands {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSource> d = event.getDispatcher();

        // 保险
        d.register(Commands.literal("insurance")
                .executes(ctx -> { InsuranceSystem.show(ctx.getSource().getPlayerOrException()); return 1; })
                .then(Commands.argument("type", IntegerArgumentType.integer(1, 4))
                        .executes(ctx -> InsuranceSystem.buy(ctx.getSource().getPlayerOrException(),
                                IntegerArgumentType.getInteger(ctx, "type")) ? 1 : 0)));

        // 拍卖行
        d.register(Commands.literal("auction")
                .executes(ctx -> { AuctionSystem.show(ctx.getSource().getPlayerOrException()); return 1; })
                .then(Commands.literal("bid")
                        .then(Commands.argument("idx", IntegerArgumentType.integer(1, 10))
                                .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                        .executes(ctx -> AuctionSystem.bid(ctx.getSource().getPlayerOrException(),
                                                IntegerArgumentType.getInteger(ctx, "idx"),
                                                IntegerArgumentType.getInteger(ctx, "amount")) ? 1 : 0))))
                .then(Commands.literal("buyout")
                        .then(Commands.argument("idx", IntegerArgumentType.integer(1, 10))
                                .executes(ctx -> AuctionSystem.buyout(ctx.getSource().getPlayerOrException(),
                                        IntegerArgumentType.getInteger(ctx, "idx")) ? 1 : 0))));

        // 房地产
        d.register(Commands.literal("realestate")
                .executes(ctx -> { RealEstateSystem.show(ctx.getSource().getPlayerOrException()); return 1; })
                .then(Commands.literal("buy")
                        .then(Commands.argument("idx", IntegerArgumentType.integer(1, 6))
                                .executes(ctx -> RealEstateSystem.buy(ctx.getSource().getPlayerOrException(),
                                        IntegerArgumentType.getInteger(ctx, "idx")) ? 1 : 0)))
                .then(Commands.literal("sell")
                        .then(Commands.argument("idx", IntegerArgumentType.integer(1, 6))
                                .executes(ctx -> RealEstateSystem.sell(ctx.getSource().getPlayerOrException(),
                                        IntegerArgumentType.getInteger(ctx, "idx")) ? 1 : 0))));

        // 研发实验室
        d.register(Commands.literal("lab")
                .executes(ctx -> { LabSystem.show(ctx.getSource().getPlayerOrException()); return 1; })
                .then(Commands.argument("idx", IntegerArgumentType.integer(1, 6))
                        .executes(ctx -> LabSystem.run(ctx.getSource().getPlayerOrException(),
                                IntegerArgumentType.getInteger(ctx, "idx")) ? 1 : 0)));

        // 声誉
        d.register(Commands.literal("reputation")
                .executes(ctx -> { ReputationSystem.show(ctx.getSource().getPlayerOrException()); return 1; }));

        // 季节
        d.register(Commands.literal("season")
                .executes(ctx -> { SeasonalSystem.show(ctx.getSource().getPlayerOrException()); return 1; }));

        // 产业联盟
        d.register(Commands.literal("alliance")
                .executes(ctx -> { AllianceSystem.show(ctx.getSource().getPlayerOrException()); return 1; })
                .then(Commands.literal("create")
                        .then(Commands.argument("name", StringArgumentType.string())
                                .then(Commands.argument("industry", StringArgumentType.string())
                                        .executes(ctx -> AllianceSystem.create(ctx.getSource().getPlayerOrException(),
                                                StringArgumentType.getString(ctx, "name"),
                                                StringArgumentType.getString(ctx, "industry")) ? 1 : 0))))
                .then(Commands.literal("invite")
                        .then(Commands.argument("player", StringArgumentType.string())
                                .executes(ctx -> {
                                    net.minecraft.entity.player.ServerPlayerEntity target = ctx.getSource().getServer().getPlayerList().getPlayerByName(StringArgumentType.getString(ctx, "player"));
                                    if (target == null) return 0;
                                    return AllianceSystem.invite(ctx.getSource().getPlayerOrException(), target) ? 1 : 0;
                                })))
                .then(Commands.literal("contribute")
                        .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                .executes(ctx -> AllianceSystem.contribute(ctx.getSource().getPlayerOrException(),
                                        IntegerArgumentType.getInteger(ctx, "amount")) ? 1 : 0))));

        // 数据分析
        d.register(Commands.literal("analytics")
                .executes(ctx -> { AnalyticsSystem.showReport(ctx.getSource().getPlayerOrException()); return 1; }));

        CampusLife.LOGGER.info("V6 commands registered! (insurance/auction/realestate/lab/reputation/season/alliance/analytics)");
    }
}
