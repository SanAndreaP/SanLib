/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.sanlib.lib.config.type;

import de.sanandrew.mods.sanlib.lib.config.Range;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.lang.reflect.Field;

@SuppressWarnings("ObjectEquality")
public class ValueTypeInteger
        implements IValueType
{
    @Override
    public boolean typeFits(Class<?> type) {
        return type == long.class || type == int.class || type == short.class || type == byte.class;
    }

    @Override
    public Object getDefaultValue(Class<?> type, Field f, Object instance) throws IllegalAccessException, IllegalArgumentException {
        return type == long.class ? (int) f.getLong(instance) : f.getInt(instance);
    }

    @Override
    public Property getProperty(Configuration config, String category, String name, Object defaultVal, String propComment, Range propRange) {
        int min = propRange.minI();
        int max = propRange.maxI();
        StringBuilder comment = new StringBuilder(propComment);
        if( min == Integer.MIN_VALUE ) {
            if( max == Integer.MAX_VALUE ) {
                comment.append(" [default: ").append(defaultVal).append(']');
            } else {
                comment.append(" [maximum: ").append(max).append(", default: ").append(defaultVal).append(']');
            }
        } else if( max == Integer.MAX_VALUE ) {
            comment.append(" [minimum: ").append(min).append(", default: ").append(defaultVal).append(']');
        } else {
            comment.append(" [range: ").append(min).append(" ~ ").append(max).append(", default: ").append(defaultVal).append(']');
        }

        return config.get(category, name, (int) defaultVal, comment.toString().trim(), min, max);
    }

    @Override
    public void setValue(Class<?> type, Field f, Object instance, Property p, Object defaultVal) throws IllegalAccessException, IllegalArgumentException {
        if( type == long.class || type == int.class ) {
            f.setInt(instance, p.getInt());
        } else if( type == short.class ) {
            f.setShort(instance, (short) p.getInt());
        } else if( type == byte.class ) {
            f.setByte(instance, (byte) p.getInt());
        }
    }
}
