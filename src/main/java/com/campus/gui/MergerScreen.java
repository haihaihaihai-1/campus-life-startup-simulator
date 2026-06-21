package com.campus.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.campus.network.NetworkHandler;
import com.campus.systems.MergerSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class MergerScreen extends ContainerScreen<MergerContainer> {
    private static final ResourceLocation TEX = new ResourceLocation("campuslife", "textures/gui/merger.png");
    public MergerScreen(MergerContainer c, PlayerInventory inv, ITextComponent t) {
        super(c, inv, t);
        this.imageWidth = 176; this.imageHeight = 200;
        this.inventoryLabelX = 10000; this.inventoryLabelY = 10000;
    }
    @Override protected void init() {
        super.init();
        for (int i = 0; i < MergerSystem.TARGETS.length && i < 5; i++) {
            final int idx = i + 1;
            MergerSystem.Target t = MergerSystem.TARGETS[i];
            this.addButton(new Button(this.leftPos + 8, this.topPos + 40 + i*22, 160, 20,
                new StringTextComponent("\u00a7e" + t.name + " \u00a76\u00a5" + t.value + " +" + t.revenueBoost + "%"),
                b -> NetworkHandler.CHANNEL.sendToServer(new NetworkHandler.GenericActionPacket("merger_acquire", idx))));
        }
    }
    @Override protected void renderBg(MatrixStack m, float pt, int x, int y) {
        RenderSystem.color4f(1f, 1f, 1f, 1f);
        this.minecraft.getTextureManager().bind(TEX);
        this.blit(m, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }
    @Override protected void renderLabels(MatrixStack m, int x, int y) {
        this.font.draw(m, "\u00a7f\u5e76\u8d2d\u8c08\u5224\u684c", 8, 6, 0x202020);
        this.font.draw(m, "\u00a7e\u8d44\u91d1: \u00a7a\u00a5" + this.menu.getPlayerMoney(), 8, 22, 0x202020);
        this.font.draw(m, "\u00a7e\u603b\u52a0\u6210: \u00a7b+" + this.menu.getBoost() + "%", 90, 22, 0x202020);
    }
}
