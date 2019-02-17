/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.sanlib;

import de.sanandrew.mods.sanlib.client.ClientProxy;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(SanLib.ID)
public class SanLib
{
    public static final String ID = "sanlib";
    public static final String NAME = "San's Library";
    //TODO: find out how the new certificate stuff works
    public static final String CERTIFICATE_FINGERPRINT = "df48348748b5e141b1d118e2302a8d5be930b708";

    public static final Logger LOG = LogManager.getLogger(ID);

//    @Mod.Instance(ID)
    public static SanLib instance;
    public static CommonProxy proxy;

    public SanLib() {
        instance = this;
        FMLJavaModLoadingContext.get().getModEventBus().register(SanLib.class);
    }

    @SubscribeEvent
    public static void initClient(FMLClientSetupEvent event) {
        proxy = new ClientProxy();
        proxy.init();
    }

    @SubscribeEvent
    public static void initServer(FMLDedicatedServerSetupEvent event) {
        proxy = new CommonProxy();
        proxy.init();
    }



//    @Mod.EventHandler
//    public void preInit(FMLPreInitializationEvent event) {
//        SLibConfig.initConfiguration(event);
//
//        proxy.loadModLexica(event.getAsmData());
//
//        proxy.preInit(event);
//    }
//
//    @Mod.EventHandler
//    public void init(FMLInitializationEvent event) {
//
//    }
//
//    @Mod.EventHandler
//    public void postInit(FMLPostInitializationEvent event) {
//        proxy.postInit(event);
//    }
//
//    @Mod.EventHandler
//    public void onFingerprintViolation(FMLFingerprintViolationEvent event) {
//        LOG.log(Level.ERROR, "Invalid Fingerprint detected! The file " + event.getSource().getName() + " may have been tampered with. This version will NOT be supported by the author");
//    }
}
