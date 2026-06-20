package com.campus.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class CryptoScreen extends ContainerScreen<CryptoContainer> {

    private static final ResourceLocation TEXTURE = new ResourceLocation("campuslife", "textures/gui/crypto.png");

    public CryptoScreen(CryptoContainer container, PlayerInventory inv, ITextComponent title) {
        super(container, inv, title);
        this.imageWidth = 176;
        this.imageHeight = 200;
        this.inventoryLabelX = 10000;
        this.inventoryLabelY = 10000;
    }

    @Override
    protected void init() {
        super.init();
        int cx = this.leftPos;
        int cy = this.topPos;

        String[] coins = {"BTC", "ETH", "DOGE"};
        int[] prices = {this.menu.getBtcPrice(), this.menu.getEthPrice(), this.menu.getDogePrice()};

        for (int i = 0; i < coins.length; i++) {
            final String coin = coins[i];
            final int price = prices[i];
            // Buy button
            this.addButton(new Button(cx + 8, cy + 20 + i * 30, 78, 22,
                new StringTextComponent("\u00a7a\u4e70\u5165 " + coin + " @" + price),
                b -> com.campus.network.NetworkHandler.CHANNEL.sendToServer(
                    new com.campus.network.NetworkHandler.CryptoBuyPacket(coin, 1))));
            // Sell button
            this.addButton(new Button(cx + 90, cy + 20 + i * 30, 78, 22,
                new StringTextComponent("\u00a7c\u5356\u51fa " + coin),
                b -> com.campus.network.NetworkHandler.CHANNEL.sendToServer(
                    new com.campus.network.NetworkHandler.CryptoSellPacket(coin, 1))));
        }
    }

    @Override
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int x, int y) {
        RenderSystem.color4f(1f, 1f, 1f, 1f);
        this.minecraft.getTextureManager().bind(TEXTURE);
        this.blit(matrixStack, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    protected void renderLabels(MatrixStack matrixStack, int x, int y) {
        this.font.draw(matrixStack, "\u00a76\u52a0\u5bc6\u8d27\u5e01\u4ea4\u6613\u6240", 8, 6, 0xFFFFFF);
        this.font.draw(matrixStack, "\u00a7e\u8d44\u91d1: \u00a7a" + this.menu.getPlayerMoney(), 8, 138, 0xFFFFFF);
    }
}
