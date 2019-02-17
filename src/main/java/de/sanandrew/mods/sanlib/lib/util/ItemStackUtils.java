/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.sanlib.lib.util;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTPrimitive;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.oredict.OreDictionary;

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
    /**
     * Checks if an ItemStack is a valid stack.
     * <p>An ItemStack is valid, if:
     * <ul>
     *     <li>the stack is not {@code null}</li>
     *     <li>the item of the stack is not {@code null}</li>
     *     <li>the item is not {@link Blocks#AIR}</li>
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
        return isValid(stack) && Block.getBlockFromItem(stack.getItem()) == block;
    }

    /**
     * Checks wether or not 2 ItemStacks are equal. This does not compare stack sizes! This returns true, if both are invalid/empty.
     * @param is1 The first ItemStack
     * @param is2 The second ItemStack
     * @return {@code true}, if the stacks are considered equal, {@code false} otherwise
     */
    public static boolean areEqual(@Nonnull ItemStack is1, @Nonnull ItemStack is2) {
        return (!isValid(is1) && !isValid(is2)) ||ItemStack.areItemsEqual(is1, is2);
    }

    /**
     * Writes the ItemStack as a new NBTTagCompound to the specified NBTTagCompound with the tagName as key.
     * @param stack The ItemStack to write.
     * @param tag The NBTTagCompound to be written.
     * @param tagName The key for the tag.
     */
    public static void writeStackToTag(@Nonnull ItemStack stack, NBTTagCompound tag, String tagName) {
        NBTTagCompound stackTag = new NBTTagCompound();
        stack.write(stackTag);
        tag.setTag(tagName, stackTag);
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
                || (stack1.isStackable() && areEqual(stack1, stack2)
                    && (!consumeAll || stack1.getCount() + stack2.getCount() <= stack1.getMaxStackSize()));
    }

    public static NBTTagList writeItemStacksToTag(ItemStack[] items, int maxQuantity) {
        return writeItemStacksToTag(Arrays.asList(items), maxQuantity, null);
    }

    public static NBTTagList writeItemStacksToTag(ItemStack[] items, int maxQuantity, BiConsumer<ItemStack, NBTTagCompound> callbackMethod) {
        return writeItemStacksToTag(Arrays.asList(items), maxQuantity, callbackMethod);
    }

    public static NBTTagList writeItemStacksToTag(List<ItemStack> items, int maxQuantity) {
        return writeItemStacksToTag(items, maxQuantity, null);
    }

    public static NBTTagList writeItemStacksToTag(List<ItemStack> items, int maxQuantity, BiConsumer<ItemStack, NBTTagCompound> callbackMethod) {
        NBTTagList tagList = new NBTTagList();

        for( int i = 0, max = items.size(); i < max; i++ ) {
            ItemStack stack = items.get(i);
            if( isValid(stack) ) {
                NBTTagCompound tag = new NBTTagCompound();
                tag.setShort("Slot", (short) i);
                stack.write(tag);

                if( maxQuantity > 0 ) {
                    if (maxQuantity > Short.MAX_VALUE) {
                        tag.setInt("Quantity", Math.min(stack.getCount(), maxQuantity));
                    } else if (maxQuantity > Byte.MAX_VALUE) {
                        tag.setShort("Quantity", (short) Math.min(stack.getCount(), maxQuantity));
                    } else {
                        tag.setByte("Quantity", (byte) Math.min(stack.getCount(), maxQuantity));
                    }
                }

                if( callbackMethod != null ) {
                    NBTTagCompound stackNbt = new NBTTagCompound();
                    callbackMethod.accept(stack, stackNbt);
                    tag.setTag("StackNBT", stackNbt);
                }

                tagList.add(tag);
            }
        }

        return tagList;
    }

    public static void readItemStacksFromTag(ItemStack[] items, NBTTagList tagList) {
        readItemStacksFromTag(items, tagList, null);
    }

    public static void readItemStacksFromTag(ItemStack[] items, NBTTagList tagList, BiConsumer<ItemStack, NBTTagCompound> callbackMethod) {
        for( int i = 0; i < tagList.size(); i++ ) {
            NBTTagCompound tag = tagList.getCompound(i);
            short slot = tag.getShort("Slot");
            items[slot] = ItemStack.read(tag);
            if( tag.hasKey("Quantity") ) {
                items[slot].setCount(((NBTPrimitive) tag.getTag("Quantity")).getInt());
            }

            if( callbackMethod != null && tag.hasKey("StackNBT") ) {
                callbackMethod.accept(items[slot], (NBTTagCompound) tag.getTag("StackNBT"));
            }
        }
    }

    public static void readItemStacksFromTag(List<ItemStack> items, NBTTagList tagList) {
        readItemStacksFromTag(items, tagList, null);
    }

    public static void readItemStacksFromTag(List<ItemStack> items, NBTTagList tagList, BiConsumer<ItemStack, NBTTagCompound> callbackMethod) {
        for( int i = 0; i < tagList.size(); i++ ) {
            NBTTagCompound tag = tagList.getCompound(i);
            short slot = tag.getShort("Slot");
            items.set(slot, ItemStack.read(tag));
            if( tag.hasKey("Quantity") ) {
                items.get(slot).setCount(((NBTPrimitive) tag.getTag("Quantity")).getInt());
            }

            if( callbackMethod != null && tag.hasKey("StackNBT") ) {
                callbackMethod.accept(items.get(slot), (NBTTagCompound) tag.getTag("StackNBT"));
            }
        }
    }
}
