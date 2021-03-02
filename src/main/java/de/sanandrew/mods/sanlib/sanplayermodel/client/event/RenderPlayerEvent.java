package de.sanandrew.mods.sanlib.sanplayermodel.client.event;

import de.sanandrew.mods.sanlib.Constants;
import de.sanandrew.mods.sanlib.lib.util.ReflectionUtils;
import de.sanandrew.mods.sanlib.sanplayermodel.SanPlayerModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Objects;

@Mod.EventBusSubscriber(value = {Dist.CLIENT}, modid = Constants.PM_ID)
public class RenderPlayerEvent
{
    @SubscribeEvent
    public static void onRenderPost(RenderLivingEvent.Post<?, ?> event) {
        Entity e = event.getEntity();
        if( e instanceof AbstractClientPlayerEntity && SanPlayerModel.isSanPlayer((AbstractClientPlayerEntity) e) ) {
            NetworkPlayerInfo npi = Objects.requireNonNull(Minecraft.getInstance().getConnection()).getPlayerInfo(e.getUniqueID());

            ReflectionUtils.setCachedFieldValue(NetworkPlayerInfo.class, npi, "skinType", "field_178863_g", "slim_san");
        }
    }
}
