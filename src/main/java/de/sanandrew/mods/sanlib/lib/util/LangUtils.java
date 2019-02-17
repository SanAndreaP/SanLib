/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.sanlib.lib.util;

import de.sanandrew.mods.sanlib.SanLib;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.text.translation.LanguageMap;

@SuppressWarnings("unused")
public final class LangUtils
{
    public static final TranslateKey LEXICON_GROUP_NAME = new TranslateKey("%s.lexicon.%%s.%%s.name", SanLib.ID);
    public static final TranslateKey LEXICON_ENTRY_NAME = new TranslateKey("%s.lexicon.%%s.%%s.%%s.name", SanLib.ID);
    public static final TranslateKey LEXICON_ENTRY_TEXT = new TranslateKey("%s.lexicon.%%s.%%s.%%s.text", SanLib.ID);
    public static final TranslateKey LEXICON_SRC_ENTRY_TITLE = new TranslateKey("%s.lexicon.%%s.search.title", SanLib.ID);
    public static final TranslateKey LEXICON_SRC_ENTRY_TEXT = new TranslateKey("%s.lexicon.%%s.search.text", SanLib.ID);

    public static final TranslateKey ENTITY_NAME   = new TranslateKey("entity.%s.name");
    public static final TranslateKey ENTITY_DESC   = new TranslateKey("entity.%s.desc");
    public static final TranslateKey CONTAINER_INV = new TranslateKey("container.inventory");

    /**
     * Wrapper method to {@link LanguageMap#translateKey(String)} for abbreviation.
     * <s>Also tries to translate with [NONE] to en_US if the translation fails</s>
     * @param langKey language key to be translated
     * @return translated key or langKey, if translation fails
     */
    public static String translate(String langKey, Object... args) {
        LanguageMap lmap = LanguageMap.getInstance();
        return lmap.exists(langKey) ? String.format(lmap.translateKey(langKey), args) : langKey;
    }

    public static String translate(TranslateKey langKey, Object... args) {
        return translate(langKey.key, args);
    }

    public static String translateOrDefault(String langKey, String defaultVal) {
        return LanguageMap.getInstance().exists(langKey) ? translate(langKey) : defaultVal;
    }

    public static String translateOrDefault(TranslateKey langKey, String defaultVal) {
        return translateOrDefault(langKey.key, defaultVal);
    }

    /**
     * @deprecated Use {@link EntityType#getTranslationKey()}
     * @param eClass
     * @return
     */
    @Deprecated
    public static String translateEntityCls(Class<? extends Entity> eClass) {

        return eClass.getName();
    }

    public static final class TranslateKey
    {
        private final String key;

        public TranslateKey(String key) {
            this.key = key;
        }

        public TranslateKey(String key, Object... args) {
            this(String.format(key, args));
        }

        public String get() {
            return this.key;
        }

        public String get(Object... args) {
            return String.format(this.key, args);
        }
    }
}
