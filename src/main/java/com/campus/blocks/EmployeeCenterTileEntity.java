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
public class EmployeeCenterTileEntity extends TileEntity implements INamedContainerProvider {
    private int cachedMoney = 0;
    private int cachedEmployeeCount = 0;
    public EmployeeCenterTileEntity(TileEntityType<?> type) { super(type); }
    public static EmployeeCenterTileEntity create() { return new EmployeeCenterTileEntity(CampusBlocks.EMPLOYEE_CENTER_TE.get()); }
    public void updateData(int money, int employeeCount) { this.cachedMoney = money; this.cachedEmployeeCount = employeeCount; setChanged(); }
    public int getCachedMoney() { return cachedMoney; }
    public int getCachedEmployeeCount() { return cachedEmployeeCount; }
    @Override public void load(BlockState state, CompoundNBT nbt) { super.load(state, nbt); cachedMoney = nbt.getInt("Money"); cachedEmployeeCount = nbt.getInt("EmployeeCount"); }
    @Override public CompoundNBT save(CompoundNBT nbt) { super.save(nbt); nbt.putInt("Money", cachedMoney); nbt.putInt("EmployeeCount", cachedEmployeeCount); return nbt; }
    @Override public ITextComponent getDisplayName() { return new TranslationTextComponent("block.campuslife.employee_center"); }
    @Nullable @Override public Container createMenu(int id, PlayerInventory inv, PlayerEntity player) { return new EmployeeCenterContainer(id, inv, this); }
}
