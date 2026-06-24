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
public class InsuranceCenterBlock extends Block implements ITileEntityProvider {
    public InsuranceCenterBlock(Properties p) { super(p); }
    @Override public boolean hasTileEntity(BlockState s) { return true; }
    @Nullable @Override public TileEntity newBlockEntity(IBlockReader w) { return new InsuranceCenterTileEntity(CampusBlocks.INSURANCE_CENTER_TE.get()); }
    @Override public ActionResultType use(BlockState s, World w, BlockPos p, PlayerEntity pl, Hand h, BlockRayTraceResult hit) {
        if (!w.isClientSide) {
            TileEntity te = w.getBlockEntity(p);
            if (te instanceof InsuranceCenterTileEntity) {
                InsuranceCenterTileEntity ic = (InsuranceCenterTileEntity) te;
                pl.getCapability(MoneyCapability.MONEY_CAP).ifPresent(m -> ic.updateData(m.getMoney(), 0));
                NetworkHooks.openGui((ServerPlayerEntity) pl, ic, p);
            }
            return ActionResultType.CONSUME;
        }
        return ActionResultType.SUCCESS;
    }
}
