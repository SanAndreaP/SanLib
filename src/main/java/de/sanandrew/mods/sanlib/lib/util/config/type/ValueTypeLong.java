/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.sanlib.lib.util.config.type;

import de.sanandrew.mods.sanlib.lib.util.config.Range;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.lang.reflect.Field;

@SuppressWarnings("ObjectEquality")
public class ValueTypeLong
        implements IValueType
{
    @Override
    public boolean typeFits(Class<?> type) {
        return type == long.class;
    }

    @Override
    public Object getDefaultValue(Class<?> type, Field f, Object instance) throws IllegalAccessException, IllegalArgumentException {
        return f.getLong(instance);
    }

    @Override
    public Property getProperty(Configuration config, String category, String name, Object defaultVal, String propComment, Range propRange) {
        long min = propRange.minL();
        long max = propRange.maxL();
        long dvl = (long) defaultVal;
        StringBuilder comment = new StringBuilder(propComment);

        if( min == Long.MIN_VALUE ) {
            if( max == Long.MAX_VALUE ) {
                comment.append(" [default: ").append(defaultVal).append(']');
            } else {
                comment.append(" [maximum: ").append(max).append(", default: ").append(defaultVal).append(']');
            }
        } else if( max == Long.MAX_VALUE ) {
            comment.append(" [minimum: ").append(min).append(", default: ").append(defaultVal).append(']');
        } else {
            comment.append(" [range: ").append(min).append(" ~ ").append(max).append(", default: ").append(defaultVal).append(']');
        }

        Property prop = config.get(category, name, Long.toString(dvl), comment.toString().trim(), Property.Type.DOUBLE);
        prop.setDefaultValue(Long.toString(dvl));
        prop.setMinValue(min);
        prop.setMaxValue(max);

        if( !prop.isLongValue() ) {
            prop.setValue(dvl);
        }

        return prop;
    }

    @Override
    public void setValue(Class<?> type, Field f, Object instance, Property p, Object defaultVal, Range propRange) throws IllegalAccessException, IllegalArgumentException {
        long i = p.getLong();
        if( i < propRange.minL() || i > propRange.maxL() ) {
            throw new IllegalArgumentException(String.format("The property %s does not fall within range!", p.getName()));
        }

        if( type == long.class ) {
            f.setLong(instance, i);
        }
    }
}
