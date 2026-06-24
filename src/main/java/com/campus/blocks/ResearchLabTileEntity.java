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
public class ResearchLabTileEntity extends TileEntity implements INamedContainerProvider {
    private int cachedMoney = 0;
    private int cachedTechCount = 0;
    public ResearchLabTileEntity(TileEntityType<?> type) { super(type); }
    public static ResearchLabTileEntity create() { return new ResearchLabTileEntity(CampusBlocks.RESEARCH_LAB_TE.get()); }
    public void updateData(int money, int techCount) { this.cachedMoney = money; this.cachedTechCount = techCount; setChanged(); }
    public int getCachedMoney() { return cachedMoney; }
    public int getCachedTechCount() { return cachedTechCount; }
    @Override public void load(BlockState state, CompoundNBT nbt) { super.load(state, nbt); cachedMoney = nbt.getInt("Money"); cachedTechCount = nbt.getInt("TechCount"); }
    @Override public CompoundNBT save(CompoundNBT nbt) { super.save(nbt); nbt.putInt("Money", cachedMoney); nbt.putInt("TechCount", cachedTechCount); return nbt; }
    @Override public ITextComponent getDisplayName() { return new TranslationTextComponent("block.campuslife.research_lab"); }
    @Nullable @Override public Container createMenu(int id, PlayerInventory inv, PlayerEntity player) { return new ResearchLabContainer(id, inv, this); }
}
