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
public class CompetitionArenaTileEntity extends TileEntity implements INamedContainerProvider {
    private int cachedMoney = 0;
    private int cachedCompetitionWins = 0;
    public CompetitionArenaTileEntity(TileEntityType<?> type) { super(type); }
    public static CompetitionArenaTileEntity create() { return new CompetitionArenaTileEntity(CampusBlocks.COMPETITION_ARENA_TE.get()); }
    public void updateData(int money, int competitionWins) { this.cachedMoney = money; this.cachedCompetitionWins = competitionWins; setChanged(); }
    public int getCachedMoney() { return cachedMoney; }
    public int getCachedCompetitionWins() { return cachedCompetitionWins; }
    @Override public void load(BlockState state, CompoundNBT nbt) { super.load(state, nbt); cachedMoney = nbt.getInt("Money"); cachedCompetitionWins = nbt.getInt("CompetitionWins"); }
    @Override public CompoundNBT save(CompoundNBT nbt) { super.save(nbt); nbt.putInt("Money", cachedMoney); nbt.putInt("CompetitionWins", cachedCompetitionWins); return nbt; }
    @Override public ITextComponent getDisplayName() { return new TranslationTextComponent("block.campuslife.competition_arena"); }
    @Nullable @Override public Container createMenu(int id, PlayerInventory inv, PlayerEntity player) { return new CompetitionArenaContainer(id, inv, this); }
}
