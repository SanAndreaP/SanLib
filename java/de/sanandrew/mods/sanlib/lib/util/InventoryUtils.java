/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.sanlib.lib.util;

import de.sanandrew.mods.sanlib.lib.Tuple;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import java.util.ArrayList;
import java.util.List;

public final class InventoryUtils
{
    /**
     * Gets the slot number and ItemStack from the provided Inventory that is equal to the given ItemStack.
     * @param stack The ItemStack it should search for.
     * @param inv The inventory which should be checked.
     * @param checkNbt A flag to determine wether or not to compare the NBTTagCompounds
     * @return a Tuple with the slot number ({@code Integer - 0})<br>
     *         and the ItemStack instance from the Inventory ({@code ItemStack - 1})
     */
    public static Tuple getSimilarStackFromInventory(ItemStack stack, IInventory inv, boolean checkNbt) {
        if( !ItemStackUtils.isValid(stack) ) {
            return null;
        }

        if( inv == null ) {
            return null;
        }

        int size = inv.getSizeInventory();
        for( int i = 0; i < size; i++ ) {
            ItemStack invStack = inv.getStackInSlot(i);
            if( ItemStackUtils.isValid(invStack) ) {
                if( ItemStackUtils.areEqual(stack, invStack, checkNbt) ) {
                    return new Tuple(i, invStack);
                }
            }
        }

        return null;
    }

    /**
     * Checks wether or not the given ItemStack can fit completely or partially into the provided Inventory.
     * @param is
     * @param inv
     * @param checkNBT
     * @param maxStackSize
     * @return
     */
    public static boolean canStackFitInInventory(ItemStack is, IInventory inv, boolean checkNBT, int maxStackSize) {
        return canStackFitInInventory(is, inv, checkNBT, maxStackSize, 0, inv.getSizeInventory() - (inv instanceof InventoryPlayer ? 4 : 0));
    }

    public static boolean canStackFitInInventory(ItemStack is, IInventory inv, boolean checkNBT, int maxStackSize, int begin, int end) {
        ItemStack stack = is.copy();
        List<ItemStack> invStacks = new ArrayList<>();
        for( int i = begin; i < end; i++ ) {
            ItemStack invIS = inv.getStackInSlot(i);
            if( invIS != null && ItemStackUtils.areEqual(is, invIS, checkNBT) ) {
                int fit = Math.min(invIS.getMaxStackSize(), maxStackSize) - invIS.stackSize;
                if( fit >= stack.stackSize ) {
                    return true;
                } else {
                    stack.stackSize -= fit;
                }
            } else if( invIS == null && inv.isItemValidForSlot(i, stack) ) {
                int max = Math.min(stack.getMaxStackSize(), maxStackSize);
                if( stack.stackSize - max <= 0 ) {
                    return true;
                } else {
                    stack.stackSize -= max;
                }
            }
        }

        return false;
    }

    public static ItemStack addStackToInventory(ItemStack is, IInventory inv) {
        return addStackToInventory(is, inv, true, inv.getInventoryStackLimit(), 0, inv.getSizeInventory() - (inv instanceof InventoryPlayer ? 4 : 0));
    }

    public static ItemStack addStackToInventory(ItemStack is, IInventory inv, boolean checkNBT) {
        return addStackToInventory(is, inv, checkNBT, inv.getInventoryStackLimit(), 0, inv.getSizeInventory() - (inv instanceof InventoryPlayer ? 4 : 0));
    }

    public static ItemStack addStackToInventory(ItemStack is, IInventory inv, boolean checkNBT, int maxStackSize) {
        return addStackToInventory(is, inv, checkNBT, maxStackSize, 0, inv.getSizeInventory() - (inv instanceof InventoryPlayer ? 4 : 0));
    }

    public static ItemStack addStackToInventory(ItemStack is, IInventory inv, boolean checkNBT, int maxStackSize, int begin, int end) {
        for( int i = begin; i < end && is != null; ++i ) {
            ItemStack invIS = inv.getStackInSlot(i);
            int rest;
            if( invIS != null && ItemStackUtils.areEqual(is, invIS, checkNBT) ) {
                rest = is.stackSize + invIS.stackSize;
                int maxStack = Math.min(invIS.getMaxStackSize(), maxStackSize);
                if( rest <= maxStack ) {
                    invIS.stackSize = rest;
                    inv.setInventorySlotContents(i, invIS.copy());
                    is = null;
                    break;
                }

                int rest1 = rest - maxStack;
                invIS.stackSize = maxStack;
                inv.setInventorySlotContents(i, invIS.copy());
                is.stackSize = rest1;
            } else if( invIS == null && inv.isItemValidForSlot(i, is) ) {
                if( is.stackSize <= maxStackSize ) {
                    inv.setInventorySlotContents(i, is.copy());
                    is = null;
                    break;
                }

                rest = is.stackSize - maxStackSize;
                is.stackSize = maxStackSize;
                inv.setInventorySlotContents(i, is.copy());
                is.stackSize = rest;
            }
        }

        return is;
    }

    public static ItemStack addStackToCapability(ItemStack is, ICapabilityProvider provider, EnumFacing facing, boolean simulate) {
        return addStackToCapability(is, provider, facing, simulate, Integer.MAX_VALUE, 0, Integer.MAX_VALUE);
    }

    public static ItemStack addStackToCapability(ItemStack is, ICapabilityProvider provider, EnumFacing facing, boolean simulate, int maxStackSize) {
        return addStackToCapability(is, provider, facing, simulate, maxStackSize, 0, Integer.MAX_VALUE);
    }

    public static ItemStack addStackToCapability(ItemStack is, ICapabilityProvider provider, EnumFacing facing, boolean simulate, int maxStackSize, int begin, int end) {
        if( is != null && provider.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing) ) {
            IItemHandler handler = provider.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing);
            maxStackSize = Math.min(maxStackSize, is.stackSize);
            end = Math.min(end, handler.getSlots());

            for( int i = begin; i < end; i++ ) {
                ItemStack maxStack = is.copy();
                maxStack.stackSize = maxStackSize;
                maxStack = handler.insertItem(i, maxStack, simulate);
                if( maxStack == null ) {
                    is.stackSize -= maxStackSize;
                }
                if( is.stackSize <= 0 ) {
                    return null;
                }
            }
        }

        return is;
    }
}
