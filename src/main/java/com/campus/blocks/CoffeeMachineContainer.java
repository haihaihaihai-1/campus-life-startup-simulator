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

public class CoffeeMachineContainer extends Container {
    public static final int SLOTS = 2;
    private final ItemStackHandler inventory;
    private final CoffeeMachineTileEntity tileEntity;
    private final IWorldPosCallable worldPos;

    public CoffeeMachineContainer(int containerId, PlayerInventory playerInventory, CoffeeMachineTileEntity te) {
        super(CampusBlocks.COFFEE_MACHINE_CONTAINER.get(), containerId);
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

    public CoffeeMachineContainer(int containerId, PlayerInventory playerInventory, PacketBuffer extraData) {
        super(CampusBlocks.COFFEE_MACHINE_CONTAINER.get(), containerId);
        this.inventory = new ItemStackHandler(SLOTS);
        TileEntity te = playerInventory.player.level.getBlockEntity(extraData.readBlockPos());
        this.tileEntity = te instanceof CoffeeMachineTileEntity ? (CoffeeMachineTileEntity) te : null;
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

    public int getCoffeeCount() { return tileEntity != null ? tileEntity.getCoffeeCount() : 0; }

    @Override
    public boolean stillValid(@Nonnull PlayerEntity player) {
        return stillValid(worldPos, player, CampusBlocks.COFFEE_MACHINE.get());
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

    public static net.minecraft.inventory.container.ContainerType<CoffeeMachineContainer> createContainerType() {
        return IForgeContainerType.create(CoffeeMachineContainer::new);
    }
}
