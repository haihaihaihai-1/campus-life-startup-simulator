package com.campus.items;

import com.campus.CampusLife;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class CampusItems {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, CampusLife.MOD_ID);

    // === 校园基础物品 ===
    public static final RegistryObject<Item> TEXTBOOK =
            ITEMS.register("textbook", () -> new Item(new Item.Properties()
                    .tab(ItemGroup.TAB_MISC).stacksTo(16)));

    public static final RegistryObject<Item> PENCIL =
            ITEMS.register("pencil", () -> new Item(new Item.Properties()
                    .tab(ItemGroup.TAB_MISC).stacksTo(64)));

    public static final RegistryObject<Item> ERASER =
            ITEMS.register("eraser", () -> new Item(new Item.Properties()
                    .tab(ItemGroup.TAB_MISC).stacksTo(64)));

    public static final RegistryObject<Item> REPORT_CARD =
            ITEMS.register("report_card", () -> new Item(new Item.Properties()
                    .tab(ItemGroup.TAB_MISC).stacksTo(1)));

    public static final RegistryObject<Item> EXAM_PAPER =
            ITEMS.register("exam_paper", () -> new Item(new Item.Properties()
                    .tab(ItemGroup.TAB_MISC).stacksTo(16)));

    public static final RegistryObject<Item> RULER =
            ITEMS.register("ruler", () -> new Item(new Item.Properties()
                    .tab(ItemGroup.TAB_COMBAT).defaultDurability(100)));

    public static final RegistryObject<Item> CAFETERIA_BREAD =
            ITEMS.register("cafeteria_bread", () -> new Item(new Item.Properties()
                    .tab(ItemGroup.TAB_FOOD)
                    .food(new Food.Builder().nutrition(6).saturationMod(0.7f).build())));

    public static final RegistryObject<Item> MILK_CARTON =
            ITEMS.register("milk_carton", () -> new Item(new Item.Properties()
                    .tab(ItemGroup.TAB_FOOD)
                    .food(new Food.Builder().nutrition(3).saturationMod(0.5f)
                            .effect(() -> new EffectInstance(Effects.REGENERATION, 100, 0), 1.0f).build())));

    public static final RegistryObject<Item> SCHOOL_LUNCH =
            ITEMS.register("school_lunch", () -> new Item(new Item.Properties()
                    .tab(ItemGroup.TAB_FOOD)
                    .food(new Food.Builder().nutrition(10).saturationMod(0.9f)
                            .effect(() -> new EffectInstance(Effects.MOVEMENT_SPEED, 600, 0), 1.0f).build())));

    public static final RegistryObject<Item> SCHOOL_BADGE =
            ITEMS.register("school_badge", () -> new Item(new Item.Properties()
                    .tab(ItemGroup.TAB_MISC).stacksTo(1)));

    // === 创业产品物品 ===
    public static final RegistryObject<Item> COFFEE_CUP =
            ITEMS.register("coffee_cup", () -> new Item(new Item.Properties()
                    .tab(ItemGroup.TAB_FOOD)
                    .food(new Food.Builder().nutrition(2).saturationMod(0.3f)
                            .effect(() -> new EffectInstance(Effects.MOVEMENT_SPEED, 1200, 1), 1.0f).build())
                    .stacksTo(16)));

    public static final RegistryObject<Item> HANDMADE_CRAFT =
            ITEMS.register("handmade_craft", () -> new Item(new Item.Properties()
                    .tab(ItemGroup.TAB_MISC).stacksTo(64)));

    public static final RegistryObject<Item> TECH_GADGET =
            ITEMS.register("tech_gadget", () -> new Item(new Item.Properties()
                    .tab(ItemGroup.TAB_MISC).stacksTo(16)));

    public static final RegistryObject<Item> BUSINESS_PLAN =
            ITEMS.register("business_plan", () -> new Item(new Item.Properties()
                    .tab(ItemGroup.TAB_MISC).stacksTo(1)));

    // === 创业原材料 ===
    public static final RegistryObject<Item> RAW_MATERIAL =
            ITEMS.register("raw_material", () -> new Item(new Item.Properties()
                    .tab(ItemGroup.TAB_MISC).stacksTo(64)));

    public static final RegistryObject<Item> INNOVATION_CHIP =
            ITEMS.register("innovation_chip", () -> new Item(new Item.Properties()
                    .tab(ItemGroup.TAB_MISC).stacksTo(64)));

    // === 货币物品 ===
    public static final RegistryObject<Item> COIN =
            ITEMS.register("coin", () -> new Item(new Item.Properties()
                    .tab(ItemGroup.TAB_MISC).stacksTo(64)));

    public static final RegistryObject<Item> STARTUP_KIT =
            ITEMS.register("startup_kit", () -> new Item(new Item.Properties()
                    .tab(ItemGroup.TAB_MISC).stacksTo(1)));
}
