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
public class IncubatorTileEntity extends TileEntity implements INamedContainerProvider {
    private int cachedMoney = 0;
    private int cachedIncubatorLevel = 0;
    public IncubatorTileEntity(TileEntityType<?> type) { super(type); }
    public static IncubatorTileEntity create() { return new IncubatorTileEntity(CampusBlocks.INCUBATOR_TE.get()); }
    public void updateData(int money, int incubatorLevel) { this.cachedMoney = money; this.cachedIncubatorLevel = incubatorLevel; setChanged(); }
    public int getCachedMoney() { return cachedMoney; }
    public int getCachedIncubatorLevel() { return cachedIncubatorLevel; }
    @Override public void load(BlockState state, CompoundNBT nbt) { super.load(state, nbt); cachedMoney = nbt.getInt("Money"); cachedIncubatorLevel = nbt.getInt("IncubatorLevel"); }
    @Override public CompoundNBT save(CompoundNBT nbt) { super.save(nbt); nbt.putInt("Money", cachedMoney); nbt.putInt("IncubatorLevel", cachedIncubatorLevel); return nbt; }
    @Override public ITextComponent getDisplayName() { return new TranslationTextComponent("block.campuslife.incubator"); }
    @Nullable @Override public Container createMenu(int id, PlayerInventory inv, PlayerEntity player) { return new IncubatorContainer(id, inv, this); }
}
