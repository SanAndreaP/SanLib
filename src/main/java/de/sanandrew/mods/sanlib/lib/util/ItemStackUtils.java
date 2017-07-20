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
     * If one of the ItemStacks has a damage value equal to {@link OreDictionary#WILDCARD_VALUE}, their damage value isn't checked against eachother,
     * otherwise it'll check if both are equal.
     * @param is1 The first ItemStack
     * @param is2 The second ItemStack
     * @param checkStackSize A flag to determine wether or not to compare stack sizes
     * @param checkNbt A flag to determine wether or not to compare NBTTagCompounds
     * @return {@code true}, if the stacks are considered equal, {@code false} otherwise
     */
    public static boolean areEqual(@Nonnull ItemStack is1, @Nonnull ItemStack is2, boolean checkStackSize, boolean checkNbt) {
        return areEqual(is1, is2, checkStackSize, checkNbt, true);
    }

    public static boolean areEqual(@Nonnull ItemStack is1, @Nonnull ItemStack is2, boolean checkStackSize, boolean checkNbt, boolean checkDmg) {
        if( is1.isEmpty() && is2.isEmpty() ) {
            return true;
        }

        if( !isValid(is2) || !isItem(is1, is2.getItem()) ) {
            return false;
        }

        //noinspection SimplifiableIfStatement
        if( checkDmg && (is1.getItemDamage() != OreDictionary.WILDCARD_VALUE || is2.getItemDamage() != OreDictionary.WILDCARD_VALUE) && is1.getItemDamage() != is2.getItemDamage() ) {
            return false;
        }

        return !(checkStackSize && is1.getCount() != is2.getCount()) && (!checkNbt || Objects.equals(is1.getTagCompound(), is2.getTagCompound()));
    }

    /**
     * Writes the ItemStack as a new NBTTagCompound to the specified NBTTagCompound with the tagName as key.
     * @param stack The ItemStack to write.
     * @param tag The NBTTagCompound to be written.
     * @param tagName The key for the tag.
     */
    public static void writeStackToTag(@Nonnull ItemStack stack, NBTTagCompound tag, String tagName) {
        NBTTagCompound stackTag = new NBTTagCompound();
        stack.writeToNBT(stackTag);
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
        return stack1.isEmpty() || stack2.isEmpty()
                || (stack1.isStackable() && areEqual(stack1, stack2, false, true, !stack2.getHasSubtypes())
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
                stack.writeToNBT(tag);

                if( maxQuantity > Short.MAX_VALUE ) {
                    tag.setInteger("Quantity", Math.min(stack.getCount(), maxQuantity));
                } else if( maxQuantity > Byte.MAX_VALUE ) {
                    tag.setShort("Quantity", (short) Math.min(stack.getCount(), maxQuantity));
                } else {
                    tag.setByte("Quantity", (byte) Math.min(stack.getCount(), maxQuantity));
                }

                if( callbackMethod != null ) {
                    NBTTagCompound stackNbt = new NBTTagCompound();
                    callbackMethod.accept(stack, stackNbt);
                    tag.setTag("StackNBT", stackNbt);
                }

                tagList.appendTag(tag);
            }
        }

        return tagList;
    }

    public static void readItemStacksFromTag(ItemStack[] items, NBTTagList tagList) {
        readItemStacksFromTag(items, tagList, null);
    }

    public static void readItemStacksFromTag(ItemStack[] items, NBTTagList tagList, BiConsumer<ItemStack, NBTTagCompound> callbackMethod) {
        for( int i = 0; i < tagList.tagCount(); i++ ) {
            NBTTagCompound tag = tagList.getCompoundTagAt(i);
            short slot = tag.getShort("Slot");
            items[slot] = new ItemStack(tag);
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
        for( int i = 0; i < tagList.tagCount(); i++ ) {
            NBTTagCompound tag = tagList.getCompoundTagAt(i);
            short slot = tag.getShort("Slot");
            items.set(i, new ItemStack(tag));
            if( tag.hasKey("Quantity") ) {
                items.get(i).setCount(((NBTPrimitive) tag.getTag("Quantity")).getInt());
            }

            if( callbackMethod != null && tag.hasKey("StackNBT") ) {
                callbackMethod.accept(items.get(i), (NBTTagCompound) tag.getTag("StackNBT"));
            }
        }
    }
}
