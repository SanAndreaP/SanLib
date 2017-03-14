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

import javax.annotation.Nonnull;

@SuppressWarnings("unused")
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
    public static Tuple getSimilarStackFromInventory(@Nonnull ItemStack stack, IInventory inv, boolean checkNbt) {
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
    public static boolean canStackFitInInventory(@Nonnull ItemStack is, IInventory inv, boolean checkNBT, int maxStackSize) {
        return canStackFitInInventory(is, inv, checkNBT, maxStackSize, 0, inv.getSizeInventory() - (inv instanceof InventoryPlayer ? 4 : 0));
    }

    public static boolean canStackFitInInventory(@Nonnull ItemStack is, IInventory inv, boolean checkNBT, int maxStackSize, int begin, int end) {
        ItemStack stack = is.copy();
        for( int i = begin; i < end; i++ ) {
            ItemStack invIS = inv.getStackInSlot(i);
            if( ItemStackUtils.areEqual(is, invIS, checkNBT) ) {
                int fit = Math.min(invIS.getMaxStackSize(), maxStackSize) - invIS.getCount();
                int stackCnt = stack.getCount();
                if( fit >= stackCnt ) {
                    return true;
                } else {
                    stack.setCount(stackCnt - fit);
                }
            } else if( invIS == ItemStack.EMPTY && inv.isItemValidForSlot(i, stack) ) {
                int max = Math.min(stack.getMaxStackSize(), maxStackSize);
                int stackCnt = stack.getCount();
                if( stackCnt - max <= 0 ) {
                    return true;
                } else {
                    stack.setCount(stackCnt - max);
                }
            }
        }

        return false;
    }

    public static ItemStack addStackToInventory(@Nonnull ItemStack is, IInventory inv) {
        return addStackToInventory(is, inv, true, inv.getInventoryStackLimit(), 0, inv.getSizeInventory() - (inv instanceof InventoryPlayer ? 4 : 0));
    }

    public static ItemStack addStackToInventory(@Nonnull ItemStack is, IInventory inv, boolean checkNBT) {
        return addStackToInventory(is, inv, checkNBT, inv.getInventoryStackLimit(), 0, inv.getSizeInventory() - (inv instanceof InventoryPlayer ? 4 : 0));
    }

    public static ItemStack addStackToInventory(@Nonnull ItemStack is, IInventory inv, boolean checkNBT, int maxStackSize) {
        return addStackToInventory(is, inv, checkNBT, maxStackSize, 0, inv.getSizeInventory() - (inv instanceof InventoryPlayer ? 4 : 0));
    }

    @Nonnull
    public static ItemStack addStackToInventory(@Nonnull ItemStack is, IInventory inv, boolean checkNBT, int maxStackSize, int begin, int end) {
        for( int i = begin; i < end && is != ItemStack.EMPTY; ++i ) {
            ItemStack invIS = inv.getStackInSlot(i);
            int rest;
            if( ItemStackUtils.areEqual(is, invIS, checkNBT) ) {
                rest = is.getCount() + invIS.getCount();
                int maxStack = Math.min(invIS.getMaxStackSize(), maxStackSize);
                if( rest <= maxStack ) {
                    invIS.setCount(rest);
                    inv.setInventorySlotContents(i, invIS.copy());
                    is = ItemStack.EMPTY;
                    break;
                }

                int rest1 = rest - maxStack;
                invIS.setCount(maxStack);
                inv.setInventorySlotContents(i, invIS.copy());
                is.setCount(rest1);
            } else if( invIS == ItemStack.EMPTY && inv.isItemValidForSlot(i, is) ) {
                if( is.getCount() <= maxStackSize ) {
                    inv.setInventorySlotContents(i, is.copy());
                    is = ItemStack.EMPTY;
                    break;
                }

                rest = is.getCount() - maxStackSize;
                is.setCount(maxStackSize);
                inv.setInventorySlotContents(i, is.copy());
                is.setCount(rest);
            }
        }

        return is;
    }

    @Nonnull
    public static ItemStack addStackToCapability(@Nonnull ItemStack is, ICapabilityProvider provider, EnumFacing facing, boolean simulate) {
        return addStackToCapability(is, provider, facing, simulate, Integer.MAX_VALUE, 0, Integer.MAX_VALUE);
    }

    @Nonnull
    public static ItemStack addStackToCapability(@Nonnull ItemStack is, ICapabilityProvider provider, EnumFacing facing, boolean simulate, int maxStackSize) {
        return addStackToCapability(is, provider, facing, simulate, maxStackSize, 0, Integer.MAX_VALUE);
    }

    @Nonnull
    public static ItemStack addStackToCapability(@Nonnull ItemStack is, ICapabilityProvider provider, EnumFacing facing, boolean simulate, int maxStackSize, int begin, int end) {
        if( is != ItemStack.EMPTY && provider.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing) ) {
            IItemHandler handler = provider.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing);
            assert handler != null;

            maxStackSize = Math.min(maxStackSize, is.getCount());
            end = Math.min(end, handler.getSlots());

            for( int i = begin; i < end; i++ ) {
                ItemStack maxStack = is.copy();
                maxStack.setCount(maxStackSize);
                maxStack = handler.insertItem(i, maxStack, simulate);
                if( maxStack == ItemStack.EMPTY ) {
                    is.setCount(is.getCount() - maxStackSize);
                }
                if( is.getCount() <= 0 ) {
                    return ItemStack.EMPTY;
                }
            }
        }

        return is;
    }
}
