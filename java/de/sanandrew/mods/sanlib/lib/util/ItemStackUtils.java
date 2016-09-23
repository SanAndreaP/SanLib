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

@SuppressWarnings("unused")
public final class ItemStackUtils
{
    public static boolean isValid(ItemStack stack) {
        //noinspection ConstantConditions
        return stack != null && stack.getItem() != null && Block.getBlockFromItem(stack.getItem()) != Blocks.AIR;
    }

    public static boolean isItem(ItemStack stack, Item item) {
        return isValid(stack) && stack.getItem() == item;
    }

    public static boolean isBlock(ItemStack stack, Block block) {
        return isValid(stack) && Block.getBlockFromItem(stack.getItem()) == block;
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

    public static boolean areEqual(ItemStack is1, ItemStack is2, boolean checkNbt) {
        return areEqual(is1, is2, false, checkNbt);
    }

    public static boolean areEqual(ItemStack is1, ItemStack is2) {
        return areEqual(is1, is2, false, true);
    }

    public static void writeStackToTag(ItemStack stack, NBTTagCompound tag, String tagName) {
        NBTTagCompound stackTag = new NBTTagCompound();
        stack.writeToNBT(stackTag);
        tag.setTag(tagName, stackTag);
    }
}
