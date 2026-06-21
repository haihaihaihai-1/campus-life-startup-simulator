package com.campus.gui;

import com.campus.CampusLife;
import com.campus.network.NetworkHandler;
import com.campus.systems.InvestorSystem;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class InvestorScreen extends ContainerScreen<InvestorContainer> {
    private static final ResourceLocation BG = new ResourceLocation(CampusLife.MOD_ID, "textures/gui/investor.png");
    public InvestorScreen(InvestorContainer c, PlayerInventory inv, ITextComponent t) {
        super(c, inv, t);
        this.imageWidth = 176; this.imageHeight = 200;
    }
    @Override protected void init() {
        super.init();
        InvestorSystem.Investor[] all = InvestorSystem.INVESTORS;
        for (int i = 0; i < all.length; i++) {
            final int idx = i; InvestorSystem.Investor inv = all[i];
            this.addButton(new Button(this.leftPos + 8, this.topPos + 22 + i*18, 160, 16,
                new StringTextComponent("\u00a7e" + inv.name + " \u00a76\u00a5" + inv.maxInvestment + " \u00a7c" + (int)(inv.equityRate*100) + "%"),
                b -> NetworkHandler.CHANNEL.sendToServer(new NetworkHandler.GenericActionPacket("investor_request", idx))));
        }
    }
    @Override protected void renderBg(MatrixStack m, float pt, int mx, int my) {
        RenderSystem.color4f(1,1,1,1);
        this.minecraft.getTextureManager().bind(BG);
        this.blit(m, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }
    @Override protected void renderLabels(MatrixStack m, int x, int y) {
        this.font.draw(m, "\u00a76\u6295\u8d44\u4eba\u540d\u5355 (\u00a5/\u80a1\u6743)", 8, 8, 0xFFFFFF);
    }
}
