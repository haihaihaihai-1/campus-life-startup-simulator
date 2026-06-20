package com.campus.systems;

import com.campus.economy.MoneyCapability;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;

import java.util.*;

/**
 * 拍卖系统 - 物品竞价拍卖
 */
public class AuctionSystem {

    public static class AuctionItem {
        public final String itemName;
        public final int startBid;
        public final int buyoutPrice;
        public int currentBid;
        public UUID highestBidder;
        public int remainingTicks;

        public AuctionItem(String name, int start, int buyout, int duration) {
            this.itemName = name;
            this.startBid = start;
            this.currentBid = start;
            this.buyoutPrice = buyout;
            this.remainingTicks = duration;
        }
    }

    private static final List<AuctionItem> ACTIVE = new ArrayList<>();

    static {
        // 初始化几个常驻拍卖品
        ACTIVE.add(new AuctionItem("\u521b\u65b0\u82af\u7247", 500, 5000, 12000));
        ACTIVE.add(new AuctionItem("\u9ad8\u7aef\u521b\u4e1a\u5957\u88c5", 2000, 15000, 18000));
        ACTIVE.add(new AuctionItem("\u73a0\u8d35\u539f\u6750\u6599", 800, 7000, 15000));
        ACTIVE.add(new AuctionItem("\u5546\u4e1a\u8ba1\u5212\u6a21\u677f", 300, 2500, 9600));
    }

    public static List<AuctionItem> getActiveAuctions() {
        return new ArrayList<>(ACTIVE);
    }

    public static boolean bid(ServerPlayerEntity player, int idx, int amount) {
        if (idx < 0 || idx >= ACTIVE.size()) {
            player.sendMessage(new StringTextComponent("\u00a7c\u65e0\u6548\u7f16\u53f7"), player.getUUID());
            return false;
        }
        AuctionItem item = ACTIVE.get(idx);
        if (amount <= item.currentBid) {
            player.sendMessage(new StringTextComponent("\u00a7c\u51fa\u4ef7\u9700\u9ad8\u4e8e\u5f53\u524d"), player.getUUID());
            return false;
        }
        return player.getCapability(MoneyCapability.MONEY_CAP).map(m -> {
            if (m.spendMoney(amount)) {
                item.currentBid = amount;
                item.highestBidder = player.getUUID();
                player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u51fa\u4ef7\u6210\u529f " + amount), player.getUUID());
                return true;
            }
            player.sendMessage(new StringTextComponent("\u00a7c\u8d44\u91d1\u4e0d\u8db3"), player.getUUID());
            return false;
        }).orElse(false);
    }

    public static boolean buyout(ServerPlayerEntity player, int idx) {
        if (idx < 0 || idx >= ACTIVE.size()) {
            player.sendMessage(new StringTextComponent("\u00a7c\u65e0\u6548\u7f16\u53f7"), player.getUUID());
            return false;
        }
        AuctionItem item = ACTIVE.get(idx);
        return player.getCapability(MoneyCapability.MONEY_CAP).map(m -> {
            if (m.spendMoney(item.buyoutPrice)) {
                player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u4e00\u53e3\u4ef7\u4e70\u5165: " + item.itemName), player.getUUID());
                ACTIVE.remove(idx);
                return true;
            }
            player.sendMessage(new StringTextComponent("\u00a7c\u8d44\u91d1\u4e0d\u8db3, \u9700 " + item.buyoutPrice), player.getUUID());
            return false;
        }).orElse(false);
    }
}
