package com.campus.client;

import com.campus.blocks.CampusBlocks;
import com.campus.blocks.StartupWorkbenchScreen;
import com.campus.blocks.MarketStallScreen;
import com.campus.blocks.BankCounterScreen;
import com.campus.blocks.CoffeeMachineScreen;
import net.minecraft.client.gui.ScreenManager;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

/**
 * 客户端设置 - 注册GUI Screen
 * 独立类避免服务端加载客户端类
 */
public class ClientSetup {
    public static void setup(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ScreenManager.register(CampusBlocks.STARTUP_WORKBENCH_CONTAINER.get(), StartupWorkbenchScreen::new);
            ScreenManager.register(CampusBlocks.MARKET_STALL_CONTAINER.get(), MarketStallScreen::new);
            ScreenManager.register(CampusBlocks.BANK_COUNTER_CONTAINER.get(), BankCounterScreen::new);
            ScreenManager.register(CampusBlocks.COFFEE_MACHINE_CONTAINER.get(), CoffeeMachineScreen::new);
        });
    }
}
