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
public class InvestCenterTileEntity extends TileEntity implements INamedContainerProvider {
    private int cachedMoney = 0;
    private int cachedValuation = 0;
    public InvestCenterTileEntity(TileEntityType<?> type) { super(type); }
    public static InvestCenterTileEntity create() { return new InvestCenterTileEntity(CampusBlocks.INVEST_CENTER_TE.get()); }
    public void updateData(int money, int valuation) { this.cachedMoney = money; this.cachedValuation = valuation; setChanged(); }
    public int getCachedMoney() { return cachedMoney; }
    public int getCachedValuation() { return cachedValuation; }
    @Override public void load(BlockState state, CompoundNBT nbt) { super.load(state, nbt); cachedMoney = nbt.getInt("Money"); cachedValuation = nbt.getInt("Valuation"); }
    @Override public CompoundNBT save(CompoundNBT nbt) { super.save(nbt); nbt.putInt("Money", cachedMoney); nbt.putInt("Valuation", cachedValuation); return nbt; }
    @Override public ITextComponent getDisplayName() { return new TranslationTextComponent("block.campuslife.invest_center"); }
    @Nullable @Override public Container createMenu(int id, PlayerInventory inv, PlayerEntity player) { return new InvestCenterContainer(id, inv, this); }
}
