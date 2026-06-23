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

public class MarketStallContainer extends Container {
    public static final int SLOTS = 6;
    private final IItemHandler inventory;
    private final MarketStallTileEntity tileEntity;
    private final IWorldPosCallable worldPos;

    public MarketStallContainer(int containerId, PlayerInventory playerInventory, MarketStallTileEntity te) {
        super(CampusBlocks.MARKET_STALL_CONTAINER.get(), containerId);
        this.tileEntity = te;
        this.inventory = new ItemStackHandler(SLOTS);
        this.worldPos = IWorldPosCallable.create(te.getLevel(), te.getBlockPos());
        // 6个商品槽 (2x3)
        for (int row = 0; row < 2; row++) {
            for (int col = 0; col < 3; col++) {
                this.addSlot(new SlotItemHandler(inventory, col + row * 3, 62 + col * 18, 17 + row * 18));
            }
        }
        // 玩家背包
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
        // 快捷栏
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }
    }

    public MarketStallContainer(int containerId, PlayerInventory playerInventory, PacketBuffer extraData) {
        super(CampusBlocks.MARKET_STALL_CONTAINER.get(), containerId);
        this.inventory = new ItemStackHandler(SLOTS);
        TileEntity te = playerInventory.player.level.getBlockEntity(extraData.readBlockPos());
        this.tileEntity = te instanceof MarketStallTileEntity ? (MarketStallTileEntity) te : null;
        this.worldPos = IWorldPosCallable.NULL;
        for (int row = 0; row < 2; row++) {
            for (int col = 0; col < 3; col++) {
                this.addSlot(new SlotItemHandler(inventory, col + row * 3, 62 + col * 18, 17 + row * 18));
            }
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

    public int getMarketItems() { return tileEntity != null ? tileEntity.getMarketItems() : 0; }
    public int getAvgPrice() { return tileEntity != null ? tileEntity.getAvgPrice() : 0; }

    @Override
    public boolean stillValid(@Nonnull PlayerEntity player) {
        return stillValid(worldPos, player, CampusBlocks.MARKET_STALL.get());
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

    public static net.minecraft.inventory.container.ContainerType<MarketStallContainer> createContainerType() {
        return IForgeContainerType.create(MarketStallContainer::new);
    }
}
