////////////////////////////////////////////////////////////////////////////////
// This file is subject to the terms and conditions defined in the             /
// file '.github/LICENSE.md', which is part of this source code package.       /
////////////////////////////////////////////////////////////////////////////////

package de.sanandrew.mods.sanlib.lib.util.config.type;

import de.sanandrew.mods.sanlib.lib.util.config.Range;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.lang.reflect.Field;

public class ValueTypeArrayBoolean
        implements IValueType
{
    @Override
    public boolean typeFits(Class<?> type) {
        return type == boolean[].class;
    }

    @Override
    public Object getDefaultValue(Class<?> type, Field f, Object instance) throws IllegalAccessException, IllegalArgumentException {
        return f.get(instance);
    }

    @Override
    public Property getProperty(Configuration config, String category, String name, Object defaultVal, String propComment, Range propRange) {
        boolean[] def = (boolean[]) defaultVal;
        boolean fixedList = propRange.listFixed();
        int maxListLength = propRange.maxListLength();

        StringBuilder cm = new StringBuilder();

        if( fixedList ) {
            cm.append("fixed list length: ").append(maxListLength == -1 ? def.length : maxListLength);
        } else if( maxListLength != -1 ) {
            cm.append("maximum list length: ").append(maxListLength);
        }

        if( cm.length() > 0 ) {
            propComment += " [" + (cm) + ']';
        }

        return config.get(category, name, def, propComment.trim(), fixedList, maxListLength);
    }

    @Override
    public void setValue(Class<?> type, Field f, Object instance, Property p, Object defaultVal, Range propRange) throws IllegalAccessException, IllegalArgumentException {
        boolean[] list = p.getBooleanList();
        IValueType.validateArrayLengths(p.getName(), ((boolean[]) defaultVal).length, list.length, p.getMaxListLength(), p.isListLengthFixed());
        f.set(instance, list);
    }
}
