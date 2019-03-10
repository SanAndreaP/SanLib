/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
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