package com.campus.blocks;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
public class SupplyChainScreen extends ContainerScreen<SupplyChainContainer> {
    private static final ResourceLocation TEX = new ResourceLocation("campuslife", "textures/gui/supply_chain.png");
    public SupplyChainScreen(SupplyChainContainer c, PlayerInventory pi, ITextComponent t) { super(c, pi, t); this.imageWidth = 176; this.imageHeight = 186; this.inventoryLabelY = this.imageHeight - 94; }
    @Override protected void init() {
        super.init();
        addButton(new Button(leftPos + 10, topPos + 40, 70, 20, new StringTextComponent("\u4f9b\u5e94\u5546\u7ba1\u7406"), b -> { if (minecraft != null && minecraft.player != null) minecraft.player.chat("/supplychain supplier 2000"); }));
        addButton(new Button(leftPos + 90, topPos + 40, 70, 20, new StringTextComponent("\u5e93\u5b58\u4f18\u5316"), b -> { if (minecraft != null && minecraft.player != null) minecraft.player.chat("/supplychain inventory 1500"); }));
        addButton(new Button(leftPos + 10, topPos + 62, 70, 20, new StringTextComponent("\u67e5\u770b\u4f9b\u5e94\u94fe"), b -> { if (minecraft != null && minecraft.player != null) { minecraft.player.chat("/supplychain"); onClose(); } }));
        addButton(new Button(leftPos + 90, topPos + 62, 70, 20, new StringTextComponent("\u98ce\u9669\u8bc4\u4f30"), b -> { if (minecraft != null && minecraft.player != null) { minecraft.player.chat("/supplychain risk"); onClose(); } }));
    }
    @Override protected void renderBg(MatrixStack ms, float pt, int mx, int my) { RenderSystem.color4f(1,1,1,1); if (minecraft != null) { minecraft.getTextureManager().bind(TEX); blit(ms, leftPos, topPos, 0, 0, imageWidth, imageHeight); } }
    @Override protected void renderLabels(MatrixStack ms, int mx, int my) {
        font.draw(ms, "\u00a76\u4f9b\u5e94\u94fe\u7ba1\u7406", 6, 6, 0x404040);
        font.draw(ms, "\u00a7e\u8d44\u91d1: \u00a76" + menu.getMoney(), 6, 20, 0x404040);
        font.draw(ms, "\u00a7e\u4f9b\u5e94\u94fe\u7b49\u7ea7: \u00a7b" + menu.getSupplyChainLevel(), 6, 32, 0x404040);
    }
    @Override public void render(MatrixStack ms, int mx, int my, float pt) { renderBackground(ms); super.render(ms, mx, my, pt); renderTooltip(ms, mx, my); }
}
