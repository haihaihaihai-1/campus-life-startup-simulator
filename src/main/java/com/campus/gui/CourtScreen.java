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

public class CourtScreen extends ContainerScreen<CourtContainer> {
    private static final ResourceLocation TEX = new ResourceLocation("campuslife", "textures/gui/court.png");
    public CourtScreen(CourtContainer c, PlayerInventory inv, ITextComponent t) {
        super(c, inv, t); this.imageWidth = 176; this.imageHeight = 200;
        this.inventoryLabelX = 10000; this.inventoryLabelY = 10000;
    }
    @Override protected void init() {
        super.init();
        int[] claims = {1000, 5000, 20000, 100000};
        for (int i = 0; i < claims.length; i++) {
            final int amt = claims[i];
            this.addButton(new Button(this.leftPos + 8, this.topPos + 40 + i*24, 160, 20,
                new StringTextComponent("\u00a7c\u63d0\u8d77\u8bc9\u8bbc \u00a76\u00a5" + amt),
                b -> NetworkHandler.CHANNEL.sendToServer(new NetworkHandler.GenericActionPacket("court_file", amt))));
        }
    }
    @Override protected void renderBg(MatrixStack m, float pt, int x, int y) {
        RenderSystem.color4f(1,1,1,1);
        this.minecraft.getTextureManager().bind(TEX);
        this.blit(m, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }
    @Override protected void renderLabels(MatrixStack m, int x, int y) {
        this.font.draw(m, "\u00a74\u6cd5\u9662\u5ba1\u5224\u53f0", 8, 6, 0xFFFFFF);
        this.font.draw(m, "\u00a7e\u8d44\u91d1: \u00a7a\u00a5" + this.menu.getPlayerMoney(), 8, 22, 0xFFFFFF);
        this.font.draw(m, "\u00a77\u8bf7\u9009\u62e9\u7d22\u8d54\u91d1\u989d", 8, 34, 0xFFFFFF);
    }
}
