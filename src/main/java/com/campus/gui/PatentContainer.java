package com.campus.gui;

import com.campus.economy.MoneyCapability;
import com.campus.systems.PatentSystem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.math.BlockPos;
import java.util.UUID;

public class PatentContainer extends Container {
    private final int playerMoney;
    private final UUID playerUUID;
    private final int patentCount;

    public PatentContainer(int windowId, PlayerInventory inv, BlockPos pos) {
        super(CampusContainers.PATENT_CONTAINER.get(), windowId);
        this.playerUUID = inv.player.getUUID();
        this.playerMoney = inv.player.getCapability(MoneyCapability.MONEY_CAP).map(m -> m.getMoney()).orElse(0);
        this.patentCount = PatentSystem.getPatentCount(this.playerUUID);
    }
    @Override public boolean stillValid(PlayerEntity p) { return true; }
    public int getPlayerMoney() { return playerMoney; }
    public int getPatentCount() { return patentCount; }
    public UUID getPlayerUUID() { return playerUUID; }
    public void handleFilePatent(PlayerEntity p) {
        if (p instanceof ServerPlayerEntity) PatentSystem.filePatent((ServerPlayerEntity) p);
    }
}
