////////////////////////////////////////////////////////////////////////////////
// This file is subject to the terms and conditions defined in the             /
// file '.github/LICENSE.md', which is part of this source code package.       /
////////////////////////////////////////////////////////////////////////////////

package de.sanandrew.mods.sanlib.lib.util.config.type;

import de.sanandrew.mods.sanlib.lib.util.config.Pattern;
import de.sanandrew.mods.sanlib.lib.util.config.Range;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.lang.reflect.Field;

public class ValueTypeArrayString
        implements IValueType
{
    @Override
    public boolean typeFits(Class<?> type) {
        return type == String[].class;
    }

    @Override
    public Object getDefaultValue(Class<?> type, Field f, Object instance) throws IllegalAccessException, IllegalArgumentException {
        return f.get(instance);
    }

    @Override
    public Property getProperty(Configuration config, String category, String name, Object defaultVal, String propComment, Range propRange) {
        String[] def = (String[]) defaultVal;
        boolean fixedList = propRange.listFixed();
        int maxListLength = propRange.maxListLength();
        Pattern validationPattern = propRange.validationPattern();

        StringBuilder cm = new StringBuilder();
        if( fixedList ) {
            cm.append("fixed list length: ").append(maxListLength == -1 ? def.length : maxListLength);
        } else if( maxListLength != -1 ) {
            cm.append("maximum list length: ").append(maxListLength);
        }

        if( !validationPattern.value().isEmpty() ) {
            if( cm.length() > 0 ) {
                cm.append(", ");
            }
            cm.append("element string must match: ").append(validationPattern.value());
        }

        if( cm.length() > 0 ) {
            propComment += " [" + (cm) + ']';
        }

        return config.get(category, name, def, propComment.trim(), fixedList, maxListLength, ValueTypeString.getPattern(validationPattern.value(), validationPattern.flags()));
    }

    @Override
    public void setValue(Class<?> type, Field f, Object instance, Property p, Object defaultVal, Range propRange) throws IllegalAccessException, IllegalArgumentException {
        String[] list = p.getStringList();
        IValueType.validateArrayLengths(p.getName(), ((String[]) defaultVal).length, list.length, p.getMaxListLength(), p.isListLengthFixed());

        java.util.regex.Pattern ptrn = p.getValidationPattern();
        if( ptrn != null ) {
            for( int i = 0, max = list.length; i < max; i++ ) {
                if( !ptrn.matcher(list[i]).matches() ) {
                    throw new IllegalArgumentException(String.format("The %s element of array %s does not match pattern!", MiscUtils.getListNrWithSuffix(i), p.getName()));
                }
            }
        }

        f.set(instance, list);
    }
}
