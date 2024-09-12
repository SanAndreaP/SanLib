package dev.sanandrea.mods.sanlib.client.event;

import dev.sanandrea.mods.sanlib.Constants;
import dev.sanandrea.mods.sanlib.client.Resources;
import dev.sanandrea.mods.sanlib.client.layer.SanSkirtLayer;
import dev.sanandrea.mods.sanlib.client.model.SanSkirtModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(value = Dist.CLIENT, modid = Constants.ID, bus = EventBusSubscriber.Bus.MOD)
public class RegistryHandler
{
    private RegistryHandler() {}

    @SubscribeEvent
    public static void registerModel(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(Resources.SKIRT_MODEL_ID, SanSkirtModel::createLayer);
        event.registerLayerDefinition(Resources.SKIRT_MODEL_ARMOR_ID, SanSkirtModel::createArmorLayer);
    }

    @SubscribeEvent
    public static void addLayers(EntityRenderersEvent.AddLayers event) {
        EntityRenderer<Player> er = event.getSkin(PlayerSkin.Model.SLIM);
        if( er instanceof LivingEntityRenderer<?, ?> ) {
            LivingEntityRenderer<Player, HumanoidModel<Player>> r = (LivingEntityRenderer<Player, HumanoidModel<Player>>) er;
            r.addLayer(new SanSkirtLayer<>(r, event.getEntityModels()));
        }
    }
}
