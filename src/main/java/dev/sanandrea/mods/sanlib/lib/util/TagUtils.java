/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2016-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.sanlib.lib.util;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nonnull;
import java.util.Optional;

@SuppressWarnings("unused")
public final class TagUtils
{
    private TagUtils() {}

    public static <T> boolean isThingInTag(Holder.Reference<T> tags, ResourceLocation tagId) {
        return tags != null && tags.is(tagId);
    }

    private static <T> Optional<Holder.Reference<T>> getReference(@Nonnull Registry<T> registry, T item) {
        ResourceLocation key = registry.getKey(item);
        return key == null ? Optional.empty() : registry.getHolder(key);
    }

    public static boolean isItemInTag(ResourceLocation tagId, Item item) {
        return isThingInTag(getReference(BuiltInRegistries.ITEM, item).orElse(null), tagId);
    }

    public static boolean isBlockInTag(ResourceLocation tagId, Block block) {
        return isThingInTag(getReference(BuiltInRegistries.BLOCK, block).orElse(null), tagId);
    }

    public static boolean isEntityTypeInTag(ResourceLocation tagId, EntityType<?> type) {
        return isThingInTag(getReference(BuiltInRegistries.ENTITY_TYPE, type).orElse(null), tagId);
    }

    public static boolean isEntityInTag(ResourceLocation tagId, Entity entity) {
        return isEntityTypeInTag(tagId, entity.getType());
    }
}
