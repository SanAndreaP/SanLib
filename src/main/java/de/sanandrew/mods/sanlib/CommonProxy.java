/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
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

    public void reloadModels() {

    }

    public void preInit(FMLPreInitializationEvent event) {

    }

    public void postInit(FMLPostInitializationEvent event) {

    }

    public void loadModLexica(ASMDataTable dataTable) {
    }
}
