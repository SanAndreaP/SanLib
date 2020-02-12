////////////////////////////////////////////////////////////////////////////////
// This file is subject to the terms and conditions defined in the             /
// file '.github/LICENSE.md', which is part of this source code package.       /
////////////////////////////////////////////////////////////////////////////////

package de.sanandrew.mods.sanlib.api.client.lexicon;

import net.minecraft.util.ResourceLocation;

public interface ILexicon
{
    String getModId();

    int getGuiSizeX();
    int getGuiSizeY();
    int getEntryPosX();
    int getEntryPosY();
    int getEntryWidth();
    int getEntryHeight();
    default int getNavButtonOffsetY() {
        return 190;
    }

    int getTitleColor();
    int getTextColor();
    int getLinkColor();
    int getLinkVisitedColor();

    int getGroupStencilId();
    ResourceLocation getGroupStencilTexture();
    ResourceLocation getGroupSearchIcon();
    boolean forceUnicode();

    ResourceLocation getBackgroundTexture();

    void initialize(ILexiconInst registry);
}
