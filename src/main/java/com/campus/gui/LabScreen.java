package com.campus.gui;

import com.campus.CampusLife;
import com.campus.network.NetworkHandler;
import com.campus.systems.LabSystem;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class LabScreen extends ContainerScreen<LabContainer> {
    private static final ResourceLocation BG = new ResourceLocation(CampusLife.MOD_ID, "textures/gui/lab.png");
    public LabScreen(LabContainer c, PlayerInventory inv, ITextComponent t) {
        super(c, inv, t);
        this.imageWidth = 176; this.imageHeight = 200;
    }
    @Override protected void init() {
        super.init();
        LabSystem.Experiment[] all = LabSystem.EXPERIMENTS;
        for (int i = 0; i < all.length; i++) {
            final int idx = i; LabSystem.Experiment e = all[i];
            this.addButton(new Button(this.leftPos + 8, this.topPos + 22 + i*20, 160, 18,
                new StringTextComponent("\u00a7b" + e.name + " \u00a76\u00a5" + e.cost + " \u00a7a+" + e.incomeBoost + "%"),
                b -> NetworkHandler.CHANNEL.sendToServer(new NetworkHandler.GenericActionPacket("lab_run", idx))));
        }
    }
    @Override protected void renderBg(MatrixStack m, float pt, int mx, int my) {
        RenderSystem.color4f(1,1,1,1);
        this.minecraft.getTextureManager().bind(BG);
        this.blit(m, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }
    @Override protected void renderLabels(MatrixStack m, int x, int y) {
        this.font.draw(m, "\u00a7b\u524d\u6cbf\u5b9e\u9a8c\u5ba4", 8, 8, 0xFFFFFF);
    }
}
