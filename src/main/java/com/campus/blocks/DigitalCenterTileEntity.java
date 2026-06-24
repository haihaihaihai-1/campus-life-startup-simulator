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
public class DigitalCenterTileEntity extends TileEntity implements INamedContainerProvider {
    private int cachedMoney = 0;
    private int cachedDigitalLevel = 0;
    public DigitalCenterTileEntity(TileEntityType<?> type) { super(type); }
    public static DigitalCenterTileEntity create() { return new DigitalCenterTileEntity(CampusBlocks.DIGITAL_CENTER_TE.get()); }
    public void updateData(int money, int digitalLevel) { this.cachedMoney = money; this.cachedDigitalLevel = digitalLevel; setChanged(); }
    public int getCachedMoney() { return cachedMoney; }
    public int getCachedDigitalLevel() { return cachedDigitalLevel; }
    @Override public void load(BlockState state, CompoundNBT nbt) { super.load(state, nbt); cachedMoney = nbt.getInt("Money"); cachedDigitalLevel = nbt.getInt("DigitalLevel"); }
    @Override public CompoundNBT save(CompoundNBT nbt) { super.save(nbt); nbt.putInt("Money", cachedMoney); nbt.putInt("DigitalLevel", cachedDigitalLevel); return nbt; }
    @Override public ITextComponent getDisplayName() { return new TranslationTextComponent("block.campuslife.digital_center"); }
    @Nullable @Override public Container createMenu(int id, PlayerInventory inv, PlayerEntity player) { return new DigitalCenterContainer(id, inv, this); }
}
