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
public class BrandWorkshopTileEntity extends TileEntity implements INamedContainerProvider {
    private int cachedMoney = 0;
    private int cachedBrandValue = 0;
    public BrandWorkshopTileEntity(TileEntityType<?> type) { super(type); }
    public static BrandWorkshopTileEntity create() { return new BrandWorkshopTileEntity(CampusBlocks.BRAND_WORKSHOP_TE.get()); }
    public void updateData(int money, int brandValue) { this.cachedMoney = money; this.cachedBrandValue = brandValue; setChanged(); }
    public int getCachedMoney() { return cachedMoney; }
    public int getCachedBrandValue() { return cachedBrandValue; }
    @Override public void load(BlockState state, CompoundNBT nbt) { super.load(state, nbt); cachedMoney = nbt.getInt("Money"); cachedBrandValue = nbt.getInt("BrandValue"); }
    @Override public CompoundNBT save(CompoundNBT nbt) { super.save(nbt); nbt.putInt("Money", cachedMoney); nbt.putInt("BrandValue", cachedBrandValue); return nbt; }
    @Override public ITextComponent getDisplayName() { return new TranslationTextComponent("block.campuslife.brand_workshop"); }
    @Nullable @Override public Container createMenu(int id, PlayerInventory inv, PlayerEntity player) { return new BrandWorkshopContainer(id, inv, this); }
}
