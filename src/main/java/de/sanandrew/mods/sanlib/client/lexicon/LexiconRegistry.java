/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.sanlib.client.lexicon;

import de.sanandrew.mods.sanlib.api.client.lexicon.ILexicon;
import de.sanandrew.mods.sanlib.api.client.lexicon.ILexiconInst;
import de.sanandrew.mods.sanlib.api.client.lexicon.ILexiconRegistry;
import de.sanandrew.mods.sanlib.client.lexicon.search.LexiconGroupSearch;
import net.minecraft.client.gui.Gui;
import org.apache.logging.log4j.util.Strings;

import java.util.HashMap;
import java.util.Map;

public class LexiconRegistry
        implements ILexiconRegistry
{
    public static final LexiconRegistry INSTANCE = new LexiconRegistry();

    private final Map<String, ILexiconInst> lexicons = new HashMap<>();
    private final Map<String, Gui> lexiconGuis = new HashMap<>();

    public void registerLexicon(ILexicon lexicon) {
        if( lexicon == null ) {
            throw new IllegalArgumentException("Cannot register a NULL lexicon!");
        }

        String modId = lexicon.getModId();
        if( Strings.isBlank(modId) ) {
            throw new IllegalArgumentException("Cannot register a lexicon without mod ID!");
        }

        if( this.lexicons.containsKey(modId) ) {
            throw new IllegalArgumentException("Mod ID already registered!");
        }

        ILexiconInst inst = new LexiconInstance(lexicon);

        this.lexicons.put(modId, inst);
        this.lexiconGuis.put(modId, new GuiLexicon(lexicon));
    }

    public void initialize() {
        this.lexicons.forEach((id, i) -> {
            i.getLexicon().initialize(i);
            LexiconGroupSearch.register(i);
        });
    }

    @Override
    public ILexiconInst getInstance(String modId) {
        return this.lexicons.get(modId);
    }

    @Override
    public Gui getGuiInst(String modId) {
        return this.lexiconGuis.get(modId);
    }
}
