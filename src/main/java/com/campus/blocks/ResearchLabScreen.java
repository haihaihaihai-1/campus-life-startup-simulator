package com.campus.blocks;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
public class ResearchLabScreen extends ContainerScreen<ResearchLabContainer> {
    private static final ResourceLocation TEX = new ResourceLocation("campuslife", "textures/gui/research_lab.png");
    public ResearchLabScreen(ResearchLabContainer c, PlayerInventory pi, ITextComponent t) { super(c, pi, t); this.imageWidth = 176; this.imageHeight = 186; this.inventoryLabelY = this.imageHeight - 94; }
    @Override protected void init() {
        super.init();
        addButton(new Button(leftPos + 10, topPos + 40, 70, 20, new StringTextComponent("\u7814\u53d1\u89e3\u9501"), b -> { if (minecraft != null && minecraft.player != null) minecraft.player.chat("/research unlock"); }));
        addButton(new Button(leftPos + 90, topPos + 40, 70, 20, new StringTextComponent("\u67e5\u770b\u79d1\u628f\u6811"), b -> { if (minecraft != null && minecraft.player != null) { minecraft.player.chat("/research"); onClose(); } }));
        addButton(new Button(leftPos + 10, topPos + 62, 70, 20, new StringTextComponent("\u67e5\u770b\u4e13\u5229"), b -> { if (minecraft != null && minecraft.player != null) { minecraft.player.chat("/patent"); onClose(); } }));
        addButton(new Button(leftPos + 90, topPos + 62, 70, 20, new StringTextComponent("\u67e5\u770b\u5b9e\u9a8c\u5ba4"), b -> { if (minecraft != null && minecraft.player != null) { minecraft.player.chat("/lab"); onClose(); } }));
    }
    @Override protected void renderBg(MatrixStack ms, float pt, int mx, int my) { RenderSystem.color4f(1,1,1,1); if (minecraft != null) { minecraft.getTextureManager().bind(TEX); blit(ms, leftPos, topPos, 0, 0, imageWidth, imageHeight); } }
    @Override protected void renderLabels(MatrixStack ms, int mx, int my) {
        font.draw(ms, "\u00a76\u7814\u53d1\u5b9e\u9a8c\u5ba4", 6, 6, 0x404040);
        font.draw(ms, "\u00a7e\u8d44\u91d1: \u00a76" + menu.getMoney(), 6, 20, 0x404040);
        font.draw(ms, "\u00a7e\u5df2\u89e3\u9501\u628f\u672f: \u00a7b" + menu.getTechCount(), 6, 32, 0x404040);
    }
    @Override public void render(MatrixStack ms, int mx, int my, float pt) { renderBackground(ms); super.render(ms, mx, my, pt); renderTooltip(ms, mx, my); }
}
