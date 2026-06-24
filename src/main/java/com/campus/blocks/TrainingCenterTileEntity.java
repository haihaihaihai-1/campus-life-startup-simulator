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
public class TrainingCenterTileEntity extends TileEntity implements INamedContainerProvider {
    private int cachedMoney = 0;
    private int cachedTrainingLevel = 0;
    public TrainingCenterTileEntity(TileEntityType<?> type) { super(type); }
    public static TrainingCenterTileEntity create() { return new TrainingCenterTileEntity(CampusBlocks.TRAINING_CENTER_TE.get()); }
    public void updateData(int money, int trainingLevel) { this.cachedMoney = money; this.cachedTrainingLevel = trainingLevel; setChanged(); }
    public int getCachedMoney() { return cachedMoney; }
    public int getCachedTrainingLevel() { return cachedTrainingLevel; }
    @Override public void load(BlockState state, CompoundNBT nbt) { super.load(state, nbt); cachedMoney = nbt.getInt("Money"); cachedTrainingLevel = nbt.getInt("TrainingLevel"); }
    @Override public CompoundNBT save(CompoundNBT nbt) { super.save(nbt); nbt.putInt("Money", cachedMoney); nbt.putInt("TrainingLevel", cachedTrainingLevel); return nbt; }
    @Override public ITextComponent getDisplayName() { return new TranslationTextComponent("block.campuslife.training_center"); }
    @Nullable @Override public Container createMenu(int id, PlayerInventory inv, PlayerEntity player) { return new TrainingCenterContainer(id, inv, this); }
}
