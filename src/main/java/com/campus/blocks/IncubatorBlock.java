package com.campus.blocks;

import com.campus.gui.IncubatorContainer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.state.StateContainer;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class IncubatorBlock extends HorizontalBlock {
    public IncubatorBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateContainer.getBaseState().setValue(FACING, Direction.NORTH));
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos,
                                PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if (!world.isClientSide && player instanceof ServerPlayerEntity) {
            INamedContainerProvider provider = new SimpleNamedContainerProvider(
                (windowId, inv, p) -> new IncubatorContainer(windowId, inv, pos),
                new StringTextComponent("\u5b75\u5316\u5668")
            );
            NetworkHooks.openGui((ServerPlayerEntity) player, provider, buf -> buf.writeBlockPos(pos));
        }
        return ActionResultType.sidedSuccess(world.isClientSide);
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
}
