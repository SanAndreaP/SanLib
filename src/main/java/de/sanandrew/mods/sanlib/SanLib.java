/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.sanlib;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLFingerprintViolationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = SanLib.ID, version = SanLib.VERSION, name = SanLib.NAME, acceptedMinecraftVersions = SanLib.MCVER, dependencies = SanLib.DEPENDENCIES,
     acceptableRemoteVersions = SanLib.ACCEPTED_REMOTE_VER, certificateFingerprint = SanLib.CERTIFICATE_FINGERPRINT)
public class SanLib
{
    public static final String ID = "sanlib";
    public static final String NAME = "San's Library";
    public static final String CERTIFICATE_FINGERPRINT = "df48348748b5e141b1d118e2302a8d5be930b708";
    static final String VERSION = "1.5.1";
    static final String ACCEPTED_REMOTE_VER = "[1.5.1,)";
    static final String MCVER = "[1.12.2, 1.13)";
    static final String DEPENDENCIES = "required-after:forge@[14.23.2.2611,]";

    public static final Logger LOG = LogManager.getLogger(ID);

    private static final String COMMON_PROXY = "de.sanandrew.mods.sanlib.CommonProxy";
    private static final String CLIENT_PROXY = "de.sanandrew.mods.sanlib.client.ClientProxy";

    @Mod.Instance(ID)
    public static SanLib instance;
    @SidedProxy(clientSide = CLIENT_PROXY, serverSide = COMMON_PROXY, modId = ID)
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        SLibConfig.initConfiguration(event);

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

    @Mod.EventHandler
    public void onFingerprintViolation(FMLFingerprintViolationEvent event) {
        LOG.log(Level.ERROR, "Invalid Fingerprint detected! The file " + event.getSource().getName() + " may have been tampered with. This version will NOT be supported by the author");
    }
}
