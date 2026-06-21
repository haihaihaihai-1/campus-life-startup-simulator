package com.campus.gui;

import com.campus.economy.MoneyCapability;
import com.campus.systems.MergerSystem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.math.BlockPos;
import java.util.UUID;

public class MergerContainer extends Container {
    private final int playerMoney;
    private final UUID playerUUID;
    private final int boost;

    public MergerContainer(int windowId, PlayerInventory inv, BlockPos pos) {
        super(CampusContainers.MERGER_CONTAINER.get(), windowId);
        this.playerUUID = inv.player.getUUID();
        this.playerMoney = inv.player.getCapability(MoneyCapability.MONEY_CAP).map(m -> m.getMoney()).orElse(0);
        this.boost = MergerSystem.getTotalBoost(this.playerUUID);
    }
    @Override public boolean stillValid(PlayerEntity p) { return true; }
    public int getPlayerMoney() { return playerMoney; }
    public int getBoost() { return boost; }
    public void handleAcquire(PlayerEntity p, int idx) {
        if (p instanceof ServerPlayerEntity) MergerSystem.acquire((ServerPlayerEntity) p, idx);
    }
}
