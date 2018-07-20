package de.sanandrew.mods.sanlib.api.client.lexicon;

import net.minecraft.util.ResourceLocation;

public interface ILexicon
{
    String getModId();

    String getCraftingRenderID();
    String getStandardRenderID();
    int getGuiSizeX();
    int getGuiSizeY();
    int getEntryPosX();
    int getEntryPosY();
    int getEntryWidth();
    int getEntryHeight();

    int getTitleColor();
    int getTextColor();
    int getLinkColor();

    ResourceLocation getBackgroundTexture();
}
