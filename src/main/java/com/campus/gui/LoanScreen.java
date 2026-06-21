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

public class LoanScreen extends ContainerScreen<LoanContainer> {
    private static final ResourceLocation TEX = new ResourceLocation("campuslife", "textures/gui/loan.png");
    public LoanScreen(LoanContainer c, PlayerInventory inv, ITextComponent t) {
        super(c, inv, t);
        this.imageWidth = 176; this.imageHeight = 200;
        this.inventoryLabelX = 10000; this.inventoryLabelY = 10000;
    }
    @Override protected void init() {
        super.init();
        int[] amounts = {500, 1000, 2000, 5000};
        for (int i = 0; i < amounts.length; i++) {
            final int amt = amounts[i];
            this.addButton(new Button(this.leftPos + 8, this.topPos + 40 + i * 22, 78, 20,
                new StringTextComponent("\u00a7a\u501f\u00a5" + amt),
                b -> NetworkHandler.CHANNEL.sendToServer(new NetworkHandler.GenericActionPacket("loan_take", amt))));
            this.addButton(new Button(this.leftPos + 90, this.topPos + 40 + i * 22, 78, 20,
                new StringTextComponent("\u00a7c\u8fd8\u00a5" + amt),
                b -> NetworkHandler.CHANNEL.sendToServer(new NetworkHandler.GenericActionPacket("loan_repay", amt))));
        }
    }
    @Override protected void renderBg(MatrixStack m, float pt, int x, int y) {
        RenderSystem.color4f(1f, 1f, 1f, 1f);
        this.minecraft.getTextureManager().bind(TEX);
        this.blit(m, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }
    @Override protected void renderLabels(MatrixStack m, int x, int y) {
        this.font.draw(m, "\u00a76\u94f6\u884c\u8d37\u6b3e ATM", 8, 6, 0xFFFFFF);
        this.font.draw(m, "\u00a7e\u8d44\u91d1: \u00a7a\u00a5" + this.menu.getPlayerMoney(), 8, 22, 0xFFFFFF);
        this.font.draw(m, "\u00a77\u5229\u606f 5%/tick \u6700\u9ad8\u00a55000", 8, 174, 0xFFFFFF);
    }
}
