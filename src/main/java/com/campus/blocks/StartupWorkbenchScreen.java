package com.campus.blocks;

import com.campus.economy.MoneyCapability;
import com.campus.economy.SkillCapability;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

/**
 * 创业工坊GUI界面
 * 参考模式: McJtyLib GenericGuiContainer (MIT) + Forge原生ContainerScreen
 * 显示: 资金/等级/经验/员工 + 操作按钮
 */
public class StartupWorkbenchScreen extends ContainerScreen<StartupWorkbenchContainer> {

    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation("campuslife", "textures/gui/startup_workbench.png");

    public StartupWorkbenchScreen(StartupWorkbenchContainer container, PlayerInventory playerInventory, ITextComponent title) {
        super(container, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 186;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void init() {
        super.init();
        // 投资按钮
        this.addButton(new Button(this.leftPos + 10, this.topPos + 60, 70, 20,
            new StringTextComponent("\u6295\u8d44 100"), button -> {
            // 发送投资命令到服务端
            assert this.minecraft != null;
            if (this.minecraft.player != null) {
                this.minecraft.player.chat("/business invest 100");
            }
        }));

        // 查看面板按钮
        this.addButton(new Button(this.leftPos + 90, this.topPos + 60, 70, 20,
            new StringTextComponent("\u521b\u4e1a\u9762\u677f"), button -> {
            assert this.minecraft != null;
            if (this.minecraft.player != null) {
                this.minecraft.player.chat("/business");
                this.onClose();
            }
        }));

        // 市场按钮
        this.addButton(new Button(this.leftPos + 10, this.topPos + 84, 70, 20,
            new StringTextComponent("\u5e02\u573a\u884c\u60c5"), button -> {
            assert this.minecraft != null;
            if (this.minecraft.player != null) {
                this.minecraft.player.chat("/market list");
                this.onClose();
            }
        }));

        // 统计面板按钮
        this.addButton(new Button(this.leftPos + 90, this.topPos + 84, 70, 20,
            new StringTextComponent("\u6570\u636e\u9762\u677f"), button -> {
            assert this.minecraft != null;
            if (this.minecraft.player != null) {
                this.minecraft.player.chat("/stats");
                this.onClose();
            }
        }));
    }

    @Override
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        assert this.minecraft != null;
        this.minecraft.getTextureManager().bind(GUI_TEXTURE);
        int x = this.leftPos;
        int y = this.topPos;
        this.blit(matrixStack, x, y, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    protected void renderLabels(MatrixStack matrixStack, int mouseX, int mouseY) {
        StartupWorkbenchContainer container = this.menu;
        // 标题
        this.font.draw(matrixStack, "\u00a76\u521b\u4e1a\u5de5\u574a", 6, 6, 0x404040);

        // 资金
        this.font.draw(matrixStack, "\u00a7e\u8d44\u91d1: \u00a76" + container.getMoney() + " \u91d1\u5e01", 6, 20, 0x404040);
        // 等级
        this.font.draw(matrixStack, "\u00a7e\u7b49\u7ea7: \u00a7bLv." + container.getLevel(), 6, 32, 0x404040);
        // 经验
        this.font.draw(matrixStack, "\u00a7e\u7ecf\u9a8c: \u00a7a" + container.getExp() + "/" + (container.getLevel() * 100), 6, 44, 0x404040);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);
    }
}
