////////////////////////////////////////////////////////////////////////////////
// This file is subject to the terms and conditions defined in the             /
// file '.github/LICENSE.md', which is part of this source code package.       /
////////////////////////////////////////////////////////////////////////////////

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

@Mod(modid = Constants.ID, version = Constants.VERSION, name = Constants.NAME, acceptedMinecraftVersions = Constants.MCVER, dependencies = Constants.DEPENDENCIES,
     acceptableRemoteVersions = Constants.ACCEPTED_REMOTE_VER, certificateFingerprint = Constants.CERTIFICATE_FINGERPRINT)
public class SanLib
{
    public static final Logger LOG = LogManager.getLogger(Constants.ID);

    @Mod.Instance(Constants.ID)
    public static SanLib instance;
    @SidedProxy(clientSide = Constants.CLIENT_PROXY, serverSide = Constants.COMMON_PROXY, modId = Constants.ID)
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
