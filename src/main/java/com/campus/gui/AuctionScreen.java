package com.campus.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.campus.systems.AuctionSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class AuctionScreen extends ContainerScreen<AuctionContainer> {

    private static final ResourceLocation TEXTURE = new ResourceLocation("campuslife", "textures/gui/auction.png");

    public AuctionScreen(AuctionContainer container, PlayerInventory inv, ITextComponent title) {
        super(container, inv, title);
        this.imageWidth = 176;
        this.imageHeight = 200;
        this.inventoryLabelX = 10000;
        this.inventoryLabelY = 10000;
    }

    @Override
    protected void init() {
        super.init();
        java.util.List<AuctionSystem.AuctionItem> items = this.menu.getActiveAuctions();
        int cx = this.leftPos;
        int cy = this.topPos;

        int max = Math.min(items.size(), 4);
        for (int i = 0; i < max; i++) {
            final int idx = i;
            AuctionSystem.AuctionItem item = items.get(i);
            String label = item.itemName + " | " + item.currentBid + "\u91d1";
            this.addButton(new Button(cx + 8, cy + 20 + i * 28, 110, 24,
                new StringTextComponent("\u00a76\u7ade\u4ef7+10%: " + label),
                b -> com.campus.network.NetworkHandler.CHANNEL.sendToServer(
                    new com.campus.network.NetworkHandler.AuctionBidPacket(idx, (int)(item.currentBid * 1.1)))));
            this.addButton(new Button(cx + 122, cy + 20 + i * 28, 46, 24,
                new StringTextComponent("\u00a7c\u4e00\u53e3\u4ef7"),
                b -> com.campus.network.NetworkHandler.CHANNEL.sendToServer(
                    new com.campus.network.NetworkHandler.AuctionBuyoutPacket(idx))));
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
        this.font.draw(matrixStack, "\u00a76\u62cd\u5356\u884c", 8, 6, 0xFFFFFF);
        this.font.draw(matrixStack, "\u00a7e\u8d44\u91d1: \u00a7a" + this.menu.getPlayerMoney(), 8, 138, 0xFFFFFF);
        int active = this.menu.getActiveAuctions().size();
        this.font.draw(matrixStack, "\u00a77\u5728\u62cd\u62cd\u54c1: " + active, 8, 150, 0xFFFFFF);
    }
}
