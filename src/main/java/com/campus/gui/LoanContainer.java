package com.campus.gui;

import com.campus.economy.MoneyCapability;
import com.campus.systems.LoanSystem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.math.BlockPos;

import java.util.UUID;

public class LoanContainer extends Container {
    private final int playerMoney;
    private final UUID playerUUID;

    public LoanContainer(int windowId, PlayerInventory inv, BlockPos pos) {
        super(CampusContainers.LOAN_CONTAINER.get(), windowId);
        this.playerUUID = inv.player.getUUID();
        this.playerMoney = inv.player.getCapability(MoneyCapability.MONEY_CAP).map(m -> m.getMoney()).orElse(0);
    }
    @Override public boolean stillValid(PlayerEntity p) { return true; }
    public int getPlayerMoney() { return playerMoney; }
    public UUID getPlayerUUID() { return playerUUID; }

    public void handleTakeLoan(PlayerEntity p, int amount) {
        if (p instanceof ServerPlayerEntity) LoanSystem.takeLoan((ServerPlayerEntity) p, amount);
    }
    public void handleRepayLoan(PlayerEntity p, int amount) {
        if (p instanceof ServerPlayerEntity) LoanSystem.repayLoan((ServerPlayerEntity) p, amount);
    }
}
