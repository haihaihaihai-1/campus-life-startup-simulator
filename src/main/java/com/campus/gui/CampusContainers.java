package com.campus.gui;

import com.campus.CampusLife;
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

    public static final RegistryObject<ContainerType<ContractContainer>> CONTRACT_CONTAINER =
        CONTAINERS.register("contract",
            () -> IForgeContainerType.create((windowId, inv, buf) ->
                new ContractContainer(windowId, inv, buf.readBlockPos())));

    public static final RegistryObject<ContainerType<ESGContainer>> ESG_CONTAINER =
        CONTAINERS.register("esg",
            () -> IForgeContainerType.create((windowId, inv, buf) ->
                new ESGContainer(windowId, inv, buf.readBlockPos())));

    public static final RegistryObject<ContainerType<IPOContainer>> IPO_CONTAINER =
        CONTAINERS.register("ipo",
            () -> IForgeContainerType.create((windowId, inv, buf) ->
                new IPOContainer(windowId, inv, buf.readBlockPos())));

    public static final RegistryObject<ContainerType<VCContainer>> VC_CONTAINER =
        CONTAINERS.register("vc",
            () -> IForgeContainerType.create((windowId, inv, buf) ->
                new VCContainer(windowId, inv, buf.readBlockPos())));

    public static final RegistryObject<ContainerType<StockContainer>> STOCK_CONTAINER =
        CONTAINERS.register("stock",
            () -> IForgeContainerType.create((windowId, inv, buf) ->
                new StockContainer(windowId, inv, buf.readBlockPos())));
}
