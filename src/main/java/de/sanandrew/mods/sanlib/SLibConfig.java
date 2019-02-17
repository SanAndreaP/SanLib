/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.sanlib;

import net.minecraftforge.fml.common.Mod;

//TODO: use ForgeConfig
@Mod.EventBusSubscriber(modid = SanLib.ID)
public final class SLibConfig
{
    private static final String CONFIG_VER = "2.0";

//    private static Configuration config;

    public static final class Client
    {
        public static int glSecondaryTextureUnit = 7;
        public static boolean useShaders = true;
        public static boolean allowCustomSanModel = true;
        public static boolean setSplashTitle = true;
    }

//    static void initConfiguration(FMLPreInitializationEvent event) {
//        config = ConfigUtils.loadConfigFile(event.getSuggestedConfigurationFile(), CONFIG_VER, SanLib.NAME);//new Configuration(event.getSuggestedConfigurationFile(), "1.0.0", true);
//        synchronize();
//    }
//
//    private static void synchronize() {
//        ConfigUtils.loadCategories(config, SLibConfig.class);
//
//        if( config.hasChanged() ) {
//            config.save();
//        }
//    }
//
//    @SubscribeEvent
//    public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent eventArgs) {
//        if( eventArgs.getModID().equals(SanLib.ID) ) {
//            synchronize();
//        }
//    }
}
