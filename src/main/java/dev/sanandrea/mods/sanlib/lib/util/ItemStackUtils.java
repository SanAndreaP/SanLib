/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2016-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.sanlib.lib.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import dev.sanandrea.mods.sanlib.SanLib;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * An utility class for ItemStacks
 */
@SuppressWarnings({ "unused", "SameParameterValue", "WeakerAccess", "ObjectEquality" })
public final class ItemStackUtils
{
    private static final String NBT_STACK_TAG = "StackNBT";
    private static final String NBT_SLOT      = "Slot";

    private ItemStackUtils() {}

    /**
     * Checks if an ItemStack is a valid stack.
     * <p>An ItemStack is valid, if:
     * <ul>
     *     <li>the stack is not {@code null}</li>
     *     <li>the item of the stack is not {@code null}</li>
     *     <li>the item is not {@link net.minecraft.world.level.block.Blocks#AIR}</li>
     *     <li>the stack size is &gt; 0</li>
     * </ul>
     * </p>
     *
     * @param stack The ItemStack to be checked.
     *
     * @return {@code true}, if the stack is valid, {@code false} otherwise
     */
    public static boolean isValid(@Nonnull ItemStack stack) {
        return !stack.isEmpty();
    }

    @Nonnull
    public static ItemStack getEmpty() {
        return ItemStack.EMPTY;
    }

    /**
     * Checks if an ItemStack is valid and contains a specific item.
     *
     * @param stack The ItemStack to be checked.
     * @param item  The item that should be held in the ItemStack
     *
     * @return {@code true}, if the stack is valid and contains the specified item, {@code false} otherwise
     *
     * @see #isValid(ItemStack)
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isItem(@Nonnull ItemStack stack, Item item) {
        return isValid(stack) && stack.getItem() == item;
    }

    /**
     * Checks if an ItemStack is valid and contains a specific block.
     *
     * @param stack The ItemStack to be checked.
     * @param block The block that should be held in the ItemStack
     *
     * @return {@code true}, if the stack is valid and contains the specified block, {@code false} otherwise
     *
     * @see #isValid(ItemStack)
     */
    public static boolean isBlock(@Nonnull ItemStack stack, Block block) {
        return isValid(stack) && Block.byItem(stack.getItem()) == block;
    }

    public static JsonElement toJson(@Nonnull ItemStack stack) {
        if( !isValid(stack) ) {
            return JsonNull.INSTANCE;
        }

        JsonUtils.ObjectBuilder stackBuilder = JsonUtils.ObjectBuilder.create();
        stackBuilder.value("id", BuiltInRegistries.ITEM.getKey(stack.getItem()));
        stackBuilder.value("count", stack.getCount());

        if( !stack.isComponentsPatchEmpty() ) {
            DataComponentPatch      components        = stack.getComponentsPatch();
            JsonUtils.ObjectBuilder componentsBuilder = JsonUtils.ObjectBuilder.create();
            for( var component : components.entrySet() ) {
                ResourceLocation key = BuiltInRegistries.DATA_COMPONENT_TYPE.getKey(component.getKey());
                if( key != null ) {
                    componentsBuilder.valueIf(key.toString(), encode(component), Objects::nonNull);
                }
            }

            stackBuilder.valueIf("components", componentsBuilder.get(), v -> !v.isEmpty());
        }

        return stackBuilder.get();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static JsonElement encode(Map.Entry<DataComponentType<?>, Optional<?>> entry) {
        DataComponentType type = entry.getKey();
        Optional          val  = entry.getValue();
        if( val.isPresent() && type.codec() instanceof Codec codec ) {
            RegistryOps<JsonElement> context = VanillaRegistries.createLookup().createSerializationContext(JsonOps.INSTANCE);
            DataResult<JsonElement>  result  = codec.encodeStart(context, val.get());
            if( result.isSuccess() ) {
                return result.getOrThrow();
            } else {
                return JsonNull.INSTANCE;
            }
        }

        return null;
    }

    public static ItemStack fromJson(JsonElement json) {
        if( json == null || json.isJsonNull() ) {
            return ItemStack.EMPTY;
        } else if( json.isJsonArray() ) {
            throw new JsonSyntaxException("Cannot load array for a single item");
        }

        final ResourceLocation   key;
        final int                count;
        final DataComponentPatch components;

        if( json instanceof JsonObject jObj ) {
            key = JsonUtils.getLocation(jObj.get("id"), BuiltInRegistries.ITEM.getDefaultKey());
            count = JsonUtils.getIntVal(jObj.get("count"), 1);
            components = decode(jObj.get("components"));
        } else if( json.isJsonPrimitive() ) {
            key = JsonUtils.getLocation(json, BuiltInRegistries.ITEM.getDefaultKey());
            count = 1;
            components = DataComponentPatch.EMPTY;
        } else {
            throw new JsonParseException("Unknown JSON element type");
        }

        Optional<Holder.Reference<Item>> item = BuiltInRegistries.ITEM.getHolder(key);
        return item.map(itemReference -> new ItemStack(itemReference, count, components))
                   .orElse(ItemStack.EMPTY);

    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static DataComponentPatch decode(JsonElement data) {
        if( data instanceof JsonObject jObj ) {
            DataComponentPatch.Builder patchBuilder = DataComponentPatch.builder();

            RegistryOps<JsonElement> context = VanillaRegistries.createLookup().createSerializationContext(JsonOps.INSTANCE);
            for( var entry : jObj.entrySet() ) {
                ResourceLocation               dcKey = ResourceLocation.parse(entry.getKey());
                Optional<DataComponentType<?>> type  = BuiltInRegistries.DATA_COMPONENT_TYPE.getOptional(dcKey);
                if( type.isPresent() && type.get().codec() instanceof Codec<?> codec ) {
                    var val = codec.parse(context, entry.getValue());
                    if( val.isSuccess() ) {
                        patchBuilder.set((DataComponentType) type.get(), val.getOrThrow());
                    } else {
                        val.ifError(e -> SanLib.LOG.warn(e.message()));
                    }
                }
            }

            return patchBuilder.build();
        }

        return DataComponentPatch.EMPTY;
    }

//    /**
//     * Checks whether 2 ItemStacks are equal. This does not compare stack sizes! This compares Components!
//     * @param is1 The first ItemStack
//     * @param is2 The second ItemStack
//     * @return {@code true}, if the stacks are considered equal, {@code false} otherwise
//     */
//    public static boolean areEqual(@Nonnull ItemStack is1, @Nonnull ItemStack is2) {
//        return areEqual(is1, is2, false, true);
//    }
//
//    /**
//     * Checks wether or not 2 ItemStacks are equal. This does not compare stack sizes!
//     * @param is1 The first ItemStack
//     * @param is2 The second ItemStack
//     * @param checkComponents A flag to determine whether to compare Components
//     * @return {@code true}, if the stacks are considered equal, {@code false} otherwise
//     */
//    public static boolean areEqual(@Nonnull ItemStack is1, @Nonnull ItemStack is2, boolean checkComponents) {
//        return areEqual(is1, is2, false, checkComponents);
//    }
//
//    /**
//     * Checks wether or not 2 ItemStacks are equal.
//     * @param is1 The first ItemStack
//     * @param is2 The second ItemStack
//     * @param checkStackSize A flag to determine wether or not to compare stack sizes
//     * @param checkComponents A flag to determine wether or not to compare NBTTagCompounds
//     * @return {@code true}, if the stacks are considered equal, {@code false} otherwise
//     */
//    public static boolean areEqual(@Nonnull ItemStack is1, @Nonnull ItemStack is2, boolean checkStackSize, boolean checkComponents) {
//        return areEqualBase(is1, is2, checkStackSize) && (!checkComponents || Objects.equals(is1.getComponents(), is2.getComponents()));
//    }

//    public static boolean areEqualComponentsFit(@Nonnull ItemStack mainIS, @Nonnull ItemStack otherIS, boolean checkStackSize) {
//        return areEqualComponentsFit(mainIS, otherIS, checkStackSize, true);
//    }
//
//    public static boolean areEqualComponentsFit(@Nonnull ItemStack mainIS, @Nonnull ItemStack otherIS, boolean checkStackSize, boolean strict) {
//        if( areEqualBase(mainIS, otherIS, checkStackSize) ) {
//            DataComponentMap mainComp = mainIS.getComponents();
//            DataComponentMap otherComp = otherIS.getComponents();
//
//            return (otherIS.isEmpty() && mainIS.isEmpty()) || ()
//        }
//        return areEqualBase(mainIS, otherIS, checkStackSize)
//               && ( !otherIS.getComponents().isEmpty() || (mainIS.hasTag() && ComponentUtils.doesComponentsContainOther(mainIS.getTag(), otherIS.getTag(), strict)) );
//    }
//
//    private static boolean areEqualBase(@Nonnull ItemStack is1, @Nonnull ItemStack is2, boolean checkStackSize) {
//        if( !isValid(is1) && !isValid(is2) ) {
//            return true;
//        }
//
//        if( !isValid(is2) || !isItem(is1, is2.getItem()) ) {
//            return false;
//        }
//
//        if( is1.getDamageValue() != is2.getDamageValue() ) {
//            return false;
//        }
//
//        return !(checkStackSize && is1.getCount() != is2.getCount());
//    }
//
//    /**
//     * Writes the ItemStack as a new NBTTagCompound to the specified NBTTagCompound with the tagName as key.
//     * @param stack The ItemStack to write.
//     * @param tag The NBTTagCompound to be written.
//     * @param tagName The key for the tag.
//     */
//    public static void writeStackToTag(@Nonnull ItemStack stack, CompoundNBT tag, String tagName) {
//        CompoundNBT stackTag = new CompoundNBT();
//        stack.save(stackTag);
//        tag.put(tagName, stackTag);
//    }

    /**
     * Checks wether or not the given ItemStack can be found in the provided ItemStack array.
     *
     * @param stack  The ItemStack it should search for.
     * @param stacks The ItemStack array which should be checked.
     *
     * @return true, if the ItemStack can be found, false otherwise.
     */
    public static boolean isStackInArray(@Nonnull final ItemStack stack, ItemStack... stacks) {
        return Arrays.stream(stacks).anyMatch(currStack -> ItemStack.isSameItem(stack, currStack));
    }

    public static boolean isStackInList(@Nonnull final ItemStack stack, List<ItemStack> stacks) {
        return stacks.stream().anyMatch(currStack -> ItemStack.isSameItem(stack, currStack));
    }

    public static boolean canStack(@Nonnull ItemStack stack1, @Nonnull ItemStack stack2, boolean consumeAll) {
        return !isValid(stack1) || !isValid(stack2)
               || (stack1.isStackable() && ItemStack.isSameItemSameComponents(stack1, stack2)
                   && (!consumeAll || stack1.getCount() + stack2.getCount() <= stack1.getMaxStackSize()));
    }

//    public static ListNBT writeItemStacksToTag(ItemStack[] items, int maxQuantity) {
//        return writeItemStacksToTag(Arrays.asList(items), maxQuantity, null);
//    }
//
//    public static ListNBT writeItemStacksToTag(ItemStack[] items, int maxQuantity, SlotCallback callbackMethod) {
//        return writeItemStacksToTag(Arrays.asList(items), maxQuantity, callbackMethod);
//    }
//
//    public static ListNBT writeItemStacksToTag(List<ItemStack> items, int maxQuantity) {
//        return writeItemStacksToTag(items, maxQuantity, null);
//    }
//
//    public static ListNBT writeItemStacksToTag(List<ItemStack> items, int maxQuantity, SlotCallback callbackMethod) {
//        ListNBT tagList = new ListNBT();
//
//        for( int slot = 0, max = items.size(); slot < max; slot++ ) {
//            ItemStack stack = items.get(slot).copy();
//            if( isValid(stack) ) {
//                stack.setCount(Math.min(stack.getCount(), maxQuantity));
//
//                CompoundNBT tag = new CompoundNBT();
//                tag.putShort(NBT_SLOT, (short) slot);
//                stack.save(tag);
//
//                if( callbackMethod != null ) {
//                    CompoundNBT stackNbt = new CompoundNBT();
//                    callbackMethod.accept(stack, slot, stackNbt);
//                    tag.put(NBT_STACK_TAG, stackNbt);
//                }
//
//                tagList.add(tag);
//            }
//        }
//
//        return tagList;
//    }
//
//    public static void readItemStacksFromTag(ItemStack[] items, ListNBT tagList) {
//        readItemStacksFromTag((slot, itm) -> items[slot] = itm, tagList, null);
//    }
//
//    public static void readItemStacksFromTag(ItemStack[] items, ListNBT tagList, SlotCallback callbackMethod) {
//        readItemStacksFromTag((slot, itm) -> items[slot] = itm, tagList, callbackMethod);
//    }
//
//    public static void readItemStacksFromTag(List<ItemStack> items, ListNBT tagList) {
//        readItemStacksFromTag(items::set, tagList, null);
//    }
//
//    public static void readItemStacksFromTag(List<ItemStack> items, ListNBT tagList, SlotCallback callbackMethod) {
//        readItemStacksFromTag(items::set, tagList, callbackMethod);
//    }
//
//    private static void readItemStacksFromTag(BiConsumer<Short, ItemStack> setItem, ListNBT tagList, SlotCallback callbackMethod) {
//        for( int i = 0; i < tagList.size(); i++ ) {
//            CompoundNBT tag = tagList.getCompound(i);
//            short slot = tag.getShort(NBT_SLOT);
//            ItemStack stack = ItemStack.of(tag);
//
//            setItem.accept(slot, stack);
//
//            if( callbackMethod != null && tag.contains(NBT_STACK_TAG) ) {
//                callbackMethod.accept(stack, slot, tag.getCompound(NBT_STACK_TAG));
//            }
//        }
//    }

    public static void dropBlockItem(ItemStack stack, Level world, BlockPos pos) {
        if( isValid(stack) ) {
            float xOff = MiscUtils.RNG.randomFloat() * 0.8F + 0.1F;
            float yOff = MiscUtils.RNG.randomFloat() * 0.8F + 0.1F;
            float zOff = MiscUtils.RNG.randomFloat() * 0.8F + 0.1F;

            ItemEntity item = new ItemEntity(world, (pos.getX() + xOff), (pos.getY() + yOff), (pos.getZ() + zOff), stack.copy());

            final float motionSpeed = 0.05F;
            item.setDeltaMovement(((float) MiscUtils.RNG.randomGaussian() * motionSpeed),
                                  ((float) MiscUtils.RNG.randomGaussian() * motionSpeed + 0.2F),
                                  ((float) MiscUtils.RNG.randomGaussian() * motionSpeed));
            world.addFreshEntity(item);
        }
    }

    public static NonNullList<ItemStack> getCompactItems(NonNullList<ItemStack> items, int maxInvStackSize) {
        return getCompactItems(items, maxInvStackSize, null);
    }

    public static NonNullList<ItemStack> getCompactItems(NonNullList<ItemStack> items, int maxInvStackSize, Integer maxStackSize) {
        NonNullList<ItemStack> cmpItems = NonNullList.create();

        items.sort((i1, i2) -> ItemStack.isSameItemSameComponents(i1, i2) ? 0 : i1.getDescriptionId().compareTo(i2.getDescriptionId()));
        items.forEach(v -> {
            int cmpSize = cmpItems.size();
            if( cmpSize < 1 ) {
                cmpItems.add(v.copy());
            } else {
                ItemStack cs = cmpItems.get(cmpSize - 1);
                if( ItemStack.isSameItemSameComponents(cs, v) ) {
                    int rest = Math.min(MiscUtils.get(maxStackSize, cs::getMaxStackSize), maxInvStackSize) - cs.getCount();
                    if( rest >= v.getCount() ) {
                        cs.grow(v.getCount());
                    } else {
                        cs.grow(rest);
                        ItemStack restStack = v.copy();
                        restStack.shrink(rest);
                        cmpItems.add(restStack);
                    }
                } else {
                    cmpItems.add(v.copy());
                }
            }
        });

        return cmpItems;
    }

//    @FunctionalInterface
//    public interface SlotCallback
//    {
//        void accept(ItemStack stack, int slot, CompoundNBT nbt);
//    }
}
