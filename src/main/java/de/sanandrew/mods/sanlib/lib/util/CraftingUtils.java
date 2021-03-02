////////////////////////////////////////////////////////////////////////////////
// This file is subject to the terms and conditions defined in the             /
// file '.github/LICENSE.md', which is part of this source code package.       /
////////////////////////////////////////////////////////////////////////////////

package de.sanandrew.mods.sanlib.lib.util;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;

@SuppressWarnings("unused")
public final class CraftingUtils
{

    public static IRecipe<?> findShapedRecipe(final ItemStack result) {
        throw new RuntimeException("NYI");
//        return CraftingManager..getRecipeList().stream().filter(recipe -> ItemStackUtils.areEqual(recipe.getRecipeOutput(), result, result.hasTagCompound()))
//                              .findFirst().orElse(null);
    }
}
