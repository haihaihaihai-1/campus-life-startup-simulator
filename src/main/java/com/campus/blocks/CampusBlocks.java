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

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        ITEMS.register(name, () -> new BlockItem(toReturn.get(),
                new Item.Properties().tab(ItemGroup.TAB_BUILDING_BLOCKS)));
        return toReturn;
    }
}
