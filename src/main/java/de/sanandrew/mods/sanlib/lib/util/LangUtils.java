////////////////////////////////////////////////////////////////////////////////
// This file is subject to the terms and conditions defined in the             /
// file '.github/LICENSE.md', which is part of this source code package.       /
////////////////////////////////////////////////////////////////////////////////

package de.sanandrew.mods.sanlib.lib.util;

import de.sanandrew.mods.sanlib.Constants;
import net.minecraft.entity.EntityType;
import net.minecraft.util.text.LanguageMap;

@SuppressWarnings({ "unused", "WeakerAccess" })
public final class LangUtils
{
    public static final TranslateKey LEXICON_GROUP_NAME = new TranslateKey("%s.lexicon.%%s.%%s.name", Constants.ID);
    public static final TranslateKey LEXICON_ENTRY_NAME = new TranslateKey("%s.lexicon.%%s.%%s.%%s.name", Constants.ID);
    public static final TranslateKey LEXICON_ENTRY_TEXT = new TranslateKey("%s.lexicon.%%s.%%s.%%s.text", Constants.ID);
    public static final TranslateKey LEXICON_SRC_ENTRY_TITLE = new TranslateKey("%s.lexicon.%%s.search.title", Constants.ID);
    public static final TranslateKey LEXICON_SRC_ENTRY_TEXT = new TranslateKey("%s.lexicon.%%s.search.text", Constants.ID);

    public static final TranslateKey ENTITY_NAME   = new TranslateKey("entity.%s.name");
    public static final TranslateKey CONTAINER_INV = new TranslateKey("container.inventory");

    /**
     * translates the language key to a string and formats it with the specified arguments, if any.
     * @param langKey language key to be translated
     * @return translated key or langKey, if translation fails
     */
    public static String translate(String langKey, Object... args) {
        return translateOrDefault(langKey, langKey, args);
    }

    public static String translate(TranslateKey langKey, Object... args) {
        return translate(langKey.key, args);
    }

    public static String translateOrDefault(String langKey, String defaultVal, Object... args) {
        final String s;
        if( LanguageMap.getInstance().func_230506_b_(langKey) ) {
            s = args == null || args.length < 1
                ? LanguageMap.getInstance().func_230503_a_(langKey)
                : String.format(LanguageMap.getInstance().func_230503_a_(langKey), args);
        } else {
            s = defaultVal;
        }

        return s.replace("\\n", "\n");
    }

    public static String translateOrDefault(TranslateKey langKey, String defaultVal) {
        return translateOrDefault(langKey.key, defaultVal);
    }

    /**
     * @deprecated Use {@link EntityType#getName()} instead whenever possible
     */
    @Deprecated
    public static String translateEntityCls(EntityType<?> type) {
        return translate(type.getTranslationKey());
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
