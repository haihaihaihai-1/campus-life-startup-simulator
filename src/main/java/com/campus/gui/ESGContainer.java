package com.campus.gui;

import com.campus.economy.MoneyCapability;
import com.campus.systems.ESGSystem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.math.BlockPos;

import java.util.UUID;

public class ESGContainer extends Container {
    private final BlockPos pos;
    private final int playerMoney;
    private final int rating;
    private final UUID playerUUID;

    public ESGContainer(int windowId, PlayerInventory inv, BlockPos pos) {
        super(CampusContainers.ESG_CONTAINER.get(), windowId);
        this.pos = pos;
        this.playerUUID = inv.player.getUUID();
        this.playerMoney = inv.player.getCapability(MoneyCapability.MONEY_CAP).map(m -> m.getMoney()).orElse(0);
        this.rating = ESGSystem.getRating(this.playerUUID);
    }

    @Override
    public boolean stillValid(PlayerEntity p) { return true; }

    public int getPlayerMoney() { return playerMoney; }
    public int getRating() { return rating; }

    public void handleInvest(int dimension, int amount, PlayerEntity p) {
        if (p instanceof ServerPlayerEntity) {
            ESGSystem.invest((ServerPlayerEntity) p, dimension + 1, amount);
        }
    }
}
