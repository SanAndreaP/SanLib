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

public class ValueTypeArrayInteger
        implements IValueType
{
    @Override
    public boolean typeFits(Class<?> type) {
        return type == int[].class;
    }

    @Override
    public Object getDefaultValue(Class<?> type, Field f, Object instance) throws IllegalAccessException, IllegalArgumentException {
        return f.get(instance);
    }

    @Override
    public Property getProperty(Configuration config, String category, String name, Object defaultVal, String propComment, Range propRange) {
        int[] def = (int[]) defaultVal;
        int min = propRange.minI();
        int max = propRange.maxI();
        boolean fixedList = propRange.listFixed();
        int maxListLength = propRange.maxListLength();

        StringBuilder cm = new StringBuilder();

        if( fixedList ) {
            cm.append("fixed list length: ").append(maxListLength == -1 ? def.length : maxListLength);
        } else if( maxListLength != -1 ) {
            cm.append("maximum list length: ").append(maxListLength);
        }

        if( min != Integer.MIN_VALUE || max != Integer.MAX_VALUE ) {
            if( cm.length() > 0 ) {
                cm.append(", ");
            }
            if( min == Integer.MIN_VALUE ) {
                cm.append("element maximum: ").append(max);
            } else if( max == Integer.MAX_VALUE ) {
                cm.append("element minimum: ").append(min);
            } else {
                cm.append("element range: ").append(min).append(" ~ ").append(max);
            }
        }

        if( cm.length() > 0 ) {
            propComment += " [" + (cm) + ']';
        }

        return config.get(category, name, def, propComment.trim(), min, max, fixedList, maxListLength);
    }

    @Override
    public void setValue(Class<?> type, Field f, Object instance, Property p, Object defaultVal, Range propRange) throws IllegalAccessException, IllegalArgumentException {
        int[] list = p.getIntList();

        IValueType.validateArrayLengths(p.getName(), ((int[]) defaultVal).length, list.length, p.getMaxListLength(), p.isListLengthFixed());
        int minP = propRange.minI();
        int maxP = propRange.maxI();
        for( int i = 0, max = list.length; i < max; i++ ) {
            if( list[i] < minP || i > maxP ) {
                throw new IllegalArgumentException(String.format("The %s element of array %s does not fall within range!", MiscUtils.getListNrWithSuffix(i), p.getName()));
            }
        }

        f.set(instance, list);
    }

}
