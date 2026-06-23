package com.campus;

import com.campus.economy.MoneyCapability;
import com.campus.economy.SkillCapability;
import com.campus.blocks.CampusBlocks;
import com.campus.items.CampusItems;
import com.campus.client.ClientSetup;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("campuslife")
public class CampusLife {

    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "campuslife";

    public CampusLife() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);

        CampusItems.ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        CampusBlocks.BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        CampusBlocks.ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        CampusBlocks.TILE_ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
        CampusBlocks.CONTAINERS.register(FMLJavaModLoadingContext.get().getModEventBus());

        // 客户端注册 (安全: 仅客户端执行)
        if (FMLEnvironment.dist.isClient()) {
            FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientSetup::setup);
        }

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event) {
        MoneyCapability.register();
        SkillCapability.register();
        LOGGER.info("Campus Life \u6821\u56ed\u521b\u4e1a\u6a21\u62df\u5668\u5df2\u52a0\u8f7d! \u7ecf\u6d4e\u7cfb\u7edf+\u521b\u4e1a\u5de5\u574a+\u5e02\u573a\u4ea4\u6613\u5df2\u5c31\u7eea!");
    }
}
