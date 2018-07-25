/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
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
