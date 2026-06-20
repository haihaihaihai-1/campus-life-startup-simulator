package com.campus.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.campus.network.NetworkHandler;
import com.campus.systems.VCSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class VCScreen extends ContainerScreen<VCContainer> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("campuslife", "textures/gui/vc.png");

    public VCScreen(VCContainer container, PlayerInventory inv, ITextComponent title) {
        super(container, inv, title);
        this.imageWidth = 176;
        this.imageHeight = 200;
        this.inventoryLabelX = 10000;
        this.inventoryLabelY = 10000;
    }

    @Override
    protected void init() {
        super.init();
        VCContainer menu = this.menu;
        VCSystem.Round[] rounds = VCSystem.Round.values();
        for (int i = 0; i < rounds.length; i++) {
            final int idx = i;
            VCSystem.Round r = rounds[i];
            String label = r.name + " | " + r.amount + "\u5e01 | -" + r.equity + "%\u80a1\u6743";
            boolean done = idx < menu.getCurrentRound();
            boolean canRaise = !done && idx == menu.getCurrentRound() && menu.getPlayerLevel() >= r.reqLevel;
            String prefix = done ? "\u00a78\u2714 " : (canRaise ? "\u00a7a" : "\u00a7c");
            this.addButton(new Button(this.leftPos + 6, this.topPos + 22 + i * 18, 164, 16,
                new StringTextComponent(prefix + label),
                b -> NetworkHandler.CHANNEL.sendToServer(new NetworkHandler.GenericActionPacket("vc", idx))));
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
        VCContainer menu = this.menu;
        this.font.draw(m, "\u00a76\u98ce\u9669\u6295\u8d44\u4e2d\u5fc3", 8, 6, 0xFFFFFF);
        this.font.draw(m, "\u00a7e\u4f30\u503c: \u00a7a" + menu.getValuation() + "\u91d1\u5e01", 8, 138, 0xFFFFFF);
        this.font.draw(m, "\u00a7e\u5df2\u5b8c\u6210: \u00a7a" + menu.getCurrentRound() + "\u8f6e", 8, 150, 0xFFFFFF);
    }
}
