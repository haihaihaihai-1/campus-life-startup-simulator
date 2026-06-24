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
public class RealEstateCenterTileEntity extends TileEntity implements INamedContainerProvider {
    private int cachedMoney = 0;
    private int cachedPropertyCount = 0;
    public RealEstateCenterTileEntity(TileEntityType<?> type) { super(type); }
    public static RealEstateCenterTileEntity create() { return new RealEstateCenterTileEntity(CampusBlocks.REAL_ESTATE_CENTER_TE.get()); }
    public void updateData(int money, int propertyCount) { this.cachedMoney = money; this.cachedPropertyCount = propertyCount; setChanged(); }
    public int getCachedMoney() { return cachedMoney; }
    public int getCachedPropertyCount() { return cachedPropertyCount; }
    @Override public void load(BlockState state, CompoundNBT nbt) { super.load(state, nbt); cachedMoney = nbt.getInt("Money"); cachedPropertyCount = nbt.getInt("PropertyCount"); }
    @Override public CompoundNBT save(CompoundNBT nbt) { super.save(nbt); nbt.putInt("Money", cachedMoney); nbt.putInt("PropertyCount", cachedPropertyCount); return nbt; }
    @Override public ITextComponent getDisplayName() { return new TranslationTextComponent("block.campuslife.real_estate_center"); }
    @Nullable @Override public Container createMenu(int id, PlayerInventory inv, PlayerEntity player) { return new RealEstateCenterContainer(id, inv, this); }
}
