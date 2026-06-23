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
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import javax.annotation.Nonnull;

public class BankCounterContainer extends Container {
    public static final int SLOTS = 3;
    private final ItemStackHandler inventory;
    private final BankCounterTileEntity tileEntity;
    private final IWorldPosCallable worldPos;

    public BankCounterContainer(int containerId, PlayerInventory playerInventory, BankCounterTileEntity te) {
        super(CampusBlocks.BANK_COUNTER_CONTAINER.get(), containerId);
        this.tileEntity = te;
        this.inventory = new ItemStackHandler(SLOTS);
        this.worldPos = IWorldPosCallable.create(te.getLevel(), te.getBlockPos());
        for (int col = 0; col < SLOTS; col++) {
            this.addSlot(new SlotItemHandler(inventory, col, 62 + col * 18, 17));
        }
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }
    }

    public BankCounterContainer(int containerId, PlayerInventory playerInventory, PacketBuffer extraData) {
        super(CampusBlocks.BANK_COUNTER_CONTAINER.get(), containerId);
        this.inventory = new ItemStackHandler(SLOTS);
        TileEntity te = playerInventory.player.level.getBlockEntity(extraData.readBlockPos());
        this.tileEntity = te instanceof BankCounterTileEntity ? (BankCounterTileEntity) te : null;
        this.worldPos = IWorldPosCallable.NULL;
        for (int col = 0; col < SLOTS; col++) {
            this.addSlot(new SlotItemHandler(inventory, col, 62 + col * 18, 17));
        }
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }
    }

    public int getMoney() { return tileEntity != null ? tileEntity.getCachedMoney() : 0; }
    public int getLoan() { return tileEntity != null ? tileEntity.getCachedLoan() : 0; }

    @Override
    public boolean stillValid(@Nonnull PlayerEntity player) {
        return stillValid(worldPos, player, CampusBlocks.BANK_COUNTER.get());
    }

    @Override
    @Nonnull
    public ItemStack quickMoveStack(@Nonnull PlayerEntity player, int index) {
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

    public static net.minecraft.inventory.container.ContainerType<BankCounterContainer> createContainerType() {
        return IForgeContainerType.create(BankCounterContainer::new);
    }
}
