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
public class AuctionHouseTileEntity extends TileEntity implements INamedContainerProvider {
    private int cachedMoney = 0;
    private int cachedAuctionCount = 0;
    public AuctionHouseTileEntity(TileEntityType<?> type) { super(type); }
    public static AuctionHouseTileEntity create() { return new AuctionHouseTileEntity(CampusBlocks.AUCTION_HOUSE_TE.get()); }
    public void updateData(int money, int auctionCount) { this.cachedMoney = money; this.cachedAuctionCount = auctionCount; setChanged(); }
    public int getCachedMoney() { return cachedMoney; }
    public int getCachedAuctionCount() { return cachedAuctionCount; }
    @Override public void load(BlockState state, CompoundNBT nbt) { super.load(state, nbt); cachedMoney = nbt.getInt("Money"); cachedAuctionCount = nbt.getInt("AuctionCount"); }
    @Override public CompoundNBT save(CompoundNBT nbt) { super.save(nbt); nbt.putInt("Money", cachedMoney); nbt.putInt("AuctionCount", cachedAuctionCount); return nbt; }
    @Override public ITextComponent getDisplayName() { return new TranslationTextComponent("block.campuslife.auction_house"); }
    @Nullable @Override public Container createMenu(int id, PlayerInventory inv, PlayerEntity player) { return new AuctionHouseContainer(id, inv, this); }
}
