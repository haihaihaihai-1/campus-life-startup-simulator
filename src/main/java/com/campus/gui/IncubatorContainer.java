package com.campus.gui;

import com.campus.economy.MoneyCapability;
import com.campus.economy.SkillCapability;
import com.campus.systems.IncubatorSystem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.registries.ObjectHolder;

public class IncubatorContainer extends Container {

    private BlockPos pos;
    private int playerMoney;
    private int playerLevel;
    private int currentIncubator = -1;
    private int remainingTicks = 0;

    public IncubatorContainer(int windowId, PlayerInventory inv, BlockPos pos) {
        super(CampusContainers.INCUBATOR_CONTAINER.get(), windowId);
        this.pos = pos;
        this.playerMoney = inv.player.getCapability(MoneyCapability.MONEY_CAP)
            .map(m -> m.getMoney()).orElse(0);
        this.playerLevel = inv.player.getCapability(SkillCapability.SKILL_CAP)
            .map(s -> s.getLevel()).orElse(1);
        IncubatorSystem.IncubatorSession session = IncubatorSystem.getSession(inv.player.getUUID());
        if (session != null) {
            this.currentIncubator = session.incubatorIdx;
            this.remainingTicks = session.remainingTicks;
        }
    }

    public IncubatorContainer(int windowId, PlayerInventory inv, PacketBuffer buf) {
        this(windowId, inv, buf.readBlockPos());
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        return true;
    }

    public int getPlayerMoney() { return playerMoney; }
    public int getPlayerLevel() { return playerLevel; }
    public int getCurrentIncubator() { return currentIncubator; }
    public int getRemainingTicks() { return remainingTicks; }

    public void handleJoin(int idx, PlayerEntity player) {
        if (player instanceof net.minecraft.entity.player.ServerPlayerEntity) {
            IncubatorSystem.join((net.minecraft.entity.player.ServerPlayerEntity) player, idx + 1);
        }
    }
}
