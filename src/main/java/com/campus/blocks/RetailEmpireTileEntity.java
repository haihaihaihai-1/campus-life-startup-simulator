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
public class RetailEmpireTileEntity extends TileEntity implements INamedContainerProvider {
    private int cachedMoney = 0;
    private int cachedRetailLevel = 0;
    public RetailEmpireTileEntity(TileEntityType<?> type) { super(type); }
    public static RetailEmpireTileEntity create() { return new RetailEmpireTileEntity(CampusBlocks.RETAIL_EMPIRE_TE.get()); }
    public void updateData(int money, int retailLevel) { this.cachedMoney = money; this.cachedRetailLevel = retailLevel; setChanged(); }
    public int getCachedMoney() { return cachedMoney; }
    public int getCachedRetailLevel() { return cachedRetailLevel; }
    @Override public void load(BlockState state, CompoundNBT nbt) { super.load(state, nbt); cachedMoney = nbt.getInt("Money"); cachedRetailLevel = nbt.getInt("RetailLevel"); }
    @Override public CompoundNBT save(CompoundNBT nbt) { super.save(nbt); nbt.putInt("Money", cachedMoney); nbt.putInt("RetailLevel", cachedRetailLevel); return nbt; }
    @Override public ITextComponent getDisplayName() { return new TranslationTextComponent("block.campuslife.retail_empire"); }
    @Nullable @Override public Container createMenu(int id, PlayerInventory inv, PlayerEntity player) { return new RetailEmpireContainer(id, inv, this); }
}
