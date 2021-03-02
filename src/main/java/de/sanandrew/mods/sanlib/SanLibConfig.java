////////////////////////////////////////////////////////////////////////////////
// This file is subject to the terms and conditions defined in the             /
// file '.github/LICENSE.md', which is part of this source code package.       /
////////////////////////////////////////////////////////////////////////////////

package de.sanandrew.mods.sanlib;

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
        public static ForgeConfigSpec.ConfigValue<Integer> glSecondaryTextureUnit;
        public static ForgeConfigSpec.ConfigValue<Boolean> useShaders;
        public static ForgeConfigSpec.ConfigValue<Boolean> allowCustomSanModel;
        public static ForgeConfigSpec.ConfigValue<Boolean> setSplashTitle;
        public static ForgeConfigSpec.ConfigValue<Boolean> enableEmissiveTextures;

        Client(ForgeConfigSpec.Builder builder) {
            builder.comment("Client-only configuration")
                   .push("client");
            glSecondaryTextureUnit = builder.comment("The GL Texture Unit to use for the secondary sampler passed in to some of the shaders. DO NOT TOUCH THIS IF YOU DON'T KNOW WHAT YOU'RE DOING")
                                            .define("glSecondaryTextureUnit", 7);
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
