////////////////////////////////////////////////////////////////////////////////
// This file is subject to the terms and conditions defined in the             /
// file '.github/LICENSE.md', which is part of this source code package.       /
////////////////////////////////////////////////////////////////////////////////

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
