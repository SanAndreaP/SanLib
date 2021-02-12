package de.sanandrew.mods.sanlib.sanplayermodel.client;

import de.sanandrew.mods.sanlib.sanplayermodel.CommonProxy;
import de.sanandrew.mods.sanlib.sanplayermodel.client.renderer.entity.RenderSanPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.Loader;

public class ClientProxy
        extends CommonProxy
{
    @Override
    public void registerRenderStuff() {
        RenderManager rm = Minecraft.getMinecraft().getRenderManager();
        rm.skinMap.put("slim_san", new RenderSanPlayer(rm));
    }

    public static void renderIdoSwimming(RenderPlayerEvent.Pre event) {
        if( Loader.isModLoaded("ido") ) {
            EntityPlayer player = event.getEntityPlayer();
            if (!player.noClip) {
                boolean type = false;
                if (player.isInWater() && player.isSprinting() || player.height == 0.6F) {
                    event.setCanceled(true);
                    if (Minecraft.getMinecraft().getRenderViewEntity() instanceof AbstractClientPlayer ) {
                        AbstractClientPlayer client = (AbstractClientPlayer) Minecraft.getMinecraft().getRenderViewEntity();
                        type = client.getSkinType().contains("slim");
                    }

                    xyz.kaydax.ido.legacy.RenderPlayerSwiming sp = new xyz.kaydax.ido.legacy.RenderPlayerSwiming(event.getRenderer().getRenderManager(), type);
                    sp.doRender((AbstractClientPlayer) event.getEntity(), event.getX(), event.getY(), event.getZ(), event.getEntity().rotationYaw, event.getPartialRenderTick());
                }
            }
        }
    }
}
