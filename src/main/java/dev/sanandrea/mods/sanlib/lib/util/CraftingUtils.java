/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2016-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.sanlib.lib.util;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.world.World;

@SuppressWarnings("unused")
public final class CraftingUtils
{
    public static <C extends IInventory, T extends IRecipe<C>> T findRecipe(World level, IRecipeType<T> type, final ItemStack result) {
        return level.getRecipeManager().getAllRecipesFor(type).stream().filter(r -> ItemStackUtils.areEqual(result, r.getResultItem(), result.hasTag()))
                    .findFirst().orElse(null);
    }
}
