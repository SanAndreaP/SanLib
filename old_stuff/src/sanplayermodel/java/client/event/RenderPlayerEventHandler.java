////////////////////////////////////////////////////////////////////////////////
// This file is subject to the terms and conditions defined in the             /
// file '.github/LICENSE.md', which is part of this source code package.       /
////////////////////////////////////////////////////////////////////////////////

package de.sanandrew.mods.sanlib.sanplayermodel.old.client.event;

import de.sanandrew.mods.sanlib.SLibConfig;
import de.sanandrew.mods.sanlib.sanplayermodel.SanPlayerModel;
import de.sanandrew.mods.sanlib.sanplayermodel.old.client.renderer.entity.RenderSanPlayer;
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
        Minecraft mc = Minecraft.getMinecraft();
        if( this.sanPlayerModel == null && mc.player != null ) {
            this.sanPlayerModel = new RenderSanPlayer(mc.getRenderManager());
            mc.getRenderManager().getSkinMap().get("slim").layerRenderers.forEach(layer -> {
                if( !layer.getClass().getName().startsWith("net.minecraft.") ) {
                    this.sanPlayerModel.addLayer(layer);
                }
            });
        }
    }

//    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerRender(RenderPlayerEvent.Pre event) {
        if( SLibConfig.Client.allowCustomSanModel && SanPlayerModel.isSanPlayer(event.getEntityPlayer()) ) {
            this.lazyLoad();

            this.playerPartTicks = event.getPartialRenderTick();
        }
    }

//    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onLivingRender(Pre event) {
        if( SLibConfig.Client.allowCustomSanModel && event.getEntity() instanceof EntityPlayer && SanPlayerModel.isSanPlayer((EntityPlayer) event.getEntity())  ) {
            this.lazyLoad();

            if( this.sanPlayerModel != null ) {
                this.sanPlayerModel.doRender((AbstractClientPlayer) event.getEntity(), event.getX(), event.getY() + ((EntityPlayer) event.getEntity()).renderOffsetY, event.getZ(), 0.0F, this.playerPartTicks);
                event.setCanceled(true);
            }
        }
    }

//    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onHandRender(RenderHandEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        if( SLibConfig.Client.allowCustomSanModel && SanPlayerModel.isSanPlayer(mc.player) ) {
            this.lazyLoad();

            GL11.glPushMatrix();

            boolean flag = mc.getRenderViewEntity() instanceof EntityLivingBase && ((EntityLivingBase) mc.getRenderViewEntity()).isPlayerSleeping();
            if( mc.gameSettings.thirdPersonView == 0 && this.sanPlayerModel != null && !flag && !mc.gameSettings.hideGUI && mc.playerController != null && !mc.playerController.isSpectator() ) {
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

            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glPopMatrix();
        }
    }
}
