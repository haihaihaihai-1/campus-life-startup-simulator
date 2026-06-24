package com.campus.blocks;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
public class TaxOfficeScreen extends ContainerScreen<TaxOfficeContainer> {
    private static final ResourceLocation TEX = new ResourceLocation("campuslife", "textures/gui/tax_office.png");
    public TaxOfficeScreen(TaxOfficeContainer c, PlayerInventory pi, ITextComponent t) { super(c, pi, t); this.imageWidth = 176; this.imageHeight = 186; this.inventoryLabelY = this.imageHeight - 94; }
    @Override protected void init() {
        super.init();
        addButton(new Button(leftPos + 10, topPos + 40, 70, 20, new StringTextComponent("\u67e5\u770b\u7a0e\u52a1"), b -> { if (minecraft != null && minecraft.player != null) { minecraft.player.chat("/tax"); onClose(); } }));
        addButton(new Button(leftPos + 90, topPos + 40, 70, 20, new StringTextComponent("\u7a0e\u52a1\u7b79\u5212"), b -> { if (minecraft != null && minecraft.player != null) minecraft.player.chat("/taxplan"); }));
        addButton(new Button(leftPos + 10, topPos + 62, 70, 20, new StringTextComponent("\u67e5\u770b\u9000\u7a0e"), b -> { if (minecraft != null && minecraft.player != null) { minecraft.player.chat("/taxreturn"); onClose(); } }));
        addButton(new Button(leftPos + 90, topPos + 62, 70, 20, new StringTextComponent("\u67e5\u770b\u5ba1\u8ba1"), b -> { if (minecraft != null && minecraft.player != null) { minecraft.player.chat("/audit"); onClose(); } }));
    }
    @Override protected void renderBg(MatrixStack ms, float pt, int mx, int my) { RenderSystem.color4f(1,1,1,1); if (minecraft != null) { minecraft.getTextureManager().bind(TEX); blit(ms, leftPos, topPos, 0, 0, imageWidth, imageHeight); } }
    @Override protected void renderLabels(MatrixStack ms, int mx, int my) {
        font.draw(ms, "\u00a76\u7a0e\u52a1\u5c40", 6, 6, 0x404040);
        font.draw(ms, "\u00a7e\u8d44\u91d1: \u00a76" + menu.getMoney(), 6, 20, 0x404040);
        font.draw(ms, "\u00a7e\u5df2\u7f34\u7a0e: \u00a7b" + menu.getTaxPaid(), 6, 32, 0x404040);
    }
    @Override public void render(MatrixStack ms, int mx, int my, float pt) { renderBackground(ms); super.render(ms, mx, my, pt); renderTooltip(ms, mx, my); }
}
