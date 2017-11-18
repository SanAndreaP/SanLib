/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.sanlib;

import de.sanandrew.mods.sanlib.command.CommandSanLib;
import de.sanandrew.mods.sanlib.network.PacketRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = SanLib.ID, version = SanLib.VERSION, name = "San's Library", acceptedMinecraftVersions = SanLib.MCVER, dependencies = SanLib.DEPENDENCIES, acceptableRemoteVersions = "[1.3.0,1.3.1]")
public class SanLib
{
    public static final String ID = "sanlib";
    public static final String VERSION = "1.3.1";
    public static final String CHANNEL = "SanLibNWCH";
    public static final String MCVER = "[1.12, 1.13)";
    public static final String DEPENDENCIES = "required-after:forge@[14.21.1.2387,]";

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
        network = NetworkRegistry.INSTANCE.newSimpleChannel(CHANNEL);
        PacketRegistry.initialize();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent evt) {

    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent evt) {

    }

    @Mod.EventHandler
    public void onServerLoad(FMLServerStartingEvent evt) {
        evt.registerServerCommand(new CommandSanLib());
    }
}
