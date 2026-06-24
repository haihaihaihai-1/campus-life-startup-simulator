package com.campus.blocks;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
public class LogisticsCenterScreen extends ContainerScreen<LogisticsCenterContainer> {
    private static final ResourceLocation TEX = new ResourceLocation("campuslife", "textures/gui/logistics_center.png");
    public LogisticsCenterScreen(LogisticsCenterContainer c, PlayerInventory pi, ITextComponent t) { super(c, pi, t); this.imageWidth = 176; this.imageHeight = 186; this.inventoryLabelY = this.imageHeight - 94; }
    @Override protected void init() {
        super.init();
        addButton(new Button(leftPos + 10, topPos + 40, 70, 20, new StringTextComponent("\u4f18\u5316\u8def\u7ebf"), b -> { if (minecraft != null && minecraft.player != null) minecraft.player.chat("/logistics optimize 2000"); }));
        addButton(new Button(leftPos + 90, topPos + 40, 70, 20, new StringTextComponent("\u53d1\u8d27"), b -> { if (minecraft != null && minecraft.player != null) minecraft.player.chat("/logistics ship 1000"); }));
        addButton(new Button(leftPos + 10, topPos + 62, 70, 20, new StringTextComponent("\u67e5\u770b\u7269\u6d41"), b -> { if (minecraft != null && minecraft.player != null) { minecraft.player.chat("/logistics"); onClose(); } }));
        addButton(new Button(leftPos + 90, topPos + 62, 70, 20, new StringTextComponent("\u4ed3\u50a8\u7ba1\u7406"), b -> { if (minecraft != null && minecraft.player != null) { minecraft.player.chat("/logistics warehouse"); onClose(); } }));
    }
    @Override protected void renderBg(MatrixStack ms, float pt, int mx, int my) { RenderSystem.color4f(1,1,1,1); if (minecraft != null) { minecraft.getTextureManager().bind(TEX); blit(ms, leftPos, topPos, 0, 0, imageWidth, imageHeight); } }
    @Override protected void renderLabels(MatrixStack ms, int mx, int my) {
        font.draw(ms, "\u00a76\u7269\u6d41\u4e2d\u5fc3", 6, 6, 0x404040);
        font.draw(ms, "\u00a7e\u8d44\u91d1: \u00a76" + menu.getMoney(), 6, 20, 0x404040);
        font.draw(ms, "\u00a7e\u7269\u6d41\u7b49\u7ea7: \u00a7b" + menu.getLogisticsLevel(), 6, 32, 0x404040);
    }
    @Override public void render(MatrixStack ms, int mx, int my, float pt) { renderBackground(ms); super.render(ms, mx, my, pt); renderTooltip(ms, mx, my); }
}
