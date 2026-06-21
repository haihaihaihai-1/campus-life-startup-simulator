package com.campus.gui;

import com.campus.CampusLife;
import com.campus.network.NetworkHandler;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class CRMScreen extends ContainerScreen<CRMContainer> {
    private static final ResourceLocation BG = new ResourceLocation(CampusLife.MOD_ID, "textures/gui/crm.png");
    private static final String[] TIERS = {"\u00a77\u4e00\u822c\u5ba2\u6237 \u00a76\u00a550", "\u00a7a\u4f18\u8d28\u5ba2\u6237 \u00a76\u00a5200", "\u00a7bVIP\u5ba2\u6237 \u00a76\u00a5800", "\u00a7d\u4f01\u4e1a\u5927\u5ba2\u6237 \u00a76\u00a53000"};
    public CRMScreen(CRMContainer c, PlayerInventory inv, ITextComponent t) {
        super(c, inv, t);
        this.imageWidth = 176; this.imageHeight = 200;
    }
    @Override protected void init() {
        super.init();
        for (int i = 0; i < TIERS.length; i++) {
            final int tier = i + 1;
            this.addButton(new Button(this.leftPos + 8, this.topPos + 30 + i*24, 160, 22,
                new StringTextComponent(TIERS[i]),
                b -> NetworkHandler.CHANNEL.sendToServer(new NetworkHandler.GenericActionPacket("crm_acquire", tier))));
        }
    }
    @Override protected void renderBg(MatrixStack m, float pt, int mx, int my) {
        RenderSystem.color4f(1,1,1,1);
        this.minecraft.getTextureManager().bind(BG);
        this.blit(m, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }
    @Override protected void renderLabels(MatrixStack m, int x, int y) {
        this.font.draw(m, "\u00a7b\u5ba2\u6237\u83b7\u5ba2 (\u6bcf5\u5206\u949f\u6536\u6536\u5165)", 8, 8, 0xFFFFFF);
    }
}
