package de.sanandrew.mods.sanlib.lib.config.type;

import de.sanandrew.mods.sanlib.lib.config.Range;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.lang.reflect.Field;

public interface IValueType
{
    boolean typeFits(Class<?> type);

    Object getDefaultValue(Class<?> type, Field f, Object instance) throws IllegalAccessException, IllegalArgumentException;

    Property getProperty(Configuration config, String category, String name, Object defaultVal, String propComment, Range propRange);

    void setValue(Class<?> type, Field f, Object instance, Property p, Object defaultVal, Range propRange) throws IllegalAccessException, IllegalArgumentException;

    static void validateArrayLengths(String qName, int defaultLength, int currLength, int maxLength, boolean fixed) throws IllegalArgumentException {
        if( fixed ) {
            if( currLength != (maxLength >= 0 ? maxLength : defaultLength) ) {
                throw new IllegalArgumentException(String.format("Current length of array %s in config does not match fixed length!", qName));
            }
        } else if( maxLength >= 0 && currLength > maxLength ) {
            throw new IllegalArgumentException(String.format("Current length of array %s in config is bigger than maximum length!", qName));
        }
    }
}
