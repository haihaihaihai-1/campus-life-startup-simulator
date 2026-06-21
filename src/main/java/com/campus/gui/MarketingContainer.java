package com.campus.gui;

import com.campus.economy.MoneyCapability;
import com.campus.systems.MarketingSystem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.math.BlockPos;
import java.util.UUID;

public class MarketingContainer extends Container {
    private final int playerMoney;
    private final UUID playerUUID;
    private final int boostPercent;

    public MarketingContainer(int windowId, PlayerInventory inv, BlockPos pos) {
        super(CampusContainers.MARKETING_CONTAINER.get(), windowId);
        this.playerUUID = inv.player.getUUID();
        this.playerMoney = inv.player.getCapability(MoneyCapability.MONEY_CAP).map(m -> m.getMoney()).orElse(0);
        this.boostPercent = MarketingSystem.getBoostPercent(this.playerUUID);
    }
    @Override public boolean stillValid(PlayerEntity p) { return true; }
    public int getPlayerMoney() { return playerMoney; }
    public int getBoostPercent() { return boostPercent; }
    public void handleLaunch(PlayerEntity p, int typeIdx, int budget) {
        if (p instanceof ServerPlayerEntity) {
            MarketingSystem.AdType type = MarketingSystem.getTypeByIndex(typeIdx);
            if (type != null) MarketingSystem.launchCampaign((ServerPlayerEntity) p, type, budget);
        }
    }
}
