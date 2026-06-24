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
public class InternationalCenterTileEntity extends TileEntity implements INamedContainerProvider {
    private int cachedMoney = 0;
    private int cachedGlobalLevel = 0;
    public InternationalCenterTileEntity(TileEntityType<?> type) { super(type); }
    public static InternationalCenterTileEntity create() { return new InternationalCenterTileEntity(CampusBlocks.INTERNATIONAL_CENTER_TE.get()); }
    public void updateData(int money, int globalLevel) { this.cachedMoney = money; this.cachedGlobalLevel = globalLevel; setChanged(); }
    public int getCachedMoney() { return cachedMoney; }
    public int getCachedGlobalLevel() { return cachedGlobalLevel; }
    @Override public void load(BlockState state, CompoundNBT nbt) { super.load(state, nbt); cachedMoney = nbt.getInt("Money"); cachedGlobalLevel = nbt.getInt("GlobalLevel"); }
    @Override public CompoundNBT save(CompoundNBT nbt) { super.save(nbt); nbt.putInt("Money", cachedMoney); nbt.putInt("GlobalLevel", cachedGlobalLevel); return nbt; }
    @Override public ITextComponent getDisplayName() { return new TranslationTextComponent("block.campuslife.international_center"); }
    @Nullable @Override public Container createMenu(int id, PlayerInventory inv, PlayerEntity player) { return new InternationalCenterContainer(id, inv, this); }
}
