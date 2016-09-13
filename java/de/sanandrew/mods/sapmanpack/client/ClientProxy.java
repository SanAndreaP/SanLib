/*******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.sapmanpack.client;

import de.sanandrew.mods.sapmanpack.CommonProxy;
import de.sanandrew.mods.sapmanpack.client.event.RenderPlayerEventHandler;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy
        extends CommonProxy
{
    @Override
    public void registerRenderStuff() {
        MinecraftForge.EVENT_BUS.register(new RenderPlayerEventHandler());
    }
}
