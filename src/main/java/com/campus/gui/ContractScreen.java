package com.campus.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.campus.network.NetworkHandler;
import com.campus.systems.ContractSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class ContractScreen extends ContainerScreen<ContractContainer> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("campuslife", "textures/gui/contract.png");

    public ContractScreen(ContractContainer container, PlayerInventory inv, ITextComponent title) {
        super(container, inv, title);
        this.imageWidth = 176;
        this.imageHeight = 200;
        this.inventoryLabelX = 10000;
        this.inventoryLabelY = 10000;
    }

    @Override
    protected void init() {
        super.init();
        ContractContainer menu = this.menu;
        for (int i = 0; i < ContractSystem.SUPPLIERS.length; i++) {
            final int idx = i;
            ContractSystem.Supplier s = ContractSystem.SUPPLIERS[i];
            String label = s.name + " | " + s.contractFee + "\u5e01 | -" + s.discountPercent + "%";
            boolean canSign = menu.getPlayerLevel() >= s.requiredLevel && menu.getPlayerMoney() >= s.contractFee;
            this.addButton(new Button(this.leftPos + 8, this.topPos + 20 + i * 20, 160, 18,
                new StringTextComponent((canSign ? "\u00a7a" : "\u00a7c") + label),
                b -> NetworkHandler.CHANNEL.sendToServer(new NetworkHandler.GenericActionPacket("contract", idx))));
        }
    }

    @Override
    protected void renderBg(MatrixStack m, float pt, int x, int y) {
        RenderSystem.color4f(1f, 1f, 1f, 1f);
        this.minecraft.getTextureManager().bind(TEXTURE);
        this.blit(m, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    protected void renderLabels(MatrixStack m, int x, int y) {
        ContractContainer menu = this.menu;
        this.font.draw(m, "\u00a76\u4f9b\u5e94\u5408\u540c\u4e2d\u5fc3", 8, 6, 0xFFFFFF);
        this.font.draw(m, "\u00a7e\u8d44\u91d1: \u00a7a" + menu.getPlayerMoney(), 8, 138, 0xFFFFFF);
        int discount = ContractSystem.getDiscount(menu.getPlayerUUID());
        if (discount > 0) {
            this.font.draw(m, "\u00a7a\u5f53\u524d\u6298\u6263: -" + discount + "%", 8, 150, 0xFFFFFF);
        } else {
            this.font.draw(m, "\u00a77\u672a\u7b7e\u8ba2\u5408\u540c", 8, 150, 0xFFFFFF);
        }
    }
}
