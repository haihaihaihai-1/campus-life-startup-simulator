package com.campus.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.campus.network.NetworkHandler;
import com.campus.systems.SupplyChainSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class SupplyScreen extends ContainerScreen<SupplyContainer> {
    private static final ResourceLocation TEX = new ResourceLocation("campuslife", "textures/gui/supply.png");
    public SupplyScreen(SupplyContainer c, PlayerInventory inv, ITextComponent t) {
        super(c, inv, t); this.imageWidth = 176; this.imageHeight = 200;
        this.inventoryLabelX = 10000; this.inventoryLabelY = 10000;
    }
    @Override protected void init() {
        super.init();
        SupplyChainSystem.ProductionLine[] lines = SupplyChainSystem.LINES;
        for (int i = 0; i < lines.length; i++) {
            final int idx = i;
            SupplyChainSystem.ProductionLine line = lines[i];
            this.addButton(new Button(this.leftPos + 8, this.topPos + 30 + i*30, 160, 26,
                new StringTextComponent("\u00a7e" + line.name + " \u00a76\u00a5" + line.cost),
                b -> NetworkHandler.CHANNEL.sendToServer(new NetworkHandler.GenericActionPacket("supply_start", idx))));
        }
    }
    @Override protected void renderBg(MatrixStack m, float pt, int x, int y) {
        RenderSystem.color4f(1,1,1,1);
        this.minecraft.getTextureManager().bind(TEX);
        this.blit(m, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }
    @Override protected void renderLabels(MatrixStack m, int x, int y) {
        this.font.draw(m, "\u00a76\u751f\u4ea7\u7ebf", 8, 6, 0xFFFFFF);
        this.font.draw(m, "\u00a7e\u8d44\u91d1: \u00a7a\u00a5" + this.menu.getPlayerMoney(), 8, 18, 0xFFFFFF);
    }
}
