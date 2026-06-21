package com.campus.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.campus.network.NetworkHandler;
import com.campus.systems.TaxSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class TaxScreen extends ContainerScreen<TaxContainer> {
    private static final ResourceLocation TEX = new ResourceLocation("campuslife", "textures/gui/tax.png");
    public TaxScreen(TaxContainer c, PlayerInventory inv, ITextComponent t) {
        super(c, inv, t); this.imageWidth = 176; this.imageHeight = 200;
        this.inventoryLabelX = 10000; this.inventoryLabelY = 10000;
    }
    @Override protected void init() {
        super.init();
        this.addButton(new Button(this.leftPos + 8, this.topPos + 80, 160, 22,
            new StringTextComponent("\u00a7a\u4e3b\u52a8\u62a5\u7a0e"),
            b -> NetworkHandler.CHANNEL.sendToServer(new NetworkHandler.GenericActionPacket("tax_pay", 0))));
        this.addButton(new Button(this.leftPos + 8, this.topPos + 108, 160, 22,
            new StringTextComponent("\u00a7e\u9ad8\u7ea7\u6296\u7a0e\u7b14\u8bb0 (-\u00a51000)"),
            b -> NetworkHandler.CHANNEL.sendToServer(new NetworkHandler.GenericActionPacket("tax_plan", 0))));
    }
    @Override protected void renderBg(MatrixStack m, float pt, int x, int y) {
        RenderSystem.color4f(1,1,1,1);
        this.minecraft.getTextureManager().bind(TEX);
        this.blit(m, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }
    @Override protected void renderLabels(MatrixStack m, int x, int y) {
        this.font.draw(m, "\u00a74\u7a0e\u52a1\u5c40", 8, 6, 0xFFFFFF);
        this.font.draw(m, "\u00a7e\u8d44\u91d1: \u00a7a\u00a5" + this.menu.getPlayerMoney(), 8, 22, 0xFFFFFF);
        TaxSystem.TaxRecord rec = TaxSystem.getRecord(this.menu.getPlayerUUID());
        this.font.draw(m, "\u00a77\u7d2f\u8ba1\u7eb3\u7a0e: \u00a7a\u00a5" + rec.getTotalPaid(), 8, 38, 0xFFFFFF);
        this.font.draw(m, "\u00a77\u4e0a\u6b21\u91d1\u989d: \u00a7b\u00a5" + rec.lastTaxAmount, 8, 50, 0xFFFFFF);
    }
}
