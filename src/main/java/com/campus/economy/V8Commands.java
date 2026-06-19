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
public class V8Commands {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSource> d = event.getDispatcher();

        d.register(Commands.literal("vc").executes(ctx -> { VCSystem.show(ctx.getSource().getPlayerOrException()); return 1; })
                .then(Commands.argument("round", IntegerArgumentType.integer(1, 6))
                        .executes(ctx -> VCSystem.raiseRound(ctx.getSource().getPlayerOrException(), IntegerArgumentType.getInteger(ctx, "round")) ? 1 : 0)));

        d.register(Commands.literal("noncompete").executes(ctx -> { NonCompeteSystem.show(ctx.getSource().getPlayerOrException()); return 1; })
                .then(Commands.argument("type", IntegerArgumentType.integer(1, 4))
                        .executes(ctx -> NonCompeteSystem.sign(ctx.getSource().getPlayerOrException(), IntegerArgumentType.getInteger(ctx, "type")) ? 1 : 0)));

        d.register(Commands.literal("scf").executes(ctx -> { SupplyChainFinanceSystem.show(ctx.getSource().getPlayerOrException()); return 1; })
                .then(Commands.literal("finance").then(Commands.argument("amount", IntegerArgumentType.integer(1))
                        .executes(ctx -> SupplyChainFinanceSystem.finance(ctx.getSource().getPlayerOrException(), IntegerArgumentType.getInteger(ctx, "amount")) ? 1 : 0)))
                .then(Commands.literal("upgrade").executes(ctx -> SupplyChainFinanceSystem.upgradeCredit(ctx.getSource().getPlayerOrException()) ? 1 : 0)));

        d.register(Commands.literal("green").executes(ctx -> { GreenSystem.show(ctx.getSource().getPlayerOrException()); return 1; })
                .then(Commands.literal("buy").then(Commands.argument("amount", IntegerArgumentType.integer(1))
                        .executes(ctx -> GreenSystem.buyCredits(ctx.getSource().getPlayerOrException(), IntegerArgumentType.getInteger(ctx, "amount")) ? 1 : 0)))
                .then(Commands.literal("certify").executes(ctx -> GreenSystem.certify(ctx.getSource().getPlayerOrException()) ? 1 : 0)));

        d.register(Commands.literal("lawsuit").executes(ctx -> { LawsuitSystem.show(ctx.getSource().getPlayerOrException()); return 1; })
                .then(Commands.literal("file").then(Commands.argument("target", StringArgumentType.string()).then(Commands.argument("amount", IntegerArgumentType.integer(1))
                        .executes(ctx -> LawsuitSystem.file(ctx.getSource().getPlayerOrException(), StringArgumentType.getString(ctx, "target"), IntegerArgumentType.getInteger(ctx, "amount")) ? 1 : 0)))));

        d.register(Commands.literal("collab").executes(ctx -> { BrandCollabSystem.show(ctx.getSource().getPlayerOrException()); return 1; })
                .then(Commands.argument("type", IntegerArgumentType.integer(1, 5))
                        .executes(ctx -> BrandCollabSystem.launch(ctx.getSource().getPlayerOrException(), IntegerArgumentType.getInteger(ctx, "type")) ? 1 : 0)));

        d.register(Commands.literal("option").executes(ctx -> { StockOptionSystem.show(ctx.getSource().getPlayerOrException()); return 1; })
                .then(Commands.literal("grant").then(Commands.argument("amount", IntegerArgumentType.integer(1))
                        .executes(ctx -> StockOptionSystem.grant(ctx.getSource().getPlayerOrException(), IntegerArgumentType.getInteger(ctx, "amount")) ? 1 : 0)))
                .then(Commands.literal("exercise").executes(ctx -> StockOptionSystem.exercise(ctx.getSource().getPlayerOrException()) ? 1 : 0)));

        d.register(Commands.literal("data").executes(ctx -> { DataAssetSystem.show(ctx.getSource().getPlayerOrException()); return 1; })
                .then(Commands.literal("acquire").then(Commands.argument("source", IntegerArgumentType.integer(1, 6))
                        .executes(ctx -> DataAssetSystem.acquire(ctx.getSource().getPlayerOrException(), IntegerArgumentType.getInteger(ctx, "source")) ? 1 : 0)))
                .then(Commands.literal("monetize").executes(ctx -> DataAssetSystem.monetize(ctx.getSource().getPlayerOrException()) ? 1 : 0)));

        d.register(Commands.literal("forex").executes(ctx -> { CrossBorderSystem.show(ctx.getSource().getPlayerOrException()); return 1; })
                .then(Commands.literal("buy").then(Commands.argument("currency", IntegerArgumentType.integer(1, 6)).then(Commands.argument("gold", IntegerArgumentType.integer(1))
                        .executes(ctx -> CrossBorderSystem.exchange(ctx.getSource().getPlayerOrException(), IntegerArgumentType.getInteger(ctx, "currency"), IntegerArgumentType.getInteger(ctx, "gold")) ? 1 : 0))))
                .then(Commands.literal("sell").then(Commands.argument("currency", IntegerArgumentType.integer(1, 6)).then(Commands.argument("foreign", IntegerArgumentType.integer(1))
                        .executes(ctx -> CrossBorderSystem.convertBack(ctx.getSource().getPlayerOrException(), IntegerArgumentType.getInteger(ctx, "currency"), IntegerArgumentType.getInteger(ctx, "foreign")) ? 1 : 0)))));

        d.register(Commands.literal("metaverse").executes(ctx -> { MetaverseSystem.show(ctx.getSource().getPlayerOrException()); return 1; })
                .then(Commands.argument("id", IntegerArgumentType.integer(1, 7))
                        .executes(ctx -> MetaverseSystem.buy(ctx.getSource().getPlayerOrException(), IntegerArgumentType.getInteger(ctx, "id")) ? 1 : 0)));

        CampusLife.LOGGER.info("V8 commands registered! (vc/noncompete/scf/green/lawsuit/collab/option/data/forex/metaverse)");
    }
}
