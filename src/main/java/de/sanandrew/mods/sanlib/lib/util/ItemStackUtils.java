////////////////////////////////////////////////////////////////////////////////
// This file is subject to the terms and conditions defined in the             /
// file '.github/LICENSE.md', which is part of this source code package.       /
////////////////////////////////////////////////////////////////////////////////

package de.sanandrew.mods.sanlib.lib.util;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTPrimitive;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
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
     * Checks wether or not 2 ItemStacks are equal. This does not compare stack sizes! This compares NBTTagCompounds!
     * @param is1 The first ItemStack
     * @param is2 The second ItemStack
     * @return {@code true}, if the stacks are considered equal, {@code false} otherwise
     */
    public static boolean areEqual(@Nonnull ItemStack is1, @Nonnull ItemStack is2) {
        return areEqual(is1, is2, false, true, true);
    }

    /**
     * Checks wether or not 2 ItemStacks are equal. This does not compare stack sizes!
     * @param is1 The first ItemStack
     * @param is2 The second ItemStack
     * @param checkNbt A flag to determine wether or not to compare NBTTagCompounds
     * @return {@code true}, if the stacks are considered equal, {@code false} otherwise
     */
    public static boolean areEqual(@Nonnull ItemStack is1, @Nonnull ItemStack is2, boolean checkNbt) {
        return areEqual(is1, is2, false, checkNbt, true);
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
        return areEqualBase(is1, is2, checkStackSize, checkDmg) && (!checkNbt || Objects.equals(is1.getTagCompound(), is2.getTagCompound()));
    }

    public static boolean areEqualNbtFit(@Nonnull ItemStack mainIS, @Nonnull ItemStack otherIS, boolean checkStackSize, boolean checkDmg) {
        return areEqualNbtFit(mainIS, otherIS, checkStackSize, checkDmg, true);
    }

    public static boolean areEqualNbtFit(@Nonnull ItemStack mainIS, @Nonnull ItemStack otherIS, boolean checkStackSize, boolean checkDmg, boolean strict) {
        return areEqualBase(mainIS, otherIS, checkStackSize, checkDmg)
               && ( !otherIS.hasTagCompound() || (mainIS.hasTagCompound() && MiscUtils.doesNbtContainOther(mainIS.getTagCompound(), otherIS.getTagCompound(), strict)) );
    }

    private static boolean areEqualBase(@Nonnull ItemStack is1, @Nonnull ItemStack is2, boolean checkStackSize, boolean checkDmg) {
        if( !isValid(is1) && !isValid(is2) ) {
            return true;
        }

        if( !isValid(is2) || !isItem(is1, is2.getItem()) ) {
            return false;
        }

        //noinspection SimplifiableIfStatement
        if( checkDmg && is1.getItemDamage() != OreDictionary.WILDCARD_VALUE && is2.getItemDamage() != OreDictionary.WILDCARD_VALUE && is1.getItemDamage() != is2.getItemDamage() ) {
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
        return !isValid(stack1) || !isValid(stack2)
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
            items.set(slot, new ItemStack(tag));
            if( tag.hasKey("Quantity") ) {
                items.get(slot).setCount(((NBTPrimitive) tag.getTag("Quantity")).getInt());
            }

            if( callbackMethod != null && tag.hasKey("StackNBT") ) {
                callbackMethod.accept(items.get(slot), (NBTTagCompound) tag.getTag("StackNBT"));
            }
        }
    }

    public static void dropBlockItem(ItemStack stack, World world, BlockPos pos) {
        if( isValid(stack) ) {
            float xOff = MiscUtils.RNG.randomFloat() * 0.8F + 0.1F;
            float yOff = MiscUtils.RNG.randomFloat() * 0.8F + 0.1F;
            float zOff = MiscUtils.RNG.randomFloat() * 0.8F + 0.1F;

            EntityItem item = new EntityItem(world, (pos.getX() + xOff), (pos.getY() + yOff), (pos.getZ() + zOff), stack.copy());

            final float motionSpeed = 0.05F;
            item.motionX = ((float) MiscUtils.RNG.randomGaussian() * motionSpeed);
            item.motionY = ((float) MiscUtils.RNG.randomGaussian() * motionSpeed + 0.2F);
            item.motionZ = ((float) MiscUtils.RNG.randomGaussian() * motionSpeed);
            world.spawnEntity(item);
        }
    }

    public static NonNullList<ItemStack> getCompactItems(NonNullList<ItemStack> items, int maxInvStackSize) {
        return getCompactItems(items, maxInvStackSize, null);
    }

    public static NonNullList<ItemStack> getCompactItems(NonNullList<ItemStack> items, int maxInvStackSize, Integer maxStackSize) {
        NonNullList<ItemStack> cmpItems = NonNullList.create();

        items.sort((i1, i2) -> ItemStackUtils.areEqual(i1, i2, false, true, true) ? 0 : i1.getTranslationKey().compareTo(i2.getTranslationKey()));
        items.forEach(v -> {
            int cmpSize = cmpItems.size();
            if( cmpSize < 1 ) {
                cmpItems.add(v.copy());
            } else {
                ItemStack cs = cmpItems.get(cmpSize - 1);
                if( ItemStackUtils.areEqual(cs, v, false, true, true) ) {
                    int rest = Math.min(MiscUtils.defIfNull(maxStackSize, cs::getMaxStackSize), maxInvStackSize) - cs.getCount();
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
}
