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
public class TaxOfficeTileEntity extends TileEntity implements INamedContainerProvider {
    private int cachedMoney = 0;
    private int cachedTaxPaid = 0;
    public TaxOfficeTileEntity(TileEntityType<?> type) { super(type); }
    public static TaxOfficeTileEntity create() { return new TaxOfficeTileEntity(CampusBlocks.TAX_OFFICE_TE.get()); }
    public void updateData(int money, int taxPaid) { this.cachedMoney = money; this.cachedTaxPaid = taxPaid; setChanged(); }
    public int getCachedMoney() { return cachedMoney; }
    public int getCachedTaxPaid() { return cachedTaxPaid; }
    @Override public void load(BlockState state, CompoundNBT nbt) { super.load(state, nbt); cachedMoney = nbt.getInt("Money"); cachedTaxPaid = nbt.getInt("TaxPaid"); }
    @Override public CompoundNBT save(CompoundNBT nbt) { super.save(nbt); nbt.putInt("Money", cachedMoney); nbt.putInt("TaxPaid", cachedTaxPaid); return nbt; }
    @Override public ITextComponent getDisplayName() { return new TranslationTextComponent("block.campuslife.tax_office"); }
    @Nullable @Override public Container createMenu(int id, PlayerInventory inv, PlayerEntity player) { return new TaxOfficeContainer(id, inv, this); }
}
