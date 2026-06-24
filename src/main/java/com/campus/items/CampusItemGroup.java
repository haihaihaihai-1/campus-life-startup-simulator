package com.campus.items;

import com.campus.CampusLife;
import com.campus.blocks.CampusBlocks;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CampusItemGroup extends ItemGroup {
    public static final CampusItemGroup INSTANCE = new CampusItemGroup(ItemGroup.TABS.length, "campuslife");
    
    public CampusItemGroup(int index, String label) {
        super(index, label);
    }
    
    @Override
    @OnlyIn(Dist.CLIENT)
    public ItemStack makeIcon() {
        return new ItemStack(CampusBlocks.STARTUP_WORKBENCH.get().asItem());
    }
    
    @Override
    public void fillItemList(NonNullList<ItemStack> items) {
        // 添加所有方块
        items.add(new ItemStack(CampusBlocks.STARTUP_WORKBENCH.get()));
        items.add(new ItemStack(CampusBlocks.MARKET_STALL.get()));
        items.add(new ItemStack(CampusBlocks.BANK_COUNTER.get()));
        items.add(new ItemStack(CampusBlocks.COFFEE_MACHINE.get()));
        items.add(new ItemStack(CampusBlocks.INVEST_CENTER.get()));
        items.add(new ItemStack(CampusBlocks.RESEARCH_LAB.get()));
        items.add(new ItemStack(CampusBlocks.EMPLOYEE_CENTER.get()));
        items.add(new ItemStack(CampusBlocks.INCUBATOR.get()));
        items.add(new ItemStack(CampusBlocks.STOCK_TERMINAL.get()));
        items.add(new ItemStack(CampusBlocks.TRAINING_CENTER.get()));
        items.add(new ItemStack(CampusBlocks.TAX_OFFICE.get()));
        items.add(new ItemStack(CampusBlocks.ACHIEVEMENT_HALL.get()));
        items.add(new ItemStack(CampusBlocks.COMPETITION_ARENA.get()));
        items.add(new ItemStack(CampusBlocks.ALLIANCE_HQ.get()));
        // 添加所有物品
        CampusItems.ITEMS.getEntries().forEach(entry -> {
            if (!items.contains(new ItemStack(entry.get()))) {
                items.add(new ItemStack(entry.get()));
            }
        });
    }
}
