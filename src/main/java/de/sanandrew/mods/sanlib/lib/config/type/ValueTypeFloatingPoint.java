/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.sanlib.lib.config.type;

import de.sanandrew.mods.sanlib.lib.config.Range;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.lang.reflect.Field;

public class ValueTypeFloatingPoint
    implements IValueType
{
    @Override
    public boolean typeFits(Class<?> type) {
        return type == float.class || type == double.class;
    }

    @Override
    public Object getDefaultValue(Class<?> type, Field f, Object instance) throws IllegalAccessException, IllegalArgumentException {
        return type == float.class ? Double.valueOf(Float.valueOf(f.getFloat(instance)).toString()) : f.getDouble(instance);
    }

    @Override
    @SuppressWarnings("FloatingPointEquality")
    public Property getProperty(Configuration config, String category, String name, Object defaultVal, String propComment, Range propRange) {
        double min = propRange.minD();
        double max = propRange.maxD();

        StringBuilder comment = new StringBuilder(propComment);
        if( min == -Double.MAX_VALUE ) {
            if( max == Double.MAX_VALUE ) {
                comment.append(" [default: ").append(defaultVal).append(']');
            } else {
                comment.append(" [maximum: ").append(max).append(", default: ").append(defaultVal).append(']');
            }
        } else if( max == Double.MAX_VALUE ) {
            comment.append(" [minimum: ").append(min).append(", default: ").append(defaultVal).append(']');
        } else {
            comment.append(" [range: ").append(min).append(" ~ ").append(max).append(", default: ").append(defaultVal).append(']');
        }

        return config.get(category, name, (double) defaultVal, comment.toString().trim(), min, max);
    }

    @Override
    public void setValue(Class<?> type, Field f, Object instance, Property p, Object defaultVal, Range propRange) throws IllegalAccessException, IllegalArgumentException {
        double d = p.getDouble();
        if( d < propRange.minD() || d > propRange.maxD() ) {
            throw new IllegalArgumentException(String.format("The property %s does not fall within range!", p.getName()));
        }

        if( type == float.class ) {
            f.setFloat(instance, (float) d);
        } else {
            f.setDouble(instance, d);
        }
    }
}
