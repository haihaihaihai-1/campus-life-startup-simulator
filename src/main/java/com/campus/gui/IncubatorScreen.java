package com.campus.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.campus.network.NetworkHandler;
import com.campus.systems.IncubatorSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class IncubatorScreen extends ContainerScreen<IncubatorContainer> {

    private static final ResourceLocation TEXTURE = new ResourceLocation("campuslife", "textures/gui/incubator.png");

    public IncubatorScreen(IncubatorContainer container, PlayerInventory inv, ITextComponent title) {
        super(container, inv, title);
        this.imageWidth = 176;
        this.imageHeight = 200;
        this.inventoryLabelX = 10000;
        this.inventoryLabelY = 10000;
    }

    @Override
    protected void init() {
        super.init();
        IncubatorContainer menu = this.menu;
        int cx = this.leftPos;
        int cy = this.topPos;

        for (int i = 0; i < IncubatorSystem.INCUBATORS.length; i++) {
            final int idx = i;
            IncubatorSystem.Incubator inc = IncubatorSystem.INCUBATORS[i];
            String label = inc.name + " | " + inc.fee + "\u5e01 | Lv." + inc.requiredLevel;
            boolean canJoin = menu.getPlayerLevel() >= inc.requiredLevel && menu.getPlayerMoney() >= inc.fee;
            this.addButton(new Button(cx + 8, cy + 20 + i * 28, 160, 24,
                new StringTextComponent((canJoin ? "\u00a7a" : "\u00a7c") + label),
                b -> NetworkHandler.CHANNEL.sendToServer(new NetworkHandler.IncubatorJoinPacket(idx))));
        }
    }

    @Override
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int x, int y) {
        RenderSystem.color4f(1f, 1f, 1f, 1f);
        this.minecraft.getTextureManager().bind(TEXTURE);
        this.blit(matrixStack, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    protected void renderLabels(MatrixStack matrixStack, int x, int y) {
        IncubatorContainer menu = this.menu;
        this.font.draw(matrixStack, "\u00a76\u5b75\u5316\u5668\u7cfb\u7edf", 8, 6, 0xFFFFFF);
        this.font.draw(matrixStack, "\u00a7e\u8d44\u91d1: \u00a7a" + menu.getPlayerMoney() + " \u00a7e\u7b49\u7ea7: \u00a7aLv." + menu.getPlayerLevel(), 8, 138, 0xFFFFFF);
        if (menu.getCurrentIncubator() >= 0) {
            String name = IncubatorSystem.INCUBATORS[menu.getCurrentIncubator()].name;
            int mins = menu.getRemainingTicks() / 1200;
            this.font.draw(matrixStack, "\u00a7a\u5f53\u524d: " + name + " (" + mins + "\u5206\u949f)", 8, 150, 0xFFFFFF);
        } else {
            this.font.draw(matrixStack, "\u00a77\u672a\u5165\u9a7b\u5b75\u5316\u5668", 8, 150, 0xFFFFFF);
        }
    }
}
