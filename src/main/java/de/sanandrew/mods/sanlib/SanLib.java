////////////////////////////////////////////////////////////////////////////////
// This file is subject to the terms and conditions defined in the             /
// file '.github/LICENSE.md', which is part of this source code package.       /
////////////////////////////////////////////////////////////////////////////////

package de.sanandrew.mods.sanlib;

import de.sanandrew.mods.sanlib.lib.network.MessageHandler;
import de.sanandrew.mods.sanlib.network.MessageEntityRender;
import de.sanandrew.mods.sanlib.network.MessageReloadModels;
import de.sanandrew.mods.sanlib.recipes.BetterNBTIngredient;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
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
        IEventBus meb = FMLJavaModLoadingContext.get().getModEventBus();
        meb.addListener(this::setup);
        meb.register(this);

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, SanLibConfig.CLIENT_SPEC);
    }

    private void setup(FMLCommonSetupEvent event) {
        NETWORK.registerMessage(0, MessageReloadModels.class, MessageReloadModels::new);
        NETWORK.registerMessage(1, MessageEntityRender.class, MessageEntityRender::new);
    }

    @SubscribeEvent
    public void registerRecipeSerialziers(RegistryEvent.Register<IRecipeSerializer<?>> event) {
        CraftingHelper.register(new ResourceLocation(Constants.ID, "nbt"), BetterNBTIngredient.Serializer.INSTANCE);
    }
}
