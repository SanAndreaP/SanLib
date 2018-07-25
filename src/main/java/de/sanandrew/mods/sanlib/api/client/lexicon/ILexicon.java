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
