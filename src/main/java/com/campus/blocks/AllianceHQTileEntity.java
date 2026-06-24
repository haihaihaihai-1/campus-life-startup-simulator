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
public class AllianceHQTileEntity extends TileEntity implements INamedContainerProvider {
    private int cachedMoney = 0;
    private int cachedAllianceCount = 0;
    public AllianceHQTileEntity(TileEntityType<?> type) { super(type); }
    public static AllianceHQTileEntity create() { return new AllianceHQTileEntity(CampusBlocks.ALLIANCE_HQ_TE.get()); }
    public void updateData(int money, int allianceCount) { this.cachedMoney = money; this.cachedAllianceCount = allianceCount; setChanged(); }
    public int getCachedMoney() { return cachedMoney; }
    public int getCachedAllianceCount() { return cachedAllianceCount; }
    @Override public void load(BlockState state, CompoundNBT nbt) { super.load(state, nbt); cachedMoney = nbt.getInt("Money"); cachedAllianceCount = nbt.getInt("AllianceCount"); }
    @Override public CompoundNBT save(CompoundNBT nbt) { super.save(nbt); nbt.putInt("Money", cachedMoney); nbt.putInt("AllianceCount", cachedAllianceCount); return nbt; }
    @Override public ITextComponent getDisplayName() { return new TranslationTextComponent("block.campuslife.alliance_hq"); }
    @Nullable @Override public Container createMenu(int id, PlayerInventory inv, PlayerEntity player) { return new AllianceHQContainer(id, inv, this); }
}
