package com.campus.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.campus.network.NetworkHandler;
import com.campus.systems.BlockchainNotarySystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class NotaryScreen extends ContainerScreen<NotaryContainer> {
    private static final ResourceLocation TEX = new ResourceLocation("campuslife", "textures/gui/notary.png");
    public NotaryScreen(NotaryContainer c, PlayerInventory inv, ITextComponent t) {
        super(c, inv, t); this.imageWidth = 176; this.imageHeight = 200;
        this.inventoryLabelX = 10000; this.inventoryLabelY = 10000;
    }
    @Override protected void init() {
        super.init();
        String[] docs = {"\u4e13\u5229", "\u5408\u540c", "\u80a1\u4efd", "\u552e\u540e\u8bc1"};
        for (int i = 0; i < docs.length; i++) {
            final int idx = i;
            this.addButton(new Button(this.leftPos + 8, this.topPos + 50 + i*22, 160, 20,
                new StringTextComponent("\u00a7a\u533a\u5757\u94fe\u5b58\u8bc1: " + docs[i]),
                b -> NetworkHandler.CHANNEL.sendToServer(new NetworkHandler.GenericActionPacket("notary_save", idx))));
        }
    }
    @Override protected void renderBg(MatrixStack m, float pt, int x, int y) {
        RenderSystem.color4f(1,1,1,1);
        this.minecraft.getTextureManager().bind(TEX);
        this.blit(m, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }
    @Override protected void renderLabels(MatrixStack m, int x, int y) {
        this.font.draw(m, "\u00a72\u533a\u5757\u94fe\u516c\u8bc1", 8, 6, 0xFFFFFF);
        int trust = BlockchainNotarySystem.getTrust(this.menu.getPlayerUUID());
        this.font.draw(m, "\u00a7e\u4fe1\u7528\u5206: \u00a7a" + trust, 8, 22, 0xFFFFFF);
        this.font.draw(m, "\u00a77\u6bcf\u6b21\u516c\u8bc1+1\u4fe1\u7528", 8, 34, 0xFFFFFF);
    }
}
