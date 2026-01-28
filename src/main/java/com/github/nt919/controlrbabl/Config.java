package com.github.nt919.controlrbabl;

import net.glasslauncher.mods.gcapi3.api.*;

public class Config {

    @ConfigRoot(value = "config", visibleName = "Alpha Slabs")
    public static ConfigFields config = new ConfigFields();

    public static class ConfigFields {

        @ConfigEntry(
                name = "Enable Alpha Stone Slab Recipe",
                description = "Restart required",
                multiplayerSynced = true
        )
        public Boolean enableAlphaSlabRecipes = true;

        @ConfigEntry(
                name = "Enable New Cobblestone Slab Recipe",
                description = "Restart required",
                multiplayerSynced = true
        )
        public Boolean enableCobbleSlabRecipe = true;

        @ConfigEntry(
                name = "Enable Alpha Six Stone Slab Recipe",
                description = "Restart required",
                multiplayerSynced = true
        )
        public Boolean enableAlpha6SlabRecipes = false;
    }
}
