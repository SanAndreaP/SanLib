/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.sanlib;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = SanLib.ID, version = SanLib.VERSION, name = "San's Library", acceptedMinecraftVersions = SanLib.MCVER, dependencies = SanLib.DEPENDENCIES,
     acceptableRemoteVersions = SanLib.ACCEPTED_REMOTE_VER)
public class SanLib
{
    public static final String ID = "sanlib";
    public static final String VERSION = "1.5.1";
    public static final String ACCEPTED_REMOTE_VER = "[1.5.0,)";
    public static final String CHANNEL = "SanLibNWCH";
    public static final String MCVER = "[1.12.2, 1.13)";
    public static final String DEPENDENCIES = "required-after:forge@[14.23.2.2611,]";

    public static final Logger LOG = LogManager.getLogger(ID);

    private static final String COMMON_PROXY = "de.sanandrew.mods.sanlib.CommonProxy";
    private static final String CLIENT_PROXY = "de.sanandrew.mods.sanlib.client.ClientProxy";

    public static SimpleNetworkWrapper network;

    @Mod.Instance(ID)
    public static SanLib instance;
    @SidedProxy(clientSide = CLIENT_PROXY, serverSide = COMMON_PROXY, modId = ID)
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        SLibConfiguration.initConfiguration(event);
        network = NetworkRegistry.INSTANCE.newSimpleChannel(CHANNEL);

        proxy.loadModLexica(event.getAsmData());

        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {

    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }
}
