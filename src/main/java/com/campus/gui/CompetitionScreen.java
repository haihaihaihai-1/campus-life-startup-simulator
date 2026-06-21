package com.campus.gui;

import com.campus.CampusLife;
import com.campus.network.NetworkHandler;
import com.campus.systems.CompetitionSystem;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class CompetitionScreen extends ContainerScreen<CompetitionContainer> {
    private static final ResourceLocation BG = new ResourceLocation(CampusLife.MOD_ID, "textures/gui/competition.png");
    public CompetitionScreen(CompetitionContainer c, PlayerInventory inv, ITextComponent t) {
        super(c, inv, t);
        this.imageWidth = 176; this.imageHeight = 200;
    }
    @Override protected void init() {
        super.init();
        this.addButton(new Button(this.leftPos + 8, this.topPos + 30, 160, 22,
            new StringTextComponent("\u00a7a\u2705 \u62a5\u540d\u53c2\u8d5b"),
            b -> NetworkHandler.CHANNEL.sendToServer(new NetworkHandler.GenericActionPacket("competition_join", 0))));
        this.addButton(new Button(this.leftPos + 8, this.topPos + 58, 160, 22,
            new StringTextComponent("\u00a7e\u2295 \u52a0100\u5206 (\u6d4b\u8bd5)"),
            b -> NetworkHandler.CHANNEL.sendToServer(new NetworkHandler.GenericActionPacket("competition_score", 100))));
        this.addButton(new Button(this.leftPos + 8, this.topPos + 86, 160, 22,
            new StringTextComponent("\u00a7b\u2295 \u52a0500\u5206 (\u4ea4\u672c\u91d1)"),
            b -> NetworkHandler.CHANNEL.sendToServer(new NetworkHandler.GenericActionPacket("competition_score", 500))));
    }
    @Override protected void renderBg(MatrixStack m, float pt, int mx, int my) {
        RenderSystem.color4f(1,1,1,1);
        this.minecraft.getTextureManager().bind(BG);
        this.blit(m, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }
    @Override protected void renderLabels(MatrixStack m, int x, int y) {
        this.font.draw(m, "\u00a7c\u521b\u4e1a\u7ade\u8d5b", 8, 8, 0xFFFFFF);
        boolean active = CompetitionSystem.isCompetitionActive();
        this.font.draw(m, active ? "\u00a7a\u25cf \u8d5b\u4e8b\u8fdb\u884c\u4e2d" : "\u00a77\u25cb \u4e0b\u573a: " + CompetitionSystem.getNextCompetitionIn() + " ticks", 8, 18, 0xFFFFFF);
    }
}
