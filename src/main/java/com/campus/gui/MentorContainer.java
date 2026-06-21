package com.campus.gui;

import com.campus.economy.MoneyCapability;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.math.BlockPos;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class MentorContainer extends Container {
    protected final PlayerInventory playerInv;
    protected final BlockPos pos;

    public MentorContainer(int id, PlayerInventory inv, BlockPos pos) {
        super(CampusContainers.MENTOR_CONTAINER.get(), id);
        this.playerInv = inv;
        this.pos = pos;
    }

    @Override public boolean stillValid(PlayerEntity p) { return true; }

    public UUID getPlayerUUID() { return playerInv.player.getUUID(); }

    public int getMoney() {
        AtomicInteger r = new AtomicInteger(0);
        playerInv.player.getCapability(MoneyCapability.MONEY_CAP).ifPresent(m -> r.set(m.getMoney()));
        return r.get();
    }
}
