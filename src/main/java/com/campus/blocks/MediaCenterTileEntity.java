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
public class MediaCenterTileEntity extends TileEntity implements INamedContainerProvider {
    private int cachedMoney = 0;
    private int cachedFollowerCount = 0;
    public MediaCenterTileEntity(TileEntityType<?> type) { super(type); }
    public static MediaCenterTileEntity create() { return new MediaCenterTileEntity(CampusBlocks.MEDIA_CENTER_TE.get()); }
    public void updateData(int money, int followerCount) { this.cachedMoney = money; this.cachedFollowerCount = followerCount; setChanged(); }
    public int getCachedMoney() { return cachedMoney; }
    public int getCachedFollowerCount() { return cachedFollowerCount; }
    @Override public void load(BlockState state, CompoundNBT nbt) { super.load(state, nbt); cachedMoney = nbt.getInt("Money"); cachedFollowerCount = nbt.getInt("FollowerCount"); }
    @Override public CompoundNBT save(CompoundNBT nbt) { super.save(nbt); nbt.putInt("Money", cachedMoney); nbt.putInt("FollowerCount", cachedFollowerCount); return nbt; }
    @Override public ITextComponent getDisplayName() { return new TranslationTextComponent("block.campuslife.media_center"); }
    @Nullable @Override public Container createMenu(int id, PlayerInventory inv, PlayerEntity player) { return new MediaCenterContainer(id, inv, this); }
}
