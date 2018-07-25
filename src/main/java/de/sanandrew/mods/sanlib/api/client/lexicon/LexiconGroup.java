/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.sanlib.api.client.lexicon;

import joptsimple.internal.Strings;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@SideOnly(Side.CLIENT)
public abstract class LexiconGroup
        implements ILexiconGroup
{
    private final String id;
    private final ResourceLocation icon;
    private final Map<String, ILexiconEntry> idToEntryMap;
    private final List<ILexiconEntry> entriesRO;
    protected final List<ILexiconEntry> entries;

    public LexiconGroup(String id, ResourceLocation icon) {
        this.id = id;
        this.icon = icon;
        this.idToEntryMap = new HashMap<>();
        this.entries = new LinkedList<>();
        this.entriesRO = Collections.unmodifiableList(this.entries);
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public ResourceLocation getIcon() {
        return this.icon;
    }

    @Override
    public List<ILexiconEntry> getEntries() {
        return this.entriesRO;
    }

    @Override
    public ILexiconEntry getEntry(String id) {
        return this.idToEntryMap.get(id);
    }

    @Override
    public boolean addEntry(ILexiconEntry entry) throws IllegalArgumentException {
        if( entry == null ) {
            throw new IllegalArgumentException(String.format("Cannot register null as lexicon entry for group %s!", this.id));
        }

        String id = entry.getId();
        if( Strings.isNullOrEmpty(id) ) {
            throw new IllegalArgumentException(String.format("Cannot register a lexicon entry without ID for group %s!", this.id));
        }
        if( this.idToEntryMap.containsKey(id) ) {
            throw new IllegalArgumentException(String.format("Cannot register a lexicon entry with an already registered ID => \"%s\" for group %s!", id, this.id));
        }

        this.idToEntryMap.put(id, entry);
        this.entries.add(entry);

        return true;
    }

    @Override
    public ILexiconEntry removeEntry(String id) {
        ILexiconEntry entry = this.idToEntryMap.get(id);
        if( entry != null ) {
            this.idToEntryMap.remove(id);
            this.entries.remove(entry);
        }
        return entry;
    }
}
