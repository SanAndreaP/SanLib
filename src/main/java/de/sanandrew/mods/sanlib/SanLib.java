////////////////////////////////////////////////////////////////////////////////
// This file is subject to the terms and conditions defined in the             /
// file '.github/LICENSE.md', which is part of this source code package.       /
////////////////////////////////////////////////////////////////////////////////

package de.sanandrew.mods.sanlib;

import de.sanandrew.mods.sanlib.lib.network.MessageHandler;
import de.sanandrew.mods.sanlib.network.MessageReloadModels;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Constants.ID)
public class SanLib
{
    public static final Logger LOG = LogManager.getLogger(Constants.ID);

    public static final MessageHandler NETWORK = new MessageHandler(Constants.ID, "1.0.0");

    public SanLib() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, SanLibConfig.CLIENT_SPEC);
    }

    private void setup(FMLCommonSetupEvent event) {
        NETWORK.registerMessage(0, MessageReloadModels.class, MessageReloadModels::new);
    }
}
