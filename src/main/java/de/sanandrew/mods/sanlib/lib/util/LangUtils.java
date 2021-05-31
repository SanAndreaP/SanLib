////////////////////////////////////////////////////////////////////////////////
// This file is subject to the terms and conditions defined in the             /
// file '.github/LICENSE.md', which is part of this source code package.       /
////////////////////////////////////////////////////////////////////////////////

package de.sanandrew.mods.sanlib.lib.util;

import de.sanandrew.mods.sanlib.Constants;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.LanguageMap;

import java.util.Arrays;

@SuppressWarnings({ "unused", "WeakerAccess" })
public final class LangUtils
{
    public static final TranslateKey LEXICON_GROUP_NAME = newKey("%s.lexicon.%%s.%%s.name").format(Constants.ID).build();
    public static final TranslateKey LEXICON_ENTRY_NAME = newKey("%s.lexicon.%%s.%%s.%%s.name").format(Constants.ID).build();
    public static final TranslateKey LEXICON_ENTRY_TEXT = newKey("%s.lexicon.%%s.%%s.%%s.text").format(Constants.ID).build();
    public static final TranslateKey LEXICON_SRC_ENTRY_TITLE = newKey("%s.lexicon.%%s.search.title").format(Constants.ID).build();
    public static final TranslateKey LEXICON_SRC_ENTRY_TEXT = newKey("%s.lexicon.%%s.search.text").format(Constants.ID).build();

    public static final TranslateKey ENTITY_NAME   = newKey("entity.%s").withoutRlColon().build();
    public static final TranslateKey CONTAINER_INV = newKey("container.inventory").build();

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

    public static TKeyBuilder newKey(String key) {
        return new TKeyBuilder(key);
    }

    public static final class TKeyBuilder
    {
        String key;
        boolean noRlColonFormat;

        private TKeyBuilder(String key) {
            this.key = key;
        }

        public TKeyBuilder format(Object... args) {
            this.key = String.format(this.key, args);

            return this;
        }

        public TKeyBuilder withoutRlColon() {
            this.noRlColonFormat = true;

            return this;
        }

        public TranslateKey build() {
            return new TranslateKey(this.key, this.noRlColonFormat);
        }
    }

    public static final class TranslateKey
    {
        private final String key;
        private final boolean noRlColonFormat;

        private TranslateKey(String key, boolean noRlColonFormat) {
            this.key = key;
            this.noRlColonFormat = noRlColonFormat;
        }

        public String get() {
            return this.key;
        }

        public String get(Object... args) {
            if( this.noRlColonFormat ) {
                args = Arrays.stream(args)
                             .map(a -> a instanceof ResourceLocation ? a.toString().replaceAll(":", ".") : a)
                             .toArray();
            }

            return String.format(this.key, args);
        }
    }
}
