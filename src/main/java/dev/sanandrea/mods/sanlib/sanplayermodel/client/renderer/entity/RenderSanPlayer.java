/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2016-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.sanlib.sanplayermodel.client.renderer.entity;

import dev.sanandrea.mods.sanlib.sanplayermodel.client.model.ModelSanPlayer;
import dev.sanandrea.mods.sanlib.sanplayermodel.client.renderer.entity.layers.LayerSanArmor;
import dev.sanandrea.mods.sanlib.sanplayermodel.client.renderer.entity.layers.LayerSanSkirt;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.layers.BipedArmorLayer;

public class RenderSanPlayer
        extends PlayerRenderer
{
    public RenderSanPlayer(EntityRendererManager renderManager) {
        super(renderManager, true);

        this.model = new ModelSanPlayer<>(0.0F);

        this.layers.removeIf(BipedArmorLayer.class::isInstance);

        this.addLayer(new LayerSanSkirt<>(this));
        this.addLayer(new LayerSanArmor<>(this, new LayerSanArmor.ModelSanBiped<>(0.4F), new LayerSanArmor.ModelSanBiped<>(0.9F)));
    }
}
