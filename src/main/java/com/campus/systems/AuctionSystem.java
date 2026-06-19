package com.campus.systems;

import com.campus.CampusLife;
import com.campus.economy.MoneyCapability;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.*;

/**
 * 拍卖行系统 - 竞拍稀有物品
 */
@Mod.EventBusSubscriber(modid = CampusLife.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AuctionSystem {

    public static class AuctionItem {
        public String name, description;
        public int startPrice, currentPrice, buyoutPrice;
        public UUID highestBidder;
        public String highestBidderName;
        public int durationTicks;

        public AuctionItem(String name, String desc, int start, int buyout, int duration) {
            this.name = name; this.description = desc; this.startPrice = start;
            this.currentPrice = start; this.buyoutPrice = buyout; this.durationTicks = duration;
        }
    }

    private static final List<AuctionItem> activeAuctions = new ArrayList<>();
    private static int tickCounter = 0;
    private static final Random RAND = new Random();

    public static final Object[][] ITEM_POOL = {
        {"\u4f20\u8bf4\u4e2d\u7684\u521b\u4e1a\u624b\u518c", "\u521b\u4e1a\u754c\u7ecf\u5178", 500, 5000},
        {"\u9ec4\u91d1\u80a1\u6743\u8bc1\u4e66", "10%\u80a1\u6743\u51ed\u8bc1", 2000, 20000},
        {"\u72ec\u89d2\u517d\u62a5\u544a", "\u72ec\u5bb6\u5546\u4e1a\u673a\u5bc6", 1000, 10000},
        {"\u79d1\u6280\u4e13\u5229\u5305", "5\u9879\u9ad8\u4ef7\u503c\u4e13\u5229", 3000, 30000},
        {"\u9876\u7ea7\u5bfc\u5e08\u63a8\u8350\u4fe1", "\u83b7\u5f97\u9876\u7ea7\u5bfc\u5e08\u63a8\u8350", 800, 8000},
        {"\u9650\u91cf\u7248\u521b\u4e1a\u5957\u88c5", "\u7a00\u6709\u521b\u4e1a\u8d44\u6e90\u5305", 1500, 15000}
    };

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        tickCounter++;

        // 每30秒检查拍卖
        if (tickCounter % 600 == 0) {
            Iterator<AuctionItem> it = activeAuctions.iterator();
            while (it.hasNext()) {
                AuctionItem item = it.next();
                item.durationTicks -= 600;
                if (item.durationTicks <= 0) {
                    if (item.highestBidder != null && ServerLifecycleHooks.getCurrentServer() != null) {
                        ServerPlayerEntity winner = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(item.highestBidder);
                        if (winner != null) {
                            winner.sendMessage(new StringTextComponent("\u00a7a\u2714 \u62cd\u5356\u6210\u4ea4! \u4f60\u8d62\u5f97: " + item.name + " (\u00a76" + item.currentPrice + "\u91d1\u5e01)"), winner.getUUID());
                        }
                    }
                    it.remove();
                }
            }
        }

        // 每3分钟生成新拍卖品
        if (tickCounter % 3600 == 0 && activeAuctions.size() < 3) {
            Object[] pool = ITEM_POOL[RAND.nextInt(ITEM_POOL.length)];
            activeAuctions.add(new AuctionItem((String)pool[0], (String)pool[1], (Integer)pool[2], (Integer)pool[3], 6000 + RAND.nextInt(6000)));
            if (ServerLifecycleHooks.getCurrentServer() != null) {
                for (ServerPlayerEntity p : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
                    p.sendMessage(new StringTextComponent("\u00a7b\u2500\u2500\u2500 \u65b0\u62cd\u5356\u54c1\u4e0a\u67b6! \u8f93\u5165 /auction \u67e5\u770b \u2500\u2500\u2500"), p.getUUID());
                }
            }
        }
    }

    public static void show(ServerPlayerEntity player) {
        UUID uuid = player.getUUID();
        player.sendMessage(new StringTextComponent("\u00a76\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7e\ud83d\udd2e \u62cd\u5356\u884c (" + activeAuctions.size() + ")  \u00a76\u2551"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u255a\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255d"), uuid);

        if (activeAuctions.isEmpty()) {
            player.sendMessage(new StringTextComponent("\u00a77\u5f53\u524d\u65e0\u62cd\u5356\u54c1"), uuid);
        } else {
            for (int i = 0; i < activeAuctions.size(); i++) {
                AuctionItem item = activeAuctions.get(i);
                String bidder = item.highestBidderName != null ? item.highestBidderName : "\u65e0";
                player.sendMessage(new StringTextComponent(
                        "\u00a7e[" + (i+1) + "] " + item.name +
                        " \u00a7f|\u00a77 " + item.description +
                        " \u00a7f|\u00a76 \u8d77\u62cd:" + item.startPrice +
                        " \u00a7f|\u00a7a \u5f53\u524d:" + item.currentPrice +
                        " \u00a7f|\u00a7c \u4e00\u53e3\u4ef7:" + item.buyoutPrice +
                        " \u00a7f|\u00a7b \u7ade\u62cd\u8005:" + bidder +
                        " \u00a7f|\u00a77 " + (item.durationTicks/1200) + "\u5206\u949f"), uuid);
            }
            player.sendMessage(new StringTextComponent("\u00a7e/auction bid <\u7f16\u53f7> <\u4ef7\u683c> | /auction buyout <\u7f16\u53f7>"), uuid);
        }
    }

    public static boolean bid(ServerPlayerEntity player, int idx, int amount) {
        UUID uuid = player.getUUID();
        if (idx < 1 || idx > activeAuctions.size()) { player.sendMessage(new StringTextComponent("\u00a7c\u65e0\u6548\u7f16\u53f7!"), uuid); return false; }
        AuctionItem item = activeAuctions.get(idx - 1);
        if (amount <= item.currentPrice) { player.sendMessage(new StringTextComponent("\u00a7c\u51fa\u4ef7\u5fc5\u987b\u9ad8\u4e8e\u5f53\u524d\u4ef7!"), uuid); return false; }

        return player.getCapability(MoneyCapability.MONEY_CAP).map(m -> {
            if (m.spendMoney(amount)) {
                if (item.highestBidder != null) {
                    ServerPlayerEntity prev = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(item.highestBidder);
                    if (prev != null) {
                        prev.getCapability(MoneyCapability.MONEY_CAP).ifPresent(pm -> pm.addMoney(item.currentPrice));
                        prev.sendMessage(new StringTextComponent("\u00a7e\u4f60\u88ab\u8d85\u51fa! \u5df2\u9000\u8fd8 " + item.currentPrice + "\u91d1\u5e01"), prev.getUUID());
                    }
                }
                item.currentPrice = amount;
                item.highestBidder = uuid;
                item.highestBidderName = player.getName().getString();
                player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u51fa\u4ef7\u6210\u529f! " + item.name + " @ " + amount + "\u91d1\u5e01"), uuid);
                return true;
            }
            player.sendMessage(new StringTextComponent("\u00a7c\u8d44\u91d1\u4e0d\u8db3!"), uuid); return false;
        }).orElse(false);
    }

    public static boolean buyout(ServerPlayerEntity player, int idx) {
        UUID uuid = player.getUUID();
        if (idx < 1 || idx > activeAuctions.size()) { player.sendMessage(new StringTextComponent("\u00a7c\u65e0\u6548\u7f16\u53f7!"), uuid); return false; }
        AuctionItem item = activeAuctions.get(idx - 1);

        return player.getCapability(MoneyCapability.MONEY_CAP).map(m -> {
            if (m.spendMoney(item.buyoutPrice)) {
                if (item.highestBidder != null) {
                    ServerPlayerEntity prev = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(item.highestBidder);
                    if (prev != null) { prev.getCapability(MoneyCapability.MONEY_CAP).ifPresent(pm -> pm.addMoney(item.currentPrice)); }
                }
                player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u4e00\u53e3\u4ef7\u8d2d\u4e70\u6210\u529f! " + item.name), uuid);
                activeAuctions.remove(idx - 1);
                return true;
            }
            player.sendMessage(new StringTextComponent("\u00a7c\u8d44\u91d1\u4e0d\u8db3! \u9700" + item.buyoutPrice), uuid); return false;
        }).orElse(false);
    }
}
