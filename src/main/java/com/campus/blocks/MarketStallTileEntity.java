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

public class MarketStallTileEntity extends TileEntity implements INamedContainerProvider {
    private int cachedMarketItems = 0;
    private int cachedAvgPrice = 0;

    public MarketStallTileEntity(TileEntityType<?> type) {
        super(type);
    }

    public static MarketStallTileEntity create() {
        return new MarketStallTileEntity(CampusBlocks.MARKET_STALL_TE.get());
    }

    public void updateData(int items, int avgPrice) {
        this.cachedMarketItems = items;
        this.cachedAvgPrice = avgPrice;
        setChanged();
    }

    public int getMarketItems() { return cachedMarketItems; }
    public int getAvgPrice() { return cachedAvgPrice; }

    @Override
    public void load(BlockState state, CompoundNBT nbt) {
        super.load(state, nbt);
        cachedMarketItems = nbt.getInt("MarketItems");
        cachedAvgPrice = nbt.getInt("AvgPrice");
    }

    @Override
    public CompoundNBT save(CompoundNBT nbt) {
        super.save(nbt);
        nbt.putInt("MarketItems", cachedMarketItems);
        nbt.putInt("AvgPrice", cachedAvgPrice);
        return nbt;
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent("block.campuslife.market_stall");
    }

    @Nullable
    @Override
    public Container createMenu(int containerId, PlayerInventory playerInventory, PlayerEntity player) {
        return new MarketStallContainer(containerId, playerInventory, this);
    }
}
