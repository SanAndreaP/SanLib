/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.sapmanpack;

import de.sanandrew.mods.sapmanpack.sanplayermodel.CommonProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = SanLib.ID, version = SanLib.VERSION, name = "San's Library")
public class SanLib
{
    public static final String ID = "sanlib";
    public static final String VERSION = "1.0.0";

    public static final Logger LOG = LogManager.getLogger(ID);

//    public static final String COMMON_PROXY = "de.sanandrew.mods.sapmanpack.sanplayermodel.CommonProxy";
//    public static final String CLIENT_PROXY = "de.sanandrew.mods.sapmanpack.sanplayermodel.client.ClientProxy";

    @Mod.Instance(ID)
    public static SanLib instance;
//    @SidedProxy(clientSide = CLIENT_PROXY, serverSide = COMMON_PROXY, modId = ID)
//    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {

    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent evt) {
//        proxy.registerRenderStuff();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent evt) {

    }
}
