/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.sanlib.sanplayermodel.client.event;

import de.sanandrew.mods.sanlib.sanplayermodel.SanPlayerModel;
import de.sanandrew.mods.sanlib.sanplayermodel.client.renderer.entity.RenderSanPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderLivingEvent.Pre;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderPlayerEventHandler
{
    private RenderSanPlayer sanPlayerModel = null;
    private float playerPartTicks = 0.0F;

    private void lazyLoad() {
        if( this.sanPlayerModel == null ) {
            this.sanPlayerModel = new RenderSanPlayer(Minecraft.getMinecraft().getRenderManager());
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerRender(RenderPlayerEvent.Pre event) {
        this.lazyLoad();

        if( SanPlayerModel.isSanPlayer(event.getEntityPlayer()) ) {
            playerPartTicks = event.getPartialRenderTick();
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onLivingRender(Pre event) {
        this.lazyLoad();

        if( event.getEntity() instanceof EntityPlayer /*&& event.getRenderer() != this.sanPlayerModel*/ && SanPlayerModel.isSanPlayer((EntityPlayer) event.getEntity()) ) {
            this.sanPlayerModel.doRender((AbstractClientPlayer) event.getEntity(), event.getX(), event.getY() + ((EntityPlayer) event.getEntity()).renderOffsetY, event.getZ(), 0.0F, this.playerPartTicks);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onHandRender(RenderHandEvent event) {
        this.lazyLoad();

        GL11.glPushMatrix();
        Minecraft mc = Minecraft.getMinecraft();

        boolean flag = mc.getRenderViewEntity() instanceof EntityLivingBase && ((EntityLivingBase)mc.getRenderViewEntity()).isPlayerSleeping();
        if( mc.gameSettings.thirdPersonView == 0 && !flag && !mc.gameSettings.hideGUI && mc.playerController != null && !mc.playerController.isSpectator() ) {
            if( SanPlayerModel.isSanPlayer(mc.player) ) {
                String skinType = mc.player.getSkinType();
                Render<AbstractClientPlayer> rend = mc.getRenderManager().getEntityRenderObject(mc.player);
                RenderPlayer skin = mc.getRenderManager().getSkinMap().get(skinType);

                mc.getRenderManager().entityRenderMap.put(mc.player.getClass(), this.sanPlayerModel);
                mc.getRenderManager().skinMap.put(skinType, this.sanPlayerModel);

                event.setCanceled(true);
                mc.entityRenderer.enableLightmap();
                mc.entityRenderer.itemRenderer.renderItemInFirstPerson(event.getPartialTicks());
                mc.entityRenderer.disableLightmap();

                mc.getRenderManager().entityRenderMap.put(mc.player.getClass(), rend);
                mc.getRenderManager().skinMap.put(skinType, skin);
            }
        }

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glPopMatrix();
    }
}
