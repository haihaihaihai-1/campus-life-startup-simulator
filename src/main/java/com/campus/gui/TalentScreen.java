package com.campus.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.campus.network.NetworkHandler;
import com.campus.systems.TalentMarketSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class TalentScreen extends ContainerScreen<TalentContainer> {
    private static final ResourceLocation TEX = new ResourceLocation("campuslife", "textures/gui/talent.png");
    public TalentScreen(TalentContainer c, PlayerInventory inv, ITextComponent t) {
        super(c, inv, t); this.imageWidth = 176; this.imageHeight = 200;
        this.inventoryLabelX = 10000; this.inventoryLabelY = 10000;
    }
    @Override protected void init() {
        super.init();
        TalentMarketSystem.Talent[] arr = TalentMarketSystem.Talent.values();
        for (int i = 0; i < arr.length; i++) {
            final int idx = i;
            TalentMarketSystem.Talent t = arr[i];
            this.addButton(new Button(this.leftPos + 8, this.topPos + 30 + i*18, 160, 16,
                new StringTextComponent("\u00a7e" + t.name + " \u00a76\u00a5" + t.cost + " \u00a7a+" + t.income + "/d"),
                b -> NetworkHandler.CHANNEL.sendToServer(new NetworkHandler.GenericActionPacket("talent_recruit", idx))));
        }
    }
    @Override protected void renderBg(MatrixStack m, float pt, int x, int y) {
        RenderSystem.color4f(1,1,1,1);
        this.minecraft.getTextureManager().bind(TEX);
        this.blit(m, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }
    @Override protected void renderLabels(MatrixStack m, int x, int y) {
        this.font.draw(m, "\u00a76\u4eba\u624d\u5e02\u573a", 8, 6, 0xFFFFFF);
        this.font.draw(m, "\u00a7e\u8d44\u91d1: \u00a7a\u00a5" + this.menu.getPlayerMoney(), 8, 18, 0xFFFFFF);
    }
}
