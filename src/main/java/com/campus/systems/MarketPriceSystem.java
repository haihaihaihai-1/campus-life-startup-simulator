package com.campus.systems;

import com.campus.CampusLife;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.*;

/**
 * 动态市场定价系统拼图 - 供需关系影响价格
 * 参考: 经济学供需模型
 */
@Mod.EventBusSubscriber(modid = CampusLife.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MarketPriceSystem {

    public static class Product {
        public String name;
        public int basePrice;
        public int currentPrice;
        public int supply;
        public int demand;
        public String category;

        public Product(String name, int basePrice, String category) {
            this.name = name;
            this.basePrice = basePrice;
            this.currentPrice = basePrice;
            this.supply = 100;
            this.demand = 100;
            this.category = category;
        }
    }

    public static final Map<String, Product> market = new HashMap<>();
    private static int tickCounter = 0;

    static {
        market.put("coffee_cup", new Product("\u5496\u5561\u676f", 50, "\u996e\u54c1"));
        market.put("handmade_craft", new Product("\u624b\u5de5\u827a\u54c1", 80, "\u5de5\u827a"));
        market.put("tech_gadget", new Product("\u79d1\u6280\u5c0f\u73a9\u5177", 200, "\u79d1\u6280"));
        market.put("raw_material", new Product("\u539f\u6750\u6599", 20, "\u6750\u6599"));
        market.put("innovation_chip", new Product("\u521b\u65b0\u82af\u7247", 150, "\u79d1\u6280"));
        market.put("textbook", new Product("\u8bfe\u672c", 30, "\u6587\u5177"));
        market.put("business_plan", new Product("\u521b\u4e1a\u8ba1\u5212\u4e66", 300, "\u6587\u5177"));
        market.put("startup_kit", new Product("\u521b\u4e1a\u5957\u88c5", 500, "\u7279\u6b8a"));
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        tickCounter++;

        // 每2分钟更新一次市场价格
        if (tickCounter % 2400 == 0) {
            Random rand = new Random();
            for (Product p : market.values()) {
                // 随机波动供需
                p.supply += rand.nextInt(20) - 10;
                p.demand += rand.nextInt(20) - 10;
                p.supply = Math.max(10, Math.min(500, p.supply));
                p.demand = Math.max(10, Math.min(500, p.demand));

                // 价格 = 基础价格 * (需求/供给) * 随机波动
                double ratio = (double) p.demand / p.supply;
                double fluctuation = 0.9 + rand.nextDouble() * 0.2;
                p.currentPrice = (int)(p.basePrice * ratio * fluctuation);
                p.currentPrice = Math.max(p.basePrice / 3, Math.min(p.basePrice * 3, p.currentPrice));
            }

            // 广播市场更新
            if (ServerLifecycleHooks.getCurrentServer() != null) {
                for (ServerPlayerEntity player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
                    player.sendMessage(new StringTextComponent(
                            "\u00a7b\u2500\u2500\u2500 \u5e02\u573a\u4ef7\u683c\u5df2\u66f4\u65b0 \u2500\u2500\u2500"), player.getUUID());
                    player.sendMessage(new StringTextComponent(
                            "\u00a7e\u8f93\u5165 /market list \u67e5\u770b\u6700\u65b0\u4ef7\u683c"), player.getUUID());
                }
            }
        }
    }

    public static void showMarket(ServerPlayerEntity player) {
        UUID uuid = player.getUUID();
        player.sendMessage(new StringTextComponent("\u00a76\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7b\u25c8 \u5e02\u573a\u884c\u60c5  \u00a76\u2551"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u255a\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255d"), uuid);

        for (Product p : market.values()) {
            String trend;
            if (p.currentPrice > p.basePrice * 1.2) trend = "\u00a7a\u2191\u4e0a\u6da8";
            else if (p.currentPrice < p.basePrice * 0.8) trend = "\u00a7c\u2193\u4e0b\u8dcc";
            else trend = "\u00a7e\u2192\u5e73\u7a33";

            player.sendMessage(new StringTextComponent(
                    "\u00a7e" + p.name +
                    " \u00a7f|\u00a76 \u4ef7:" + p.currentPrice +
                    " \u00a7f|\u00a7a \u4f9b:" + p.supply +
                    " \u00a7f|\u00a7c \u9700:" + p.demand +
                    " \u00a7f| " + trend +
                    " \u00a77[" + p.category + "]"), uuid);
        }
        player.sendMessage(new StringTextComponent("\u00a7e\u8f93\u5165 /market buy <\u7269\u54c1> \u8d2d\u4e70"), uuid);
    }

    public static int getPrice(String productName) {
        Product p = market.get(productName);
        return p != null ? p.currentPrice : 0;
    }

    public static void recordPurchase(String productName, int quantity) {
        Product p = market.get(productName);
        if (p != null) {
            p.supply -= quantity;
            p.demand += quantity;
        }
    }

    public static void recordSale(String productName, int quantity) {
        Product p = market.get(productName);
        if (p != null) {
            p.supply += quantity;
            p.demand -= quantity;
        }
    }

    public static String findProduct(String input) {
        for (String key : market.keySet()) {
            if (key.equalsIgnoreCase(input) || market.get(key).name.equals(input)) {
                return key;
            }
        }
        return null;
    }
}
