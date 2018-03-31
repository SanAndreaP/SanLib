/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.sanlib;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public final class SLibConfiguration
{
    private static Configuration config;

    public static int glSecondaryTextureUnit = 7;
    public static boolean useShaders = true;
    public static boolean allowCustomSanModel = true;

    public static void initConfiguration(FMLPreInitializationEvent event) {
        config = new Configuration(event.getSuggestedConfigurationFile(), "1.0.0", true);
        MinecraftForge.EVENT_BUS.register(new SLibConfiguration());
        syncConfig();
    }

    public static void syncConfig() {
        String desc;

        desc = "The GL Texture Unit to use for the secondary sampler passed in to some of the shaders. DO NOT TOUCH THIS IF YOU DON'T KNOW WHAT YOU'RE DOING";
        glSecondaryTextureUnit = config.getInt("glSecondaryTextureUnit", Configuration.CATEGORY_CLIENT, glSecondaryTextureUnit, Integer.MIN_VALUE, Integer.MAX_VALUE, desc);

        desc = "Whether or not to use shaders. When disabled, some fancier rendering won't work. Only disable if there's incompatibilities with another mod!";
        useShaders = config.getBoolean("useShaders", Configuration.CATEGORY_CLIENT, useShaders, desc);

        desc = "Whether or not to allow a custom player model for the mod author to be rendered. Does not affect anything else. Turn off if you have issues when looking at the autor.";
        allowCustomSanModel = config.getBoolean("allowCustomSanModel", Configuration.CATEGORY_CLIENT, allowCustomSanModel, desc);


        if( config.hasChanged() ) {
            config.save();
        }
    }

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent eventArgs) {
        if( eventArgs.getModID().equals(SanLib.ID) ) {
            syncConfig();
        }
    }
}
