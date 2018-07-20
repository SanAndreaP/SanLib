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

    void setValue(Class<?> type, Field f, Object instance, Property p, Object defaultVal) throws IllegalAccessException, IllegalArgumentException;
}
