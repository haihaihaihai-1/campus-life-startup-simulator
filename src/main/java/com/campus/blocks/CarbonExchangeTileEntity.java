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
public class CarbonExchangeTileEntity extends TileEntity implements INamedContainerProvider {
    private int cachedMoney = 0;
    private int cachedCarbonCredits = 0;
    public CarbonExchangeTileEntity(TileEntityType<?> type) { super(type); }
    public static CarbonExchangeTileEntity create() { return new CarbonExchangeTileEntity(CampusBlocks.CARBON_EXCHANGE_TE.get()); }
    public void updateData(int money, int carbonCredits) { this.cachedMoney = money; this.cachedCarbonCredits = carbonCredits; setChanged(); }
    public int getCachedMoney() { return cachedMoney; }
    public int getCachedCarbonCredits() { return cachedCarbonCredits; }
    @Override public void load(BlockState state, CompoundNBT nbt) { super.load(state, nbt); cachedMoney = nbt.getInt("Money"); cachedCarbonCredits = nbt.getInt("CarbonCredits"); }
    @Override public CompoundNBT save(CompoundNBT nbt) { super.save(nbt); nbt.putInt("Money", cachedMoney); nbt.putInt("CarbonCredits", cachedCarbonCredits); return nbt; }
    @Override public ITextComponent getDisplayName() { return new TranslationTextComponent("block.campuslife.carbon_exchange"); }
    @Nullable @Override public Container createMenu(int id, PlayerInventory inv, PlayerEntity player) { return new CarbonExchangeContainer(id, inv, this); }
}
