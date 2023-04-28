/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2016-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.sanlib;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public final class SanLibConfig
{
    public static final Client          CLIENT;
    static final        ForgeConfigSpec CLIENT_SPEC;
    static {
        final Pair<Client, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Client::new);
        CLIENT_SPEC = specPair.getRight();
        CLIENT = specPair.getLeft();
    }

    public static final class Client
    {
        public static ForgeConfigSpec.IntValue glSecondaryTextureUnit;
        public static ForgeConfigSpec.BooleanValue useShaders;
        public static ForgeConfigSpec.BooleanValue allowCustomSanModel;
        public static ForgeConfigSpec.BooleanValue setSplashTitle;
        public static ForgeConfigSpec.BooleanValue enableEmissiveTextures;

        Client(ForgeConfigSpec.Builder builder) {
            builder.comment("Client-only configuration")
                   .push("client");
            glSecondaryTextureUnit = builder.comment("The GL Texture Unit to use for the secondary sampler passed in to some of the shaders. DO NOT TOUCH THIS IF YOU DON'T KNOW WHAT YOU'RE DOING")
                                            .defineInRange("glSecondaryTextureUnit", 7, 0, 255);
            useShaders = builder.comment("Whether or not to use shaders. When disabled, some fancier rendering won't work. Only disable if there's incompatibilities with another mod!")
                                .define("useShaders", true);
            allowCustomSanModel = builder.comment("Whether or not to allow a custom player model for the mod author to be rendered. Does not affect anything else. Turn off if you have issues when looking at the author.")
                                         .define("allowCustomSanModel", true);
            setSplashTitle = builder.comment("Whether or not to allow splash text to be written into the window title (requires game restart to take effect).")
                                    .define("setSplashTitle", true);
            enableEmissiveTextures = builder.comment("Whether or not to allow emissive textures on supported models (requires game restart to take effect). This is managed by Optifine instead, if installed and depends on the resource pack.")
                                            .define("enableEmissiveTextures", true);
        }
    }
}
