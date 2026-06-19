package com.campus.systems;

import com.campus.CampusLife;
import com.campus.items.CampusItems;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

/**
 * 合成配方系统拼图 - 创业产品制造配方
 * 参考: Forge Recipe 注册模式
 */
@Mod.EventBusSubscriber(modid = CampusLife.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class RecipeSystem {

    public static final List<String> RECIPES = new ArrayList<>();

    @SubscribeEvent
    public static void setup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            CampusLife.LOGGER.info("Registering campus crafting recipes...");
            RECIPES.add("coffee_cup: milk_carton + wheat");
            RECIPES.add("handmade_craft: raw_material x2 + pencil");
            RECIPES.add("tech_gadget: innovation_chip x3 + redstone");
            RECIPES.add("business_plan: textbook + paper");
            RECIPES.add("startup_kit: business_plan + coin x3");
            CampusLife.LOGGER.info("Registered " + RECIPES.size() + " crafting recipes!");
        });
    }

    public static void registerShapedRecipe(String output, String[] pattern, Object... ingredients) {
        RECIPES.add(output + ": " + String.join("|", pattern));
    }

    public static List<String> getRecipes() {
        return RECIPES;
    }
}
