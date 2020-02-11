/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.sanlib.sanplayermodel;

import com.mojang.authlib.GameProfile;
import de.sanandrew.mods.sanlib.Constants;
import de.sanandrew.mods.sanlib.SanLib;
import de.sanandrew.mods.sanlib.lib.util.UuidUtils;
import de.sanandrew.mods.sanlib.sanplayermodel.entity.EntitySanArmorStand;
import de.sanandrew.mods.sanlib.sanplayermodel.event.ItemClickEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.UUID;

@Mod(modid = Constants.PM_ID, version = Constants.PM_VERSION, name = Constants.PM_VERSION, dependencies = "after:" + Constants.ID,
     acceptableRemoteVersions = Constants.PM_ACCEPTED_REMOTE_VER)
public class SanPlayerModel
{

    public static final Logger LOG = LogManager.getLogger(Constants.PM_ID);

    @Mod.Instance(Constants.PM_ID)
    public static SanPlayerModel instance;
    @SidedProxy(clientSide = Constants.PM_CLIENT_PROXY, serverSide = Constants.PM_COMMON_PROXY, modId = Constants.PM_ID)
    public static CommonProxy proxy;

    public static final String[] SANPLAYER_NAMES_UUID = new String[] { "SanAndreasP", "044d980d-5c2a-4030-95cf-cbfde69ea3cb" };

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        EntityRegistry.registerModEntity(new ResourceLocation(Constants.PM_ID, "sanArmorStand"), EntitySanArmorStand.class, "sanArmorStand", 0, this, 64, 1, true);

        MinecraftForge.EVENT_BUS.register(new ItemClickEvent());

        proxy.registerRenderStuff();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent evt) {

    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent evt) {

    }

    public static boolean isSanPlayer(EntityPlayer e) {
        for( String val : SANPLAYER_NAMES_UUID ) {
            GameProfile profile = e.getGameProfile();
            if( (UuidUtils.isStringUuid(val) && UUID.fromString(val).equals(profile.getId())) || profile.getName().equals(val) ) {
                return true;
            }
        }

        return false;
    }
}
