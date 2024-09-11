/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2016-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.sanlib;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@SuppressWarnings("unused")
@EventBusSubscriber(modid = Constants.ID, bus = EventBusSubscriber.Bus.MOD)
public final class SanLibConfig
{
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.IntValue     GL_SEC_TEX_UNIT     = BUILDER.comment("The GL Texture Unit to use for the secondary sampler passed in to some of the shaders. DO NOT TOUCH THIS IF YOU DON'T KNOW WHAT YOU'RE DOING")
                                                                                 .defineInRange("glSecondaryTextureUnit", 7, 0, 255);
    private static final ModConfigSpec.BooleanValue USE_SHADERS         = BUILDER.comment("Whether or not to use shaders. When disabled, some fancier rendering won't work. Only disable if there's incompatibilities with another mod!")
                                                                                 .define("useShaders", true);
    private static final ModConfigSpec.BooleanValue SET_SPLASH_TITLE    = BUILDER.comment("Whether or not to allow splash text to be written into the window title (requires game restart to take effect).")
                                                                                 .define("setSplashTitle", true);

    static final ModConfigSpec SPEC = BUILDER.build();

    public static int     glSecondaryTextureUnit;
    public static boolean useShaders;
    public static boolean setSplashTitle;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        glSecondaryTextureUnit = GL_SEC_TEX_UNIT.get();
        useShaders = USE_SHADERS.get();
        setSplashTitle = SET_SPLASH_TITLE.get();
    }
}
