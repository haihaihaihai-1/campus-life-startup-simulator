package com.campus.gui;

import com.campus.economy.MoneyCapability;
import com.campus.economy.SkillCapability;
import com.campus.systems.ContractSystem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.math.BlockPos;

import java.util.UUID;

public class ContractContainer extends Container {
    private final BlockPos pos;
    private final int playerMoney;
    private final int playerLevel;
    private final UUID playerUUID;
    private int activeContractIdx = -1;

    public ContractContainer(int windowId, PlayerInventory inv, BlockPos pos) {
        super(CampusContainers.CONTRACT_CONTAINER.get(), windowId);
        this.pos = pos;
        this.playerUUID = inv.player.getUUID();
        this.playerMoney = inv.player.getCapability(MoneyCapability.MONEY_CAP).map(m -> m.getMoney()).orElse(0);
        this.playerLevel = inv.player.getCapability(SkillCapability.SKILL_CAP).map(s -> s.getLevel()).orElse(1);
    }

    @Override
    public boolean stillValid(PlayerEntity p) { return true; }

    public int getPlayerMoney() { return playerMoney; }
    public int getPlayerLevel() { return playerLevel; }
    public UUID getPlayerUUID() { return playerUUID; }

    public void handleSign(int idx, PlayerEntity player) {
        if (player instanceof ServerPlayerEntity) {
            ContractSystem.sign((ServerPlayerEntity) player, idx + 1);
        }
    }
}
