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
            registerBlock("market_stall", () -> new Block(Block.Properties.of(Material.WOOD)
                    .strength(1.5f, 3.0f).sound(SoundType.WOOD)));

    public static final RegistryObject<Block> BANK_COUNTER =
            registerBlock("bank_counter", () -> new Block(Block.Properties.of(Material.STONE)
                    .strength(3.5f, 10.0f).sound(SoundType.STONE)));

    public static final RegistryObject<Block> COFFEE_MACHINE =
            registerBlock("coffee_machine", () -> new Block(Block.Properties.of(Material.METAL)
                    .strength(2.0f, 6.0f).sound(SoundType.METAL)));

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        ITEMS.register(name, () -> new BlockItem(toReturn.get(),
                new Item.Properties().tab(ItemGroup.TAB_BUILDING_BLOCKS)));
        return toReturn;
    }
}
