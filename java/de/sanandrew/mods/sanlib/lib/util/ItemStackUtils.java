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
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Objects;

/**
 * An utility class for ItemStacks
 */
@SuppressWarnings("unused")
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
    public static boolean isValid(ItemStack stack) {
        //noinspection ConstantConditions
        return stack != null && stack.getItem() != null && Block.getBlockFromItem(stack.getItem()) != Blocks.AIR && stack.stackSize > 0;
    }

    /**
     * Checks if an ItemStack is valid and contains a specific item.
     * @param stack The ItemStack to be checked.
     * @param item The item that should be held in the ItemStack
     * @return {@code true}, if the stack is valid and contains the specified item, {@code false} otherwise
     * @see #isValid(ItemStack)
     */
    public static boolean isItem(ItemStack stack, Item item) {
        return isValid(stack) && stack.getItem() == item;
    }

    /**
     * Checks if an ItemStack is valid and contains a specific block.
     * @param stack The ItemStack to be checked.
     * @param block The block that should be held in the ItemStack
     * @return {@code true}, if the stack is valid and contains the specified block, {@code false} otherwise
     * @see #isValid(ItemStack)
     */
    public static boolean isBlock(ItemStack stack, Block block) {
        return isValid(stack) && Block.getBlockFromItem(stack.getItem()) == block;
    }

    public static boolean areEqual(ItemStack is1, ItemStack is2) {
        return areEqual(is1, is2, false, true);
    }

    public static boolean areEqual(ItemStack is1, ItemStack is2, boolean checkNbt) {
        return areEqual(is1, is2, false, checkNbt);
    }

    public static boolean areEqual(ItemStack is1, ItemStack is2, boolean checkStackSize, boolean checkNbt) {
        if( is1 == null && is2 == null ) {
            return true;
        }

        if( !isValid(is2) || !isItem(is1, is2.getItem()) ) {
            return false;
        }

        assert is1 != null;
        return !(checkStackSize && is1.stackSize != is2.stackSize) && (!checkNbt || Objects.equals(is1.getTagCompound(), is2.getTagCompound()));
    }

    public static void writeStackToTag(ItemStack stack, NBTTagCompound tag, String tagName) {
        NBTTagCompound stackTag = new NBTTagCompound();
        stack.writeToNBT(stackTag);
        tag.setTag(tagName, stackTag);
    }
}
