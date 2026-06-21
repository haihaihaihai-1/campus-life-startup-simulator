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

/** 地产中介台 - 右键打开地产 GUI */
public class RealtyKioskBlock extends HorizontalBlock {
    public RealtyKioskBlock(Properties p) { super(p); this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH)); }
    @Override protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> b) { b.add(FACING); }
    @Nullable @Override public BlockState getStateForPlacement(BlockItemUseContext ctx) {
        return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
    }
    @Override public ActionResultType use(BlockState s, World w, BlockPos pos, PlayerEntity pl, Hand h, BlockRayTraceResult hit) {
        if (!w.isClientSide && pl instanceof ServerPlayerEntity) {
            INamedContainerProvider provider = new SimpleNamedContainerProvider(
                (id, inv, p) -> CampusContainers.REALTY_CONTAINER.get().create(id, inv, encodeBuf(pos)),
                new TranslationTextComponent("block.campuslife.realty_kiosk"));
            NetworkHooks.openGui((ServerPlayerEntity) pl, provider, buf -> buf.writeBlockPos(pos));
        }
        return ActionResultType.sidedSuccess(w.isClientSide);
    }
    private static net.minecraft.network.PacketBuffer encodeBuf(BlockPos pos) {
        net.minecraft.network.PacketBuffer buf = new net.minecraft.network.PacketBuffer(io.netty.buffer.Unpooled.buffer());
        buf.writeBlockPos(pos); return buf;
    }
}
