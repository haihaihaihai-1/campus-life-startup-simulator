package com.campus.blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IWorldPosCallable;
import net.minecraftforge.common.extensions.IForgeContainerType;
import javax.annotation.Nonnull;
public class MediaCenterContainer extends Container {
    private final MediaCenterTileEntity tileEntity;
    private final IWorldPosCallable worldPos;
    public MediaCenterContainer(int id, PlayerInventory inv, MediaCenterTileEntity te) {
        super(CampusBlocks.MEDIA_CENTER_CONTAINER.get(), id);
        this.tileEntity = te;
        this.worldPos = IWorldPosCallable.create(te.getLevel(), te.getBlockPos());
        addPlayerSlots(inv);
    }
    public MediaCenterContainer(int id, PlayerInventory inv, PacketBuffer data) {
        super(CampusBlocks.MEDIA_CENTER_CONTAINER.get(), id);
        TileEntity te = inv.player.level.getBlockEntity(data.readBlockPos());
        this.tileEntity = te instanceof MediaCenterTileEntity ? (MediaCenterTileEntity) te : null;
        this.worldPos = IWorldPosCallable.NULL;
        addPlayerSlots(inv);
    }
    private void addPlayerSlots(PlayerInventory inv) {
        for (int r = 0; r < 3; r++) for (int c = 0; c < 9; c++) addSlot(new Slot(inv, c + r * 9 + 9, 8 + c * 18, 84 + r * 18));
        for (int c = 0; c < 9; c++) addSlot(new Slot(inv, c, 8 + c * 18, 142));
    }
    public int getMoney() { return tileEntity != null ? tileEntity.getCachedMoney() : 0; }
    public int getFollowerCount() { return tileEntity != null ? tileEntity.getCachedFollowerCount() : 0; }
    @Override public boolean stillValid(@Nonnull PlayerEntity p) { return stillValid(worldPos, p, CampusBlocks.MEDIA_CENTER.get()); }
    @Override @Nonnull public ItemStack quickMoveStack(@Nonnull PlayerEntity p, int i) { return ItemStack.EMPTY; }
    public static net.minecraft.inventory.container.ContainerType<MediaCenterContainer> createContainerType() { return IForgeContainerType.create(MediaCenterContainer::new); }
}
