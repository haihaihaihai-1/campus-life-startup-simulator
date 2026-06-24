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
public class DataCenterTileEntity extends TileEntity implements INamedContainerProvider {
    private int cachedMoney = 0;
    private int cachedDataPoints = 0;
    public DataCenterTileEntity(TileEntityType<?> type) { super(type); }
    public static DataCenterTileEntity create() { return new DataCenterTileEntity(CampusBlocks.DATA_CENTER_TE.get()); }
    public void updateData(int money, int dataPoints) { this.cachedMoney = money; this.cachedDataPoints = dataPoints; setChanged(); }
    public int getCachedMoney() { return cachedMoney; }
    public int getCachedDataPoints() { return cachedDataPoints; }
    @Override public void load(BlockState state, CompoundNBT nbt) { super.load(state, nbt); cachedMoney = nbt.getInt("Money"); cachedDataPoints = nbt.getInt("DataPoints"); }
    @Override public CompoundNBT save(CompoundNBT nbt) { super.save(nbt); nbt.putInt("Money", cachedMoney); nbt.putInt("DataPoints", cachedDataPoints); return nbt; }
    @Override public ITextComponent getDisplayName() { return new TranslationTextComponent("block.campuslife.data_center"); }
    @Nullable @Override public Container createMenu(int id, PlayerInventory inv, PlayerEntity player) { return new DataCenterContainer(id, inv, this); }
}
