/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.sanlib.sanplayermodel;

import com.mojang.authlib.GameProfile;
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

@Mod(modid = SanPlayerModel.ID, version = SanPlayerModel.VERSION, name = "San's Player Model", dependencies = "after:" + SanLib.ID, acceptableRemoteVersions = "[1.0.2,)")
public class SanPlayerModel
{
    public static final String ID = "sanplayermodel";
    public static final String VERSION = "1.1.1";

    public static final Logger LOG = LogManager.getLogger(ID);

    public static final String COMMON_PROXY = "de.sanandrew.mods.sanlib.sanplayermodel.CommonProxy";
    public static final String CLIENT_PROXY = "de.sanandrew.mods.sanlib.sanplayermodel.client.ClientProxy";

    @Mod.Instance(ID)
    public static SanPlayerModel instance;
    @SidedProxy(clientSide = CLIENT_PROXY, serverSide = COMMON_PROXY, modId = ID)
    public static CommonProxy proxy;

    public static final String[] SANPLAYER_NAMES_UUID = new String[] { "SanAndreasP", "044d980d-5c2a-4030-95cf-cbfde69ea3cb" };

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        EntityRegistry.registerModEntity(new ResourceLocation(ID, "sanArmorStand"), EntitySanArmorStand.class, "sanArmorStand", 0, this, 64, 1, true);

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
