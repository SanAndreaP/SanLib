/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.sanlib.client.lexicon;

import de.sanandrew.mods.sanlib.api.client.lexicon.ILexiconInst;
import de.sanandrew.mods.sanlib.api.client.lexicon.ILexiconRegistry;
import net.minecraft.client.gui.Gui;

public class LexiconRegistry
        implements ILexiconRegistry
{
    public static final LexiconRegistry INSTANCE = new LexiconRegistry();




    @Override
    public ILexiconInst getInstance(String modId) {
        return null;
    }

    @Override
    public Gui getGuiInst(String modId) {
        return null;
    }
}
