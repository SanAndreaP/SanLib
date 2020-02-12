////////////////////////////////////////////////////////////////////////////////
// This file is subject to the terms and conditions defined in the             /
// file '.github/LICENSE.md', which is part of this source code package.       /
////////////////////////////////////////////////////////////////////////////////

package de.sanandrew.mods.sanlib.api.client.lexicon;

import de.sanandrew.mods.sanlib.lib.util.LangUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

@SideOnly(Side.CLIENT)
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
