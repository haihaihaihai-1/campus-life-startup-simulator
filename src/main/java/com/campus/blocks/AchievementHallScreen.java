package com.campus.blocks;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
public class AchievementHallScreen extends ContainerScreen<AchievementHallContainer> {
    private static final ResourceLocation TEX = new ResourceLocation("campuslife", "textures/gui/achievement_hall.png");
    public AchievementHallScreen(AchievementHallContainer c, PlayerInventory pi, ITextComponent t) { super(c, pi, t); this.imageWidth = 176; this.imageHeight = 186; this.inventoryLabelY = this.imageHeight - 94; }
    @Override protected void init() {
        super.init();
        addButton(new Button(leftPos + 10, topPos + 40, 70, 20, new StringTextComponent("\u67e5\u770b\u6210\u5c31"), b -> { if (minecraft != null && minecraft.player != null) { minecraft.player.chat("/achievements"); onClose(); } }));
        addButton(new Button(leftPos + 90, topPos + 40, 70, 20, new StringTextComponent("\u67e5\u770b\u6392\u540d"), b -> { if (minecraft != null && minecraft.player != null) { minecraft.player.chat("/rank"); onClose(); } }));
        addButton(new Button(leftPos + 10, topPos + 62, 70, 20, new StringTextComponent("\u67e5\u770b\u7edf\u8ba1"), b -> { if (minecraft != null && minecraft.player != null) { minecraft.player.chat("/stats"); onClose(); } }));
        addButton(new Button(leftPos + 90, topPos + 62, 70, 20, new StringTextComponent("\u4f01\u4e1a\u9a7e\u9a76\u8231"), b -> { if (minecraft != null && minecraft.player != null) { minecraft.player.chat("/cockpit"); onClose(); } }));
    }
    @Override protected void renderBg(MatrixStack ms, float pt, int mx, int my) { RenderSystem.color4f(1,1,1,1); if (minecraft != null) { minecraft.getTextureManager().bind(TEX); blit(ms, leftPos, topPos, 0, 0, imageWidth, imageHeight); } }
    @Override protected void renderLabels(MatrixStack ms, int mx, int my) {
        font.draw(ms, "\u00a76\u6210\u5c31\u5927\u5385", 6, 6, 0x404040);
        font.draw(ms, "\u00a7e\u8d44\u91d1: \u00a76" + menu.getMoney(), 6, 20, 0x404040);
        font.draw(ms, "\u00a7e\u6210\u5c31\u6570: \u00a7b" + menu.getAchievementCount(), 6, 32, 0x404040);
    }
    @Override public void render(MatrixStack ms, int mx, int my, float pt) { renderBackground(ms); super.render(ms, mx, my, pt); renderTooltip(ms, mx, my); }
}
