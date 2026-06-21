package com.campus.gui;

import com.campus.economy.MoneyCapability;
import com.campus.systems.EmployeeSystem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.math.BlockPos;
import java.util.UUID;

public class EmployeeContainer extends Container {
    private final int playerMoney;
    private final UUID playerUUID;
    private final int totalEmployees;
    private final int totalIncome;

    public EmployeeContainer(int windowId, PlayerInventory inv, BlockPos pos) {
        super(CampusContainers.EMPLOYEE_CONTAINER.get(), windowId);
        this.playerUUID = inv.player.getUUID();
        this.playerMoney = inv.player.getCapability(MoneyCapability.MONEY_CAP).map(m -> m.getMoney()).orElse(0);
        this.totalEmployees = EmployeeSystem.calculateTotalEmployees(this.playerUUID);
        this.totalIncome = EmployeeSystem.calculateTotalIncome(this.playerUUID);
    }
    @Override public boolean stillValid(PlayerEntity p) { return true; }
    public int getPlayerMoney() { return playerMoney; }
    public int getTotalEmployees() { return totalEmployees; }
    public int getTotalIncome() { return totalIncome; }
    public void handleHire(PlayerEntity p, String type) {
        if (p instanceof ServerPlayerEntity) EmployeeSystem.hire((ServerPlayerEntity) p, type);
    }
}
