package de.sanandrew.mods.sanlib.lib.util;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ITagCollection;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;

@SuppressWarnings("unused")
public class TagUtils
{
    public static <T> boolean isThingInTag(ITagCollection<T> tags, ResourceLocation tagId, T thing) {
        ITag<T> t = tags.getTag(tagId);
        return t != null && t.contains(thing);
    }

    public static boolean isItemInTag(ResourceLocation tagId, Item item) {
        return isThingInTag(ItemTags.getAllTags(), tagId, item);
    }

    public static boolean isBlockInTag(ResourceLocation tagId, Block block) {
        return isThingInTag(BlockTags.getAllTags(), tagId, block);
    }

    public static boolean isEntityTypeInTag(ResourceLocation tagId, EntityType<?> type) {
        return isThingInTag(EntityTypeTags.getAllTags(), tagId, type);
    }

    public static boolean isEntityInTag(ResourceLocation tagId, Entity entity) {
        return isThingInTag(EntityTypeTags.getAllTags(), tagId, entity.getType());
    }
}
