package com.campus.economy;

import com.campus.CampusLife;
import com.campus.systems.*;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CampusLife.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class FinalCommands {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSource> d = event.getDispatcher();

        // === 税务命令 ===
        d.register(Commands.literal("tax")
                .executes(ctx -> { TaxSystem.showTaxInfo(ctx.getSource().getPlayerOrException()); return 1; }));

        // === 事件命令 ===
        d.register(Commands.literal("event")
                .executes(ctx -> { RandomEventSystem.showEventStatus(ctx.getSource().getPlayerOrException()); return 1; }));

        // === 广告命令 ===
        d.register(Commands.literal("ad")
                .executes(ctx -> { MarketingSystem.showCampaigns(ctx.getSource().getPlayerOrException()); return 1; })
                .then(Commands.argument("type", IntegerArgumentType.integer(1, 5))
                        .then(Commands.argument("budget", IntegerArgumentType.integer(1))
                                .executes(ctx -> {
                                    int typeIdx = IntegerArgumentType.getInteger(ctx, "type");
                                    int budget = IntegerArgumentType.getInteger(ctx, "budget");
                                    MarketingSystem.AdType type = MarketingSystem.getTypeByIndex(typeIdx);
                                    if (type == null) return 0;
                                    return MarketingSystem.launchCampaign(ctx.getSource().getPlayerOrException(), type, budget) ? 1 : 0;
                                }))));

        // === 专利命令 ===
        d.register(Commands.literal("patent")
                .executes(ctx -> { PatentSystem.showPatents(ctx.getSource().getPlayerOrException()); return 1; })
                .then(Commands.literal("file")
                        .executes(ctx -> PatentSystem.filePatent(ctx.getSource().getPlayerOrException()) ? 1 : 0)));

        // === 导师命令 ===
        d.register(Commands.literal("mentor")
                .executes(ctx -> { MentorSystem.showMentors(ctx.getSource().getPlayerOrException()); return 1; })
                .then(Commands.argument("id", IntegerArgumentType.integer(1, 5))
                        .executes(ctx -> MentorSystem.consultMentor(ctx.getSource().getPlayerOrException(),
                                IntegerArgumentType.getInteger(ctx, "id")) ? 1 : 0)));

        // === 签到命令 ===
        d.register(Commands.literal("checkin")
                .executes(ctx -> { DailyRewardSystem.showStatus(ctx.getSource().getPlayerOrException()); return 1; })
                .then(Commands.literal("claim")
                        .executes(ctx -> { DailyRewardSystem.checkDailyReward(ctx.getSource().getPlayerOrException()); return 1; })));

        // === 统计面板 ===
        d.register(Commands.literal("stats")
                .executes(ctx -> { StatsSystem.showDashboard(ctx.getSource().getPlayerOrException()); return 1; }));

        CampusLife.LOGGER.info("Final commands registered! (tax/event/ad/patent/mentor/checkin/stats)");
    }
}
