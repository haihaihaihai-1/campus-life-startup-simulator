package com.campus.economy;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import com.campus.CampusLife;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 经济系统命令拼图 - 银行/市场交易命令
 * 参考: Forge Command 系统
 */
@Mod.EventBusSubscriber(modid = CampusLife.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EconomyCommands {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSource> dispatcher = event.getDispatcher();

        // /money - 查看余额
        dispatcher.register(Commands.literal("money")
                .executes(ctx -> showMoney(ctx.getSource())));

        // /money pay <player> <amount> - 付款给其他玩家
        dispatcher.register(Commands.literal("money")
                .then(Commands.literal("pay")
                        .then(Commands.argument("target", StringArgumentType.string())
                                .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                        .executes(ctx -> payMoney(ctx.getSource(),
                                                StringArgumentType.getString(ctx, "target"),
                                                IntegerArgumentType.getInteger(ctx, "amount")))))));

        // /business - 查看创业信息
        dispatcher.register(Commands.literal("business")
                .executes(ctx -> showBusiness(ctx.getSource())));

        // /business invest <amount> - 投资创业(消耗金钱获得经验)
        dispatcher.register(Commands.literal("business")
                .then(Commands.literal("invest")
                        .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                .executes(ctx -> invest(ctx.getSource(),
                                        IntegerArgumentType.getInteger(ctx, "amount"))))));

        // /market sell <item> <price> - 上架商品
        dispatcher.register(Commands.literal("market")
                .then(Commands.literal("sell")
                        .then(Commands.argument("item", StringArgumentType.string())
                                .then(Commands.argument("price", IntegerArgumentType.integer(1))
                                        .executes(ctx -> sellItem(ctx.getSource(),
                                                StringArgumentType.getString(ctx, "item"),
                                                IntegerArgumentType.getInteger(ctx, "price")))))));

        // /market buy <item> - 购买商品
        dispatcher.register(Commands.literal("market")
                .then(Commands.literal("buy")
                        .then(Commands.argument("item", StringArgumentType.string())
                                .executes(ctx -> buyItem(ctx.getSource(),
                                        StringArgumentType.getString(ctx, "item"))))));

        // /shop - 创业商店(购买原材料)
        dispatcher.register(Commands.literal("shop")
                .executes(ctx -> openShop(ctx.getSource())));

        CampusLife.LOGGER.info("Economy commands registered!");
    }

    private static int showMoney(CommandSource source) throws CommandSyntaxException {
        ServerPlayerEntity player = source.getPlayerOrException();
        player.getCapability(MoneyCapability.MONEY_CAP).ifPresent(money -> {
            player.sendMessage(new StringTextComponent(
                    "\u00a7a\u2756 \u6821\u56ed\u94f6\u884c \u00a7r\u4f59\u989d: \u00a76" + money.getMoney() + " \u00a7e\u91d1\u5e01"), player.getUUID());
        });
        return 1;
    }

    private static int payMoney(CommandSource source, String targetName, int amount) throws CommandSyntaxException {
        ServerPlayerEntity player = source.getPlayerOrException();
        ServerPlayerEntity target = source.getServer().getPlayerList().getPlayerByName(targetName);
        if (target == null) {
            player.sendMessage(new StringTextComponent("\u00a7c\u73a9\u5bb6 " + targetName + " \u4e0d\u5728\u7ebf!"), player.getUUID());
            return 0;
        }
        boolean success = player.getCapability(MoneyCapability.MONEY_CAP).map(m -> {
            if (m.spendMoney(amount)) {
                target.getCapability(MoneyCapability.MONEY_CAP).ifPresent(t -> t.addMoney(amount));
                player.sendMessage(new StringTextComponent(
                        "\u00a7a\u5df2\u5411 " + targetName + " \u8f6c\u8d26 " + amount + " \u91d1\u5e01"), player.getUUID());
                target.sendMessage(new StringTextComponent(
                        "\u00a7a\u6536\u5230\u6765\u81ea " + player.getName().getString() + " \u7684\u8f6c\u8d26 " + amount + " \u91d1\u5e01"), target.getUUID());
                return true;
            }
            player.sendMessage(new StringTextComponent("\u00a7c\u4f59\u989d\u4e0d\u8db3!"), player.getUUID());
            return false;
        }).orElse(false);
        return success ? 1 : 0;
    }

    private static int showBusiness(CommandSource source) throws CommandSyntaxException {
        ServerPlayerEntity player = source.getPlayerOrException();
        player.getCapability(SkillCapability.SKILL_CAP).ifPresent(skill -> {
            player.sendMessage(new StringTextComponent(
                    "\u00a76\u2500\u2500\u2500\u2500 \u521b\u4e1a\u9762\u677f \u2500\u2500\u2500\u2500"), player.getUUID());
            player.sendMessage(new StringTextComponent(
                    "\u00a7e\u7b49\u7ea7: \u00a7f" + skill.getLevel() + "  " + skill.getRank()), player.getUUID());
            player.sendMessage(new StringTextComponent(
                    "\u00a7e\u7ecf\u9a8c: \u00a7f" + skill.getExp() + "/" + (skill.getLevel() * 100)), player.getUUID());
        });
        player.getCapability(MoneyCapability.MONEY_CAP).ifPresent(money -> {
            player.sendMessage(new StringTextComponent(
                    "\u00a7e\u8d44\u91d1: \u00a76" + money.getMoney() + " \u91d1\u5e01"), player.getUUID());
        });
        return 1;
    }

    private static int invest(CommandSource source, int amount) throws CommandSyntaxException {
        ServerPlayerEntity player = source.getPlayerOrException();
        boolean success = player.getCapability(MoneyCapability.MONEY_CAP).map(m -> {
            if (m.spendMoney(amount)) {
                int exp = amount / 10;
                player.getCapability(SkillCapability.SKILL_CAP).ifPresent(s -> s.addExp(exp));
                player.sendMessage(new StringTextComponent(
                        "\u00a7a\u6295\u8d44\u6210\u529f! \u6d88\u8017 " + amount + " \u91d1\u5e01, \u83b7\u5f97 " + exp + " \u521b\u4e1a\u7ecf\u9a8c"), player.getUUID());
                return true;
            }
            player.sendMessage(new StringTextComponent("\u00a7c\u8d44\u91d1\u4e0d\u8db3!"), player.getUUID());
            return false;
        }).orElse(false);
        return success ? 1 : 0;
    }

    private static int sellItem(CommandSource source, String item, int price) throws CommandSyntaxException {
        ServerPlayerEntity player = source.getPlayerOrException();
        player.sendMessage(new StringTextComponent(
                "\u00a7a\u5df2\u4e0a\u67b6: " + item + " \u4ef7\u683c: " + price + " \u91d1\u5e01"), player.getUUID());
        return 1;
    }

    private static int buyItem(CommandSource source, String item) throws CommandSyntaxException {
        ServerPlayerEntity player = source.getPlayerOrException();
        player.sendMessage(new StringTextComponent(
                "\u00a7e\u5e02\u573a\u7cfb\u7edf\u5f00\u53d1\u4e2d... \u8d2d\u4e70: " + item), player.getUUID());
        return 1;
    }

    private static int openShop(CommandSource source) throws CommandSyntaxException {
        ServerPlayerEntity player = source.getPlayerOrException();
        player.sendMessage(new StringTextComponent(
                "\u00a76\u2500\u2500\u2500\u2500 \u6821\u56ed\u521b\u4e1a\u5546\u5e97 \u2500\u2500\u2500\u2500"), player.getUUID());
        player.sendMessage(new StringTextComponent(
                "\u00a7e\u8f93\u5165 /business invest <\u91d1\u989d> \u6295\u8d44\u521b\u4e1a"), player.getUUID());
        player.sendMessage(new StringTextComponent(
                "\u00a7e\u8f93\u5165 /money pay <\u73a9\u5bb6> <\u91d1\u989d> \u8f6c\u8d26"), player.getUUID());
        player.sendMessage(new StringTextComponent(
                "\u00a7e\u8f93\u5165 /market sell <\u7269\u54c1> <\u4ef7\u683c> \u4e0a\u67b6\u5546\u54c1"), player.getUUID());
        return 1;
    }
}
