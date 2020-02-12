////////////////////////////////////////////////////////////////////////////////
// This file is subject to the terms and conditions defined in the             /
// file '.github/LICENSE.md', which is part of this source code package.       /
////////////////////////////////////////////////////////////////////////////////

package de.sanandrew.mods.sanlib.api.client.lexicon;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;

import javax.annotation.Nonnull;

@Deprecated
public interface ILexiconEntryCraftingGrid
        extends ILexiconEntry
{
    @Nonnull
    @Deprecated
    default NonNullList<IRecipe> getRecipes() {
        return NonNullList.create();
    }

    @Nonnull
    @Deprecated
    default NonNullList<ItemStack> getRecipeResults() {
        return NonNullList.create();
    }
}
