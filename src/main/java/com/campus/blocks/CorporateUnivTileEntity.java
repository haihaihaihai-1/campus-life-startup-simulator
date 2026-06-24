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
public class CorporateUnivTileEntity extends TileEntity implements INamedContainerProvider {
    private int cachedMoney = 0;
    private int cachedCourseCount = 0;
    public CorporateUnivTileEntity(TileEntityType<?> type) { super(type); }
    public static CorporateUnivTileEntity create() { return new CorporateUnivTileEntity(CampusBlocks.CORPORATE_UNIV_TE.get()); }
    public void updateData(int money, int courseCount) { this.cachedMoney = money; this.cachedCourseCount = courseCount; setChanged(); }
    public int getCachedMoney() { return cachedMoney; }
    public int getCachedCourseCount() { return cachedCourseCount; }
    @Override public void load(BlockState state, CompoundNBT nbt) { super.load(state, nbt); cachedMoney = nbt.getInt("Money"); cachedCourseCount = nbt.getInt("CourseCount"); }
    @Override public CompoundNBT save(CompoundNBT nbt) { super.save(nbt); nbt.putInt("Money", cachedMoney); nbt.putInt("CourseCount", cachedCourseCount); return nbt; }
    @Override public ITextComponent getDisplayName() { return new TranslationTextComponent("block.campuslife.corporate_univ"); }
    @Nullable @Override public Container createMenu(int id, PlayerInventory inv, PlayerEntity player) { return new CorporateUnivContainer(id, inv, this); }
}
