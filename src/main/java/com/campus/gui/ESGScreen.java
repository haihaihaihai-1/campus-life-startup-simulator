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

public class ESGScreen extends ContainerScreen<ESGContainer> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("campuslife", "textures/gui/esg.png");
    private static final String[] DIMS = {"\u73af\u5883(E)", "\u793e\u4f1a(S)", "\u6cbb\u7406(G)"};
    private static final int[] AMOUNTS = {10, 50, 100};

    public ESGScreen(ESGContainer container, PlayerInventory inv, ITextComponent title) {
        super(container, inv, title);
        this.imageWidth = 176;
        this.imageHeight = 200;
        this.inventoryLabelX = 10000;
        this.inventoryLabelY = 10000;
    }

    @Override
    protected void init() {
        super.init();
        for (int d = 0; d < 3; d++) {
            for (int a = 0; a < 3; a++) {
                final int dim = d, amt = AMOUNTS[a];
                String label = DIMS[d] + " +" + amt;
                this.addButton(new Button(this.leftPos + 8 + a * 54, this.topPos + 30 + d * 26, 50, 22,
                    new StringTextComponent("\u00a7a" + label),
                    b -> NetworkHandler.CHANNEL.sendToServer(new NetworkHandler.GenericActionPacket("esg", dim * 1000 + amt))));
            }
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
        ESGContainer menu = this.menu;
        this.font.draw(m, "\u00a76ESG \u8bc4\u7ea7\u4e2d\u5fc3", 8, 6, 0xFFFFFF);
        this.font.draw(m, "\u00a7e\u5f53\u524d\u8bc4\u7ea7: \u00a7a" + menu.getRating() + "/100", 8, 18, 0xFFFFFF);
        this.font.draw(m, "\u00a7e\u8d44\u91d1: \u00a7a" + menu.getPlayerMoney() + "\u5e01 \u00a77(\u6210\u672c=\u91d1\u989d\u00d710)", 8, 138, 0xFFFFFF);
        this.font.draw(m, "\u00a77\u70b9\u51fb\u6309\u94ae\u6295\u8d44\u63d0\u5347\u8bc4\u7ea7", 8, 150, 0xFFFFFF);
    }
}
