////////////////////////////////////////////////////////////////////////////////
// This file is subject to the terms and conditions defined in the             /
// file '.github/LICENSE.md', which is part of this source code package.       /
////////////////////////////////////////////////////////////////////////////////

package de.sanandrew.mods.sanlib.lib.util;

import com.google.common.collect.Maps;
import org.objectweb.asm.Type;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;

/**
 * An utility class for Java reflection.
 */
@SuppressWarnings("unused")
public final class ReflectionUtils
{
    /**
     * A map of cached Methods, used to make reflection more efficient. The key is the class
     * name, followed by the SRG method name and an underscore in between.
     */
    private static final Map<String, Method> CACHED_METHODS = Maps.newHashMap();
    
    /**
     * A map of cached Fields, used to make reflection more efficient. Tke key is the class
     * name, followed by the SRG field name and an underscore in between.
     */
    private static final Map<String, Field> CACHED_FIELDS = Maps.newHashMap();

    /**
     * A map of cached Constructors, used to make reflection more efficient. Tke key is the class
     * name, followed by the argument types with underscores in between.
     */
    private static final Map<String, Constructor<?>> CACHED_CTOR = Maps.newHashMap();
    
    /**
     * A simple check to see if a class exists without initializing it.
     * 
     * @param className: The name of the class you are looking for.
     * @return boolean: Whether or not the specified class exists in the current environment.
     */
    public static boolean doesClassExist(String className) {
        try {
            Class.forName(className, false, null);
            return true;
        } catch( ClassNotFoundException | NoClassDefFoundError exception ) {
            return false;
        }
    }
    
    /**
     * Retrieves a Class by its name.
     * 
     * @param className: The name of the class you are trying to get.
     * @return The Class that was retrieved.
     */
    public static <T> Class<T> getClass(String className) {
        try {
            return getCasted(Class.forName(className));
        } catch (Exception exception) {
            return null;
        }
    }
    
    /**
     * Sets the value of a cached field.
     * 
     * @param classToAccess: The Class that contains the Field being set.
     * @param instance: An instance of the Class that contains the Field.
     * @param mcpName: The MCP mapping for the field name.
     * @param srgName: The SRG mapping for the field name.
     * @param value: The new value being set for the Field.
     */
    public static <T, E> void setCachedFieldValue(Class<? super E> classToAccess, E instance, String mcpName, String srgName, T value) {
        Field field = getCachedField(classToAccess, mcpName, srgName);
        
        try {
            field.set(instance, value);
        } catch( Throwable exception ) {
            throw new UnableToSetFieldException(exception);
        }
    }
    
    /**
     * Retrieves the value of a cached Field.
     * 
     * @param classToAccess: The Class that contains the Field being used.
     * @param instance: An instance of the Class that contains the Field.
     * @param mcpName: The MCP mapping for the field name.
     * @param srgName: The SRG mapping for the field name.
     * @return The value that the Field was set to.
     */
    public static <T, E> T getCachedFieldValue(Class<? super E> classToAccess, E instance, String mcpName, String srgName) {
        Field field = getCachedField(classToAccess, mcpName, srgName);
        
        try {
            return getCasted(field.get(instance));
        } catch( Exception exception ) {
            throw new UnableToGetFieldException(exception);
        }
    }
    
    /**
     * Invokes a Method that has been cached.
     * 
     * @param classToAccess: The Class that contains the method being accessed.
     * @param instance: An instance of the Class that contains the method.
     * @param mcpName: The MCP mapping for the method name.
     * @param srgName: The SRG mapping for the method name.
     * @param parameterTypes: The parameters used by this Method.
     * @param parameterValues: The parameter values to pass to the method.
     * @return The data returned by the method.
     */
    public static <T, E> T invokeCachedMethod(Class<? super E> classToAccess, E instance, String mcpName, String srgName, Class<?>[] parameterTypes, Object[] parameterValues) {
        Method method = getCachedMethod(classToAccess, mcpName, srgName, parameterTypes);
        
        try {
            return getCasted(method.invoke(instance, parameterValues));
        } catch( Exception exception ) {
            throw new UnableToInvokeMethodException(exception);
        }
    }
    
    /**
     * Retrieves a cached Field from the Field cache. This is used to re-access fields accessed
     * through reflection.
     * 
     * @param classToAccess: The Class that contains the field being accessed.
     * @param mcpName: The MCP mapping for the field name.
     * @param srgName: The SRG mapping for the field name.
     * @return A Field object which represents the field being accessed.
     */
    public static Field getCachedField(Class<?> classToAccess, String mcpName, String srgName) {
        String key = classToAccess.getCanonicalName() + '_' + srgName;
        
        if( CACHED_FIELDS.containsKey(key) ) {
            return CACHED_FIELDS.get(key);
        }
            
        return cacheAccessedField(classToAccess, mcpName, srgName);
    }
    
    /**
     * Caches a field that was accessed through reflections. This is used to make re-accessing
     * that field more efficient. This is for private use only.
     * 
     * @param classToAccess: The Class that contains the field being accessed.
     * @param mcpName: The MCP mapping for the field name.
     * @param srgName: The SRG mapping for the field name.
     * @return A Field object which represents the field being accessed.
     */
    private static Field cacheAccessedField(Class<?> classToAccess, String mcpName, String srgName) {
        Field method;
        String key = classToAccess.getCanonicalName() + '_' + srgName;
        
        try {
            method = classToAccess.getDeclaredField(srgName);
            method.setAccessible(true);
            CACHED_FIELDS.put(key, method);
            return method;
        } catch (Throwable ex1) {
            try {
                method = classToAccess.getDeclaredField(mcpName);
                method.setAccessible(true);
                CACHED_FIELDS.put(key, method);
                return method;
            } catch( Throwable ex2 ) {
                throw new UnableToFindFieldException(ex2);
            }
        }
    }
    
    /**
     * Retrieves a cached Method from the method cache. This is used to re-access methods
     * accessed via reflection.
     * 
     * @param classToAccess: The Class that contains the method you want.
     * @param mcpName: The MCP mapping for the method name.
     * @param srgName: The SRG mapping for the method name.
     * @param parameterTypes: The parameters that are used by the method.
     * @return A Method object which represents the method being found.
     */
    public static Method getCachedMethod(Class<?> classToAccess, String mcpName, String srgName, Class<?>... parameterTypes) {
        String key = classToAccess.getCanonicalName() + '_' + srgName;
        
        if( CACHED_METHODS.containsKey(key) ) {
            return CACHED_METHODS.get(key);
        }
            
        return cacheAccessedMethod(classToAccess, mcpName, srgName, parameterTypes);
    }
    
    /**
     * Caches a Method accessed through reflection to a cache. This makes it easier and more
     * efficient to access the method more than once. For internal use only.
     * 
     * @param classToAccess: The Class that contains the method being accessed.
     * @param mcpName: The MCP mapping for the method name.
     * @param srgName: The SRG mapping for the method name.
     * @param parameterTypes: The parameters that are used by this method.
     * @return A Method object which represents the newly cached method.
     */
    private static Method cacheAccessedMethod(Class<?> classToAccess, String mcpName, String srgName, Class<?>... parameterTypes) {
        Method method;
        String key = classToAccess.getCanonicalName() + '_' + srgName;
        
        try {
            method = classToAccess.getDeclaredMethod(srgName, parameterTypes);
            method.setAccessible(true);
            CACHED_METHODS.put(key, method);
            return method;
        } catch( Throwable ex1 ) {
            try {
                method = classToAccess.getDeclaredMethod(mcpName, parameterTypes);
                method.setAccessible(true);
                CACHED_METHODS.put(key, method);
                return method;
            } catch( Throwable ex2 ) {
                throw new UnableToFindMethodException(ex2);
            }
        }
    }

    /**
     * Retrieves a new instance of a given class by invoking its constructor from the constructor cache. This is used to instanciate private classes
     * or non-private classes with private constructors.
     *
     * @param classToAccess: The Class that needs to be instanciated.
     * @param parameterTypes: The parameters that are used by the method.
     * @return A Method object which represents the method being found.
     */
    public static Object getNew(String classToAccess, Class<?>[] parameterTypes, Object... parameters) {
        String key = classToAccess + '_' + String.join("", Arrays.stream(parameterTypes).map(Type::getDescriptor).toArray(String[]::new));
        Constructor<?> c;

        if( CACHED_CTOR.containsKey(key) ) {
            c = CACHED_CTOR.get(key);
        } else {
            c = cacheAccessedConstructor(classToAccess, parameterTypes);
        }

        try {
            return c.newInstance(parameters);
        } catch( InstantiationException | IllegalAccessException | InvocationTargetException e ) {
            throw new UnableToInstantiateException(e);
        }
    }

    /**
     * Caches a constructor accessed through reflection to a cache. This makes it easier and more
     * efficient to access the constructor more than once. For internal use only.
     *
     * @param classToAccess: The Class that contains the method being accessed.
     * @param parameterTypes: The parameters that are used by this constructor.
     * @return A Constructor object which represents the newly cached constructor.
     */
    private static Constructor<?> cacheAccessedConstructor(String classToAccess, Class<?>... parameterTypes) {
        Constructor<?> ctor;
        String key = classToAccess + '_' + String.join("", Arrays.stream(parameterTypes).map(Type::getDescriptor).toArray(String[]::new));

        try {
            Class<?> clazz = Class.forName(classToAccess);
            ctor = clazz.getDeclaredConstructor(parameterTypes);
            ctor.setAccessible(true);
            CACHED_CTOR.put(key, ctor);
            return ctor;
        } catch( Throwable ex ) {
            throw new UnableToAccessCtorException(ex);
        }
    }
    
    /**
     * Casts an Object to a generic Type.
     * 
     * @param obj: The Object to cast.
     * @return <T> T: The Object as a generic Type.
     */
    @SuppressWarnings("unchecked")
    public static <T> T getCasted(Object obj) {
        return (T) obj;
    }
    
    public static class UnableToSetFieldException
            extends RuntimeException
    {
        private static final long serialVersionUID = -9156919529246923057L;

        /**
         * An exception thrown when an attempt is made to set a field via reflection, however
         * the field could not be set.
         * 
         * @param exception: An instance of the exception being thrown.
         */
        public UnableToSetFieldException(Throwable exception) {
            super(exception);
        }
    }
    
    public static class UnableToGetFieldException
            extends RuntimeException
    {
        private static final long serialVersionUID = -4541085527693869891L;

        /**
         * An exception thrown when an attempt is made to retrieve a field via reflection.
         * 
         * @param exception: An instance of the exception being thrown.
         */
        public UnableToGetFieldException(Throwable exception) {
            super(exception);
        }
    }
    
    public static class UnableToInvokeMethodException
            extends RuntimeException
    {
        private static final long serialVersionUID = -784953849174764940L;

        /**
         * An exception thrown when an attempt to invoke a method via reflection has failed.
         * 
         * @param exception: An instance of the exception being thrown.
         */
        public UnableToInvokeMethodException(Throwable exception) {
            super(exception);
        }
    }
    
    public static class UnableToFindMethodException
            extends RuntimeException
    {
        private static final long serialVersionUID = -8564063759409317615L;

        /**
         * An exception thrown when an attempt to look up a method is made, but it could not be
         * found.
         * 
         * @param exception: An instance of the exception being thrown.
         */
        public UnableToFindMethodException(Throwable exception) {
            super(exception);
        }
    }
    
    public static class UnableToFindFieldException
            extends RuntimeException
    {
        private static final long serialVersionUID = -8782108462439942148L;

        /**
         * An exception thrown when an attempt to look up a field is made, but it could not be
         * found.
         * 
         * @param exception: An instance of the exception being thrown.
         */
        public UnableToFindFieldException(Throwable exception) {
            super(exception);
        }
    }

    public static class UnableToAccessCtorException
            extends RuntimeException
    {
        private static final long serialVersionUID = -8782108462439942148L;

        /**
         * An exception thrown when an attempt to access a constructor is made, but it failed to do so.
         *
         * @param exception: An instance of the exception being thrown.
         */
        public UnableToAccessCtorException(Throwable exception) {
            super(exception);
        }
    }

    public static class UnableToInstantiateException
            extends RuntimeException
    {
        private static final long serialVersionUID = -8782108462439942148L;

        /**
         * An exception thrown when an attempt to instantiate a class via constructor is made, but it failed to do so.
         *
         * @param exception: An instance of the exception being thrown.
         */
        public UnableToInstantiateException(Throwable exception) {
            super(exception);
        }
    }
}