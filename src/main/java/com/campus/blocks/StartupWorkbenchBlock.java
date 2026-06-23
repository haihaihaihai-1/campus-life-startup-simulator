package com.campus.blocks;

import com.campus.economy.MoneyCapability;
import com.campus.economy.SkillCapability;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

/**
 * 创业工坊方块 - 右键打开GUI界面
 * 参考模式: McJtyLib GenericGuiContainer (MIT) + Forge原生Container模式
 */
public class StartupWorkbenchBlock extends Block implements ITileEntityProvider {

    public StartupWorkbenchBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity newBlockEntity(IBlockReader world) {
        return new StartupWorkbenchTileEntity(CampusBlocks.STARTUP_WORKBENCH_TE.get());
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if (!world.isClientSide) {
            TileEntity te = world.getBlockEntity(pos);
            if (te instanceof StartupWorkbenchTileEntity) {
                StartupWorkbenchTileEntity workbench = (StartupWorkbenchTileEntity) te;
                player.getCapability(MoneyCapability.MONEY_CAP).ifPresent(money -> {
                    player.getCapability(SkillCapability.SKILL_CAP).ifPresent(skill -> {
                        workbench.updateData(money.getMoney(), skill.getLevel(), skill.getExp(),
                            com.campus.systems.EmployeeSystem.calculateTotalEmployees(player.getUUID()));
                    });
                });
                NetworkHooks.openGui((ServerPlayerEntity) player, workbench, pos);
            }
            return ActionResultType.CONSUME;
        }
        return ActionResultType.SUCCESS;
    }
}
