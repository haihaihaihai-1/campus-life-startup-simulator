package com.campus.gui;

import com.campus.economy.MoneyCapability;
import com.campus.systems.IPOSystem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.math.BlockPos;

import java.util.UUID;

public class IPOContainer extends Container {
    private final BlockPos pos;
    private final int playerMoney;
    private final boolean listed;
    private final int marketCap;
    private final UUID playerUUID;

    public IPOContainer(int windowId, PlayerInventory inv, BlockPos pos) {
        super(CampusContainers.IPO_CONTAINER.get(), windowId);
        this.pos = pos;
        this.playerUUID = inv.player.getUUID();
        this.playerMoney = inv.player.getCapability(MoneyCapability.MONEY_CAP).map(m -> m.getMoney()).orElse(0);
        this.listed = IPOSystem.isListed(this.playerUUID);
        this.marketCap = IPOSystem.getMarketCap(this.playerUUID);
    }

    @Override
    public boolean stillValid(PlayerEntity p) { return true; }

    public int getPlayerMoney() { return playerMoney; }
    public boolean isListed() { return listed; }
    public int getMarketCap() { return marketCap; }

    public void handleIPO(PlayerEntity p) {
        if (p instanceof ServerPlayerEntity) {
            IPOSystem.goIPO((ServerPlayerEntity) p);
        }
    }
}
