////////////////////////////////////////////////////////////////////////////////
// This file is subject to the terms and conditions defined in the             /
// file '.github/LICENSE.md', which is part of this source code package.       /
////////////////////////////////////////////////////////////////////////////////

package de.sanandrew.mods.sanlib;

import de.sanandrew.mods.sanlib.lib.util.config.Category;
import de.sanandrew.mods.sanlib.lib.util.config.ConfigUtils;
import de.sanandrew.mods.sanlib.lib.util.config.Value;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = Constants.ID)
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
        @Value(comment = "Whether or not to allow emissive textures on supported models. This is managed by Optifine instead, if installed and depends on the resource pack.", reqMcRestart = true)
        public static boolean enableEmissiveTextures = true;
    }

    static void initConfiguration(FMLPreInitializationEvent event) {
        config = ConfigUtils.loadConfigFile(event.getSuggestedConfigurationFile(), CONFIG_VER, Constants.NAME);//new Configuration(event.getSuggestedConfigurationFile(), "1.0.0", true);
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
        if( eventArgs.getModID().equals(Constants.ID) ) {
            synchronize();
        }
    }
}
