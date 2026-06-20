package com.campus.gui;

import com.campus.CampusLife;
import com.campus.blocks.CampusBlocks;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * 容器类型注册
 */
public class CampusContainers {
    public static final DeferredRegister<ContainerType<?>> CONTAINERS =
        DeferredRegister.create(ForgeRegistries.CONTAINERS, CampusLife.MOD_ID);

    public static final RegistryObject<ContainerType<IncubatorContainer>> INCUBATOR_CONTAINER =
        CONTAINERS.register("incubator",
            () -> IForgeContainerType.create((windowId, inv, buf) ->
                new IncubatorContainer(windowId, inv, buf.readBlockPos())));

    public static final RegistryObject<ContainerType<AuctionContainer>> AUCTION_CONTAINER =
        CONTAINERS.register("auction",
            () -> IForgeContainerType.create((windowId, inv, buf) ->
                new AuctionContainer(windowId, inv, buf.readBlockPos())));

    public static final RegistryObject<ContainerType<CryptoContainer>> CRYPTO_CONTAINER =
        CONTAINERS.register("crypto",
            () -> IForgeContainerType.create((windowId, inv, buf) ->
                new CryptoContainer(windowId, inv, buf.readBlockPos())));
}
