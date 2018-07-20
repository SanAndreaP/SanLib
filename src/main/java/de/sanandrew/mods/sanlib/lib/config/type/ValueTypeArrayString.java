/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.sanlib.lib.config.type;

import de.sanandrew.mods.sanlib.lib.config.Pattern;
import de.sanandrew.mods.sanlib.lib.config.Range;
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

        if( !validationPattern.regex().isEmpty() ) {
            if( cm.length() > 0 ) {
                cm.append(", ");
            }
            cm.append("element string must match: ").append(validationPattern.regex());
        }

        if( cm.length() > 0 ) {
            propComment += " [" + (cm) + ']';
        }

        return config.get(category, name, def, propComment.trim(), fixedList, maxListLength, ValueTypeString.getPattern(validationPattern.regex(), validationPattern.flags()));
    }

    @Override
    public void setValue(Class<?> type, Field f, Object instance, Property p, Object defaultVal) throws IllegalAccessException, IllegalArgumentException {
        String[] list = p.getStringList();
        ValueTypeArrayInteger.validateArrayLengths(((String[]) defaultVal).length, list.length, p.getMaxListLength(), p.isListLengthFixed());
        f.set(instance, list);
    }
}
