////////////////////////////////////////////////////////////////////////////////
// This file is subject to the terms and conditions defined in the             /
// file '.github/LICENSE.md', which is part of this source code package.       /
////////////////////////////////////////////////////////////////////////////////

package de.sanandrew.mods.sanlib.lib.util;

import de.sanandrew.mods.sanlib.lib.Tuple;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import java.util.Objects;

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
            } else if( !ItemStackUtils.isValid(invIS) && inv.isItemValidForSlot(i, stack) ) {
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
        for( int i = begin; i < end && ItemStackUtils.isValid(is); ++i ) {
            ItemStack invIS = inv.getStackInSlot(i);
            int rest;
            if( ItemStackUtils.areEqual(is, invIS, checkNBT) ) {
                rest = is.getCount() + invIS.getCount();
                int maxStack = Math.min(invIS.getMaxStackSize(), maxStackSize);
                if( rest <= maxStack ) {
                    invIS.setCount(rest);
                    inv.setInventorySlotContents(i, invIS.copy());
                    is = ItemStackUtils.getEmpty();
                    break;
                }

                int rest1 = rest - maxStack;
                invIS.setCount(maxStack);
                inv.setInventorySlotContents(i, invIS.copy());
                is.setCount(rest1);
            } else if( !ItemStackUtils.isValid(invIS) && inv.isItemValidForSlot(i, is) ) {
                if( is.getCount() <= maxStackSize ) {
                    inv.setInventorySlotContents(i, is.copy());
                    is = ItemStackUtils.getEmpty();
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
        if( ItemStackUtils.isValid(is) && provider.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing) ) {
            IItemHandler handler = Objects.requireNonNull(provider.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing));

            maxStackSize = Math.min(maxStackSize, is.getCount());
            end = Math.min(end, handler.getSlots());

            for( int i = begin; i < end; i++ ) {
                ItemStack remain = is.copy();
                remain.setCount(maxStackSize);
                remain = handler.insertItem(i, remain, simulate);

                is.setCount(is.getCount() - maxStackSize + (ItemStackUtils.isValid(remain) ? remain.getCount() : 0));
                maxStackSize = Math.min(maxStackSize, is.getCount());

                if( is.getCount() <= 0 ) {
                    return ItemStackUtils.getEmpty();
                }
            }
        }

        return is;
    }

    public static boolean mergeItemStack(Container container, @Nonnull ItemStack stack, int beginSlot, int endSlot, boolean reverse) {
        boolean slotChanged = false;
        int start = beginSlot;

        if( reverse ) {
            start = endSlot - 1;
        }

        Slot      slot;
        ItemStack slotStack;

        if( stack.isStackable() ) {
            while( stack.getCount() > 0 && (!reverse && start < endSlot || reverse && start >= beginSlot) ) {
                slot = container.inventorySlots.get(start);
                slotStack = slot.getStack();

                if( ItemStackUtils.areEqual(slotStack, stack) && slot.isItemValid(stack) ) {
                    int combStackSize = slotStack.getCount() + stack.getCount();
                    int maxStackSize = Math.min(stack.getMaxStackSize(), slot.getItemStackLimit(stack));

                    if( combStackSize <= maxStackSize ) {
                        stack.setCount(0);
                        slotStack.setCount(combStackSize);
                        slot.onSlotChanged();
                        slotChanged = true;
                    } else if( slotStack.getCount() < maxStackSize ) {
                        stack.shrink(maxStackSize - slotStack.getCount());
                        slotStack.setCount(maxStackSize);
                        slot.onSlotChanged();
                        slotChanged = true;
                    }
                }

                if( reverse ) {
                    start--;
                } else {
                    start++;
                }
            }
        }

        if( stack.getCount() > 0 ) {
            if( reverse ) {
                start = endSlot - 1;
            } else {
                start = beginSlot;
            }

            while( stack.getCount() > 0 && (!reverse && start < endSlot || reverse && start >= beginSlot) ) {
                slot = container.inventorySlots.get(start);

                if( !ItemStackUtils.isValid(slot.getStack()) && slot.isItemValid(stack) ) {
                    int maxStackSize = Math.min(stack.getMaxStackSize(), slot.getItemStackLimit(stack));
                    if( stack.getCount() > maxStackSize ) {
                        ItemStack newSlotStack = stack.copy();

                        stack.shrink(maxStackSize);
                        newSlotStack.setCount(maxStackSize);
                        slot.putStack(newSlotStack);
                        slot.onSlotChanged();
                        slotChanged = true;
                    } else {
                        slot.putStack(stack.copy());
                        slot.onSlotChanged();
                        stack.setCount(0);
                        slotChanged = true;
                        break;
                    }
                }

                if( reverse ) {
                    start--;
                } else {
                    start++;
                }
            }
        }

        return slotChanged;
    }

    public static boolean finishTransfer(EntityPlayer player, ItemStack origStack, Slot slot, ItemStack slotStack) {
        if( slotStack.getCount() == 0 ) { // if stackSize of slot got to 0
            slot.putStack(ItemStack.EMPTY);
        } else { // update changed slot stack state
            slot.onSlotChanged();
        }

        if( slotStack.getCount() == origStack.getCount() ) { // if nothing changed stackSize-wise
            return true;
        }

        slot.onTake(player, slotStack);

        return false;
    }

    public static void dropBlockItems(IInventory inv, World world, BlockPos pos) {
        for( int i = 0, max = inv.getSizeInventory(); i < max; i++ ) {
            ItemStackUtils.dropBlockItem(inv.getStackInSlot(i), world, pos);
        }
    }
}
