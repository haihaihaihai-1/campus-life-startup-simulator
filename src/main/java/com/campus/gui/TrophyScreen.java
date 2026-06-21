package com.campus.gui;

import com.campus.CampusLife;
import com.campus.network.NetworkHandler;
import com.campus.systems.AchievementSystem;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class TrophyScreen extends ContainerScreen<TrophyContainer> {
    private static final ResourceLocation BG = new ResourceLocation(CampusLife.MOD_ID, "textures/gui/trophy.png");
    public TrophyScreen(TrophyContainer c, PlayerInventory inv, ITextComponent t) {
        super(c, inv, t);
        this.imageWidth = 176; this.imageHeight = 200;
    }
    @Override protected void init() {
        super.init();
        // 显示前 8 个成就
        AchievementSystem.Achievement[] all = AchievementSystem.ACHIEVEMENTS;
        int limit = Math.min(8, all.length);
        for (int i = 0; i < limit; i++) {
            AchievementSystem.Achievement a = all[i];
            this.addButton(new Button(this.leftPos + 8, this.topPos + 22 + i*20, 160, 18,
                new StringTextComponent("\u00a76\u2605 " + a.name + " \u00a7a+" + a.reward + " \u00a77" + a.category),
                b -> NetworkHandler.CHANNEL.sendToServer(new NetworkHandler.GenericActionPacket("trophy_show", 0))));
        }
    }
    @Override protected void renderBg(MatrixStack m, float pt, int mx, int my) {
        RenderSystem.color4f(1,1,1,1);
        this.minecraft.getTextureManager().bind(BG);
        this.blit(m, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }
    @Override protected void renderLabels(MatrixStack m, int x, int y) {
        this.font.draw(m, "\u00a76\u8363\u8a89\u6210\u5c31", 8, 8, 0xFFFFFF);
    }
}
