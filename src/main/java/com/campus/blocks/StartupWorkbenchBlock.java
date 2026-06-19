package com.campus.blocks;

import com.campus.economy.MoneyCapability;
import com.campus.economy.SkillCapability;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

/**
 * 创业工坊方块 - 右键交互触发创业功能
 * 参考: Alchemistry 交互方块模式 + Forge Block 交互系统
 */
public class StartupWorkbenchBlock extends Block {

    public StartupWorkbenchBlock(Properties properties) {
        super(properties);
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if (!world.isClientSide) {
            player.getCapability(MoneyCapability.MONEY_CAP).ifPresent(money -> {
                player.getCapability(SkillCapability.SKILL_CAP).ifPresent(skill -> {
                    player.sendMessage(new StringTextComponent(
                            "\u00a76\u2500\u2500\u2500\u2500 \u521b\u4e1a\u5de5\u574a \u2500\u2500\u2500\u2500"), player.getUUID());
                    player.sendMessage(new StringTextComponent(
                            "\u00a7e\u5f53\u524d\u8d44\u91d1: \u00a76" + money.getMoney() + " \u91d1\u5e01"), player.getUUID());
                    player.sendMessage(new StringTextComponent(
                            "\u00a7e\u521b\u4e1a\u7b49\u7ea7: \u00a7f" + skill.getLevel() + " " + skill.getRank()), player.getUUID());
                    player.sendMessage(new StringTextComponent(
                            "\u00a7e\u521b\u4e1a\u7ecf\u9a8c: \u00a7f" + skill.getExp() + "/" + (skill.getLevel() * 100)), player.getUUID());
                    player.sendMessage(new StringTextComponent(
                            "\u00a7a\u8f93\u5165 /business invest <\u91d1\u989d> \u6295\u8d44\u521b\u4e1a"), player.getUUID());
                    player.sendMessage(new StringTextComponent(
                            "\u00a7a\u8f93\u5165 /market sell <\u7269\u54c1> <\u4ef7\u683c> \u4e0a\u67b6\u5546\u54c1"), player.getUUID());
                    player.sendMessage(new StringTextComponent(
                            "\u00a7a\u8f93\u5165 /money pay <\u73a9\u5bb6> <\u91d1\u989d> \u8f6c\u8d26"), player.getUUID());
                });
            });
        }
        return ActionResultType.sidedSuccess(world.isClientSide);
    }
}
