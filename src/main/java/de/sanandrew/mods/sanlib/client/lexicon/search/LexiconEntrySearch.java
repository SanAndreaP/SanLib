////////////////////////////////////////////////////////////////////////////////
// This file is subject to the terms and conditions defined in the             /
// file '.github/LICENSE.md', which is part of this source code package.       /
////////////////////////////////////////////////////////////////////////////////

package de.sanandrew.mods.sanlib.client.lexicon.search;

import de.sanandrew.mods.sanlib.api.client.lexicon.ILexiconEntry;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public final class LexiconEntrySearch
        implements ILexiconEntry
{
    public static final String ID = "search";
    private final ItemStack icon;

    public LexiconEntrySearch() {
        this.icon = new ItemStack(Blocks.BARRIER, 1);
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public String getGroupId() {
        return LexiconGroupSearch.GRP_NAME;
    }

    @Override
    public String getPageRenderId() {
        return LexiconRenderSearch.RENDER_SEARCH_ID;
    }

    @Nonnull
    @Override
    public ItemStack getEntryIcon() {
        return this.icon;
    }

    @Nonnull
    @Override
    public String getSrcTitle() {
        return "";
    }

    @Nonnull
    @Override
    public String getSrcText() {
        return "";
    }
}
