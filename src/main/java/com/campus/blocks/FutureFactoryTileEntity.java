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
public class FutureFactoryTileEntity extends TileEntity implements INamedContainerProvider {
    private int cachedMoney = 0;
    private int cachedProductionLevel = 0;
    public FutureFactoryTileEntity(TileEntityType<?> type) { super(type); }
    public static FutureFactoryTileEntity create() { return new FutureFactoryTileEntity(CampusBlocks.FUTURE_FACTORY_TE.get()); }
    public void updateData(int money, int productionLevel) { this.cachedMoney = money; this.cachedProductionLevel = productionLevel; setChanged(); }
    public int getCachedMoney() { return cachedMoney; }
    public int getCachedProductionLevel() { return cachedProductionLevel; }
    @Override public void load(BlockState state, CompoundNBT nbt) { super.load(state, nbt); cachedMoney = nbt.getInt("Money"); cachedProductionLevel = nbt.getInt("ProductionLevel"); }
    @Override public CompoundNBT save(CompoundNBT nbt) { super.save(nbt); nbt.putInt("Money", cachedMoney); nbt.putInt("ProductionLevel", cachedProductionLevel); return nbt; }
    @Override public ITextComponent getDisplayName() { return new TranslationTextComponent("block.campuslife.future_factory"); }
    @Nullable @Override public Container createMenu(int id, PlayerInventory inv, PlayerEntity player) { return new FutureFactoryContainer(id, inv, this); }
}
