package com.campus.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;

/**
 * 创业工坊方块实体
 * 参考模式: McJtyLib GenericTileEntity (MIT) + Forge原生TileEntity
 */
public class StartupWorkbenchTileEntity extends TileEntity implements INamedContainerProvider {

    private int cachedMoney = 0;
    private int cachedLevel = 1;
    private int cachedExp = 0;
    private int cachedEmployees = 0;

    public StartupWorkbenchTileEntity(TileEntityType<?> type) {
        super(type);
    }

    public static StartupWorkbenchTileEntity create() {
        return new StartupWorkbenchTileEntity(CampusBlocks.STARTUP_WORKBENCH_TE.get());
    }

    public void updateData(int money, int level, int exp, int employees) {
        this.cachedMoney = money;
        this.cachedLevel = level;
        this.cachedExp = exp;
        this.cachedEmployees = employees;
        setChanged();
    }

    public int getCachedMoney() { return cachedMoney; }
    public int getCachedLevel() { return cachedLevel; }
    public int getCachedExp() { return cachedExp; }
    public int getCachedEmployees() { return cachedEmployees; }

    @Override
    public void load(BlockState state, CompoundNBT nbt) {
        super.load(state, nbt);
        cachedMoney = nbt.getInt("Money");
        cachedLevel = nbt.getInt("Level");
        cachedExp = nbt.getInt("Exp");
        cachedEmployees = nbt.getInt("Employees");
    }

    @Override
    public CompoundNBT save(CompoundNBT nbt) {
        super.save(nbt);
        nbt.putInt("Money", cachedMoney);
        nbt.putInt("Level", cachedLevel);
        nbt.putInt("Exp", cachedExp);
        nbt.putInt("Employees", cachedEmployees);
        return nbt;
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent("block.campuslife.startup_workbench");
    }

    @Nullable
    @Override
    public Container createMenu(int containerId, PlayerInventory playerInventory, PlayerEntity player) {
        return new StartupWorkbenchContainer(containerId, playerInventory, this);
    }
}
