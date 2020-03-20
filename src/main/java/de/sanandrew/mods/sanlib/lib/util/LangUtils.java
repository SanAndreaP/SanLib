////////////////////////////////////////////////////////////////////////////////
// This file is subject to the terms and conditions defined in the             /
// file '.github/LICENSE.md', which is part of this source code package.       /
////////////////////////////////////////////////////////////////////////////////

package de.sanandrew.mods.sanlib.lib.util;

import de.sanandrew.mods.sanlib.Constants;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;

@SuppressWarnings({ "unused", "WeakerAccess" })
public final class LangUtils
{
    public static final TranslateKey LEXICON_GROUP_NAME = new TranslateKey("%s.lexicon.%%s.%%s.name", Constants.ID);
    public static final TranslateKey LEXICON_ENTRY_NAME = new TranslateKey("%s.lexicon.%%s.%%s.%%s.name", Constants.ID);
    public static final TranslateKey LEXICON_ENTRY_TEXT = new TranslateKey("%s.lexicon.%%s.%%s.%%s.text", Constants.ID);
    public static final TranslateKey LEXICON_SRC_ENTRY_TITLE = new TranslateKey("%s.lexicon.%%s.search.title", Constants.ID);
    public static final TranslateKey LEXICON_SRC_ENTRY_TEXT = new TranslateKey("%s.lexicon.%%s.search.text", Constants.ID);

    public static final TranslateKey ENTITY_NAME   = new TranslateKey("entity.%s.name");
    public static final TranslateKey ENTITY_DESC   = new TranslateKey("entity.%s.desc");
    public static final TranslateKey CONTAINER_INV = new TranslateKey("container.inventory");

    /**
     * Wrapper method to {@link net.minecraft.util.text.translation.I18n#canTranslate(String)} for abbreviation.
     * <s>Also tries to translate with [NONE] to en_US if the translation fails</s>
     * @param langKey language key to be translated
     * @return translated key or langKey, if translation fails
     */
    @SuppressWarnings("deprecation")
    public static String translate(String langKey, Object... args) {
        final String s;
        if( net.minecraft.util.text.translation.I18n.canTranslate(langKey) ) {
            s = args == null || args.length < 1
                ? net.minecraft.util.text.translation.I18n.translateToLocal(langKey)
                : net.minecraft.util.text.translation.I18n.translateToLocalFormatted(langKey, args);
        } else {
            s = langKey;
        }

        return s.replace("\\n", "\n");
    }

    public static String translate(TranslateKey langKey, Object... args) {
        return translate(langKey.key, args);
    }

    @SuppressWarnings("deprecation")
    public static String translateOrDefault(String langKey, String defaultVal) {
        return net.minecraft.util.text.translation.I18n.canTranslate(langKey) ? translate(langKey) : defaultVal;
    }

    public static String translateOrDefault(TranslateKey langKey, String defaultVal) {
        return translateOrDefault(langKey.key, defaultVal);
    }

    public static String translateEntityCls(Class<? extends Entity> eClass) {
        String namedEntry = EntityList.getTranslationName(EntityList.getKey(eClass));
        if( namedEntry != null ) {
            return translate(ENTITY_NAME.get(namedEntry));
        }

        return "[UNKNOWN] " + eClass.getName();
    }

    public static String translateEntityClsDesc(Class<? extends Entity> eClass) {
        String namedEntry = EntityList.getTranslationName(EntityList.getKey(eClass));
        if( namedEntry != null ) {
            return translate(ENTITY_DESC.get(namedEntry));
        }

        return "";
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
