package com.campus.blocks;

import com.campus.economy.MoneyCapability;
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

public class MarketStallBlock extends Block implements ITileEntityProvider {
    public MarketStallBlock(Properties properties) { super(properties); }

    @Override
    public boolean hasTileEntity(BlockState state) { return true; }

    @Nullable
    @Override
    public TileEntity newBlockEntity(IBlockReader world) {
        return new MarketStallTileEntity(CampusBlocks.MARKET_STALL_TE.get());
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if (!world.isClientSide) {
            TileEntity te = world.getBlockEntity(pos);
            if (te instanceof MarketStallTileEntity) {
                MarketStallTileEntity stall = (MarketStallTileEntity) te;
                player.getCapability(MoneyCapability.MONEY_CAP).ifPresent(money -> {
                    stall.updateData(8, 50);
                });
                NetworkHooks.openGui((ServerPlayerEntity) player, stall, pos);
            }
            return ActionResultType.CONSUME;
        }
        return ActionResultType.SUCCESS;
    }
}
