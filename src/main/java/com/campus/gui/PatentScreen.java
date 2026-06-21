package com.campus.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.campus.network.NetworkHandler;
import com.campus.systems.PatentSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class PatentScreen extends ContainerScreen<PatentContainer> {
    private static final ResourceLocation TEX = new ResourceLocation("campuslife", "textures/gui/patent.png");
    public PatentScreen(PatentContainer c, PlayerInventory inv, ITextComponent t) {
        super(c, inv, t);
        this.imageWidth = 176; this.imageHeight = 200;
        this.inventoryLabelX = 10000; this.inventoryLabelY = 10000;
    }
    @Override protected void init() {
        super.init();
        this.addButton(new Button(this.leftPos + 23, this.topPos + 90, 130, 30,
            new StringTextComponent("\u00a76\u7533\u8bf7\u4e13\u5229 (-\u00a51000)"),
            b -> NetworkHandler.CHANNEL.sendToServer(new NetworkHandler.GenericActionPacket("patent_file", 0))));
    }
    @Override protected void renderBg(MatrixStack m, float pt, int x, int y) {
        RenderSystem.color4f(1f, 1f, 1f, 1f);
        this.minecraft.getTextureManager().bind(TEX);
        this.blit(m, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }
    @Override protected void renderLabels(MatrixStack m, int x, int y) {
        this.font.draw(m, "\u00a76\u4e13\u5229\u5c55\u793a\u67dc", 8, 6, 0xFFFFFF);
        this.font.draw(m, "\u00a7e\u8d44\u91d1: \u00a7a\u00a5" + this.menu.getPlayerMoney(), 8, 22, 0xFFFFFF);
        this.font.draw(m, "\u00a7e\u5df2\u62e5\u6709\u4e13\u5229: \u00a7b" + this.menu.getPatentCount(), 8, 36, 0xFFFFFF);
        this.font.draw(m, "\u00a77\u9886\u57df:", 8, 56, 0xFFFFFF);
        for (int i = 0; i < Math.min(PatentSystem.PATENT_FIELDS.length, 4); i++) {
            this.font.draw(m, "\u00a7f \u00b7 " + PatentSystem.PATENT_FIELDS[i], 8, 68 + i*10, 0xFFFFFF);
        }
    }
}
