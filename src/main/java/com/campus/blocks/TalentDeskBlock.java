package com.campus.blocks;

import com.campus.gui.CampusContainers;
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
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

public class TalentDeskBlock extends HorizontalBlock {
    public TalentDeskBlock(Properties props) { super(props); this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH)); }
    @Override protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> b) { b.add(FACING); }
    @Nullable @Override public BlockState getStateForPlacement(BlockItemUseContext ctx) { return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite()); }
    @Override public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if (!world.isClientSide && player instanceof ServerPlayerEntity) {
            ServerPlayerEntity sp = (ServerPlayerEntity) player;
            INamedContainerProvider provider = new SimpleNamedContainerProvider(
                (id, inv, p) -> CampusContainers.TALENT_CONTAINER.get().create(id, inv,
                    new net.minecraft.network.PacketBuffer(io.netty.buffer.Unpooled.buffer()).writeBlockPos(pos)),
                new TranslationTextComponent("block.campuslife.talent_desk"));
            NetworkHooks.openGui(sp, provider, buf -> buf.writeBlockPos(pos));
        }
        return ActionResultType.sidedSuccess(world.isClientSide);
    }
}
