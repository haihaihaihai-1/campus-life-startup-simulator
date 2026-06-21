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

public class CarbonScreen extends ContainerScreen<CarbonContainer> {
    private static final ResourceLocation TEX = new ResourceLocation("campuslife", "textures/gui/carbon.png");
    public CarbonScreen(CarbonContainer c, PlayerInventory inv, ITextComponent t) {
        super(c, inv, t); this.imageWidth = 176; this.imageHeight = 200;
        this.inventoryLabelX = 10000; this.inventoryLabelY = 10000;
    }
    @Override protected void init() {
        super.init();
        int[] amts = {10, 50, 100, 500};
        for (int i = 0; i < amts.length; i++) {
            final int amt = amts[i];
            this.addButton(new Button(this.leftPos + 8, this.topPos + 40 + i*22, 78, 20,
                new StringTextComponent("\u00a7a\u4e70 " + amt),
                b -> NetworkHandler.CHANNEL.sendToServer(new NetworkHandler.GenericActionPacket("carbon_buy", amt))));
            this.addButton(new Button(this.leftPos + 90, this.topPos + 40 + i*22, 78, 20,
                new StringTextComponent("\u00a7c\u5356 " + amt),
                b -> NetworkHandler.CHANNEL.sendToServer(new NetworkHandler.GenericActionPacket("carbon_sell", amt))));
        }
    }
    @Override protected void renderBg(MatrixStack m, float pt, int x, int y) {
        RenderSystem.color4f(1,1,1,1);
        this.minecraft.getTextureManager().bind(TEX);
        this.blit(m, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }
    @Override protected void renderLabels(MatrixStack m, int x, int y) {
        this.font.draw(m, "\u00a7a\u00a7l\ud83c\udf31 \u78b3\u4ea4\u6613", 8, 6, 0xFFFFFF);
        this.font.draw(m, "\u00a7e\u8d44\u91d1: \u00a7a\u00a5" + this.menu.getPlayerMoney(), 8, 22, 0xFFFFFF);
    }
}
