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
public class EmployeeCenterContainer extends Container {
    public static final int SLOTS = 3;
    private final IItemHandler inventory;
    private final EmployeeCenterTileEntity tileEntity;
    private final IWorldPosCallable worldPos;
    public EmployeeCenterContainer(int id, PlayerInventory inv, EmployeeCenterTileEntity te) {
        super(CampusBlocks.EMPLOYEE_CENTER_CONTAINER.get(), id);
        this.tileEntity = te;
        this.inventory = new ItemStackHandler(SLOTS);
        this.worldPos = IWorldPosCallable.create(te.getLevel(), te.getBlockPos());
        addSlots(inv);
    }
    public EmployeeCenterContainer(int id, PlayerInventory inv, PacketBuffer data) {
        super(CampusBlocks.EMPLOYEE_CENTER_CONTAINER.get(), id);
        this.inventory = new ItemStackHandler(SLOTS);
        TileEntity te = inv.player.level.getBlockEntity(data.readBlockPos());
        this.tileEntity = te instanceof EmployeeCenterTileEntity ? (EmployeeCenterTileEntity) te : null;
        this.worldPos = IWorldPosCallable.NULL;
        addSlots(inv);
    }
    private void addSlots(PlayerInventory inv) {
        for (int col = 0; col < SLOTS; col++) addSlot(new SlotItemHandler(inventory, col, 62 + col * 18, 17));
        for (int r = 0; r < 3; r++) for (int c = 0; c < 9; c++) addSlot(new Slot(inv, c + r * 9 + 9, 8 + c * 18, 84 + r * 18));
        for (int c = 0; c < 9; c++) addSlot(new Slot(inv, c, 8 + c * 18, 142));
    }
    public int getMoney() { return tileEntity != null ? tileEntity.getCachedMoney() : 0; }
    public int getEmployeeCount() { return tileEntity != null ? tileEntity.getCachedEmployeeCount() : 0; }
    @Override public boolean stillValid(@Nonnull PlayerEntity p) { return stillValid(worldPos, p, CampusBlocks.EMPLOYEE_CENTER.get()); }
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
    public static net.minecraft.inventory.container.ContainerType<EmployeeCenterContainer> createContainerType() { return IForgeContainerType.create(EmployeeCenterContainer::new); }
}
