package com.campus.gui;

import com.campus.economy.MoneyCapability;
import com.campus.economy.SkillCapability;
import com.campus.systems.VCSystem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.math.BlockPos;

import java.util.UUID;

public class VCContainer extends Container {
    private final BlockPos pos;
    private final int playerMoney;
    private final int playerLevel;
    private final int currentRound;
    private final int valuation;
    private final UUID playerUUID;

    public VCContainer(int windowId, PlayerInventory inv, BlockPos pos) {
        super(CampusContainers.VC_CONTAINER.get(), windowId);
        this.pos = pos;
        this.playerUUID = inv.player.getUUID();
        this.playerMoney = inv.player.getCapability(MoneyCapability.MONEY_CAP).map(m -> m.getMoney()).orElse(0);
        this.playerLevel = inv.player.getCapability(SkillCapability.SKILL_CAP).map(s -> s.getLevel()).orElse(1);
        this.currentRound = VCSystem.getRound(this.playerUUID);
        this.valuation = VCSystem.getValuation(this.playerUUID);
    }

    @Override
    public boolean stillValid(PlayerEntity p) { return true; }

    public int getPlayerMoney() { return playerMoney; }
    public int getPlayerLevel() { return playerLevel; }
    public int getCurrentRound() { return currentRound; }
    public int getValuation() { return valuation; }

    public void handleRaise(int idx, PlayerEntity p) {
        if (p instanceof ServerPlayerEntity) {
            VCSystem.raiseRound((ServerPlayerEntity) p, idx + 1);
        }
    }
}
