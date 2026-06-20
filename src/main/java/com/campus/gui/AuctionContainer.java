package com.campus.gui;

import com.campus.economy.MoneyCapability;
import com.campus.systems.AuctionSystem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.registries.ObjectHolder;

public class AuctionContainer extends Container {

    private BlockPos pos;
    private int playerMoney;

    public AuctionContainer(int windowId, PlayerInventory inv, BlockPos pos) {
        super(CampusContainers.AUCTION_CONTAINER.get(), windowId);
        this.pos = pos;
        this.playerMoney = inv.player.getCapability(MoneyCapability.MONEY_CAP)
            .map(m -> m.getMoney()).orElse(0);
    }

    @Override
    public boolean stillValid(PlayerEntity player) { return true; }

    public int getPlayerMoney() { return playerMoney; }

    public java.util.List<AuctionSystem.AuctionItem> getActiveAuctions() {
        return AuctionSystem.getActiveAuctions();
    }

    public void handleBid(int auctionIdx, int bidAmount, PlayerEntity player) {
        if (player instanceof net.minecraft.entity.player.ServerPlayerEntity) {
            AuctionSystem.bid((net.minecraft.entity.player.ServerPlayerEntity) player, auctionIdx, bidAmount);
        }
    }

    public void handleBuyout(int auctionIdx, PlayerEntity player) {
        if (player instanceof net.minecraft.entity.player.ServerPlayerEntity) {
            AuctionSystem.buyout((net.minecraft.entity.player.ServerPlayerEntity) player, auctionIdx);
        }
    }
}
