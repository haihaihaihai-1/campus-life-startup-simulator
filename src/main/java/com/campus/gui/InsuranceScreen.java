package com.campus.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.campus.network.NetworkHandler;
import com.campus.systems.InsuranceSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class InsuranceScreen extends ContainerScreen<InsuranceContainer> {
    private static final ResourceLocation TEX = new ResourceLocation("campuslife", "textures/gui/insurance.png");
    public InsuranceScreen(InsuranceContainer c, PlayerInventory inv, ITextComponent t) {
        super(c, inv, t);
        this.imageWidth = 176; this.imageHeight = 200;
        this.inventoryLabelX = 10000; this.inventoryLabelY = 10000;
    }
    @Override protected void init() {
        super.init();
        InsuranceSystem.InsuranceType[] types = InsuranceSystem.InsuranceType.values();
        for (int i = 0; i < types.length; i++) {
            final int idx = i + 1;
            InsuranceSystem.InsuranceType t = types[i];
            this.addButton(new Button(this.leftPos + 6, this.topPos + 30 + i*26, 162, 22,
                new StringTextComponent("\u00a7b" + t.name + " \u00a76\u00a5" + t.premium + " \u00a7e\u8d54" + (int)(t.coverage*100) + "%"),
                b -> NetworkHandler.CHANNEL.sendToServer(new NetworkHandler.GenericActionPacket("insurance_buy", idx))));
        }
    }
    @Override protected void renderBg(MatrixStack m, float pt, int x, int y) {
        RenderSystem.color4f(1f, 1f, 1f, 1f);
        this.minecraft.getTextureManager().bind(TEX);
        this.blit(m, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }
    @Override protected void renderLabels(MatrixStack m, int x, int y) {
        this.font.draw(m, "\u00a7f\u4fdd\u9669\u67dc\u53f0", 8, 6, 0x202020);
        this.font.draw(m, "\u00a7e\u00a5" + this.menu.getPlayerMoney(), 110, 6, 0x202020);
    }
}
