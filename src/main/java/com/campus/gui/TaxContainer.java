package com.campus.gui;

import com.campus.economy.MoneyCapability;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.math.BlockPos;
import java.util.UUID;

public class TaxContainer extends Container {
    private final int playerMoney;
    private final UUID playerUUID;
    public TaxContainer(int windowId, PlayerInventory inv, BlockPos pos) {
        super(CampusContainers.TAX_CONTAINER.get(), windowId);
        this.playerUUID = inv.player.getUUID();
        this.playerMoney = inv.player.getCapability(MoneyCapability.MONEY_CAP).map(m -> m.getMoney()).orElse(0);
    }
    @Override public boolean stillValid(PlayerEntity p) { return true; }
    public int getPlayerMoney() { return playerMoney; }
    public UUID getPlayerUUID() { return playerUUID; }
}
