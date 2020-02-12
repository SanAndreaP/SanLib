////////////////////////////////////////////////////////////////////////////////
// This file is subject to the terms and conditions defined in the             /
// file '.github/LICENSE.md', which is part of this source code package.       /
////////////////////////////////////////////////////////////////////////////////

package de.sanandrew.mods.sanlib.lib.util;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.oredict.ShapedOreRecipe;

@SuppressWarnings("unused")
public final class CraftingUtils
{
    public static int getOreRecipeWidth(ShapedOreRecipe recipe) {
        return recipe == null ? 0 : ReflectionUtils.getCachedFieldValue(ShapedOreRecipe.class, recipe, "width", "width");
    }

    public static int getOreRecipeHeight(ShapedOreRecipe recipe) {
        return recipe == null ? 0 : ReflectionUtils.getCachedFieldValue(ShapedOreRecipe.class, recipe, "height", "height");
    }

    public static IRecipe findShapedRecipe(final ItemStack result) {
        throw new RuntimeException("NYI");
//        return CraftingManager..getRecipeList().stream().filter(recipe -> ItemStackUtils.areEqual(recipe.getRecipeOutput(), result, result.hasTagCompound()))
//                              .findFirst().orElse(null);
    }
}
