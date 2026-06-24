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
public class SupplyChainTileEntity extends TileEntity implements INamedContainerProvider {
    private int cachedMoney = 0;
    private int cachedSupplyChainLevel = 0;
    public SupplyChainTileEntity(TileEntityType<?> type) { super(type); }
    public static SupplyChainTileEntity create() { return new SupplyChainTileEntity(CampusBlocks.SUPPLY_CHAIN_TE.get()); }
    public void updateData(int money, int supplyChainLevel) { this.cachedMoney = money; this.cachedSupplyChainLevel = supplyChainLevel; setChanged(); }
    public int getCachedMoney() { return cachedMoney; }
    public int getCachedSupplyChainLevel() { return cachedSupplyChainLevel; }
    @Override public void load(BlockState state, CompoundNBT nbt) { super.load(state, nbt); cachedMoney = nbt.getInt("Money"); cachedSupplyChainLevel = nbt.getInt("SupplyChainLevel"); }
    @Override public CompoundNBT save(CompoundNBT nbt) { super.save(nbt); nbt.putInt("Money", cachedMoney); nbt.putInt("SupplyChainLevel", cachedSupplyChainLevel); return nbt; }
    @Override public ITextComponent getDisplayName() { return new TranslationTextComponent("block.campuslife.supply_chain"); }
    @Nullable @Override public Container createMenu(int id, PlayerInventory inv, PlayerEntity player) { return new SupplyChainContainer(id, inv, this); }
}
