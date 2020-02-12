////////////////////////////////////////////////////////////////////////////////
// This file is subject to the terms and conditions defined in the             /
// file '.github/LICENSE.md', which is part of this source code package.       /
////////////////////////////////////////////////////////////////////////////////

package de.sanandrew.mods.sanlib.api.client.lexicon;

import net.minecraft.client.gui.Gui;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

@SideOnly(Side.CLIENT)
public interface ILexiconInst
{
    boolean registerGroup(ILexiconGroup group);

    List<ILexiconGroup> getGroups();

    ILexiconGroup getGroup(String id);

    ILexiconGroup removeGroup(String id);

    boolean registerPageRender(ILexiconPageRender render);

    ILexiconPageRender getPageRender(String id);

    ILexiconPageRender removePageRender(String id);

    ILexicon getLexicon();

    @Deprecated
    String getCraftingRenderID();
    
    String getStandardRenderID();

    String getTranslatedTitle(ILexiconEntry entry);

    String getTranslatedText(ILexiconEntry entry);

    Gui getGui();
}
