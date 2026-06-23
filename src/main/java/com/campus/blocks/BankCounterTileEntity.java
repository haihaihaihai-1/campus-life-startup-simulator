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

public class BankCounterTileEntity extends TileEntity implements INamedContainerProvider {
    private int cachedMoney = 0;
    private int cachedLoan = 0;

    public BankCounterTileEntity(TileEntityType<?> type) { super(type); }

    public static BankCounterTileEntity create() {
        return new BankCounterTileEntity(CampusBlocks.BANK_COUNTER_TE.get());
    }

    public void updateData(int money, int loan) {
        this.cachedMoney = money;
        this.cachedLoan = loan;
        setChanged();
    }

    public int getCachedMoney() { return cachedMoney; }
    public int getCachedLoan() { return cachedLoan; }

    @Override
    public void load(BlockState state, CompoundNBT nbt) {
        super.load(state, nbt);
        cachedMoney = nbt.getInt("Money");
        cachedLoan = nbt.getInt("Loan");
    }

    @Override
    public CompoundNBT save(CompoundNBT nbt) {
        super.save(nbt);
        nbt.putInt("Money", cachedMoney);
        nbt.putInt("Loan", cachedLoan);
        return nbt;
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent("block.campuslife.bank_counter");
    }

    @Nullable
    @Override
    public Container createMenu(int containerId, PlayerInventory playerInventory, PlayerEntity player) {
        return new BankCounterContainer(containerId, playerInventory, this);
    }
}
