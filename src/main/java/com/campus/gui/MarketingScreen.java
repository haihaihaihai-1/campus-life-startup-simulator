package com.campus.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.campus.network.NetworkHandler;
import com.campus.systems.MarketingSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class MarketingScreen extends ContainerScreen<MarketingContainer> {
    private static final ResourceLocation TEX = new ResourceLocation("campuslife", "textures/gui/marketing.png");
    public MarketingScreen(MarketingContainer c, PlayerInventory inv, ITextComponent t) {
        super(c, inv, t);
        this.imageWidth = 176; this.imageHeight = 200;
        this.inventoryLabelX = 10000; this.inventoryLabelY = 10000;
    }
    @Override protected void init() {
        super.init();
        MarketingSystem.AdType[] types = MarketingSystem.AdType.values();
        // 每类型2档预算
        int rows = Math.min(types.length, 5);
        for (int i = 0; i < rows; i++) {
            final int idx = i;
            MarketingSystem.AdType t = types[i];
            final int budget1 = 100, budget2 = 500;
            this.addButton(new Button(this.leftPos + 6, this.topPos + 32 + i*26, 100, 20,
                new StringTextComponent("\u00a7f" + t.name),
                b -> {}));
            this.addButton(new Button(this.leftPos + 108, this.topPos + 32 + i*26, 30, 20,
                new StringTextComponent("\u00a76\u00a5" + budget1),
                b -> NetworkHandler.CHANNEL.sendToServer(new NetworkHandler.GenericActionPacket("marketing_" + idx, budget1))));
            this.addButton(new Button(this.leftPos + 140, this.topPos + 32 + i*26, 30, 20,
                new StringTextComponent("\u00a76\u00a5" + budget2),
                b -> NetworkHandler.CHANNEL.sendToServer(new NetworkHandler.GenericActionPacket("marketing_" + idx, budget2))));
        }
    }
    @Override protected void renderBg(MatrixStack m, float pt, int x, int y) {
        RenderSystem.color4f(1f, 1f, 1f, 1f);
        this.minecraft.getTextureManager().bind(TEX);
        this.blit(m, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }
    @Override protected void renderLabels(MatrixStack m, int x, int y) {
        this.font.draw(m, "\u00a7f\u8425\u9500\u5e7f\u544a\u724c", 8, 6, 0xFFFFFF);
        this.font.draw(m, "\u00a7e\u00a5" + this.menu.getPlayerMoney() + " | +" + this.menu.getBoostPercent() + "%", 8, 20, 0xFFFFFF);
    }
}
