////////////////////////////////////////////////////////////////////////////////
// This file is subject to the terms and conditions defined in the             /
// file '.github/LICENSE.md', which is part of this source code package.       /
////////////////////////////////////////////////////////////////////////////////

package de.sanandrew.mods.sanlib.api.client.lexicon;

import net.minecraft.client.gui.Gui;

public interface ILexiconRegistry
{
    ILexiconInst getInstance(String modId);

    Gui getGuiInst(String modId);
}
