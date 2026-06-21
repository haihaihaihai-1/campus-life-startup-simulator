package com.campus.gui;

import com.campus.CampusLife;
import com.campus.network.NetworkHandler;
import com.campus.systems.GrantSystem;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class GrantScreen extends ContainerScreen<GrantContainer> {
    private static final ResourceLocation BG = new ResourceLocation(CampusLife.MOD_ID, "textures/gui/grant.png");
    public GrantScreen(GrantContainer c, PlayerInventory inv, ITextComponent t) {
        super(c, inv, t);
        this.imageWidth = 176; this.imageHeight = 200;
    }
    @Override protected void init() {
        super.init();
        GrantSystem.Grant[] all = GrantSystem.GRANTS;
        for (int i = 0; i < all.length; i++) {
            final int idx = i; GrantSystem.Grant g = all[i];
            this.addButton(new Button(this.leftPos + 8, this.topPos + 22 + i*16, 160, 14,
                new StringTextComponent("\u00a7a" + g.name + " \u00a76\u00a5" + g.amount + " \u00a77Lv." + g.requiredLevel),
                b -> NetworkHandler.CHANNEL.sendToServer(new NetworkHandler.GenericActionPacket("grant_apply", idx))));
        }
    }
    @Override protected void renderBg(MatrixStack m, float pt, int mx, int my) {
        RenderSystem.color4f(1,1,1,1);
        this.minecraft.getTextureManager().bind(BG);
        this.blit(m, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }
    @Override protected void renderLabels(MatrixStack m, int x, int y) {
        this.font.draw(m, "\u00a76\u653f\u5e9c\u8865\u8d34\u9879\u76ee", 8, 8, 0xFFFFFF);
    }
}
