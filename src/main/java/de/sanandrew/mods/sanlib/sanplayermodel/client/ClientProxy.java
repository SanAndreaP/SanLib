////////////////////////////////////////////////////////////////////////////////
// This file is subject to the terms and conditions defined in the             /
// file '.github/LICENSE.md', which is part of this source code package.       /
////////////////////////////////////////////////////////////////////////////////

package de.sanandrew.mods.sanlib.sanplayermodel.client;

import de.sanandrew.mods.sanlib.sanplayermodel.CommonProxy;
import de.sanandrew.mods.sanlib.sanplayermodel.client.event.RenderPlayerEventHandler;
import de.sanandrew.mods.sanlib.sanplayermodel.client.renderer.entity.RenderSanArmorStand;
import de.sanandrew.mods.sanlib.sanplayermodel.entity.EntitySanArmorStand;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientProxy
        extends CommonProxy
{
    @Override
    public void registerRenderStuff() {
        MinecraftForge.EVENT_BUS.register(new RenderPlayerEventHandler());

        RenderingRegistry.registerEntityRenderingHandler(EntitySanArmorStand.class, RenderSanArmorStand::new);
    }
}
