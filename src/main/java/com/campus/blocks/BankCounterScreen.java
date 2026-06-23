package com.campus.blocks;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class BankCounterScreen extends ContainerScreen<BankCounterContainer> {
    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation("campuslife", "textures/gui/bank_counter.png");

    public BankCounterScreen(BankCounterContainer container, PlayerInventory playerInventory, ITextComponent title) {
        super(container, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 186;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void init() {
        super.init();
        this.addButton(new Button(this.leftPos + 10, this.topPos + 40, 70, 20,
            new StringTextComponent("\u8d37\u6b3e 1000"), button -> {
            assert this.minecraft != null;
            if (this.minecraft.player != null) { this.minecraft.player.chat("/loan take 1000"); }
        }));
        this.addButton(new Button(this.leftPos + 90, this.topPos + 40, 70, 20,
            new StringTextComponent("\u8fd8\u6b3e 500"), button -> {
            assert this.minecraft != null;
            if (this.minecraft.player != null) { this.minecraft.player.chat("/loan repay 500"); }
        }));
        this.addButton(new Button(this.leftPos + 10, this.topPos + 62, 70, 20,
            new StringTextComponent("\u7406\u8d22"), button -> {
            assert this.minecraft != null;
            if (this.minecraft.player != null) { this.minecraft.player.chat("/savings"); this.onClose(); }
        }));
        this.addButton(new Button(this.leftPos + 90, this.topPos + 62, 70, 20,
            new StringTextComponent("\u80a1\u5e02"), button -> {
            assert this.minecraft != null;
            if (this.minecraft.player != null) { this.minecraft.player.chat("/stock"); this.onClose(); }
        }));
    }

    @Override
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        assert this.minecraft != null;
        this.minecraft.getTextureManager().bind(GUI_TEXTURE);
        this.blit(matrixStack, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    protected void renderLabels(MatrixStack matrixStack, int mouseX, int mouseY) {
        this.font.draw(matrixStack, "\u00a76\u94f6\u884c\u67dc\u53f0", 6, 6, 0x404040);
        this.font.draw(matrixStack, "\u00a7e\u8d44\u91d1: \u00a76" + this.menu.getMoney() + " \u91d1\u5e01", 6, 20, 0x404040);
        this.font.draw(matrixStack, "\u00a7e\u8d37\u6b3e: \u00a7c" + this.menu.getLoan() + " \u91d1\u5e01", 6, 32, 0x404040);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);
    }
}
