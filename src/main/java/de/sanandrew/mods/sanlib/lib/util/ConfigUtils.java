/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.sanlib.lib.util;

import de.sanandrew.mods.sanlib.SanLib;
import de.sanandrew.mods.sanlib.lib.Tuple;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Level;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@SuppressWarnings({"NewClassNamingConvention", "unused", "WeakerAccess"})
public class ConfigUtils
{
    public static Configuration loadConfigFile(File cfgFile, String version, String modName) {
        Configuration config = new Configuration(cfgFile, version, true);
        String loadedVer = config.getLoadedConfigVersion();
        if( loadedVer != null && Integer.parseInt(loadedVer.split("\\.")[0]) < Integer.parseInt(version.split("\\.")[0]) ) {
            try {
                FileUtils.copyFile(cfgFile, new File(cfgFile.getAbsoluteFile() + ".old"));
                config.getCategoryNames().forEach(cat -> config.removeCategory(config.getCategory(cat)));
                SanLib.LOG.log(Level.WARN, String.format("%s config file is too outdated! Config will be overwritten - the old config file can be found at %s.old", modName, cfgFile.getAbsoluteFile()));
            } catch( IOException ex ) {
                SanLib.LOG.log(Level.ERROR, String.format("%s config file is too outdated but cannot be updated! This will cause errors! Please copy the old config somewhere and remove it from the config folder!", modName), ex);
            }
        }

        return config;
    }

    private static void loadCategories(Configuration config, Class<?> base) {
        for( Class<?> c : base.getDeclaredClasses() ) {
            loadCategory(config, c);
        }
    }

    public static void loadCategory(Configuration config, Class<?> c) {
        Category cat = c.getAnnotation(Category.class);
        if( cat != null ) {
            ConfigCategory cCat = config.getCategory(cat.value());
            if( !cat.inherit() ) {
                cCat.setComment(cat.comment());
                cCat.setRequiresMcRestart(cat.reqMcRestart());
                cCat.setRequiresWorldRestart(cat.reqWorldRestart());
            }

            if( c.isEnum() ) {
                for( Field f : c.getDeclaredFields() ) {
                    if( f.isEnumConstant() && f.getAnnotation(EnumExclude.class) == null ) {
                        try {
                            loadValues(config, cat, c, f.get(null), f.getName().toLowerCase(Locale.ROOT));
                        } catch( IllegalAccessException ex ) {
                            SanLib.LOG.log(Level.ERROR, String.format("Could not load config value for enum value %s in enum %s", f.getName(), f.getDeclaringClass().getName()), ex);
                        }
                    }
                }
            } else {
                loadCategories(config, c);
                loadValues(config, cat, c);

                try {
                    Method init = c.getDeclaredMethod("init");
                    init.invoke(null);
                } catch( NoSuchMethodException | IllegalAccessException | InvocationTargetException ignored ) { }
            }
        }
    }

    private static void loadValues(Configuration config, Category cat, Class<?> c) {
        loadValues(config, cat, c, null, null);
    }

    private static final Map<String, Tuple> DEFAULTS = new HashMap<>();
    @SuppressWarnings("ObjectEquality")
    private static void loadValues(Configuration config, Category cat, Class<?> c, Object inst, String instName) {
        for( Field f : c.getDeclaredFields() ) {
            Value val = f.getAnnotation(Value.class);
            try {
                if( val != null ) {
                    Class<?> cv = f.getType();
                    String name = val.value().isEmpty() ? f.getName() : String.format(val.value(), instName);
                    String comment = String.format(val.comment(), instName);
                    String category = val.category().isEmpty() ? cat.value() : val.category();
                    String defKey = getDefaultKey(config, category, name);

                    if( cv == long.class || cv == int.class || cv == short.class || cv == byte.class ) {
                        if( !DEFAULTS.containsKey(defKey) ) {
                            DEFAULTS.put(defKey, new Tuple(cv == long.class ? (int) f.getLong(inst) : f.getInt(inst)));
                        }
                        Property p = config.get(category, name, DEFAULTS.get(defKey).<Integer>getValue(0), comment, val.range().minI(), val.range().maxI());
                        p.setRequiresMcRestart(val.reqMcRestart());
                        p.setRequiresWorldRestart(val.reqWorldRestart());

                        if( cv == long.class || cv == int.class ) {
                            f.setInt(inst, p.getInt());
                        } else if( cv == short.class ) {
                            f.setShort(inst, (short) p.getInt());
                        } else if( cv == byte.class ) {
                            f.setByte(inst, (byte) p.getInt());
                        }
                    } else if( cv == double.class || cv == float.class ) {
                        if( !DEFAULTS.containsKey(defKey) ) {
                            DEFAULTS.put(defKey, new Tuple(cv == float.class ? Double.valueOf(Float.valueOf(f.getFloat(inst)).toString()) : f.getDouble(inst)));
                        }
                        Property p = config.get(category, name, DEFAULTS.get(defKey).<Double>getValue(0), comment, val.range().minD(), val.range().maxD());
                        p.setRequiresMcRestart(val.reqMcRestart());
                        p.setRequiresWorldRestart(val.reqWorldRestart());

                        if( cv == float.class ) {
                            f.setFloat(inst, (float) p.getDouble());
                        } else {
                            f.setDouble(inst, p.getDouble());
                        }
                    } else if( cv == boolean.class ) {
                        if( !DEFAULTS.containsKey(defKey) ) {
                            DEFAULTS.put(defKey, new Tuple(f.getBoolean(inst)));
                        }
                        Property p = config.get(category, name, DEFAULTS.get(defKey).<Boolean>getValue(0), comment);
                        p.setRequiresMcRestart(val.reqMcRestart());
                        p.setRequiresWorldRestart(val.reqWorldRestart());
                        f.setBoolean(inst, p.getBoolean());
                    } else if( cv == String.class ) {
                        if( !DEFAULTS.containsKey(defKey) ) {
                            DEFAULTS.put(defKey, new Tuple(f.get(inst).toString()));
                        }
                        Property p = config.get(category, name, DEFAULTS.get(defKey).<String>getValue(0), comment);
                        p.setRequiresMcRestart(val.reqMcRestart());
                        p.setRequiresWorldRestart(val.reqWorldRestart());
                        f.set(inst, p.getString());
                    } else if( cv == int[].class ) {
                        if( !DEFAULTS.containsKey(defKey) ) {
                            DEFAULTS.put(defKey, new Tuple((Object) f.get(inst)));
                        }
                        Property p = config.get(category, name, DEFAULTS.get(defKey).<int[]>getValue(0), comment, val.range().minI(), val.range().maxI(),
                                                val.range().listFixed(), val.range().maxListLength());
                        p.setRequiresMcRestart(val.reqMcRestart());
                        p.setRequiresWorldRestart(val.reqWorldRestart());
                        f.set(inst, p.getIntList());
                    } else if( cv == double[].class ) {
                        if( !DEFAULTS.containsKey(defKey) ) {
                            DEFAULTS.put(defKey, new Tuple((Object) f.get(inst)));
                        }
                        Property p = config.get(category, name, DEFAULTS.get(defKey).getValue(0), comment, val.range().minD(), val.range().maxD(),
                                                val.range().listFixed(), val.range().maxListLength());
                        p.setRequiresMcRestart(val.reqMcRestart());
                        p.setRequiresWorldRestart(val.reqWorldRestart());
                        f.set(inst, p.getDoubleList());
                    } else if( cv == boolean[].class ) {
                        if( !DEFAULTS.containsKey(defKey) ) {
                            DEFAULTS.put(defKey, new Tuple((Object) f.get(inst)));
                        }
                        Property p = config.get(category, name, DEFAULTS.get(defKey).getValue(0), comment, val.range().listFixed(), val.range().maxListLength());
                        p.setRequiresMcRestart(val.reqMcRestart());
                        p.setRequiresWorldRestart(val.reqWorldRestart());
                        f.set(inst, p.getBooleanList());
                    } else if( cv == String[].class ) {
                        if( !DEFAULTS.containsKey(defKey) ) {
                            DEFAULTS.put(defKey, new Tuple((Object) f.get(inst)));
                        }
                        Pattern validationPattern = val.range().validationPattern();
                        @SuppressWarnings("MagicConstant")
                        Property p = config.get(category, name, DEFAULTS.get(defKey).getValue(0), comment, val.range().listFixed(), val.range().maxListLength(),
                                                validationPattern.regex().isEmpty() ? null : java.util.regex.Pattern.compile(validationPattern.regex(),
                                                                                                                             validationPattern.flags()));
                        p.setRequiresMcRestart(val.reqMcRestart());
                        p.setRequiresWorldRestart(val.reqWorldRestart());
                        f.set(inst, p.getStringList());
                    }
                }
            } catch( IllegalAccessException | IllegalArgumentException ex ) {
                SanLib.LOG.log(Level.ERROR, String.format("Could not load config value for field %s in class %s", f.getName(), f.getDeclaringClass().getName()), ex);
            }
        }
    }

    private static String getDefaultKey(Configuration config, String cat, String val) {
        return config.toString() + Configuration.CATEGORY_SPLITTER + config.getCategory(cat).getQualifiedName() + Configuration.CATEGORY_SPLITTER + val;
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface Category
    {
        String value();
        String comment() default "";
        boolean reqMcRestart() default false;
        boolean reqWorldRestart() default false;
        boolean inherit() default false;
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Value
    {
        String value() default "";
        String category() default "";
        String comment() default "";
        Range range() default @Range;
        boolean reqMcRestart() default false;
        boolean reqWorldRestart() default false;
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.ANNOTATION_TYPE)
    public @interface Range
    {
        int minI() default Integer.MIN_VALUE;
        int maxI() default Integer.MAX_VALUE;
        double minD() default -Double.MAX_VALUE;
        double maxD() default Double.MAX_VALUE;
        boolean listFixed() default false;
        int maxListLength() default -1;
        Pattern validationPattern() default @Pattern;
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.ANNOTATION_TYPE)
    public @interface Pattern {
        String regex() default "";
        int flags() default 0;
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface EnumExclude {}
}
