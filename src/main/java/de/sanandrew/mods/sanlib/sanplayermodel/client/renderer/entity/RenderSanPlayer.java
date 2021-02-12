package de.sanandrew.mods.sanlib.sanplayermodel.client.renderer.entity;

import de.sanandrew.mods.sanlib.sanplayermodel.client.model.ModelSanPlayer;
import de.sanandrew.mods.sanlib.sanplayermodel.client.renderer.entity.layers.LayerSanArmor;
import de.sanandrew.mods.sanlib.sanplayermodel.client.renderer.entity.layers.LayerSanSkirt;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;

public class RenderSanPlayer
        extends RenderPlayer
{
    public RenderSanPlayer(RenderManager renderManager) {
        super(renderManager, true);

        this.mainModel = new ModelSanPlayer(0.0F);

        this.layerRenderers.removeIf(LayerBipedArmor.class::isInstance);

        this.addLayer(new LayerSanSkirt(this));
        this.addLayer(new LayerSanArmor(this));
    }
}
