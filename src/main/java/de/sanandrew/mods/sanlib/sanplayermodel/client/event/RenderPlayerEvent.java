package de.sanandrew.mods.sanlib.sanplayermodel.client.event;

import de.sanandrew.mods.sanlib.Constants;
import de.sanandrew.mods.sanlib.lib.util.ReflectionUtils;
import de.sanandrew.mods.sanlib.sanplayermodel.SanPlayerModel;
import de.sanandrew.mods.sanlib.sanplayermodel.client.ClientProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.Objects;

@Mod.EventBusSubscriber(value = { Side.CLIENT}, modid = Constants.PM_ID)
public class RenderPlayerEvent
{
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onPlayerRenderPre(net.minecraftforge.client.event.RenderPlayerEvent.Pre event) {
        Entity e = event.getEntity();
        if( e instanceof EntityPlayer && SanPlayerModel.isSanPlayer((EntityPlayer) e) ) {
            ClientProxy.renderIdoSwimming(event);
        }
    }

    @SubscribeEvent
    public static void onRenderPost(RenderLivingEvent.Post<?> event) {
        Entity e = event.getEntity();
        if( e instanceof EntityPlayer && SanPlayerModel.isSanPlayer((EntityPlayer) e) ) {
            NetworkPlayerInfo npi = Objects.requireNonNull(Minecraft.getMinecraft().getConnection()).getPlayerInfo(e.getUniqueID());

            ReflectionUtils.setCachedFieldValue(NetworkPlayerInfo.class, npi, "skinType", "field_178863_g", "slim_san");
        }
    }
}
