/*******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.sapmanpack.sanplayermodel.client.event;

import com.mojang.authlib.GameProfile;
import de.sanandrew.mods.sapmanpack.lib.UuidUtils;
import de.sanandrew.mods.sapmanpack.sanplayermodel.client.renderer.entity.RenderSanPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderLivingEvent.Pre;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.util.UUID;

public class RenderPlayerEventHandler
{
    private static final String[] SANPLAYER_NAMES_UUID = new String[] { "SanAndreasP", "044d980d-5c2a-4030-95cf-cbfde69ea3cb" };

    private RenderSanPlayer sanPlayerModel = null;

    private float playerPartTicks = 0.0F;

    private void lazyLoad() {
        if( this.sanPlayerModel == null ) {
            this.sanPlayerModel = new RenderSanPlayer(Minecraft.getMinecraft().getRenderManager());

//            if( Minecraft.getMinecraft().getResourceManager() instanceof SimpleReloadableResourceManager ) {
//                ((SimpleReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(this.sanPlayerModel);
//            }
        }
    }

    @SubscribeEvent
    public void onPlayerRender(RenderPlayerEvent.Pre event) {
        this.lazyLoad();

        if( isPlayerNameOrUuidEqual(event.getEntityPlayer(), SANPLAYER_NAMES_UUID) ) {
            playerPartTicks = event.getPartialRenderTick();
        }
    }

    @SubscribeEvent
    public void onLivingRender(Pre event) {
        this.lazyLoad();

        if( event.getEntity() instanceof EntityPlayer && event.getRenderer() != this.sanPlayerModel && isPlayerNameOrUuidEqual((EntityPlayer) event.getEntity(), SANPLAYER_NAMES_UUID) ) {
            this.sanPlayerModel.doRender((AbstractClientPlayer) event.getEntity(), event.getX(), event.getY() + ((EntityPlayer) event.getEntity()).renderOffsetY, event.getZ(), 0.0F, this.playerPartTicks);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    @SuppressWarnings( "unchecked" )
    public void onHandRender(RenderHandEvent event) {
        this.lazyLoad();

        GL11.glPushMatrix();
        Minecraft mc = Minecraft.getMinecraft();

        boolean flag = mc.getRenderViewEntity() instanceof EntityLivingBase && ((EntityLivingBase)mc.getRenderViewEntity()).isPlayerSleeping();
        if( mc.gameSettings.thirdPersonView == 0 && !flag && !mc.gameSettings.hideGUI && mc.playerController != null && !mc.playerController.isSpectator() ) {
            if( isPlayerNameOrUuidEqual(mc.thePlayer, SANPLAYER_NAMES_UUID) ) {
                String skinType = mc.thePlayer.getSkinType();
                Render<AbstractClientPlayer> rend = mc.getRenderManager().getEntityRenderObject((AbstractClientPlayer) mc.thePlayer);
                RenderPlayer skin = mc.getRenderManager().getSkinMap().get(skinType);

                mc.getRenderManager().entityRenderMap.put(mc.thePlayer.getClass(), this.sanPlayerModel);
                mc.getRenderManager().skinMap.put(skinType, this.sanPlayerModel);

                event.setCanceled(true);
                mc.entityRenderer.enableLightmap();
                mc.entityRenderer.itemRenderer.renderItemInFirstPerson(event.getPartialTicks());
                mc.entityRenderer.disableLightmap();

                mc.getRenderManager().entityRenderMap.put(mc.thePlayer.getClass(), rend);
                mc.getRenderManager().skinMap.put(skinType, skin);
            }
        }

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glPopMatrix();
    }

    public static boolean isPlayerNameOrUuidEqual(EntityPlayer e, String... namesUuids) {
        for( String val : namesUuids ) {
            GameProfile profile = e.getGameProfile();
            if( (UuidUtils.isStringUuid(val) && profile.getId().equals(UUID.fromString(val))) || profile.getName().equals(val) ) {
                return true;
            }
        }

        return false;
    }
}
