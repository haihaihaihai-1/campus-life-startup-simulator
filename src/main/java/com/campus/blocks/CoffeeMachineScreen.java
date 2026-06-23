package com.campus.blocks;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class CoffeeMachineScreen extends ContainerScreen<CoffeeMachineContainer> {
    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation("campuslife", "textures/gui/coffee_machine.png");

    public CoffeeMachineScreen(CoffeeMachineContainer container, PlayerInventory playerInventory, ITextComponent title) {
        super(container, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 186;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void init() {
        super.init();
        this.addButton(new Button(this.leftPos + 10, this.topPos + 40, 70, 20,
            new StringTextComponent("\u751f\u4ea7\u5496\u5561"), button -> {
            assert this.minecraft != null;
            if (this.minecraft.player != null) { this.minecraft.player.chat("/produce 1 1"); }
        }));
        this.addButton(new Button(this.leftPos + 90, this.topPos + 40, 70, 20,
            new StringTextComponent("\u67e5\u770b\u914d\u65b9"), button -> {
            assert this.minecraft != null;
            if (this.minecraft.player != null) { this.minecraft.player.chat("/produce"); this.onClose(); }
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
        this.font.draw(matrixStack, "\u00a76\u5496\u5561\u673a", 6, 6, 0x404040);
        this.font.draw(matrixStack, "\u00a7e\u5496\u5561\u5e93\u5b58: \u00a7a" + this.menu.getCoffeeCount(), 6, 20, 0x404040);
        this.font.draw(matrixStack, "\u00a77\u8f93\u5165\u539f\u6750\u6599\u751f\u4ea7\u5496\u5561", 6, 32, 0x404040);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);
    }
}
