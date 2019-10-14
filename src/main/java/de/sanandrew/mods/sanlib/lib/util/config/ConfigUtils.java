/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.sanlib.lib.util.config;

import de.sanandrew.mods.sanlib.SanLib;
import de.sanandrew.mods.sanlib.lib.util.config.type.IValueType;
import de.sanandrew.mods.sanlib.lib.util.config.type.ValueTypeArrayBoolean;
import de.sanandrew.mods.sanlib.lib.util.config.type.ValueTypeArrayDouble;
import de.sanandrew.mods.sanlib.lib.util.config.type.ValueTypeArrayInteger;
import de.sanandrew.mods.sanlib.lib.util.config.type.ValueTypeArrayString;
import de.sanandrew.mods.sanlib.lib.util.config.type.ValueTypeBoolean;
import de.sanandrew.mods.sanlib.lib.util.config.type.ValueTypeFloatingPoint;
import de.sanandrew.mods.sanlib.lib.util.config.type.ValueTypeInteger;
import de.sanandrew.mods.sanlib.lib.util.config.type.ValueTypeString;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Level;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@SuppressWarnings({"unused", "WeakerAccess"})
public class ConfigUtils
{
    @SuppressWarnings("serial")
    public static final List<IValueType> TYPE_LIST = new ArrayList<IValueType>() {{
        this.add(new ValueTypeInteger());
        this.add(new ValueTypeFloatingPoint());
        this.add(new ValueTypeBoolean());
        this.add(new ValueTypeString());
        this.add(new ValueTypeArrayInteger());
        this.add(new ValueTypeArrayDouble());
        this.add(new ValueTypeArrayBoolean());
        this.add(new ValueTypeArrayString());
    }};

    private static final Map<String, Object> DEFAULTS = new HashMap<>();

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

    public static void loadCategories(Configuration config, Class<?> base) {
        invokeInit(base, Init.Stage.PRE);
        loadCategories(config, base, null);
        invokeInit(base, Init.Stage.POST);
    }

    private static void loadCategories(Configuration config, Class<?> base, String prefix) {
        for( Class<?> c : base.getDeclaredClasses() ) {
            loadCategory(config, c, prefix);
        }
    }

    private static void invokeInit(Class<?> base, Init.Stage stage) {
        if( !base.isEnum() ) {
            try {
                Method init = Arrays.stream(base.getMethods()).filter(m -> {
                    Init a = m.getAnnotation(Init.class);
                    return a != null && a.value() == stage;
                }).findFirst().orElseGet(() -> getOldInit(base));
                if( init != null ) {
                    init.invoke(null);
                }
            } catch( IllegalAccessException | InvocationTargetException ex ) {
                SanLib.LOG.log(Level.WARN, String.format("Could not call initializer in class %s", base.getName()), ex);
            }
        }
    }

    @Deprecated
    private static Method getOldInit(Class<?> c) {
        try {
            return c.getMethod("init");
        } catch( NoSuchMethodException ignored ) { }

        return null;
    }

    public static void loadCategory(Configuration config, Class<?> c, String prefix) {
        Category cat = c.getAnnotation(Category.class);
        loadCategory(config, c, prefix, cat, null);
    }

    private static <E> void loadCategory(Configuration config, Class<? extends E> c, String prefix, Category cat, E enumInst) {
        if( cat != null ) {
            String qualifiedName = prefix != null ? prefix + Configuration.CATEGORY_SPLITTER + cat.value() : cat.value();
            ConfigCategory cCat = config.getCategory(qualifiedName);
            if( !cat.inherit() ) {
                cCat.setComment(cat.comment());
                cCat.setRequiresMcRestart(cat.reqMcRestart());
                cCat.setRequiresWorldRestart(cat.reqWorldRestart());
            }

            if( c.isEnum() ) {
                if( enumInst != null ) {
                    loadValues(config, qualifiedName, c, enumInst);
                } else {
                    for( Field f : c.getDeclaredFields() ) {
                        if( f.isEnumConstant() && f.getAnnotation(EnumExclude.class) == null ) {
                            try {
                                Category catInner = new Category() {
                                    private final String name = f.getName().toLowerCase(Locale.ROOT);
                                    @Override public Class<? extends Annotation> annotationType() { return Category.class; }
                                    @Override public String value() { return this.name; }
                                    @Override public String comment() { return ""; }
                                    @Override public boolean reqMcRestart() { return false; }
                                    @Override public boolean reqWorldRestart() { return false; }
                                    @Override public boolean inherit() { return true; }
                                };

                                loadCategory(config, c, qualifiedName, catInner, f.get(null));
                            } catch( IllegalAccessException ex ) {
                                SanLib.LOG.log(Level.ERROR, String.format("Could not load config value for enum value %s in enum %s", f.getName(), f.getDeclaringClass().getName()), ex);
                            }
                        }
                    }
                }
            } else {
                invokeInit(c, Init.Stage.PRE);
                loadCategories(config, c, qualifiedName);
                loadValues(config, qualifiedName, c);
                invokeInit(c, Init.Stage.POST);
            }
        }
    }

    private static void loadValues(Configuration config, String catName, Class<?> c) {
        loadValues(config, catName, c, null);
    }

    @SuppressWarnings({"ObjectEquality", "FloatingPointEquality"})
    private static void loadValues(Configuration config, String catName, Class<?> c, Object inst) {
        for( Field f : c.getDeclaredFields() ) {
            Value val = f.getAnnotation(Value.class);
            try {
                if( val != null ) {
                    Class<?> cv = f.getType();
                    String name = val.value().isEmpty() ? f.getName() : val.value();
                    String category = val.category().isEmpty() ? catName : val.category();
                    String defKey = getDefaultKey(config, category, name);

                    boolean parsed = false;
                    for( IValueType type : TYPE_LIST ) {
                        if( type.typeFits(cv) ) {
                            if( !DEFAULTS.containsKey(defKey) ) {
                                DEFAULTS.put(defKey, type.getDefaultValue(cv, f, inst));
                            }

                            Object def = DEFAULTS.get(defKey);
                            Property p = type.getProperty(config, category, name, def, val.comment(), val.range());
                            p.setRequiresMcRestart(val.reqMcRestart());
                            p.setRequiresWorldRestart(val.reqWorldRestart());
                            type.setValue(cv, f, inst, p, def, val.range());

                            parsed = true;
                            break;
                        }
                    }

                    if( !parsed ) {
                        SanLib.LOG.log(Level.ERROR, String.format("The type %s of field %s in class %s is not supported! Please register a custom value parser in ConfigUtils.TYPE_LIST.",
                                                                  cv.getName(), f.getName(), f.getDeclaringClass().getName()));
                    }
                }
            } catch( IllegalAccessException | IllegalArgumentException ex ) {
                throw new RuntimeException(String.format("Could not load config value for field %s in class %s", f.getName(), f.getDeclaringClass().getName()), ex);
            }
        }
    }

    private static String getDefaultKey(Configuration config, String cat, String val) {
        return config.toString() + Configuration.CATEGORY_SPLITTER + config.getCategory(cat).getQualifiedName() + Configuration.CATEGORY_SPLITTER + val;
    }
}
