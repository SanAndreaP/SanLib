////////////////////////////////////////////////////////////////////////////////
// This file is subject to the terms and conditions defined in the             /
// file '.github/LICENSE.md', which is part of this source code package.       /
////////////////////////////////////////////////////////////////////////////////

package de.sanandrew.mods.sanlib.lib.util.config.type;

import de.sanandrew.mods.sanlib.lib.util.config.Pattern;
import de.sanandrew.mods.sanlib.lib.util.config.Range;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.lang.reflect.Field;

public class ValueTypeString
        implements IValueType
{
    @Override
    public boolean typeFits(Class<?> type) {
        return type == String.class;
    }

    @Override
    public Object getDefaultValue(Class<?> type, Field f, Object instance) throws IllegalAccessException, IllegalArgumentException {
        return f.get(instance);
    }

    @Override
    public Property getProperty(Configuration config, String category, String name, Object defaultVal, String propComment, Range propRange) {
        String def = defaultVal.toString();
        Pattern validationPattern = propRange.validationPattern();

        StringBuilder cm = new StringBuilder();
        if( !def.isEmpty() ) {
            cm.append("default: ").append(defaultVal);
        }

        if( !validationPattern.value().isEmpty() ) {
            if( cm.length() > 0 ) {
                cm.append(", ");
            }
            cm.append("string must match: ").append(validationPattern.value());
        }

        if( cm.length() > 0 ) {
            propComment += " [" + (cm) + ']';
        }

        return config.get(category, name, defaultVal.toString(), propComment.trim(), getPattern(validationPattern.value(), validationPattern.flags()));
    }

    @Override
    @SuppressWarnings("MagicConstant")
    public void setValue(Class<?> type, Field f, Object instance, Property p, Object defaultVal, Range propRange) throws IllegalAccessException, IllegalArgumentException {
        String s = p.getString();
        java.util.regex.Pattern ptrn = p.getValidationPattern();
        if( ptrn != null && !ptrn.matcher(s).matches() ) {
            throw new IllegalArgumentException(String.format("The property %s does not match pattern!", p.getName()));
        }

        f.set(instance, s);
    }

    static java.util.regex.Pattern getPattern(String pattern, int flags) {
        return pattern.isEmpty() ? null : java.util.regex.Pattern.compile(pattern, flags);
    }
}
