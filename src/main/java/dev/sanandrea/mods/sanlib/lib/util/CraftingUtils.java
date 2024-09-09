/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2016-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.sanlib.lib.util;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

@SuppressWarnings("unused")
public final class CraftingUtils
{
    private CraftingUtils() {}

    public static <C extends RecipeInput, T extends Recipe<C>> RecipeHolder<T> findRecipe(Level level, RecipeType<T> type,
                                                                                          final ItemStack result)
    {
        return level.getRecipeManager().getAllRecipesFor(type).stream()
                    .filter(r -> ItemStack.isSameItemSameComponents(r.value().getResultItem(level.registryAccess()), result))
                    .findFirst().orElse(null);
    }
}
