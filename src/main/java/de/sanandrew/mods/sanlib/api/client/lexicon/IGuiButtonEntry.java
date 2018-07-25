/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.sanlib.api.client.lexicon;

import net.minecraft.client.gui.GuiButton;

public interface IGuiButtonEntry
{
    ILexiconEntry getEntry();

    GuiButton get();
}
