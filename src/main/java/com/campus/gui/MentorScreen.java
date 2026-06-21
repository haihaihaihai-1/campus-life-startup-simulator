package com.campus.gui;

import com.campus.CampusLife;
import com.campus.network.NetworkHandler;
import com.campus.systems.MentorSystem;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class MentorScreen extends ContainerScreen<MentorContainer> {
    private static final ResourceLocation BG = new ResourceLocation(CampusLife.MOD_ID, "textures/gui/mentor.png");
    public MentorScreen(MentorContainer c, PlayerInventory inv, ITextComponent t) {
        super(c, inv, t);
        this.imageWidth = 176; this.imageHeight = 200;
    }
    @Override protected void init() {
        super.init();
        MentorSystem.Mentor[] all = MentorSystem.MENTORS;
        for (int i = 0; i < all.length; i++) {
            final int idx = i; MentorSystem.Mentor m = all[i];
            this.addButton(new Button(this.leftPos + 8, this.topPos + 22 + i*22, 160, 20,
                new StringTextComponent("\u00a7e" + m.name + " \u00a76\u00a5" + m.consultationFee + " \u00a7a" + m.effectDesc),
                b -> NetworkHandler.CHANNEL.sendToServer(new NetworkHandler.GenericActionPacket("mentor_consult", idx))));
        }
    }
    @Override protected void renderBg(MatrixStack m, float pt, int mx, int my) {
        RenderSystem.color4f(1,1,1,1);
        this.minecraft.getTextureManager().bind(BG);
        this.blit(m, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }
    @Override protected void renderLabels(MatrixStack m, int x, int y) {
        this.font.draw(m, "\u00a7e\u521b\u4e1a\u5bfc\u5e08\u4f1a\u8c08", 8, 8, 0xFFFFFF);
    }
}
