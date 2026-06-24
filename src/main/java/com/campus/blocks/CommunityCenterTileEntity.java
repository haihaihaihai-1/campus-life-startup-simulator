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
public class CommunityCenterTileEntity extends TileEntity implements INamedContainerProvider {
    private int cachedMoney = 0;
    private int cachedReputation = 0;
    public CommunityCenterTileEntity(TileEntityType<?> type) { super(type); }
    public static CommunityCenterTileEntity create() { return new CommunityCenterTileEntity(CampusBlocks.COMMUNITY_CENTER_TE.get()); }
    public void updateData(int money, int reputation) { this.cachedMoney = money; this.cachedReputation = reputation; setChanged(); }
    public int getCachedMoney() { return cachedMoney; }
    public int getCachedReputation() { return cachedReputation; }
    @Override public void load(BlockState state, CompoundNBT nbt) { super.load(state, nbt); cachedMoney = nbt.getInt("Money"); cachedReputation = nbt.getInt("Reputation"); }
    @Override public CompoundNBT save(CompoundNBT nbt) { super.save(nbt); nbt.putInt("Money", cachedMoney); nbt.putInt("Reputation", cachedReputation); return nbt; }
    @Override public ITextComponent getDisplayName() { return new TranslationTextComponent("block.campuslife.community_center"); }
    @Nullable @Override public Container createMenu(int id, PlayerInventory inv, PlayerEntity player) { return new CommunityCenterContainer(id, inv, this); }
}
