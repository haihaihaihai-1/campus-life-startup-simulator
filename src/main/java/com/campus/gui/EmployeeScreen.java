package com.campus.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.campus.network.NetworkHandler;
import com.campus.systems.EmployeeSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class EmployeeScreen extends ContainerScreen<EmployeeContainer> {
    private static final ResourceLocation TEX = new ResourceLocation("campuslife", "textures/gui/employee.png");
    public EmployeeScreen(EmployeeContainer c, PlayerInventory inv, ITextComponent t) {
        super(c, inv, t);
        this.imageWidth = 176; this.imageHeight = 200;
        this.inventoryLabelX = 10000; this.inventoryLabelY = 10000;
    }
    @Override protected void init() {
        super.init();
        EmployeeSystem.Employee[] types = EmployeeSystem.EMPLOYEE_TYPES;
        for (int i = 0; i < types.length; i++) {
            final int idx = i;
            EmployeeSystem.Employee e = types[i];
            this.addButton(new Button(this.leftPos + 6, this.topPos + 24 + i*22, 162, 20,
                new StringTextComponent("\u00a7e" + e.name + " \u00a76\u00a5" + e.hireCost + " +\u00a5" + e.dailyIncome + "/d"),
                b -> NetworkHandler.CHANNEL.sendToServer(new NetworkHandler.GenericActionPacket("employee_hire_" + idx, 0))));
        }
    }
    @Override protected void renderBg(MatrixStack m, float pt, int x, int y) {
        RenderSystem.color4f(1f, 1f, 1f, 1f);
        this.minecraft.getTextureManager().bind(TEX);
        this.blit(m, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }
    @Override protected void renderLabels(MatrixStack m, int x, int y) {
        this.font.draw(m, "\u00a76HR \u62db\u8058\u4e2d\u5fc3", 8, 6, 0xFFFFFF);
        this.font.draw(m, "\u00a7e\u4eba\u6570 " + this.menu.getTotalEmployees() + " | +\u00a5" + this.menu.getTotalIncome() + "/d", 8, 174, 0xFFFFFF);
    }
}
