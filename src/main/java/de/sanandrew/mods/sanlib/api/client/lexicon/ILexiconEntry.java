/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.sanlib.api.client.lexicon;

import de.sanandrew.mods.sanlib.lib.util.LangUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public interface ILexiconEntry
{
    String getId();

    String getGroupId();

    String getPageRenderId();

    @Nonnull
    ItemStack getEntryIcon();

    @Nonnull String getSrcTitle();

    @Nonnull String getSrcText();

    default String getTitleLangKey(String modId) {
        return LangUtils.LEXICON_ENTRY_NAME.get(modId, this.getGroupId(), this.getId());
    }

    default ResourceLocation getPicture() {
        return null;
    }

    default boolean divideAfter() {
        return false;
    }
}
