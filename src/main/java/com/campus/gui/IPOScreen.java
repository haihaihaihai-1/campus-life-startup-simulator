package com.campus.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.campus.network.NetworkHandler;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class IPOScreen extends ContainerScreen<IPOContainer> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("campuslife", "textures/gui/ipo.png");

    public IPOScreen(IPOContainer container, PlayerInventory inv, ITextComponent title) {
        super(container, inv, title);
        this.imageWidth = 176;
        this.imageHeight = 200;
        this.inventoryLabelX = 10000;
        this.inventoryLabelY = 10000;
    }

    @Override
    protected void init() {
        super.init();
        IPOContainer menu = this.menu;
        if (!menu.isListed()) {
            this.addButton(new Button(this.leftPos + 23, this.topPos + 80, 130, 30,
                new StringTextComponent("\u00a76\u667a\u80fd\u9707\u94c3 IPO \u4e0a\u5e02"),
                b -> NetworkHandler.CHANNEL.sendToServer(new NetworkHandler.GenericActionPacket("ipo", 0))));
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
        IPOContainer menu = this.menu;
        this.font.draw(m, "\u00a76IPO \u4e0a\u5e02\u4e2d\u5fc3", 8, 6, 0xFFFFFF);
        this.font.draw(m, "\u00a7e\u8d44\u91d1: \u00a7a" + menu.getPlayerMoney(), 8, 20, 0xFFFFFF);
        if (menu.isListed()) {
            this.font.draw(m, "\u00a7a\u2714 \u5df2\u4e0a\u5e02!", 8, 36, 0xFFFFFF);
            this.font.draw(m, "\u00a7e\u5e02\u503c: \u00a76" + menu.getMarketCap(), 8, 50, 0xFFFFFF);
            this.font.draw(m, "\u00a77\u6bcf2\u5206\u949f\u80a1\u4ef7\u6ce2\u52a8+\u80a1\u606f\u6536\u5165", 8, 64, 0xFFFFFF);
        } else {
            this.font.draw(m, "\u00a7c\u672a\u4e0a\u5e02", 8, 36, 0xFFFFFF);
            this.font.draw(m, "\u00a77\u70b9\u51fb\u9707\u94c3\u542f\u52a8 IPO", 8, 50, 0xFFFFFF);
            this.font.draw(m, "\u00a77\u9700\u8981 Lv.20+\u8d44\u91d1>=10\u4e07", 8, 64, 0xFFFFFF);
        }
    }
}
