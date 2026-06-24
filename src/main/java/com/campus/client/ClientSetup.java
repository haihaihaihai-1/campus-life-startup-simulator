package com.campus.client;

import com.campus.blocks.CampusBlocks;
import com.campus.blocks.StartupWorkbenchScreen;
import com.campus.blocks.MarketStallScreen;
import com.campus.blocks.BankCounterScreen;
import com.campus.blocks.CoffeeMachineScreen;
import com.campus.blocks.InvestCenterScreen;
import com.campus.blocks.ResearchLabScreen;
import com.campus.blocks.EmployeeCenterScreen;
import com.campus.blocks.IncubatorScreen;
import com.campus.blocks.StockTerminalScreen;
import net.minecraft.client.gui.ScreenManager;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

/**
 * 客户端设置 - 注册GUI Screen
 * 独立类避免服务端加载客户端类
 */
public class ClientSetup {
    public static void setup(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            // 第一批GUI
            ScreenManager.register(CampusBlocks.STARTUP_WORKBENCH_CONTAINER.get(), StartupWorkbenchScreen::new);
            ScreenManager.register(CampusBlocks.MARKET_STALL_CONTAINER.get(), MarketStallScreen::new);
            ScreenManager.register(CampusBlocks.BANK_COUNTER_CONTAINER.get(), BankCounterScreen::new);
            ScreenManager.register(CampusBlocks.COFFEE_MACHINE_CONTAINER.get(), CoffeeMachineScreen::new);
            // 第二批GUI
            ScreenManager.register(CampusBlocks.INVEST_CENTER_CONTAINER.get(), InvestCenterScreen::new);
            ScreenManager.register(CampusBlocks.RESEARCH_LAB_CONTAINER.get(), ResearchLabScreen::new);
            ScreenManager.register(CampusBlocks.EMPLOYEE_CENTER_CONTAINER.get(), EmployeeCenterScreen::new);
            ScreenManager.register(CampusBlocks.INCUBATOR_CONTAINER.get(), IncubatorScreen::new);
            ScreenManager.register(CampusBlocks.STOCK_TERMINAL_CONTAINER.get(), StockTerminalScreen::new);
        });
    }
}
