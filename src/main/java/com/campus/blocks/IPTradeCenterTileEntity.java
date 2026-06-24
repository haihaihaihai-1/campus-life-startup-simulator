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
public class IPTradeCenterTileEntity extends TileEntity implements INamedContainerProvider {
    private int cachedMoney = 0;
    private int cachedIpCount = 0;
    public IPTradeCenterTileEntity(TileEntityType<?> type) { super(type); }
    public static IPTradeCenterTileEntity create() { return new IPTradeCenterTileEntity(CampusBlocks.IP_TRADE_CENTER_TE.get()); }
    public void updateData(int money, int ipCount) { this.cachedMoney = money; this.cachedIpCount = ipCount; setChanged(); }
    public int getCachedMoney() { return cachedMoney; }
    public int getCachedIpCount() { return cachedIpCount; }
    @Override public void load(BlockState state, CompoundNBT nbt) { super.load(state, nbt); cachedMoney = nbt.getInt("Money"); cachedIpCount = nbt.getInt("IpCount"); }
    @Override public CompoundNBT save(CompoundNBT nbt) { super.save(nbt); nbt.putInt("Money", cachedMoney); nbt.putInt("IpCount", cachedIpCount); return nbt; }
    @Override public ITextComponent getDisplayName() { return new TranslationTextComponent("block.campuslife.ip_trade_center"); }
    @Nullable @Override public Container createMenu(int id, PlayerInventory inv, PlayerEntity player) { return new IPTradeCenterContainer(id, inv, this); }
}
