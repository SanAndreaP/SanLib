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
import de.sanandrew.mods.sanlib.sanplayermodel.client.ClientProxy;
import de.sanandrew.mods.sanlib.sanplayermodel.entity.EntitySanArmorStand;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.UUID;

@Mod(SanPlayerModel.ID)
@Mod.EventBusSubscriber(modid = SanPlayerModel.ID)
public class SanPlayerModel
{
    public static final String ID = "sanplayermodel";
    public static final String VERSION = "1.2.0";

    public static final Logger LOG = LogManager.getLogger(ID);

    public final SanPlayerModel instance;
    public static CommonProxy proxy;

    public static final String[] SANPLAYER_NAMES_UUID = new String[] { "SanAndreasP", "044d980d-5c2a-4030-95cf-cbfde69ea3cb" };

    public SanPlayerModel() {
        instance = this;
        FMLJavaModLoadingContext.get().getModEventBus().register(SanPlayerModel.class);
    }

    @SubscribeEvent
    public static void initClient(FMLClientSetupEvent event) {
        proxy = new ClientProxy();
        proxy.registerRenderStuff();
    }

    @SubscribeEvent
    public static void initServer(FMLDedicatedServerSetupEvent event) {
        proxy = new CommonProxy();
    }

    @SubscribeEvent
    public static void registerEntities(RegistryEvent.Register<EntityType<?>> event) {
//        event.getRegistry().register(EntityType.Builder.create(EntitySanArmorStand.class, EntitySanArmorStand::new).tracker(64, 1, true)
//                                                       .build(new ResourceLocation(ID, "sanArmorStand").toString()));
    }

//    @Mod.EventHandler
//    public void preInit(FMLPreInitializationEvent event) {
//        EntityRegistry.registerModEntity(new ResourceLocation(ID, "sanArmorStand"), EntitySanArmorStand.class, "sanArmorStand", 0, this, 64, 1, true);
//
//        MinecraftForge.EVENT_BUS.register(new ItemClickEvent());
//
//        proxy.registerRenderStuff();
//    }
//
//    @Mod.EventHandler
//    public void init(FMLInitializationEvent evt) {
//
//    }
//
//    @Mod.EventHandler
//    public void postInit(FMLPostInitializationEvent evt) {
//
//    }

    public static boolean isSanPlayer(EntityPlayer e) {
        for( String val : SANPLAYER_NAMES_UUID ) {
            GameProfile profile = e.getGameProfile();
            if( (UuidUtils.isStringUuid(val) && UUID.fromString(val).equals(profile.getId())) || profile.getName().equals(val) ) {
                return true;
            }
        }

        return true;
    }
}
