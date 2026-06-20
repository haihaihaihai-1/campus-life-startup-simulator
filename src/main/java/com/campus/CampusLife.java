package com.campus;

import com.campus.economy.MoneyCapability;
import com.campus.economy.SkillCapability;
import com.campus.blocks.CampusBlocks;
import com.campus.gui.CampusContainers;
import com.campus.gui.IncubatorScreen;
import com.campus.gui.AuctionScreen;
import com.campus.gui.CryptoScreen;
import com.campus.gui.ContractScreen;
import com.campus.gui.ESGScreen;
import com.campus.gui.IPOScreen;
import com.campus.gui.VCScreen;
import com.campus.gui.StockScreen;
import com.campus.items.CampusItems;
import com.campus.network.NetworkHandler;
import net.minecraft.client.gui.ScreenManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("campuslife")
public class CampusLife {

    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "campuslife";

    public CampusLife() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);

        CampusItems.ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        CampusBlocks.BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        CampusBlocks.ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        CampusContainers.CONTAINERS.register(FMLJavaModLoadingContext.get().getModEventBus());

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event) {
        MoneyCapability.register();
        SkillCapability.register();
        NetworkHandler.register();
        LOGGER.info("Campus Life \u6821\u56ed\u521b\u4e1a\u6a21\u62df\u5668 v15: \u7f51\u7edc\u5305+GUI\u5bb9\u5668\u5df2\u5c31\u7eea!");
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        ScreenManager.register(CampusContainers.INCUBATOR_CONTAINER.get(), IncubatorScreen::new);
        ScreenManager.register(CampusContainers.AUCTION_CONTAINER.get(), AuctionScreen::new);
        ScreenManager.register(CampusContainers.CRYPTO_CONTAINER.get(), CryptoScreen::new);
        ScreenManager.register(CampusContainers.CONTRACT_CONTAINER.get(), ContractScreen::new);
        ScreenManager.register(CampusContainers.ESG_CONTAINER.get(), ESGScreen::new);
        ScreenManager.register(CampusContainers.IPO_CONTAINER.get(), IPOScreen::new);
        ScreenManager.register(CampusContainers.VC_CONTAINER.get(), VCScreen::new);
        ScreenManager.register(CampusContainers.STOCK_CONTAINER.get(), StockScreen::new);
    }
}
