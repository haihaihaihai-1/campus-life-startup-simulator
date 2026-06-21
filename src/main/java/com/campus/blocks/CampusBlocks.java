package com.campus.blocks;

import com.campus.CampusLife;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class CampusBlocks {

    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, CampusLife.MOD_ID);

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, CampusLife.MOD_ID);

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

    public static final RegistryObject<Block> MARKET_STALL =
            registerBlock("market_stall", () -> new Block(Block.Properties.of(Material.WOOD)
                    .strength(1.5f, 3.0f).sound(SoundType.WOOD)));

    public static final RegistryObject<Block> BANK_COUNTER =
            registerBlock("bank_counter", () -> new Block(Block.Properties.of(Material.STONE)
                    .strength(3.5f, 10.0f).sound(SoundType.STONE)));

    public static final RegistryObject<Block> COFFEE_MACHINE =
            registerBlock("coffee_machine", () -> new Block(Block.Properties.of(Material.METAL)
                    .strength(2.0f, 6.0f).sound(SoundType.METAL)));

    // === GUI 交互方块（B 方案 + 扩展）===
    public static final RegistryObject<Block> INCUBATOR =
            registerBlock("incubator", () -> new IncubatorBlock(Block.Properties.of(Material.METAL)
                    .strength(3.0f, 8.0f).sound(SoundType.METAL).lightLevel(s -> 8)));

    public static final RegistryObject<Block> AUCTION_BLOCK =
            registerBlock("auction_block", () -> new AuctionBlock(Block.Properties.of(Material.WOOD)
                    .strength(3.0f, 5.0f).sound(SoundType.WOOD)));

    public static final RegistryObject<Block> CRYPTO_MINER =
            registerBlock("crypto_miner", () -> new CryptoMinerBlock(Block.Properties.of(Material.METAL)
                    .strength(2.5f, 6.0f).sound(SoundType.METAL).lightLevel(s -> 6)));

    // === v16: 5 个商业系统 GUI 方块 ===
    public static final RegistryObject<Block> CONTRACT_DESK =
            registerBlock("contract_desk", () -> new ContractDeskBlock(Block.Properties.of(Material.WOOD)
                    .strength(2.5f, 4.0f).sound(SoundType.WOOD)));

    public static final RegistryObject<Block> ESG_DISPLAY =
            registerBlock("esg_display", () -> new ESGDisplayBlock(Block.Properties.of(Material.METAL)
                    .strength(2.0f, 5.0f).sound(SoundType.METAL).lightLevel(s -> 7)));

    public static final RegistryObject<Block> IPO_BELL =
            registerBlock("ipo_bell", () -> new IPOBellBlock(Block.Properties.of(Material.METAL)
                    .strength(3.0f, 8.0f).sound(SoundType.METAL)));

    public static final RegistryObject<Block> VC_TABLE =
            registerBlock("vc_table", () -> new VCTableBlock(Block.Properties.of(Material.METAL)
                    .strength(3.0f, 6.0f).sound(SoundType.METAL)));

    public static final RegistryObject<Block> STOCK_TICKER =
            registerBlock("stock_ticker", () -> new StockTickerBlock(Block.Properties.of(Material.METAL)
                    .strength(2.0f, 5.0f).sound(SoundType.METAL).lightLevel(s -> 9)));

    // === v17: 8 个新 GUI 方块 ===
    public static final RegistryObject<Block> LOAN_ATM =
            registerBlock("loan_atm", () -> new LoanAtmBlock(Block.Properties.of(Material.METAL)
                    .strength(3.0f, 8.0f).sound(SoundType.METAL).lightLevel(s -> 7)));

    public static final RegistryObject<Block> PATENT_CABINET =
            registerBlock("patent_cabinet", () -> new PatentCabinetBlock(Block.Properties.of(Material.WOOD)
                    .strength(2.5f, 4.0f).sound(SoundType.WOOD)));

    public static final RegistryObject<Block> MERGER_TABLE =
            registerBlock("merger_table", () -> new MergerTableBlock(Block.Properties.of(Material.STONE)
                    .strength(3.5f, 8.0f).sound(SoundType.STONE)));

    public static final RegistryObject<Block> AD_BILLBOARD =
            registerBlock("ad_billboard", () -> new AdBillboardBlock(Block.Properties.of(Material.METAL)
                    .strength(2.0f, 4.0f).sound(SoundType.METAL).lightLevel(s -> 10)));

    public static final RegistryObject<Block> REALTY_KIOSK =
            registerBlock("realty_kiosk", () -> new RealtyKioskBlock(Block.Properties.of(Material.WOOD)
                    .strength(2.5f, 4.0f).sound(SoundType.WOOD)));

    public static final RegistryObject<Block> HR_DESK =
            registerBlock("hr_desk", () -> new HRDeskBlock(Block.Properties.of(Material.WOOD)
                    .strength(2.5f, 4.0f).sound(SoundType.WOOD)));

    public static final RegistryObject<Block> META_PORTAL =
            registerBlock("meta_portal", () -> new MetaPortalBlock(Block.Properties.of(Material.METAL)
                    .strength(3.0f, 10.0f).sound(SoundType.METAL).lightLevel(s -> 14)));

    public static final RegistryObject<Block> INSURANCE_KIOSK =
            registerBlock("insurance_kiosk", () -> new InsuranceKioskBlock(Block.Properties.of(Material.METAL)
                    .strength(2.5f, 5.0f).sound(SoundType.METAL).lightLevel(s -> 6)));

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        ITEMS.register(name, () -> new BlockItem(toReturn.get(),
                new Item.Properties().tab(ItemGroup.TAB_BUILDING_BLOCKS)));
        return toReturn;
    }
}
