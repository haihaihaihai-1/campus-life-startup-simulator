package com.campus.gui;

import com.campus.economy.MoneyCapability;
import com.campus.systems.CryptoSystem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.math.BlockPos;

public class CryptoContainer extends Container {

    private BlockPos pos;
    private int playerMoney;
    private int btcPrice;
    private int ethPrice;
    private int dogePrice;

    public CryptoContainer(int windowId, PlayerInventory inv, BlockPos pos) {
        super(CampusContainers.CRYPTO_CONTAINER.get(), windowId);
        this.pos = pos;
        this.playerMoney = inv.player.getCapability(MoneyCapability.MONEY_CAP)
            .map(m -> m.getMoney()).orElse(0);
        this.btcPrice = CryptoSystem.getPrice("BTC");
        this.ethPrice = CryptoSystem.getPrice("ETH");
        this.dogePrice = CryptoSystem.getPrice("DOGE");
    }

    @Override
    public boolean stillValid(PlayerEntity player) { return true; }

    public int getPlayerMoney() { return playerMoney; }
    public int getBtcPrice() { return btcPrice; }
    public int getEthPrice() { return ethPrice; }
    public int getDogePrice() { return dogePrice; }

    public int getHolding(PlayerEntity player, String coin) {
        return CryptoSystem.getHolding(player.getUUID(), coin);
    }
}
