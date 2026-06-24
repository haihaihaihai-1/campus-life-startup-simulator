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
public class QualityCenterTileEntity extends TileEntity implements INamedContainerProvider {
    private int cachedMoney = 0;
    private int cachedQualityScore = 0;
    public QualityCenterTileEntity(TileEntityType<?> type) { super(type); }
    public static QualityCenterTileEntity create() { return new QualityCenterTileEntity(CampusBlocks.QUALITY_CENTER_TE.get()); }
    public void updateData(int money, int qualityScore) { this.cachedMoney = money; this.cachedQualityScore = qualityScore; setChanged(); }
    public int getCachedMoney() { return cachedMoney; }
    public int getCachedQualityScore() { return cachedQualityScore; }
    @Override public void load(BlockState state, CompoundNBT nbt) { super.load(state, nbt); cachedMoney = nbt.getInt("Money"); cachedQualityScore = nbt.getInt("QualityScore"); }
    @Override public CompoundNBT save(CompoundNBT nbt) { super.save(nbt); nbt.putInt("Money", cachedMoney); nbt.putInt("QualityScore", cachedQualityScore); return nbt; }
    @Override public ITextComponent getDisplayName() { return new TranslationTextComponent("block.campuslife.quality_center"); }
    @Nullable @Override public Container createMenu(int id, PlayerInventory inv, PlayerEntity player) { return new QualityCenterContainer(id, inv, this); }
}
