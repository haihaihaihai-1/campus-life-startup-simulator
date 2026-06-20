package com.campus.gui;

import com.campus.economy.MoneyCapability;
import com.campus.systems.StockMarketSystem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.math.BlockPos;

public class StockContainer extends Container {
    private final BlockPos pos;
    private final int playerMoney;

    public StockContainer(int windowId, PlayerInventory inv, BlockPos pos) {
        super(CampusContainers.STOCK_CONTAINER.get(), windowId);
        this.pos = pos;
        this.playerMoney = inv.player.getCapability(MoneyCapability.MONEY_CAP).map(m -> m.getMoney()).orElse(0);
    }

    @Override
    public boolean stillValid(PlayerEntity p) { return true; }

    public int getPlayerMoney() { return playerMoney; }

    public void handleBuy(int stockIdx, int shares, PlayerEntity p) {
        if (p instanceof ServerPlayerEntity) {
            StockMarketSystem.buy((ServerPlayerEntity) p, stockIdx + 1, shares);
        }
    }
    public void handleSell(int stockIdx, int shares, PlayerEntity p) {
        if (p instanceof ServerPlayerEntity) {
            StockMarketSystem.sell((ServerPlayerEntity) p, stockIdx + 1, shares);
        }
    }
}
