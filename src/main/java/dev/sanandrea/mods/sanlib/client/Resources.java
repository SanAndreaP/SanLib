/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2016-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.sanlib.client;

import dev.sanandrea.mods.sanlib.Constants;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;

public final class Resources
{
    public static final ResourceLocation SKIRT_MODEL = ResourceLocation.fromNamespaceAndPath(Constants.ID, "models/entity/player/sanplayer_red_skirt.json");
    public static final ResourceLocation SKIRT_TEXTURE = ResourceLocation.fromNamespaceAndPath(Constants.ID, "textures/entity/player/sanplayer_red_skirt.png");

    public static final ModelLayerLocation SKIRT_MODEL_ID = new ModelLayerLocation(SKIRT_MODEL, "san_skirt");
    public static final ModelLayerLocation SKIRT_MODEL_ARMOR_ID = new ModelLayerLocation(SKIRT_MODEL, "san_skirt_armor");

    private Resources() { }

    private static final Map<Item, Map<Integer, Optional<ResourceLocation>>> SKIRT_ARMOR_TEXTURE_CACHE = new WeakHashMap<>();
    @OnlyIn(Dist.CLIENT)
    public static Optional<ResourceLocation> getSkirtArmorTexture(final Item item, int layerId) {
        Map<Integer, Optional<ResourceLocation>> layers = SKIRT_ARMOR_TEXTURE_CACHE.computeIfAbsent(item, itm -> new HashMap<>());

        return layers.computeIfAbsent(layerId, id -> {
            ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(item);
            String path;
            if( id < 1 ) {
                path = String.format("textures/entity/player/%s_%s.png", itemId.getNamespace(), itemId.getPath());
            } else {
                path = String.format("textures/entity/player/%s_%s_overlay_%d.png", itemId.getNamespace(), itemId.getPath(), id);
            }
            ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(Constants.ID, path);

            if( net.minecraft.client.Minecraft.getInstance().getResourceManager().getResource(texture).isPresent() ) {
                return Optional.of(texture);
            } else {
                return Optional.empty();
            }
        });
    }
}
