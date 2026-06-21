package com.campus.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.campus.network.NetworkHandler;
import com.campus.systems.MetaverseSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class MetaverseScreen extends ContainerScreen<MetaverseContainer> {
    private static final ResourceLocation TEX = new ResourceLocation("campuslife", "textures/gui/metaverse.png");
    public MetaverseScreen(MetaverseContainer c, PlayerInventory inv, ITextComponent t) {
        super(c, inv, t);
        this.imageWidth = 176; this.imageHeight = 200;
        this.inventoryLabelX = 10000; this.inventoryLabelY = 10000;
    }
    @Override protected void init() {
        super.init();
        for (int i = 0; i < MetaverseSystem.LANDS.length; i++) {
            final int idx = i + 1;
            MetaverseSystem.VirtualLand l = MetaverseSystem.LANDS[i];
            this.addButton(new Button(this.leftPos + 6, this.topPos + 22 + i*20, 162, 18,
                new StringTextComponent("\u00a7d" + l.name + " \u00a76\u00a5" + l.purchasePrice),
                b -> NetworkHandler.CHANNEL.sendToServer(new NetworkHandler.GenericActionPacket("metaverse_buy", idx))));
        }
    }
    @Override protected void renderBg(MatrixStack m, float pt, int x, int y) {
        RenderSystem.color4f(1f, 1f, 1f, 1f);
        this.minecraft.getTextureManager().bind(TEX);
        this.blit(m, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }
    @Override protected void renderLabels(MatrixStack m, int x, int y) {
        this.font.draw(m, "\u00a7d\u5143\u5b87\u5b99\u5730\u4ea7", 8, 6, 0xFFFFFF);
        this.font.draw(m, "\u00a7e\u00a5" + this.menu.getPlayerMoney(), 120, 6, 0xFFFFFF);
    }
}
