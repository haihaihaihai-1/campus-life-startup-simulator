package com.campus.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.campus.network.NetworkHandler;
import com.campus.systems.RealEstateSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class RealtyScreen extends ContainerScreen<RealtyContainer> {
    private static final ResourceLocation TEX = new ResourceLocation("campuslife", "textures/gui/realty.png");
    public RealtyScreen(RealtyContainer c, PlayerInventory inv, ITextComponent t) {
        super(c, inv, t);
        this.imageWidth = 176; this.imageHeight = 200;
        this.inventoryLabelX = 10000; this.inventoryLabelY = 10000;
    }
    @Override protected void init() {
        super.init();
        for (int i = 0; i < RealEstateSystem.PROPERTIES.length; i++) {
            final int idx = i + 1;
            RealEstateSystem.Property p = RealEstateSystem.PROPERTIES[i];
            this.addButton(new Button(this.leftPos + 6, this.topPos + 22 + i*22, 100, 20,
                new StringTextComponent("\u00a7e" + p.name + " \u00a76\u00a5" + p.purchasePrice),
                b -> {}));
            this.addButton(new Button(this.leftPos + 108, this.topPos + 22 + i*22, 30, 20,
                new StringTextComponent("\u00a7a\u4e70"),
                b -> NetworkHandler.CHANNEL.sendToServer(new NetworkHandler.GenericActionPacket("realty_buy", idx))));
            this.addButton(new Button(this.leftPos + 140, this.topPos + 22 + i*22, 30, 20,
                new StringTextComponent("\u00a7c\u5356"),
                b -> NetworkHandler.CHANNEL.sendToServer(new NetworkHandler.GenericActionPacket("realty_sell", idx))));
        }
    }
    @Override protected void renderBg(MatrixStack m, float pt, int x, int y) {
        RenderSystem.color4f(1f, 1f, 1f, 1f);
        this.minecraft.getTextureManager().bind(TEX);
        this.blit(m, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }
    @Override protected void renderLabels(MatrixStack m, int x, int y) {
        this.font.draw(m, "\u00a7f\u623f\u4ea7\u4e2d\u4ecb", 8, 6, 0x202020);
        this.font.draw(m, "\u00a7e\u00a5" + this.menu.getPlayerMoney(), 110, 6, 0x202020);
    }
}
