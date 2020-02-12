////////////////////////////////////////////////////////////////////////////////
// This file is subject to the terms and conditions defined in the             /
// file '.github/LICENSE.md', which is part of this source code package.       /
////////////////////////////////////////////////////////////////////////////////

package de.sanandrew.mods.sanlib;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy
{
    public EntityPlayer getClientPlayer() {
        return null;
    }

    public void preInit(FMLPreInitializationEvent event) {
    }

    public void postInit(FMLPostInitializationEvent event) {
    }

    public void loadModLexica(ASMDataTable dataTable) {
    }
}
