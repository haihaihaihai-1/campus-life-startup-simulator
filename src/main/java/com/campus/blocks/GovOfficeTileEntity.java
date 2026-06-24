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
public class GovOfficeTileEntity extends TileEntity implements INamedContainerProvider {
    private int cachedMoney = 0;
    private int cachedGovRelation = 0;
    public GovOfficeTileEntity(TileEntityType<?> type) { super(type); }
    public static GovOfficeTileEntity create() { return new GovOfficeTileEntity(CampusBlocks.GOV_OFFICE_TE.get()); }
    public void updateData(int money, int govRelation) { this.cachedMoney = money; this.cachedGovRelation = govRelation; setChanged(); }
    public int getCachedMoney() { return cachedMoney; }
    public int getCachedGovRelation() { return cachedGovRelation; }
    @Override public void load(BlockState state, CompoundNBT nbt) { super.load(state, nbt); cachedMoney = nbt.getInt("Money"); cachedGovRelation = nbt.getInt("GovRelation"); }
    @Override public CompoundNBT save(CompoundNBT nbt) { super.save(nbt); nbt.putInt("Money", cachedMoney); nbt.putInt("GovRelation", cachedGovRelation); return nbt; }
    @Override public ITextComponent getDisplayName() { return new TranslationTextComponent("block.campuslife.gov_office"); }
    @Nullable @Override public Container createMenu(int id, PlayerInventory inv, PlayerEntity player) { return new GovOfficeContainer(id, inv, this); }
}
