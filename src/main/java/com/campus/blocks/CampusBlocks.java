package com.campus.blocks;

import com.campus.CampusLife;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class CampusBlocks {

    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, CampusLife.MOD_ID);

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, CampusLife.MOD_ID);

    // === TileEntity 和 ContainerType 注册 ===
    public static final DeferredRegister<TileEntityType<?>> TILE_ENTITIES =
            DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, CampusLife.MOD_ID);

    public static final DeferredRegister<ContainerType<?>> CONTAINERS =
            DeferredRegister.create(ForgeRegistries.CONTAINERS, CampusLife.MOD_ID);

    // === 校园基础方块 ===
    public static final RegistryObject<Block> DESK =
            registerBlock("desk", () -> new Block(Block.Properties.of(Material.WOOD)
                    .strength(2.0f, 3.0f).sound(SoundType.WOOD)));

    public static final RegistryObject<Block> CHAIR =
            registerBlock("chair", () -> new Block(Block.Properties.of(Material.WOOD)
                    .strength(1.5f, 2.5f).sound(SoundType.WOOD)));

    public static final RegistryObject<Block> BLACKBOARD =
            registerBlock("blackboard", () -> new Block(Block.Properties.of(Material.STONE)
                    .strength(3.0f, 6.0f).sound(SoundType.STONE)));

    public static final RegistryObject<Block> LOCKER =
            registerBlock("locker", () -> new Block(Block.Properties.of(Material.METAL)
                    .strength(4.0f, 8.0f).sound(SoundType.METAL)));

    public static final RegistryObject<Block> SCHOOL_FLOOR =
            registerBlock("school_floor", () -> new Block(Block.Properties.of(Material.STONE)
                    .strength(1.5f, 6.0f).sound(SoundType.STONE)));

    public static final RegistryObject<Block> SCHOOL_WALL =
            registerBlock("school_wall", () -> new Block(Block.Properties.of(Material.STONE)
                    .strength(2.0f, 10.0f).sound(SoundType.STONE)));

    // === 创业设施方块 ===
    public static final RegistryObject<Block> STARTUP_WORKBENCH =
            registerBlock("startup_workbench", () -> new StartupWorkbenchBlock(Block.Properties.of(Material.WOOD)
                    .strength(2.5f, 4.0f).sound(SoundType.WOOD)));

    // 创业工坊 TileEntity (必须在STARTUP_WORKBENCH之后)
    public static final RegistryObject<TileEntityType<StartupWorkbenchTileEntity>> STARTUP_WORKBENCH_TE =
            TILE_ENTITIES.register("startup_workbench", () -> TileEntityType.Builder.of(
                StartupWorkbenchTileEntity::create, STARTUP_WORKBENCH.get()).build(null));

    // 创业工坊 ContainerType
    public static final RegistryObject<ContainerType<StartupWorkbenchContainer>> STARTUP_WORKBENCH_CONTAINER =
            CONTAINERS.register("startup_workbench", StartupWorkbenchContainer::createContainerType);

    public static final RegistryObject<Block> MARKET_STALL =
            registerBlock("market_stall", () -> new MarketStallBlock(Block.Properties.of(Material.WOOD)
                    .strength(1.5f, 3.0f).sound(SoundType.WOOD)));

    // 市场摊位 TileEntity (必须在MARKET_STALL之后)
    public static final RegistryObject<TileEntityType<MarketStallTileEntity>> MARKET_STALL_TE =
            TILE_ENTITIES.register("market_stall", () -> TileEntityType.Builder.of(
                MarketStallTileEntity::create, MARKET_STALL.get()).build(null));

    // 市场摊位 ContainerType
    public static final RegistryObject<ContainerType<MarketStallContainer>> MARKET_STALL_CONTAINER =
            CONTAINERS.register("market_stall", MarketStallContainer::createContainerType);

    public static final RegistryObject<Block> BANK_COUNTER =
            registerBlock("bank_counter", () -> new BankCounterBlock(Block.Properties.of(Material.STONE)
                    .strength(3.5f, 10.0f).sound(SoundType.STONE)));

    // 银行柜台 TileEntity (必须在BANK_COUNTER之后)
    public static final RegistryObject<TileEntityType<BankCounterTileEntity>> BANK_COUNTER_TE =
            TILE_ENTITIES.register("bank_counter", () -> TileEntityType.Builder.of(
                BankCounterTileEntity::create, BANK_COUNTER.get()).build(null));

    // 银行柜台 ContainerType
    public static final RegistryObject<ContainerType<BankCounterContainer>> BANK_COUNTER_CONTAINER =
            CONTAINERS.register("bank_counter", BankCounterContainer::createContainerType);

    public static final RegistryObject<Block> COFFEE_MACHINE =
            registerBlock("coffee_machine", () -> new CoffeeMachineBlock(Block.Properties.of(Material.METAL)
                    .strength(2.0f, 6.0f).sound(SoundType.METAL)));

    // 咖啡机 TileEntity (必须在COFFEE_MACHINE之后)
    public static final RegistryObject<TileEntityType<CoffeeMachineTileEntity>> COFFEE_MACHINE_TE =
            TILE_ENTITIES.register("coffee_machine", () -> TileEntityType.Builder.of(
                CoffeeMachineTileEntity::create, COFFEE_MACHINE.get()).build(null));

    // 咖啡机 ContainerType
    public static final RegistryObject<ContainerType<CoffeeMachineContainer>> COFFEE_MACHINE_CONTAINER =
            CONTAINERS.register("coffee_machine", CoffeeMachineContainer::createContainerType);

    // === 第二批GUI方块 ===
    public static final RegistryObject<Block> INVEST_CENTER =
            registerBlock("invest_center", () -> new InvestCenterBlock(Block.Properties.of(Material.STONE)
                    .strength(3.0f, 8.0f).sound(SoundType.STONE)));
    public static final RegistryObject<TileEntityType<InvestCenterTileEntity>> INVEST_CENTER_TE =
            TILE_ENTITIES.register("invest_center", () -> TileEntityType.Builder.of(
                InvestCenterTileEntity::create, INVEST_CENTER.get()).build(null));
    public static final RegistryObject<ContainerType<InvestCenterContainer>> INVEST_CENTER_CONTAINER =
            CONTAINERS.register("invest_center", InvestCenterContainer::createContainerType);

    public static final RegistryObject<Block> RESEARCH_LAB =
            registerBlock("research_lab", () -> new ResearchLabBlock(Block.Properties.of(Material.STONE)
                    .strength(3.5f, 10.0f).sound(SoundType.STONE)));
    public static final RegistryObject<TileEntityType<ResearchLabTileEntity>> RESEARCH_LAB_TE =
            TILE_ENTITIES.register("research_lab", () -> TileEntityType.Builder.of(
                ResearchLabTileEntity::create, RESEARCH_LAB.get()).build(null));
    public static final RegistryObject<ContainerType<ResearchLabContainer>> RESEARCH_LAB_CONTAINER =
            CONTAINERS.register("research_lab", ResearchLabContainer::createContainerType);

    public static final RegistryObject<Block> EMPLOYEE_CENTER =
            registerBlock("employee_center", () -> new EmployeeCenterBlock(Block.Properties.of(Material.WOOD)
                    .strength(2.5f, 4.0f).sound(SoundType.WOOD)));
    public static final RegistryObject<TileEntityType<EmployeeCenterTileEntity>> EMPLOYEE_CENTER_TE =
            TILE_ENTITIES.register("employee_center", () -> TileEntityType.Builder.of(
                EmployeeCenterTileEntity::create, EMPLOYEE_CENTER.get()).build(null));
    public static final RegistryObject<ContainerType<EmployeeCenterContainer>> EMPLOYEE_CENTER_CONTAINER =
            CONTAINERS.register("employee_center", EmployeeCenterContainer::createContainerType);

    public static final RegistryObject<Block> INCUBATOR =
            registerBlock("incubator", () -> new IncubatorBlock(Block.Properties.of(Material.WOOD)
                    .strength(2.0f, 3.5f).sound(SoundType.WOOD)));
    public static final RegistryObject<TileEntityType<IncubatorTileEntity>> INCUBATOR_TE =
            TILE_ENTITIES.register("incubator", () -> TileEntityType.Builder.of(
                IncubatorTileEntity::create, INCUBATOR.get()).build(null));
    public static final RegistryObject<ContainerType<IncubatorContainer>> INCUBATOR_CONTAINER =
            CONTAINERS.register("incubator", IncubatorContainer::createContainerType);

    public static final RegistryObject<Block> STOCK_TERMINAL =
            registerBlock("stock_terminal", () -> new StockTerminalBlock(Block.Properties.of(Material.METAL)
                    .strength(3.0f, 6.0f).sound(SoundType.METAL)));
    public static final RegistryObject<TileEntityType<StockTerminalTileEntity>> STOCK_TERMINAL_TE =
            TILE_ENTITIES.register("stock_terminal", () -> TileEntityType.Builder.of(
                StockTerminalTileEntity::create, STOCK_TERMINAL.get()).build(null));
    public static final RegistryObject<ContainerType<StockTerminalContainer>> STOCK_TERMINAL_CONTAINER =
            CONTAINERS.register("stock_terminal", StockTerminalContainer::createContainerType);

    // === 第三批GUI方块 ===
    public static final RegistryObject<Block> TRAINING_CENTER =
            registerBlock("training_center", () -> new TrainingCenterBlock(Block.Properties.of(Material.STONE)
                    .strength(3.0f, 8.0f).sound(SoundType.STONE)));
    public static final RegistryObject<TileEntityType<TrainingCenterTileEntity>> TRAINING_CENTER_TE =
            TILE_ENTITIES.register("training_center", () -> TileEntityType.Builder.of(
                TrainingCenterTileEntity::create, TRAINING_CENTER.get()).build(null));
    public static final RegistryObject<ContainerType<TrainingCenterContainer>> TRAINING_CENTER_CONTAINER =
            CONTAINERS.register("training_center", TrainingCenterContainer::createContainerType);

    public static final RegistryObject<Block> TAX_OFFICE =
            registerBlock("tax_office", () -> new TaxOfficeBlock(Block.Properties.of(Material.STONE)
                    .strength(3.5f, 10.0f).sound(SoundType.STONE)));
    public static final RegistryObject<TileEntityType<TaxOfficeTileEntity>> TAX_OFFICE_TE =
            TILE_ENTITIES.register("tax_office", () -> TileEntityType.Builder.of(
                TaxOfficeTileEntity::create, TAX_OFFICE.get()).build(null));
    public static final RegistryObject<ContainerType<TaxOfficeContainer>> TAX_OFFICE_CONTAINER =
            CONTAINERS.register("tax_office", TaxOfficeContainer::createContainerType);

    public static final RegistryObject<Block> ACHIEVEMENT_HALL =
            registerBlock("achievement_hall", () -> new AchievementHallBlock(Block.Properties.of(Material.WOOD)
                    .strength(2.5f, 4.0f).sound(SoundType.WOOD)));
    public static final RegistryObject<TileEntityType<AchievementHallTileEntity>> ACHIEVEMENT_HALL_TE =
            TILE_ENTITIES.register("achievement_hall", () -> TileEntityType.Builder.of(
                AchievementHallTileEntity::create, ACHIEVEMENT_HALL.get()).build(null));
    public static final RegistryObject<ContainerType<AchievementHallContainer>> ACHIEVEMENT_HALL_CONTAINER =
            CONTAINERS.register("achievement_hall", AchievementHallContainer::createContainerType);

    public static final RegistryObject<Block> COMPETITION_ARENA =
            registerBlock("competition_arena", () -> new CompetitionArenaBlock(Block.Properties.of(Material.STONE)
                    .strength(3.5f, 10.0f).sound(SoundType.STONE)));
    public static final RegistryObject<TileEntityType<CompetitionArenaTileEntity>> COMPETITION_ARENA_TE =
            TILE_ENTITIES.register("competition_arena", () -> TileEntityType.Builder.of(
                CompetitionArenaTileEntity::create, COMPETITION_ARENA.get()).build(null));
    public static final RegistryObject<ContainerType<CompetitionArenaContainer>> COMPETITION_ARENA_CONTAINER =
            CONTAINERS.register("competition_arena", CompetitionArenaContainer::createContainerType);

    public static final RegistryObject<Block> ALLIANCE_HQ =
            registerBlock("alliance_hq", () -> new AllianceHQBlock(Block.Properties.of(Material.WOOD)
                    .strength(2.5f, 4.0f).sound(SoundType.WOOD)));
    public static final RegistryObject<TileEntityType<AllianceHQTileEntity>> ALLIANCE_HQ_TE =
            TILE_ENTITIES.register("alliance_hq", () -> TileEntityType.Builder.of(
                AllianceHQTileEntity::create, ALLIANCE_HQ.get()).build(null));
    public static final RegistryObject<ContainerType<AllianceHQContainer>> ALLIANCE_HQ_CONTAINER =
            CONTAINERS.register("alliance_hq", AllianceHQContainer::createContainerType);

    // === 第四批GUI方块 ===
    public static final RegistryObject<Block> ESG_CENTER = registerBlock("esg_center", () -> new ESGCenterBlock(Block.Properties.of(Material.STONE).strength(3.0f, 8.0f).sound(SoundType.STONE)));
    public static final RegistryObject<TileEntityType<ESGCenterTileEntity>> ESG_CENTER_TE = TILE_ENTITIES.register("esg_center", () -> TileEntityType.Builder.of(ESGCenterTileEntity::create, ESG_CENTER.get()).build(null));
    public static final RegistryObject<ContainerType<ESGCenterContainer>> ESG_CENTER_CONTAINER = CONTAINERS.register("esg_center", ESGCenterContainer::createContainerType);

    public static final RegistryObject<Block> CARBON_EXCHANGE = registerBlock("carbon_exchange", () -> new CarbonExchangeBlock(Block.Properties.of(Material.STONE).strength(3.5f, 10.0f).sound(SoundType.STONE)));
    public static final RegistryObject<TileEntityType<CarbonExchangeTileEntity>> CARBON_EXCHANGE_TE = TILE_ENTITIES.register("carbon_exchange", () -> TileEntityType.Builder.of(CarbonExchangeTileEntity::create, CARBON_EXCHANGE.get()).build(null));
    public static final RegistryObject<ContainerType<CarbonExchangeContainer>> CARBON_EXCHANGE_CONTAINER = CONTAINERS.register("carbon_exchange", CarbonExchangeContainer::createContainerType);

    public static final RegistryObject<Block> DIGITAL_CENTER = registerBlock("digital_center", () -> new DigitalCenterBlock(Block.Properties.of(Material.METAL).strength(3.0f, 6.0f).sound(SoundType.METAL)));
    public static final RegistryObject<TileEntityType<DigitalCenterTileEntity>> DIGITAL_CENTER_TE = TILE_ENTITIES.register("digital_center", () -> TileEntityType.Builder.of(DigitalCenterTileEntity::create, DIGITAL_CENTER.get()).build(null));
    public static final RegistryObject<ContainerType<DigitalCenterContainer>> DIGITAL_CENTER_CONTAINER = CONTAINERS.register("digital_center", DigitalCenterContainer::createContainerType);

    public static final RegistryObject<Block> BRAND_WORKSHOP = registerBlock("brand_workshop", () -> new BrandWorkshopBlock(Block.Properties.of(Material.WOOD).strength(2.5f, 4.0f).sound(SoundType.WOOD)));
    public static final RegistryObject<TileEntityType<BrandWorkshopTileEntity>> BRAND_WORKSHOP_TE = TILE_ENTITIES.register("brand_workshop", () -> TileEntityType.Builder.of(BrandWorkshopTileEntity::create, BRAND_WORKSHOP.get()).build(null));
    public static final RegistryObject<ContainerType<BrandWorkshopContainer>> BRAND_WORKSHOP_CONTAINER = CONTAINERS.register("brand_workshop", BrandWorkshopContainer::createContainerType);

    public static final RegistryObject<Block> DATA_CENTER = registerBlock("data_center", () -> new DataCenterBlock(Block.Properties.of(Material.METAL).strength(3.0f, 6.0f).sound(SoundType.METAL)));
    public static final RegistryObject<TileEntityType<DataCenterTileEntity>> DATA_CENTER_TE = TILE_ENTITIES.register("data_center", () -> TileEntityType.Builder.of(DataCenterTileEntity::create, DATA_CENTER.get()).build(null));
    public static final RegistryObject<ContainerType<DataCenterContainer>> DATA_CENTER_CONTAINER = CONTAINERS.register("data_center", DataCenterContainer::createContainerType);

    public static final RegistryObject<Block> FUTURE_FACTORY = registerBlock("future_factory", () -> new FutureFactoryBlock(Block.Properties.of(Material.METAL).strength(3.5f, 8.0f).sound(SoundType.METAL)));
    public static final RegistryObject<TileEntityType<FutureFactoryTileEntity>> FUTURE_FACTORY_TE = TILE_ENTITIES.register("future_factory", () -> TileEntityType.Builder.of(FutureFactoryTileEntity::create, FUTURE_FACTORY.get()).build(null));
    public static final RegistryObject<ContainerType<FutureFactoryContainer>> FUTURE_FACTORY_CONTAINER = CONTAINERS.register("future_factory", FutureFactoryContainer::createContainerType);

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        ITEMS.register(name, () -> new BlockItem(toReturn.get(),
                new Item.Properties().tab(ItemGroup.TAB_BUILDING_BLOCKS)));
        return toReturn;
    }
}
