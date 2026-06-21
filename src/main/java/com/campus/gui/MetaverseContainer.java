package com.campus.gui;

import com.campus.economy.MoneyCapability;
import com.campus.systems.MetaverseSystem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.math.BlockPos;
import java.util.UUID;

public class MetaverseContainer extends Container {
    private final int playerMoney;
    private final UUID playerUUID;

    public MetaverseContainer(int windowId, PlayerInventory inv, BlockPos pos) {
        super(CampusContainers.METAVERSE_CONTAINER.get(), windowId);
        this.playerUUID = inv.player.getUUID();
        this.playerMoney = inv.player.getCapability(MoneyCapability.MONEY_CAP).map(m -> m.getMoney()).orElse(0);
    }
    @Override public boolean stillValid(PlayerEntity p) { return true; }
    public int getPlayerMoney() { return playerMoney; }
    public void handleBuy(PlayerEntity p, int idx) {
        if (p instanceof ServerPlayerEntity) MetaverseSystem.buy((ServerPlayerEntity) p, idx);
    }
}
