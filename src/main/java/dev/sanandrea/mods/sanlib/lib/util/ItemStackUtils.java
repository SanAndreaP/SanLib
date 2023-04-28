/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2016-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.sanlib.lib.util;

import net.minecraft.block.Block;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * An utility class for ItemStacks
 */
@SuppressWarnings({"unused", "SameParameterValue", "WeakerAccess", "ObjectEquality"})
public final class ItemStackUtils
{
    private static final String NBT_STACK_TAG = "StackNBT";
    private static final String NBT_SLOT = "Slot";

    private ItemStackUtils() { }

    /**
     * Checks if an ItemStack is a valid stack.
     * <p>An ItemStack is valid, if:
     * <ul>
     *     <li>the stack is not {@code null}</li>
     *     <li>the item of the stack is not {@code null}</li>
     *     <li>the item is not {@link net.minecraft.block.Blocks#AIR}</li>
     *     <li>the stack size is &gt; 0</li>
     * </ul>
     * </p>
     * @param stack The ItemStack to be checked.
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
     * @param stack The ItemStack to be checked.
     * @param item The item that should be held in the ItemStack
     * @return {@code true}, if the stack is valid and contains the specified item, {@code false} otherwise
     * @see #isValid(ItemStack)
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isItem(@Nonnull ItemStack stack, Item item) {
        return isValid(stack) && stack.getItem() == item;
    }

    /**
     * Checks if an ItemStack is valid and contains a specific block.
     * @param stack The ItemStack to be checked.
     * @param block The block that should be held in the ItemStack
     * @return {@code true}, if the stack is valid and contains the specified block, {@code false} otherwise
     * @see #isValid(ItemStack)
     */
    public static boolean isBlock(@Nonnull ItemStack stack, Block block) {
        return isValid(stack) && Block.byItem(stack.getItem()) == block;
    }

    /**
     * Checks wether or not 2 ItemStacks are equal. This does not compare stack sizes! This compares NBTTagCompounds!
     * @param is1 The first ItemStack
     * @param is2 The second ItemStack
     * @return {@code true}, if the stacks are considered equal, {@code false} otherwise
     */
    public static boolean areEqual(@Nonnull ItemStack is1, @Nonnull ItemStack is2) {
        return areEqual(is1, is2, false, true);
    }

    /**
     * Checks wether or not 2 ItemStacks are equal. This does not compare stack sizes!
     * @param is1 The first ItemStack
     * @param is2 The second ItemStack
     * @param checkNbt A flag to determine wether or not to compare NBTTagCompounds
     * @return {@code true}, if the stacks are considered equal, {@code false} otherwise
     */
    public static boolean areEqual(@Nonnull ItemStack is1, @Nonnull ItemStack is2, boolean checkNbt) {
        return areEqual(is1, is2, false, checkNbt);
    }

    /**
     * Checks wether or not 2 ItemStacks are equal.
     * @param is1 The first ItemStack
     * @param is2 The second ItemStack
     * @param checkStackSize A flag to determine wether or not to compare stack sizes
     * @param checkNbt A flag to determine wether or not to compare NBTTagCompounds
     * @return {@code true}, if the stacks are considered equal, {@code false} otherwise
     */
    public static boolean areEqual(@Nonnull ItemStack is1, @Nonnull ItemStack is2, boolean checkStackSize, boolean checkNbt) {
        return areEqualBase(is1, is2, checkStackSize) && (!checkNbt || Objects.equals(is1.getTag(), is2.getTag()));
    }

    public static boolean areEqualNbtFit(@Nonnull ItemStack mainIS, @Nonnull ItemStack otherIS, boolean checkStackSize) {
        return areEqualNbtFit(mainIS, otherIS, checkStackSize, true);
    }

    public static boolean areEqualNbtFit(@Nonnull ItemStack mainIS, @Nonnull ItemStack otherIS, boolean checkStackSize, boolean strict) {
        return areEqualBase(mainIS, otherIS, checkStackSize)
               && ( !otherIS.hasTag() || (mainIS.hasTag() && NBTUtils.doesNbtContainOther(mainIS.getTag(), otherIS.getTag(), strict)) );
    }

    private static boolean areEqualBase(@Nonnull ItemStack is1, @Nonnull ItemStack is2, boolean checkStackSize) {
        if( !isValid(is1) && !isValid(is2) ) {
            return true;
        }

        if( !isValid(is2) || !isItem(is1, is2.getItem()) ) {
            return false;
        }

        if( is1.getDamageValue() != is2.getDamageValue() ) {
            return false;
        }

        return !(checkStackSize && is1.getCount() != is2.getCount());
    }

    /**
     * Writes the ItemStack as a new NBTTagCompound to the specified NBTTagCompound with the tagName as key.
     * @param stack The ItemStack to write.
     * @param tag The NBTTagCompound to be written.
     * @param tagName The key for the tag.
     */
    public static void writeStackToTag(@Nonnull ItemStack stack, CompoundNBT tag, String tagName) {
        CompoundNBT stackTag = new CompoundNBT();
        stack.save(stackTag);
        tag.put(tagName, stackTag);
    }

    /**
     * Checks wether or not the given ItemStack can be found in the provided ItemStack array.
     * @param stack The ItemStack it should search for.
     * @param stacks The ItemStack array which should be checked.
     * @return true, if the ItemStack can be found, false otherwise.
     */
    public static boolean isStackInArray(@Nonnull final ItemStack stack, ItemStack... stacks) {
        return Arrays.stream(stacks).anyMatch(currStack -> areEqual(stack, currStack));
    }

    public static boolean isStackInList(@Nonnull final ItemStack stack, List<ItemStack> stacks) {
        return stacks.stream().anyMatch(currStack -> areEqual(stack, currStack));
    }

    public static boolean canStack(@Nonnull ItemStack stack1, @Nonnull ItemStack stack2, boolean consumeAll) {
        return !isValid(stack1) || !isValid(stack2)
                || (stack1.isStackable() && areEqual(stack1, stack2, false, true)
                    && (!consumeAll || stack1.getCount() + stack2.getCount() <= stack1.getMaxStackSize()));
    }

    public static ListNBT writeItemStacksToTag(ItemStack[] items, int maxQuantity) {
        return writeItemStacksToTag(Arrays.asList(items), maxQuantity, null);
    }

    public static ListNBT writeItemStacksToTag(ItemStack[] items, int maxQuantity, SlotCallback callbackMethod) {
        return writeItemStacksToTag(Arrays.asList(items), maxQuantity, callbackMethod);
    }

    public static ListNBT writeItemStacksToTag(List<ItemStack> items, int maxQuantity) {
        return writeItemStacksToTag(items, maxQuantity, null);
    }

    public static ListNBT writeItemStacksToTag(List<ItemStack> items, int maxQuantity, SlotCallback callbackMethod) {
        ListNBT tagList = new ListNBT();

        for( int slot = 0, max = items.size(); slot < max; slot++ ) {
            ItemStack stack = items.get(slot).copy();
            if( isValid(stack) ) {
                stack.setCount(Math.min(stack.getCount(), maxQuantity));

                CompoundNBT tag = new CompoundNBT();
                tag.putShort(NBT_SLOT, (short) slot);
                stack.save(tag);

                if( callbackMethod != null ) {
                    CompoundNBT stackNbt = new CompoundNBT();
                    callbackMethod.accept(stack, slot, stackNbt);
                    tag.put(NBT_STACK_TAG, stackNbt);
                }

                tagList.add(tag);
            }
        }

        return tagList;
    }

    public static void readItemStacksFromTag(ItemStack[] items, ListNBT tagList) {
        readItemStacksFromTag((slot, itm) -> items[slot] = itm, tagList, null);
    }

    public static void readItemStacksFromTag(ItemStack[] items, ListNBT tagList, SlotCallback callbackMethod) {
        readItemStacksFromTag((slot, itm) -> items[slot] = itm, tagList, callbackMethod);
    }

    public static void readItemStacksFromTag(List<ItemStack> items, ListNBT tagList) {
        readItemStacksFromTag(items::set, tagList, null);
    }

    public static void readItemStacksFromTag(List<ItemStack> items, ListNBT tagList, SlotCallback callbackMethod) {
        readItemStacksFromTag(items::set, tagList, callbackMethod);
    }

    private static void readItemStacksFromTag(BiConsumer<Short, ItemStack> setItem, ListNBT tagList, SlotCallback callbackMethod) {
        for( int i = 0; i < tagList.size(); i++ ) {
            CompoundNBT tag = tagList.getCompound(i);
            short slot = tag.getShort(NBT_SLOT);
            ItemStack stack = ItemStack.of(tag);

            setItem.accept(slot, stack);

            if( callbackMethod != null && tag.contains(NBT_STACK_TAG) ) {
                callbackMethod.accept(stack, slot, tag.getCompound(NBT_STACK_TAG));
            }
        }
    }

    public static void dropBlockItem(ItemStack stack, World world, BlockPos pos) {
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

        items.sort((i1, i2) -> ItemStackUtils.areEqual(i1, i2, false, true) ? 0 : i1.getDescriptionId().compareTo(i2.getDescriptionId()));
        items.forEach(v -> {
            int cmpSize = cmpItems.size();
            if( cmpSize < 1 ) {
                cmpItems.add(v.copy());
            } else {
                ItemStack cs = cmpItems.get(cmpSize - 1);
                if( ItemStackUtils.areEqual(cs, v, false, true) ) {
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

    @FunctionalInterface
    public interface SlotCallback
    {
        void accept(ItemStack stack, int slot, CompoundNBT nbt);
    }
}
