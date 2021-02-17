package de.sanandrew.mods.sanlib.sanplayermodel.client;

import de.sanandrew.mods.sanlib.lib.util.ReflectionUtils;
import de.sanandrew.mods.sanlib.sanplayermodel.CommonProxy;
import de.sanandrew.mods.sanlib.sanplayermodel.client.renderer.entity.RenderSanPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.Loader;

public class ClientProxy
        extends CommonProxy
{
    private static byte idoVer = -1;

    @Override
    public void registerRenderStuff() {
        RenderManager rm = Minecraft.getMinecraft().getRenderManager();
        rm.skinMap.put("slim_san", new RenderSanPlayer(rm));
    }

    public static void renderIdoSwimming(RenderPlayerEvent.Pre event) {
        if( hasIdoLegacy() ) {
            EntityPlayer player = event.getEntityPlayer();
            if (!player.noClip) {
                boolean type = false;
                if (player.isInWater() && player.isSprinting() || player.height == 0.6F) {
                    event.setCanceled(true);
                    if( Minecraft.getMinecraft().getRenderViewEntity() instanceof AbstractClientPlayer ) {
                        AbstractClientPlayer client = (AbstractClientPlayer) Minecraft.getMinecraft().getRenderViewEntity();
                        type = client.getSkinType().contains("slim");
                    }

                    try {
                        RenderPlayer sp = (RenderPlayer) ReflectionUtils
                                .getNew("xyz.kaydax.ido.legacy.RenderPlayerSwiming", new Class[] { RenderManager.class, boolean.class },
                                        event.getRenderer().getRenderManager(), type);
                        sp.doRender((AbstractClientPlayer) event.getEntity(), event.getX(), event.getY(), event.getZ(), event.getEntity().rotationYaw,
                                    event.getPartialRenderTick());
                    } catch( Exception ignored ) {
                        idoVer = 0;
                    }
                }
            }
        } else {
            idoVer = 0;
        }
    }

    private static boolean hasIdoLegacy() {
        if( idoVer < 0 && Loader.instance().getModList().stream().anyMatch(mc -> mc.getModId().equals("ido") && mc.getVersion().equals("1.0.6")) ) {
            idoVer = 1;
        }

        return idoVer == 1;
    }
}
