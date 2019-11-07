/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.sanlib;

import de.sanandrew.mods.sanlib.lib.util.config.Category;
import de.sanandrew.mods.sanlib.lib.util.config.ConfigUtils;
import de.sanandrew.mods.sanlib.lib.util.config.Value;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = SanLib.ID)
public final class SLibConfig
{
    private static final String CONFIG_VER = "2.0";

    private static Configuration config;

    @Category(value = Configuration.CATEGORY_CLIENT, comment = "Client-only configuration")
    public static final class Client
    {
        @Value(comment = "The GL Texture Unit to use for the secondary sampler passed in to some of the shaders. DO NOT TOUCH THIS IF YOU DON'T KNOW WHAT YOU'RE DOING")
        public static int glSecondaryTextureUnit = 7;
        @Value(comment = "Whether or not to use shaders. When disabled, some fancier rendering won't work. Only disable if there's incompatibilities with another mod!")
        public static boolean useShaders = true;
        @Value(comment = "Whether or not to allow a custom player model for the mod author to be rendered. Does not affect anything else. Turn off if you have issues when looking at the author.")
        public static boolean allowCustomSanModel = true;
        @Value(comment = "Whether or not to allow splash text to be written into the window title.", reqMcRestart = true)
        public static boolean setSplashTitle = true;
    }

    static void initConfiguration(FMLPreInitializationEvent event) {
        config = ConfigUtils.loadConfigFile(event.getSuggestedConfigurationFile(), CONFIG_VER, SanLib.NAME);//new Configuration(event.getSuggestedConfigurationFile(), "1.0.0", true);
        synchronize();
    }

    private static void synchronize() {
        ConfigUtils.loadCategories(config, SLibConfig.class);

        if( config.hasChanged() ) {
            config.save();
        }
    }

    @SubscribeEvent
    public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent eventArgs) {
        if( eventArgs.getModID().equals(SanLib.ID) ) {
            synchronize();
        }
    }
}
