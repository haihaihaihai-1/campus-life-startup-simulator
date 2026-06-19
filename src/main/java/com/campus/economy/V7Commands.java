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
public class V7Commands {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSource> d = event.getDispatcher();

        // 银行理财
        d.register(Commands.literal("savings")
                .executes(ctx -> { SavingsSystem.show(ctx.getSource().getPlayerOrException()); return 1; })
                .then(Commands.literal("deposit")
                        .then(Commands.argument("type", IntegerArgumentType.integer(1, 5))
                                .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                        .executes(ctx -> SavingsSystem.deposit(ctx.getSource().getPlayerOrException(),
                                                IntegerArgumentType.getInteger(ctx, "type"),
                                                IntegerArgumentType.getInteger(ctx, "amount")) ? 1 : 0))))
                .then(Commands.literal("withdraw")
                        .executes(ctx -> SavingsSystem.withdraw(ctx.getSource().getPlayerOrException()) ? 1 : 0)));

        // 营业执照
        d.register(Commands.literal("license")
                .executes(ctx -> { LicenseSystem.show(ctx.getSource().getPlayerOrException()); return 1; })
                .then(Commands.argument("id", IntegerArgumentType.integer(1, 7))
                        .executes(ctx -> LicenseSystem.apply(ctx.getSource().getPlayerOrException(),
                                IntegerArgumentType.getInteger(ctx, "id")) ? 1 : 0)));

        // 创新挑战
        d.register(Commands.literal("challenge")
                .executes(ctx -> { InnovationChallengeSystem.show(ctx.getSource().getPlayerOrException()); return 1; })
                .then(Commands.literal("join")
                        .executes(ctx -> { InnovationChallengeSystem.join(ctx.getSource().getPlayerOrException()); return 1; })));

        // 供应合同
        d.register(Commands.literal("contract")
                .executes(ctx -> { ContractSystem.show(ctx.getSource().getPlayerOrException()); return 1; })
                .then(Commands.argument("id", IntegerArgumentType.integer(1, 5))
                        .executes(ctx -> ContractSystem.sign(ctx.getSource().getPlayerOrException(),
                                IntegerArgumentType.getInteger(ctx, "id")) ? 1 : 0)));

        // 市场调研
        d.register(Commands.literal("research_market")
                .executes(ctx -> { MarketResearchSystem.show(ctx.getSource().getPlayerOrException()); return 1; })
                .then(Commands.argument("id", IntegerArgumentType.integer(1, 5))
                        .executes(ctx -> MarketResearchSystem.purchase(ctx.getSource().getPlayerOrException(),
                                IntegerArgumentType.getInteger(ctx, "id")) ? 1 : 0)));

        // 企业文化
        d.register(Commands.literal("culture")
                .executes(ctx -> { CultureSystem.show(ctx.getSource().getPlayerOrException()); return 1; })
                .then(Commands.argument("id", IntegerArgumentType.integer(1, 6))
                        .executes(ctx -> CultureSystem.establish(ctx.getSource().getPlayerOrException(),
                                IntegerArgumentType.getInteger(ctx, "id")) ? 1 : 0)));

        // 退税
        d.register(Commands.literal("taxreturn")
                .executes(ctx -> { TaxReturnSystem.show(ctx.getSource().getPlayerOrException()); return 1; }));

        // 跨区域扩张
        d.register(Commands.literal("expand")
                .executes(ctx -> { ExpansionSystem.show(ctx.getSource().getPlayerOrException()); return 1; })
                .then(Commands.argument("id", IntegerArgumentType.integer(1, 8))
                        .executes(ctx -> ExpansionSystem.expand(ctx.getSource().getPlayerOrException(),
                                IntegerArgumentType.getInteger(ctx, "id")) ? 1 : 0)));

        CampusLife.LOGGER.info("V7 commands registered! (savings/license/challenge/contract/research_market/culture/taxreturn/expand)");
    }
}
