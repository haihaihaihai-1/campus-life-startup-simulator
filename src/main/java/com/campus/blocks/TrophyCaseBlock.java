package com.campus.blocks;

import com.campus.gui.TrophyContainer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class TrophyCaseBlock extends HorizontalBlock {
    public TrophyCaseBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext ctx) {
        return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
    }
    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> b) { b.add(FACING); }
    @Override
    public ActionResultType use(BlockState s, World w, BlockPos p, PlayerEntity pl, Hand h, BlockRayTraceResult r) {
        if (!w.isClientSide && pl instanceof ServerPlayerEntity) {
            INamedContainerProvider provider = new SimpleNamedContainerProvider(
                (id, inv, player) -> new TrophyContainer(id, inv, p),
                new StringTextComponent("荣誉展示柜"));
            NetworkHooks.openGui((ServerPlayerEntity)pl, provider, buf -> buf.writeBlockPos(p));
        }
        return ActionResultType.SUCCESS;
    }
}
