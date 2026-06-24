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
public class InsuranceCenterTileEntity extends TileEntity implements INamedContainerProvider {
    private int cachedMoney = 0;
    private int cachedInsuranceLevel = 0;
    public InsuranceCenterTileEntity(TileEntityType<?> type) { super(type); }
    public static InsuranceCenterTileEntity create() { return new InsuranceCenterTileEntity(CampusBlocks.INSURANCE_CENTER_TE.get()); }
    public void updateData(int money, int insuranceLevel) { this.cachedMoney = money; this.cachedInsuranceLevel = insuranceLevel; setChanged(); }
    public int getCachedMoney() { return cachedMoney; }
    public int getCachedInsuranceLevel() { return cachedInsuranceLevel; }
    @Override public void load(BlockState state, CompoundNBT nbt) { super.load(state, nbt); cachedMoney = nbt.getInt("Money"); cachedInsuranceLevel = nbt.getInt("InsuranceLevel"); }
    @Override public CompoundNBT save(CompoundNBT nbt) { super.save(nbt); nbt.putInt("Money", cachedMoney); nbt.putInt("InsuranceLevel", cachedInsuranceLevel); return nbt; }
    @Override public ITextComponent getDisplayName() { return new TranslationTextComponent("block.campuslife.insurance_center"); }
    @Nullable @Override public Container createMenu(int id, PlayerInventory inv, PlayerEntity player) { return new InsuranceCenterContainer(id, inv, this); }
}
