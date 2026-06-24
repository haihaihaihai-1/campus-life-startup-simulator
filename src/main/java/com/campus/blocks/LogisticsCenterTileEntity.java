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
public class LogisticsCenterTileEntity extends TileEntity implements INamedContainerProvider {
    private int cachedMoney = 0;
    private int cachedLogisticsLevel = 0;
    public LogisticsCenterTileEntity(TileEntityType<?> type) { super(type); }
    public static LogisticsCenterTileEntity create() { return new LogisticsCenterTileEntity(CampusBlocks.LOGISTICS_CENTER_TE.get()); }
    public void updateData(int money, int logisticsLevel) { this.cachedMoney = money; this.cachedLogisticsLevel = logisticsLevel; setChanged(); }
    public int getCachedMoney() { return cachedMoney; }
    public int getCachedLogisticsLevel() { return cachedLogisticsLevel; }
    @Override public void load(BlockState state, CompoundNBT nbt) { super.load(state, nbt); cachedMoney = nbt.getInt("Money"); cachedLogisticsLevel = nbt.getInt("LogisticsLevel"); }
    @Override public CompoundNBT save(CompoundNBT nbt) { super.save(nbt); nbt.putInt("Money", cachedMoney); nbt.putInt("LogisticsLevel", cachedLogisticsLevel); return nbt; }
    @Override public ITextComponent getDisplayName() { return new TranslationTextComponent("block.campuslife.logistics_center"); }
    @Nullable @Override public Container createMenu(int id, PlayerInventory inv, PlayerEntity player) { return new LogisticsCenterContainer(id, inv, this); }
}
