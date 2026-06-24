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
public class StockTerminalTileEntity extends TileEntity implements INamedContainerProvider {
    private int cachedMoney = 0;
    private int cachedStockValue = 0;
    public StockTerminalTileEntity(TileEntityType<?> type) { super(type); }
    public static StockTerminalTileEntity create() { return new StockTerminalTileEntity(CampusBlocks.STOCK_TERMINAL_TE.get()); }
    public void updateData(int money, int stockValue) { this.cachedMoney = money; this.cachedStockValue = stockValue; setChanged(); }
    public int getCachedMoney() { return cachedMoney; }
    public int getCachedStockValue() { return cachedStockValue; }
    @Override public void load(BlockState state, CompoundNBT nbt) { super.load(state, nbt); cachedMoney = nbt.getInt("Money"); cachedStockValue = nbt.getInt("StockValue"); }
    @Override public CompoundNBT save(CompoundNBT nbt) { super.save(nbt); nbt.putInt("Money", cachedMoney); nbt.putInt("StockValue", cachedStockValue); return nbt; }
    @Override public ITextComponent getDisplayName() { return new TranslationTextComponent("block.campuslife.stock_terminal"); }
    @Nullable @Override public Container createMenu(int id, PlayerInventory inv, PlayerEntity player) { return new StockTerminalContainer(id, inv, this); }
}
