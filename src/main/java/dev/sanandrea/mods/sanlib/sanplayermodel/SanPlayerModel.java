/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2016-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.sanlib.sanplayermodel;

import com.mojang.authlib.GameProfile;
import dev.sanandrea.mods.sanlib.Constants;
import dev.sanandrea.mods.sanlib.lib.util.UuidUtils;
import dev.sanandrea.mods.sanlib.sanplayermodel.client.renderer.entity.RenderSanPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.UUID;

@Mod(Constants.PM_ID)
public class SanPlayerModel
{
    public static final Logger LOG = LogManager.getLogger(Constants.PM_ID);
    public static final String[] SANPLAYER_NAMES_UUID = new String[] { "SanAndreaP", "044d980d-5c2a-4030-95cf-cbfde69ea3cb" };

    public SanPlayerModel() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
    }

    private void clientSetup(FMLClientSetupEvent event) {
        EntityRendererManager rm = Minecraft.getInstance().getEntityRenderDispatcher();
        rm.playerRenderers.put("slim_san", new RenderSanPlayer(rm));
    }

    public static boolean isSanPlayer(PlayerEntity e) {
        for( String val : SANPLAYER_NAMES_UUID ) {
            GameProfile profile = e.getGameProfile();
            if( (UuidUtils.isStringUuid(val) && UUID.fromString(val).equals(profile.getId())) || profile.getName().equals(val) ) {
                return true;
            }
        }

        return false;
    }
}
