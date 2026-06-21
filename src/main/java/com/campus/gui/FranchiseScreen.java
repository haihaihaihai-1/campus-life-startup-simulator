package com.campus.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.campus.network.NetworkHandler;
import com.campus.systems.FranchiseSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class FranchiseScreen extends ContainerScreen<FranchiseContainer> {
    private static final ResourceLocation TEX = new ResourceLocation("campuslife", "textures/gui/franchise.png");
    public FranchiseScreen(FranchiseContainer c, PlayerInventory inv, ITextComponent t) {
        super(c, inv, t); this.imageWidth = 176; this.imageHeight = 200;
        this.inventoryLabelX = 10000; this.inventoryLabelY = 10000;
    }
    @Override protected void init() {
        super.init();
        FranchiseSystem.FranchiseType[] types = FranchiseSystem.TYPES;
        for (int i = 0; i < types.length; i++) {
            final int idx = i;
            FranchiseSystem.FranchiseType t = types[i];
            this.addButton(new Button(this.leftPos + 8, this.topPos + 30 + i*22, 160, 20,
                new StringTextComponent("\u00a7e" + t.name + " \u00a76\u00a5" + t.openCost + " \u00a7a+" + t.dailyRevenue + "/d"),
                b -> NetworkHandler.CHANNEL.sendToServer(new NetworkHandler.GenericActionPacket("franchise_open", idx))));
        }
    }
    @Override protected void renderBg(MatrixStack m, float pt, int x, int y) {
        RenderSystem.color4f(1,1,1,1);
        this.minecraft.getTextureManager().bind(TEX);
        this.blit(m, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }
    @Override protected void renderLabels(MatrixStack m, int x, int y) {
        this.font.draw(m, "\u00a7e\u52a0\u76df\u8fde\u9501", 8, 6, 0xFFFFFF);
        int count = FranchiseSystem.getShopCount(this.menu.getPlayerUUID());
        this.font.draw(m, "\u00a7e\u8d44\u91d1: \u00a7a\u00a5" + this.menu.getPlayerMoney() + " \u00a7e\u5e97\u9762: \u00a7b" + count, 8, 18, 0xFFFFFF);
    }
}
