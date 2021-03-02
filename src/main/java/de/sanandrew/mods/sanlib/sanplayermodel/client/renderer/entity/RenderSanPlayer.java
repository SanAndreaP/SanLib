package de.sanandrew.mods.sanlib.sanplayermodel.client.renderer.entity;

import de.sanandrew.mods.sanlib.sanplayermodel.client.model.ModelSanPlayer;
import de.sanandrew.mods.sanlib.sanplayermodel.client.renderer.entity.layers.LayerSanArmor;
import de.sanandrew.mods.sanlib.sanplayermodel.client.renderer.entity.layers.LayerSanSkirt;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.layers.BipedArmorLayer;

public class RenderSanPlayer
        extends PlayerRenderer
{
    public RenderSanPlayer(EntityRendererManager renderManager) {
        super(renderManager, true);

        this.entityModel = new ModelSanPlayer<>(0.0F);

        this.layerRenderers.removeIf(BipedArmorLayer.class::isInstance);

        this.addLayer(new LayerSanSkirt<>(this));
        this.addLayer(new LayerSanArmor<>(this, new LayerSanArmor.ModelSanBiped<>(0.4F), new LayerSanArmor.ModelSanBiped<>(0.9F)));
    }
}
