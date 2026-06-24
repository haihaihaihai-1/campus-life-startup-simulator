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
public class MemberCenterTileEntity extends TileEntity implements INamedContainerProvider {
    private int cachedMoney = 0;
    private int cachedVipLevel = 0;
    public MemberCenterTileEntity(TileEntityType<?> type) { super(type); }
    public static MemberCenterTileEntity create() { return new MemberCenterTileEntity(CampusBlocks.MEMBER_CENTER_TE.get()); }
    public void updateData(int money, int vipLevel) { this.cachedMoney = money; this.cachedVipLevel = vipLevel; setChanged(); }
    public int getCachedMoney() { return cachedMoney; }
    public int getCachedVipLevel() { return cachedVipLevel; }
    @Override public void load(BlockState state, CompoundNBT nbt) { super.load(state, nbt); cachedMoney = nbt.getInt("Money"); cachedVipLevel = nbt.getInt("VipLevel"); }
    @Override public CompoundNBT save(CompoundNBT nbt) { super.save(nbt); nbt.putInt("Money", cachedMoney); nbt.putInt("VipLevel", cachedVipLevel); return nbt; }
    @Override public ITextComponent getDisplayName() { return new TranslationTextComponent("block.campuslife.member_center"); }
    @Nullable @Override public Container createMenu(int id, PlayerInventory inv, PlayerEntity player) { return new MemberCenterContainer(id, inv, this); }
}
