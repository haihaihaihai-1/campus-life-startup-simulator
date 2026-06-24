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
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import javax.annotation.Nonnull;
public class AuctionHouseContainer extends Container {
    public static final int SLOTS = 3;
    private final IItemHandler inventory;
    private final AuctionHouseTileEntity tileEntity;
    private final IWorldPosCallable worldPos;
    public AuctionHouseContainer(int id, PlayerInventory inv, AuctionHouseTileEntity te) {
        super(CampusBlocks.AUCTION_HOUSE_CONTAINER.get(), id);
        this.tileEntity = te;
        this.inventory = new ItemStackHandler(SLOTS);
        this.worldPos = IWorldPosCallable.create(te.getLevel(), te.getBlockPos());
        for (int i = 0; i < SLOTS; i++) addSlot(new SlotItemHandler(inventory, i, 62 + i * 18, 17));
        addPlayerSlots(inv);
    }
    public AuctionHouseContainer(int id, PlayerInventory inv, PacketBuffer data) {
        super(CampusBlocks.AUCTION_HOUSE_CONTAINER.get(), id);
        this.inventory = new ItemStackHandler(SLOTS);
        TileEntity te = inv.player.level.getBlockEntity(data.readBlockPos());
        this.tileEntity = te instanceof AuctionHouseTileEntity ? (AuctionHouseTileEntity) te : null;
        this.worldPos = IWorldPosCallable.NULL;
        for (int i = 0; i < SLOTS; i++) addSlot(new SlotItemHandler(inventory, i, 62 + i * 18, 17));
        addPlayerSlots(inv);
    }
    private void addPlayerSlots(PlayerInventory inv) {
        for (int r = 0; r < 3; r++) for (int c = 0; c < 9; c++) addSlot(new Slot(inv, c + r * 9 + 9, 8 + c * 18, 84 + r * 18));
        for (int c = 0; c < 9; c++) addSlot(new Slot(inv, c, 8 + c * 18, 142));
    }
    public int getMoney() { return tileEntity != null ? tileEntity.getCachedMoney() : 0; }
    public int getAuctionCount() { return tileEntity != null ? tileEntity.getCachedAuctionCount() : 0; }
    @Override public boolean stillValid(@Nonnull PlayerEntity p) { return stillValid(worldPos, p, CampusBlocks.AUCTION_HOUSE.get()); }
    @Override @Nonnull public ItemStack quickMoveStack(@Nonnull PlayerEntity p, int index) {
        Slot slot = this.slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack original = slot.getItem().copy();
        if (index < SLOTS) {
            if (!this.moveItemStackTo(slot.getItem(), SLOTS, this.slots.size(), true)) return ItemStack.EMPTY;
        } else {
            if (!this.moveItemStackTo(slot.getItem(), 0, SLOTS, false)) return ItemStack.EMPTY;
        }
        if (slot.getItem().isEmpty()) slot.set(ItemStack.EMPTY);
        else slot.setChanged();
        return original;
    }
    public static net.minecraft.inventory.container.ContainerType<AuctionHouseContainer> createContainerType() { return IForgeContainerType.create(AuctionHouseContainer::new); }
}
