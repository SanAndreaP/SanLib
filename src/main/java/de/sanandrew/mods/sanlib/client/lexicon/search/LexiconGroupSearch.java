////////////////////////////////////////////////////////////////////////////////
// This file is subject to the terms and conditions defined in the             /
// file '.github/LICENSE.md', which is part of this source code package.       /
////////////////////////////////////////////////////////////////////////////////

package de.sanandrew.mods.sanlib.client.lexicon.search;

import de.sanandrew.mods.sanlib.api.client.lexicon.ILexiconGroup;
import de.sanandrew.mods.sanlib.api.client.lexicon.ILexiconInst;
import de.sanandrew.mods.sanlib.api.client.lexicon.LexiconGroup;
import net.minecraft.util.ResourceLocation;

public final class LexiconGroupSearch
        extends LexiconGroup
{
    public static final String GRP_NAME = "search";

    protected LexiconGroupSearch(ResourceLocation searchIcon) {
        super(GRP_NAME, searchIcon);
    }

    public static void register(ILexiconInst registry) {
        registry.registerPageRender(new LexiconRenderSearch());

        ILexiconGroup grp = new LexiconGroupSearch(registry.getLexicon().getGroupSearchIcon());
        registry.registerGroup(grp);

        grp.addEntry(new LexiconEntrySearch());
    }
}
