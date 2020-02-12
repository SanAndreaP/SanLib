////////////////////////////////////////////////////////////////////////////////
// This file is subject to the terms and conditions defined in the             /
// file '.github/LICENSE.md', which is part of this source code package.       /
////////////////////////////////////////////////////////////////////////////////

package de.sanandrew.mods.sanlib.api.client.lexicon;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

@SideOnly(Side.CLIENT)
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
