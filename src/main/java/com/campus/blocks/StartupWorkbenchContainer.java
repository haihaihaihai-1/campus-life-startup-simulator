package com.campus.blocks;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
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

/**
 * 创业工坊容器
 * 参考模式: McJtyLib GenericContainer (MIT) + Forge原生Container
 * 管理: 9个物品槽(原材料) + 玩家背包 + 数据同步
 */
public class StartupWorkbenchContainer extends Container {

    public static final int SLOTS = 9;
    private final IItemHandler inventory;
    private final StartupWorkbenchTileEntity tileEntity;
    private final IWorldPosCallable worldPos;

    // 服务端构造器
    public StartupWorkbenchContainer(int containerId, PlayerInventory playerInventory, StartupWorkbenchTileEntity te) {
        super(CampusBlocks.STARTUP_WORKBENCH_CONTAINER.get(), containerId);
        this.tileEntity = te;
        this.inventory = new ItemStackHandler(SLOTS);
        this.worldPos = IWorldPosCallable.create(te.getLevel(), te.getBlockPos());

        // 添加9个物品槽 (3x3)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                this.addSlot(new SlotItemHandler(inventory, col + row * 3, 62 + col * 18, 17 + row * 18));
            }
        }

        // 添加玩家背包 (3行 x 9列)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }

        // 添加玩家快捷栏
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }
    }

    // 客户端构造器 (从PacketBuffer读取)
    public StartupWorkbenchContainer(int containerId, PlayerInventory playerInventory, PacketBuffer extraData) {
        super(CampusBlocks.STARTUP_WORKBENCH_CONTAINER.get(), containerId);
        this.inventory = new ItemStackHandler(SLOTS);
        TileEntity te = playerInventory.player.level.getBlockEntity(extraData.readBlockPos());
        this.tileEntity = te instanceof StartupWorkbenchTileEntity ? (StartupWorkbenchTileEntity) te : null;
        this.worldPos = IWorldPosCallable.NULL;

        // 物品槽
        for (int row = 0; row < 3; row++) {
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

    public StartupWorkbenchTileEntity getTileEntity() {
        return tileEntity;
    }

    public int getMoney() {
        return tileEntity != null ? tileEntity.getCachedMoney() : 0;
    }

    public int getLevel() {
        return tileEntity != null ? tileEntity.getCachedLevel() : 1;
    }

    public int getExp() {
        return tileEntity != null ? tileEntity.getCachedExp() : 0;
    }

    @Override
    public boolean stillValid(@Nonnull PlayerEntity player) {
        return stillValid(worldPos, player, CampusBlocks.STARTUP_WORKBENCH.get());
    }

    @Override
    @Nonnull
    public ItemStack quickMoveStack(@Nonnull PlayerEntity player, int index) {
        Slot slot = this.slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;

        ItemStack original = slot.getItem().copy();
        if (index < SLOTS) {
            // 从工坊槽移到玩家背包
            if (!this.moveItemStackTo(slot.getItem(), SLOTS, this.slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else {
            // 从玩家背包移到工坊槽
            if (!this.moveItemStackTo(slot.getItem(), 0, SLOTS, false)) {
                return ItemStack.EMPTY;
            }
        }

        if (slot.getItem().isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        return original;
    }

    // 注册ContainerType的工厂方法
    public static ContainerType<StartupWorkbenchContainer> createContainerType() {
        return IForgeContainerType.create(StartupWorkbenchContainer::new);
    }
}
