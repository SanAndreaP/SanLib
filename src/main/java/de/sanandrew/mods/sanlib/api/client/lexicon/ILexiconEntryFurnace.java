/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.sanlib.api.client.lexicon;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;

import java.util.Collections;
import java.util.Map;

@Deprecated
public interface ILexiconEntryFurnace
        extends ILexiconEntry
{
    @Deprecated
    default Map<Ingredient, ItemStack> getRecipes() {
        return Collections.emptyMap();
    }
}
