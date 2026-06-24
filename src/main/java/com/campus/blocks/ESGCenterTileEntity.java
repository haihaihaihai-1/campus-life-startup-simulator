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
public class ESGCenterTileEntity extends TileEntity implements INamedContainerProvider {
    private int cachedMoney = 0;
    private int cachedEsgScore = 0;
    public ESGCenterTileEntity(TileEntityType<?> type) { super(type); }
    public static ESGCenterTileEntity create() { return new ESGCenterTileEntity(CampusBlocks.ESG_CENTER_TE.get()); }
    public void updateData(int money, int esgScore) { this.cachedMoney = money; this.cachedEsgScore = esgScore; setChanged(); }
    public int getCachedMoney() { return cachedMoney; }
    public int getCachedEsgScore() { return cachedEsgScore; }
    @Override public void load(BlockState state, CompoundNBT nbt) { super.load(state, nbt); cachedMoney = nbt.getInt("Money"); cachedEsgScore = nbt.getInt("EsgScore"); }
    @Override public CompoundNBT save(CompoundNBT nbt) { super.save(nbt); nbt.putInt("Money", cachedMoney); nbt.putInt("EsgScore", cachedEsgScore); return nbt; }
    @Override public ITextComponent getDisplayName() { return new TranslationTextComponent("block.campuslife.esg_center"); }
    @Nullable @Override public Container createMenu(int id, PlayerInventory inv, PlayerEntity player) { return new ESGCenterContainer(id, inv, this); }
}
