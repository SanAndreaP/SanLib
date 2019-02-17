/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.sanlib.sanplayermodel.client;

import de.sanandrew.mods.sanlib.sanplayermodel.CommonProxy;
import de.sanandrew.mods.sanlib.sanplayermodel.client.event.RenderPlayerEventHandler;
import de.sanandrew.mods.sanlib.sanplayermodel.client.renderer.entity.RenderSanArmorStand;
import de.sanandrew.mods.sanlib.sanplayermodel.entity.EntitySanArmorStand;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ClientProxy
        extends CommonProxy
{
    @Override
    public void registerRenderStuff() {
        MinecraftForge.EVENT_BUS.register(new RenderPlayerEventHandler());

        RenderingRegistry.registerEntityRenderingHandler(EntitySanArmorStand.class, RenderSanArmorStand::new);
    }
}
