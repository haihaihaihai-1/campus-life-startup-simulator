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

public class CoffeeMachineTileEntity extends TileEntity implements INamedContainerProvider {
    private int cachedCoffeeCount = 0;

    public CoffeeMachineTileEntity(TileEntityType<?> type) { super(type); }

    public static CoffeeMachineTileEntity create() {
        return new CoffeeMachineTileEntity(CampusBlocks.COFFEE_MACHINE_TE.get());
    }

    public void updateData(int coffeeCount) {
        this.cachedCoffeeCount = coffeeCount;
        setChanged();
    }

    public int getCoffeeCount() { return cachedCoffeeCount; }

    @Override
    public void load(BlockState state, CompoundNBT nbt) {
        super.load(state, nbt);
        cachedCoffeeCount = nbt.getInt("CoffeeCount");
    }

    @Override
    public CompoundNBT save(CompoundNBT nbt) {
        super.save(nbt);
        nbt.putInt("CoffeeCount", cachedCoffeeCount);
        return nbt;
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent("block.campuslife.coffee_machine");
    }

    @Nullable
    @Override
    public Container createMenu(int containerId, PlayerInventory playerInventory, PlayerEntity player) {
        return new CoffeeMachineContainer(containerId, playerInventory, this);
    }
}
