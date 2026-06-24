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
public class ResearchLabContainer extends Container {
    private final ResearchLabTileEntity tileEntity;
    private final IWorldPosCallable worldPos;
    public ResearchLabContainer(int id, PlayerInventory inv, ResearchLabTileEntity te) {
        super(CampusBlocks.RESEARCH_LAB_CONTAINER.get(), id);
        this.tileEntity = te;
        this.worldPos = IWorldPosCallable.create(te.getLevel(), te.getBlockPos());
        addPlayerSlots(inv);
    }
    public ResearchLabContainer(int id, PlayerInventory inv, PacketBuffer data) {
        super(CampusBlocks.RESEARCH_LAB_CONTAINER.get(), id);
        TileEntity te = inv.player.level.getBlockEntity(data.readBlockPos());
        this.tileEntity = te instanceof ResearchLabTileEntity ? (ResearchLabTileEntity) te : null;
        this.worldPos = IWorldPosCallable.NULL;
        addPlayerSlots(inv);
    }
    private void addPlayerSlots(PlayerInventory inv) {
        for (int r = 0; r < 3; r++) for (int c = 0; c < 9; c++) addSlot(new Slot(inv, c + r * 9 + 9, 8 + c * 18, 84 + r * 18));
        for (int c = 0; c < 9; c++) addSlot(new Slot(inv, c, 8 + c * 18, 142));
    }
    public int getMoney() { return tileEntity != null ? tileEntity.getCachedMoney() : 0; }
    public int getTechCount() { return tileEntity != null ? tileEntity.getCachedTechCount() : 0; }
    @Override public boolean stillValid(@Nonnull PlayerEntity p) { return stillValid(worldPos, p, CampusBlocks.RESEARCH_LAB.get()); }
    @Override @Nonnull public ItemStack quickMoveStack(@Nonnull PlayerEntity p, int i) { return ItemStack.EMPTY; }
    public static net.minecraft.inventory.container.ContainerType<ResearchLabContainer> createContainerType() { return IForgeContainerType.create(ResearchLabContainer::new); }
}
