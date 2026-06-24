package com.campus.blocks;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
public class CorporateUnivScreen extends ContainerScreen<CorporateUnivContainer> {
    private static final ResourceLocation TEX = new ResourceLocation("campuslife", "textures/gui/corporate_univ.png");
    public CorporateUnivScreen(CorporateUnivContainer c, PlayerInventory pi, ITextComponent t) { super(c, pi, t); this.imageWidth = 176; this.imageHeight = 186; this.inventoryLabelY = this.imageHeight - 94; }
    @Override protected void init() {
        super.init();
        addButton(new Button(leftPos + 10, topPos + 40, 70, 20, new StringTextComponent("\u62a5\u540d\u8bfe\u7a0b"), b -> { if (minecraft != null && minecraft.player != null) minecraft.player.chat("/uni 1"); }));
        addButton(new Button(leftPos + 90, topPos + 40, 70, 20, new StringTextComponent("\u67e5\u770b\u5927\u5b66"), b -> { if (minecraft != null && minecraft.player != null) minecraft.player.chat("/uni"); }));
        addButton(new Button(leftPos + 10, topPos + 62, 70, 20, new StringTextComponent("\u67e5\u770b\u5bfc\u5e08"), b -> { if (minecraft != null && minecraft.player != null) { minecraft.player.chat("/mnet consult"); onClose(); } }));
        addButton(new Button(leftPos + 90, topPos + 62, 70, 20, new StringTextComponent("\u67e5\u770b\u77e5\u8bc6"), b -> { if (minecraft != null && minecraft.player != null) { minecraft.player.chat("/knowledge"); onClose(); } }));
    }
    @Override protected void renderBg(MatrixStack ms, float pt, int mx, int my) { RenderSystem.color4f(1,1,1,1); if (minecraft != null) { minecraft.getTextureManager().bind(TEX); blit(ms, leftPos, topPos, 0, 0, imageWidth, imageHeight); } }
    @Override protected void renderLabels(MatrixStack ms, int mx, int my) {
        font.draw(ms, "\u00a76\u4f01\u4e1a\u5927\u5b66", 6, 6, 0x404040);
        font.draw(ms, "\u00a7e\u8d44\u91d1: \u00a76" + menu.getMoney(), 6, 20, 0x404040);
        font.draw(ms, "\u00a7e\u8bfe\u7a0b\u6570: \u00a7b" + menu.getCourseCount(), 6, 32, 0x404040);
    }
    @Override public void render(MatrixStack ms, int mx, int my, float pt) { renderBackground(ms); super.render(ms, mx, my, pt); renderTooltip(ms, mx, my); }
}
