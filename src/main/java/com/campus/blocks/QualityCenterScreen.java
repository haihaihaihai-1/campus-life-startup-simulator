package com.campus.blocks;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
public class QualityCenterScreen extends ContainerScreen<QualityCenterContainer> {
    private static final ResourceLocation TEX = new ResourceLocation("campuslife", "textures/gui/quality_center.png");
    public QualityCenterScreen(QualityCenterContainer c, PlayerInventory pi, ITextComponent t) { super(c, pi, t); this.imageWidth = 176; this.imageHeight = 186; this.inventoryLabelY = this.imageHeight - 94; }
    @Override protected void init() {
        super.init();
        addButton(new Button(leftPos + 10, topPos + 40, 70, 20, new StringTextComponent("\u8d28\u91cf\u68c0\u6d4b"), b -> { if (minecraft != null && minecraft.player != null) minecraft.player.chat("/quality check 1500"); }));
        addButton(new Button(leftPos + 90, topPos + 40, 70, 20, new StringTextComponent("\u8ba4\u8bc1\u7533\u8bf7"), b -> { if (minecraft != null && minecraft.player != null) minecraft.player.chat("/quality certify 3000"); }));
        addButton(new Button(leftPos + 10, topPos + 62, 70, 20, new StringTextComponent("\u67e5\u770b\u5408\u683c\u7387"), b -> { if (minecraft != null && minecraft.player != null) { minecraft.player.chat("/quality"); onClose(); } }));
        addButton(new Button(leftPos + 90, topPos + 62, 70, 20, new StringTextComponent("\u8d28\u91cf\u62a5\u544a"), b -> { if (minecraft != null && minecraft.player != null) { minecraft.player.chat("/quality report"); onClose(); } }));
    }
    @Override protected void renderBg(MatrixStack ms, float pt, int mx, int my) { RenderSystem.color4f(1,1,1,1); if (minecraft != null) { minecraft.getTextureManager().bind(TEX); blit(ms, leftPos, topPos, 0, 0, imageWidth, imageHeight); } }
    @Override protected void renderLabels(MatrixStack ms, int mx, int my) {
        font.draw(ms, "\u00a76\u8d28\u68c0\u4e2d\u5fc3", 6, 6, 0x404040);
        font.draw(ms, "\u00a7e\u8d44\u91d1: \u00a76" + menu.getMoney(), 6, 20, 0x404040);
        font.draw(ms, "\u00a7e\u8d28\u91cf\u8bc4\u5206: \u00a7b" + menu.getQualityScore(), 6, 32, 0x404040);
    }
    @Override public void render(MatrixStack ms, int mx, int my, float pt) { renderBackground(ms); super.render(ms, mx, my, pt); renderTooltip(ms, mx, my); }
}
