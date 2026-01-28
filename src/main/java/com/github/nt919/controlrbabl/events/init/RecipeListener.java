package com.github.nt919.controlrbabl.events.init;

import com.github.nt919.controlrbabl.Config;
import net.mine_diver.unsafeevents.listener.EventListener;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.CraftingRecipeManager;

import net.modificationstation.stationapi.api.event.recipe.RecipeRegisterEvent;
import net.modificationstation.stationapi.api.recipe.CraftingRegistry;
import net.modificationstation.stationapi.api.util.Identifier;


import java.util.List;

public class RecipeListener {

    @EventListener
    public void registerRecipes(RecipeRegisterEvent event) {
        Identifier type = event.recipeId;

        if (type == RecipeRegisterEvent.Vanilla.CRAFTING_SHAPED.type()) {
            List<CraftingRecipe> recipes = CraftingRecipeManager.getInstance().getRecipes();


            //Removes Current Smooth Stone and Cobblestone Slab Recipes, Adds Alpha Slab Recipe
            if (Config.config.enableAlphaSlabRecipes) {
                recipes.removeIf(recipe -> {
                    ItemStack output = recipe.getOutput();
                    // Check if it's a cobblestone slab (Block.SLAB with metadata 3)
                    return output.itemId == Block.SLAB.asItem().id && output.getDamage() == 3;
                });
                recipes.removeIf(recipe -> {
                    ItemStack output = recipe.getOutput();
                    // Check if it's a cobblestone slab (Block.SLAB with metadata 3)
                    return output.itemId == Block.SLAB.asItem().id && output.getDamage() == 0;
                });

                CraftingRegistry.addShapedRecipe(new ItemStack(Block.SLAB.asItem(), 3), "   ", "   ", "XXX", 'X', Block.COBBLESTONE);

            }

            //Reworks Cobblestone Slab Recipe
            if (Config.config.enableCobbleSlabRecipe) {
                CraftingRegistry.addShapedRecipe(new ItemStack(Block.SLAB.asItem(), 3, 3), "   ", "   ", "XXX", 'X', new ItemStack(Block.SLAB.asItem(), 1, 0));
            }

            //Enables Alternative Modern Smooth Slab Recipe
            if (Config.config.enableAlpha6SlabRecipes) {
                CraftingRegistry.addShapedRecipe(new ItemStack(Block.SLAB.asItem(), 6), "   ", "   ", "XXX", 'X', Block.COBBLESTONE);
            }
        }
    }
}