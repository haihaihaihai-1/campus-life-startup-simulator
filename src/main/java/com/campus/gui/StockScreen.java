package com.campus.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.campus.network.NetworkHandler;
import com.campus.systems.StockMarketSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class StockScreen extends ContainerScreen<StockContainer> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("campuslife", "textures/gui/stock.png");

    public StockScreen(StockContainer container, PlayerInventory inv, ITextComponent title) {
        super(container, inv, title);
        this.imageWidth = 176;
        this.imageHeight = 200;
        this.inventoryLabelX = 10000;
        this.inventoryLabelY = 10000;
    }

    @Override
    protected void init() {
        super.init();
        for (int i = 0; i < StockMarketSystem.STOCKS.length; i++) {
            final int idx = i;
            StockMarketSystem.Stock s = StockMarketSystem.STOCKS[i];
            String trend = s.currentPrice >= s.previousPrice ? "\u00a7c\u25b2" : "\u00a7a\u25bc"; // 中国习惯: 涨红跌绿
            String label = s.symbol + " " + s.currentPrice + trend;
            // 买1股按钮
            this.addButton(new Button(this.leftPos + 6, this.topPos + 18 + i * 18, 90, 16,
                new StringTextComponent(label),
                b -> {}));
            // 买
            this.addButton(new Button(this.leftPos + 98, this.topPos + 18 + i * 18, 30, 16,
                new StringTextComponent("\u00a7c\u4e70"),
                b -> NetworkHandler.CHANNEL.sendToServer(new NetworkHandler.GenericActionPacket("stock_buy", idx))));
            // 卖
            this.addButton(new Button(this.leftPos + 130, this.topPos + 18 + i * 18, 30, 16,
                new StringTextComponent("\u00a7a\u5356"),
                b -> NetworkHandler.CHANNEL.sendToServer(new NetworkHandler.GenericActionPacket("stock_sell", idx))));
        }
    }

    @Override
    protected void renderBg(MatrixStack m, float pt, int x, int y) {
        RenderSystem.color4f(1f, 1f, 1f, 1f);
        this.minecraft.getTextureManager().bind(TEXTURE);
        this.blit(m, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    protected void renderLabels(MatrixStack m, int x, int y) {
        StockContainer menu = this.menu;
        this.font.draw(m, "\u00a76\u80a1\u5e02\u4ea4\u6613\u4e2d\u5fc3", 8, 6, 0xFFFFFF);
        this.font.draw(m, "\u00a7e\u8d44\u91d1: \u00a7a" + menu.getPlayerMoney(), 8, 138, 0xFFFFFF);
        this.font.draw(m, "\u00a77\u9ed8\u8ba4\u4e70\u5356\u5404 1 \u80a1", 8, 150, 0xFFFFFF);
    }
}
