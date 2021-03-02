////////////////////////////////////////////////////////////////////////////////
// This file is subject to the terms and conditions defined in the             /
// file '.github/LICENSE.md', which is part of this source code package.       /
////////////////////////////////////////////////////////////////////////////////

package de.sanandrew.mods.sanlib.lib.util.config.type;

import de.sanandrew.mods.sanlib.lib.util.config.Range;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.lang.reflect.Field;

public class ValueTypeArrayDouble
        implements IValueType
{
    @Override
    public boolean typeFits(Class<?> type) {
        return type == double[].class;
    }

    @Override
    public Object getDefaultValue(Class<?> type, Field f, Object instance) throws IllegalAccessException, IllegalArgumentException {
        return f.get(instance);
    }

    @SuppressWarnings("FloatingPointEquality")
    @Override
    public Property getProperty(Configuration config, String category, String name, Object defaultVal, String propComment, Range propRange) {
        double[] def = (double[]) defaultVal;
        double min = propRange.minD();
        double max = propRange.maxD();
        boolean fixedList = propRange.listFixed();
        int maxListLength = propRange.maxListLength();

        StringBuilder cm = new StringBuilder();

        if( fixedList ) {
            cm.append("fixed list length: ").append(maxListLength == -1 ? def.length : maxListLength);
        } else if( maxListLength != -1 ) {
            cm.append("maximum list length: ").append(maxListLength);
        }

        if( min != -Double.MAX_VALUE || max != Double.MAX_VALUE ) {
            if( cm.length() > 0 ) {
                cm.append(", ");
            }
            if( min == -Double.MAX_VALUE ) {
                cm.append("element maximum: ").append(max);
            } else if( max == Double.MAX_VALUE ) {
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
        double[] list = p.getDoubleList();

        IValueType.validateArrayLengths(p.getName(), ((double[]) defaultVal).length, list.length, p.getMaxListLength(), p.isListLengthFixed());
        double minP = propRange.minD();
        double maxP = propRange.maxD();
        for( int i = 0, max = list.length; i < max; i++ ) {
            if( list[i] < minP || i > maxP ) {
                throw new IllegalArgumentException(String.format("The %s element of array %s does not fall within range!", MiscUtils.getListNrWithSuffix(i), p.getName()));
            }
        }

        f.set(instance, list);
    }
}
