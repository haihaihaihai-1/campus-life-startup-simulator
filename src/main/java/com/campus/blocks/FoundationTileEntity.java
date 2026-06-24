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
public class FoundationTileEntity extends TileEntity implements INamedContainerProvider {
    private int cachedMoney = 0;
    private int cachedFundBalance = 0;
    public FoundationTileEntity(TileEntityType<?> type) { super(type); }
    public static FoundationTileEntity create() { return new FoundationTileEntity(CampusBlocks.FOUNDATION_TE.get()); }
    public void updateData(int money, int fundBalance) { this.cachedMoney = money; this.cachedFundBalance = fundBalance; setChanged(); }
    public int getCachedMoney() { return cachedMoney; }
    public int getCachedFundBalance() { return cachedFundBalance; }
    @Override public void load(BlockState state, CompoundNBT nbt) { super.load(state, nbt); cachedMoney = nbt.getInt("Money"); cachedFundBalance = nbt.getInt("FundBalance"); }
    @Override public CompoundNBT save(CompoundNBT nbt) { super.save(nbt); nbt.putInt("Money", cachedMoney); nbt.putInt("FundBalance", cachedFundBalance); return nbt; }
    @Override public ITextComponent getDisplayName() { return new TranslationTextComponent("block.campuslife.foundation"); }
    @Nullable @Override public Container createMenu(int id, PlayerInventory inv, PlayerEntity player) { return new FoundationContainer(id, inv, this); }
}
