/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.sanlib.api.client.lexicon;

import net.minecraft.util.ResourceLocation;

import java.util.List;

public interface ILexiconGroup
{
    String getId();

    ResourceLocation getIcon();

    List<ILexiconEntry> getEntries();

    default void sortEntries() {}

    ILexiconEntry getEntry(String id);

    boolean addEntry(ILexiconEntry entry) throws IllegalArgumentException;

    ILexiconEntry removeEntry(String id);
}
