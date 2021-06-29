////////////////////////////////////////////////////////////////////////////////
// This file is subject to the terms and conditions defined in the             /
// file '.github/LICENSE.md', which is part of this source code package.       /
////////////////////////////////////////////////////////////////////////////////

package de.sanandrew.mods.sanlib.lib.util;

import de.sanandrew.mods.sanlib.lib.Tuple;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
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
    public static SlotDef getSimilarStackFromInventory(@Nonnull ItemStack stack, IInventory inv, boolean checkNbt) {
        if( !ItemStackUtils.isValid(stack) ) {
            return null;
        }

        if( inv == null ) {
            return null;
        }

        int size = inv.getContainerSize();
        for( int i = 0; i < size; i++ ) {
            ItemStack invStack = inv.getItem(i);
            if( ItemStackUtils.isValid(invStack) ) {
                if( ItemStackUtils.areEqual(stack, invStack, checkNbt) ) {
                    return new SlotDef(i, invStack);
                }
            }
        }

        return null;
    }

    /**
     * Checks wether or not the given ItemStack can fit completely or partially into the provided inventory.
     * @param stack The item that should fit
     * @param inv The inventory the item needs to fit in
     * @param checkNBT wether or not to check the NBT of the items
     * @param maxStackSize the maximum size the item should stack to
     * @return <tt>true</tt>, if the item fits inside the inventory, <tt>false</tt> otherwise
     */
    public static boolean canStackFitInInventory(@Nonnull ItemStack stack, IInventory inv, boolean checkNBT, int maxStackSize) {
        return canStackFitInInventory(stack, inv, checkNBT, maxStackSize, 0, inv.getContainerSize() - (inv instanceof PlayerInventory ? 4 : 0));
    }

    public static boolean canStackFitInInventory(@Nonnull ItemStack is, IInventory inv, boolean checkNBT, int maxStackSize, int begin, int end) {
        ItemStack stack = is.copy();
        for( int i = begin; i < end; i++ ) {
            ItemStack invIS = inv.getItem(i);
            if( ItemStackUtils.areEqual(is, invIS, checkNBT) ) {
                int fit = Math.min(invIS.getMaxStackSize(), maxStackSize) - invIS.getCount();
                int stackCnt = stack.getCount();
                if( fit >= stackCnt ) {
                    return true;
                } else {
                    stack.setCount(stackCnt - fit);
                }
            } else if( !ItemStackUtils.isValid(invIS) && inv.canPlaceItem(i, stack) ) {
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
        return addStackToInventory(is, inv, true, inv.getMaxStackSize(), 0, inv.getContainerSize() - (inv instanceof PlayerInventory ? 4 : 0));
    }

    public static ItemStack addStackToInventory(@Nonnull ItemStack is, IInventory inv, boolean checkNBT) {
        return addStackToInventory(is, inv, checkNBT, inv.getMaxStackSize(), 0, inv.getContainerSize() - (inv instanceof PlayerInventory ? 4 : 0));
    }

    public static ItemStack addStackToInventory(@Nonnull ItemStack is, IInventory inv, boolean checkNBT, int maxStackSize) {
        return addStackToInventory(is, inv, checkNBT, maxStackSize, 0, inv.getContainerSize() - (inv instanceof PlayerInventory ? 4 : 0));
    }

    @Nonnull
    public static ItemStack addStackToInventory(@Nonnull ItemStack is, IInventory inv, boolean checkNBT, int maxStackSize, int begin, int end) {
        for( int i = begin; i < end && ItemStackUtils.isValid(is); ++i ) {
            ItemStack invIS = inv.getItem(i);
            int rest;
            if( ItemStackUtils.areEqual(is, invIS, checkNBT) ) {
                rest = is.getCount() + invIS.getCount();
                int maxStack = Math.min(invIS.getMaxStackSize(), maxStackSize);
                if( rest <= maxStack ) {
                    invIS.setCount(rest);
                    inv.setItem(i, invIS.copy());
                    is = ItemStackUtils.getEmpty();
                    break;
                }

                int rest1 = rest - maxStack;
                invIS.setCount(maxStack);
                inv.setItem(i, invIS.copy());
                is.setCount(rest1);
            } else if( !ItemStackUtils.isValid(invIS) && inv.canPlaceItem(i, is) ) {
                if( is.getCount() <= maxStackSize ) {
                    inv.setItem(i, is.copy());
                    is = ItemStackUtils.getEmpty();
                    break;
                }

                rest = is.getCount() - maxStackSize;
                is.setCount(maxStackSize);
                inv.setItem(i, is.copy());
                is.setCount(rest);
            }
        }

        return is;
    }

    @Nonnull
    public static ItemStack addStackToCapability(@Nonnull ItemStack is, ICapabilityProvider provider, Direction facing, boolean simulate) {
        return addStackToCapability(is, provider, facing, simulate, Integer.MAX_VALUE, 0, Integer.MAX_VALUE);
    }

    @Nonnull
    public static ItemStack addStackToCapability(@Nonnull ItemStack is, ICapabilityProvider provider, Direction facing, boolean simulate, int maxStackSize) {
        return addStackToCapability(is, provider, facing, simulate, maxStackSize, 0, Integer.MAX_VALUE);
    }

    @Nonnull
    @SuppressWarnings("ConstantConditions")
    public static ItemStack addStackToCapability(@Nonnull ItemStack is, ICapabilityProvider provider, Direction facing, boolean simulate, int maxStackSize, int begin, int end) {
        if( ItemStackUtils.isValid(is) ) {
//            LazyOptional<IItemHandler> handlerLO = provider.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing);
//            if( handlerLO. )
            IItemHandler handler = provider.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing).orElse(null);
            if( handler != null ) {
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
                slot = container.slots.get(start);
                slotStack = slot.getItem();

                if( ItemStackUtils.areEqual(slotStack, stack) && slot.mayPlace(stack) ) {
                    int combStackSize = slotStack.getCount() + stack.getCount();
                    int maxStackSize = Math.min(stack.getMaxStackSize(), slot.getMaxStackSize(stack));

                    if( combStackSize <= maxStackSize ) {
                        stack.setCount(0);
                        slotStack.setCount(combStackSize);
                        slot.setChanged();
                        slotChanged = true;
                    } else if( slotStack.getCount() < maxStackSize ) {
                        stack.shrink(maxStackSize - slotStack.getCount());
                        slotStack.setCount(maxStackSize);
                        slot.setChanged();
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
                slot = container.slots.get(start);

                if( !ItemStackUtils.isValid(slot.getItem()) && slot.mayPlace(stack) ) {
                    int maxStackSize = Math.min(stack.getMaxStackSize(), slot.getMaxStackSize(stack));
                    if( stack.getCount() > maxStackSize ) {
                        ItemStack newSlotStack = stack.copy();

                        stack.shrink(maxStackSize);
                        newSlotStack.setCount(maxStackSize);
                        slot.set(newSlotStack);
                        slot.setChanged();
                        slotChanged = true;
                    } else {
                        slot.set(stack.copy());
                        slot.setChanged();
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

    public static boolean finishTransfer(PlayerEntity player, ItemStack origStack, Slot slot, ItemStack slotStack) {
        if( slotStack.getCount() == 0 ) { // if stackSize of slot got to 0
            slot.set(ItemStack.EMPTY);
        } else { // update changed slot stack state
            slot.setChanged();
        }

        if( slotStack.getCount() == origStack.getCount() ) { // if nothing changed stackSize-wise
            return true;
        }

        slot.onTake(player, slotStack);

        return false;
    }

    public static void dropBlockItems(IInventory inv, World world, BlockPos pos) {
        for( int i = 0, max = inv.getContainerSize(); i < max; i++ ) {
            ItemStackUtils.dropBlockItem(inv.getItem(i), world, pos);
        }
    }

    public static final class SlotDef
            extends Tuple
    {
        SlotDef(int i, ItemStack invStack) {
            super(i, invStack);
        }

        public int getSlotId() {
            return this.getValue(0);
        }

        public ItemStack getItem() {
            return this.getValue(1);
        }
    }
}
