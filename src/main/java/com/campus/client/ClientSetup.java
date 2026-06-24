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
import com.campus.blocks.TrainingCenterScreen;
import com.campus.blocks.TaxOfficeScreen;
import com.campus.blocks.AchievementHallScreen;
import com.campus.blocks.CompetitionArenaScreen;
import com.campus.blocks.AllianceHQScreen;
import com.campus.blocks.ESGCenterScreen;
import com.campus.blocks.CarbonExchangeScreen;
import com.campus.blocks.DigitalCenterScreen;
import com.campus.blocks.BrandWorkshopScreen;
import com.campus.blocks.DataCenterScreen;
import com.campus.blocks.FutureFactoryScreen;
import com.campus.blocks.IPTradeCenterScreen;
import com.campus.blocks.RealEstateCenterScreen;
import com.campus.blocks.InsuranceCenterScreen;
import com.campus.blocks.AuctionHouseScreen;
import com.campus.blocks.GovOfficeScreen;
import com.campus.blocks.CorporateUnivScreen;
import com.campus.blocks.MemberCenterScreen;
import com.campus.blocks.MediaCenterScreen;
import com.campus.blocks.CommunityCenterScreen;
import com.campus.blocks.FoundationScreen;
import com.campus.blocks.InternationalCenterScreen;
import com.campus.blocks.LogisticsCenterScreen;
import com.campus.blocks.QualityCenterScreen;
import com.campus.blocks.SupplyChainScreen;
import com.campus.blocks.RetailEmpireScreen;
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
            // 第三批GUI
            ScreenManager.register(CampusBlocks.TRAINING_CENTER_CONTAINER.get(), TrainingCenterScreen::new);
            ScreenManager.register(CampusBlocks.TAX_OFFICE_CONTAINER.get(), TaxOfficeScreen::new);
            ScreenManager.register(CampusBlocks.ACHIEVEMENT_HALL_CONTAINER.get(), AchievementHallScreen::new);
            ScreenManager.register(CampusBlocks.COMPETITION_ARENA_CONTAINER.get(), CompetitionArenaScreen::new);
            ScreenManager.register(CampusBlocks.ALLIANCE_HQ_CONTAINER.get(), AllianceHQScreen::new);
            // 第四批GUI
            ScreenManager.register(CampusBlocks.ESG_CENTER_CONTAINER.get(), ESGCenterScreen::new);
            ScreenManager.register(CampusBlocks.CARBON_EXCHANGE_CONTAINER.get(), CarbonExchangeScreen::new);
            ScreenManager.register(CampusBlocks.DIGITAL_CENTER_CONTAINER.get(), DigitalCenterScreen::new);
            ScreenManager.register(CampusBlocks.BRAND_WORKSHOP_CONTAINER.get(), BrandWorkshopScreen::new);
            ScreenManager.register(CampusBlocks.DATA_CENTER_CONTAINER.get(), DataCenterScreen::new);
            ScreenManager.register(CampusBlocks.FUTURE_FACTORY_CONTAINER.get(), FutureFactoryScreen::new);
            // 第五批GUI
            ScreenManager.register(CampusBlocks.IP_TRADE_CENTER_CONTAINER.get(), IPTradeCenterScreen::new);
            ScreenManager.register(CampusBlocks.REAL_ESTATE_CENTER_CONTAINER.get(), RealEstateCenterScreen::new);
            ScreenManager.register(CampusBlocks.INSURANCE_CENTER_CONTAINER.get(), InsuranceCenterScreen::new);
            ScreenManager.register(CampusBlocks.AUCTION_HOUSE_CONTAINER.get(), AuctionHouseScreen::new);
            ScreenManager.register(CampusBlocks.GOV_OFFICE_CONTAINER.get(), GovOfficeScreen::new);
            // 第六批GUI
            ScreenManager.register(CampusBlocks.CORPORATE_UNIV_CONTAINER.get(), CorporateUnivScreen::new);
            ScreenManager.register(CampusBlocks.MEMBER_CENTER_CONTAINER.get(), MemberCenterScreen::new);
            ScreenManager.register(CampusBlocks.MEDIA_CENTER_CONTAINER.get(), MediaCenterScreen::new);
            ScreenManager.register(CampusBlocks.COMMUNITY_CENTER_CONTAINER.get(), CommunityCenterScreen::new);
            ScreenManager.register(CampusBlocks.FOUNDATION_CONTAINER.get(), FoundationScreen::new);
        // 第七批GUI
        ScreenManager.register(CampusBlocks.INTERNATIONAL_CENTER_CONTAINER.get(), InternationalCenterScreen::new);
        ScreenManager.register(CampusBlocks.LOGISTICS_CENTER_CONTAINER.get(), LogisticsCenterScreen::new);
        ScreenManager.register(CampusBlocks.QUALITY_CENTER_CONTAINER.get(), QualityCenterScreen::new);
        ScreenManager.register(CampusBlocks.SUPPLY_CHAIN_CONTAINER.get(), SupplyChainScreen::new);
        ScreenManager.register(CampusBlocks.RETAIL_EMPIRE_CONTAINER.get(), RetailEmpireScreen::new);
        });
    }
}
