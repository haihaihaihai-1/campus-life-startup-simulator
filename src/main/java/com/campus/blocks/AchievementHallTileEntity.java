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
public class AchievementHallTileEntity extends TileEntity implements INamedContainerProvider {
    private int cachedMoney = 0;
    private int cachedAchievementCount = 0;
    public AchievementHallTileEntity(TileEntityType<?> type) { super(type); }
    public static AchievementHallTileEntity create() { return new AchievementHallTileEntity(CampusBlocks.ACHIEVEMENT_HALL_TE.get()); }
    public void updateData(int money, int achievementCount) { this.cachedMoney = money; this.cachedAchievementCount = achievementCount; setChanged(); }
    public int getCachedMoney() { return cachedMoney; }
    public int getCachedAchievementCount() { return cachedAchievementCount; }
    @Override public void load(BlockState state, CompoundNBT nbt) { super.load(state, nbt); cachedMoney = nbt.getInt("Money"); cachedAchievementCount = nbt.getInt("AchievementCount"); }
    @Override public CompoundNBT save(CompoundNBT nbt) { super.save(nbt); nbt.putInt("Money", cachedMoney); nbt.putInt("AchievementCount", cachedAchievementCount); return nbt; }
    @Override public ITextComponent getDisplayName() { return new TranslationTextComponent("block.campuslife.achievement_hall"); }
    @Nullable @Override public Container createMenu(int id, PlayerInventory inv, PlayerEntity player) { return new AchievementHallContainer(id, inv, this); }
}
