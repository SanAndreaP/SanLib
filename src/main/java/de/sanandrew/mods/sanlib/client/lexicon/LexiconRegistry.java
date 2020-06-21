////////////////////////////////////////////////////////////////////////////////
// This file is subject to the terms and conditions defined in the             /
// file '.github/LICENSE.md', which is part of this source code package.       /
////////////////////////////////////////////////////////////////////////////////

package de.sanandrew.mods.sanlib.client.lexicon;

import de.sanandrew.mods.sanlib.api.client.lexicon.ILexicon;
import de.sanandrew.mods.sanlib.api.client.lexicon.ILexiconGroup;
import de.sanandrew.mods.sanlib.api.client.lexicon.ILexiconInst;
import de.sanandrew.mods.sanlib.api.client.lexicon.ILexiconRegistry;
import de.sanandrew.mods.sanlib.client.lexicon.search.LexiconGroupSearch;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.LanguageMap;
import net.minecraftforge.client.resource.IResourceType;
import net.minecraftforge.client.resource.ISelectiveResourceReloadListener;
import net.minecraftforge.client.resource.SelectiveReloadStateHandler;
import net.minecraftforge.client.resource.VanillaResourceType;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.util.Strings;

import java.io.*;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

@Deprecated
public class LexiconRegistry
        implements ILexiconRegistry, ISelectiveResourceReloadListener
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
        this.lexicons.forEach((modId, lexicon) -> {
            lexicon.getLexicon().initialize(lexicon);
            LexiconGroupSearch.register(lexicon);
        });

        Minecraft mc = Minecraft.getMinecraft();
        ((IReloadableResourceManager) mc.getResourceManager()).registerReloadListener(this);
        if( !SelectiveReloadStateHandler.INSTANCE.get().test(VanillaResourceType.LANGUAGES) ) {
            this.onResourceManagerReload(Minecraft.getMinecraft().getResourceManager(), r -> r == VanillaResourceType.LANGUAGES);
        }
    }

    @Override
    public ILexiconInst getInstance(String modId) {
        return this.lexicons.get(modId);
    }

    @Override
    public Gui getGuiInst(String modId) {
        return this.lexiconGuis.get(modId);
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager, Predicate<IResourceType> resourcePredicate) {
        if( resourcePredicate.test(VanillaResourceType.LANGUAGES) ) {
            this.lexicons.forEach((modId, i) -> {
                String langLoc = "lang/sanlib.lexicon/";

                for( ILexiconGroup group : i.getGroups() ) {
                    Map<String, String> lines = null;
                    String langLocGrp = langLoc + group.getId().replace(":", ".");
                    ResourceLocation rl = new ResourceLocation(modId, langLocGrp + "/en_us.lang");
                    try( InputStream file = resourceManager.getResource(rl).getInputStream() ) {
                        lines = LanguageMap.parseLangFile(file);
                    } catch( IOException ignored ) { }

                    rl = new ResourceLocation(modId, langLocGrp + "/" + Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage().getLanguageCode() + ".lang");
                    try( InputStream file = resourceManager.getResource(rl).getInputStream() ) {
                        if( lines == null ) {
                            lines = LanguageMap.parseLangFile(file);
                        } else {
                            lines.putAll(LanguageMap.parseLangFile(file));
                        }
                    } catch( IOException ignored ) { }

                    if( lines != null ) {
                        try( StringWriter sw = new StringWriter() ) {
                            for( Map.Entry<String, String> e : lines.entrySet() ) {
                                sw.write(String.format("sanlib.lexicon.%s.%s.%s=%s\n", modId, group.getId(), e.getKey(), e.getValue()));
                            }

                            LanguageMap.inject(IOUtils.toInputStream(sw.toString(), Charset.forName("UTF-8")));
                        } catch( IOException ignored ) { }
                    }
                }
            });
        }
    }
}
