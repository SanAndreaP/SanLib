/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2016-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.sanlib.lib.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.Range;

import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;

@SuppressWarnings({"unused", "WeakerAccess"})
public final class JsonUtils
{
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private static final Range<Integer> FULL_ARRAY_RANGE = Range.between(0, Integer.MAX_VALUE);

    public static <T> T fromJson(Reader reader, Class<T> clazz) {
        JsonReader jsonReader = new JsonReader(reader);
        try {
            return GSON.getAdapter(clazz).read(jsonReader);
        } catch( IOException e ) {
            throw new JsonParseException(e);
        }
    }

    public static float getFloatVal(JsonElement json) {
        requirePrimitive(json);

        return json.getAsFloat();
    }

    public static float getFloatVal(JsonElement json, float defVal) {
        if( json == null || !json.isJsonPrimitive() ) {
            return defVal;
        }

        return json.getAsFloat();
    }

    public static void fetchFloat(JsonElement json, Consumer<Float> proc) {
        if( json != null && json.isJsonPrimitive() ) {
            proc.accept(json.getAsFloat());
        }
    }

    public static double getDoubleVal(JsonElement json) {
        requirePrimitive(json);

        return json.getAsDouble();
    }

    public static double getDoubleVal(JsonElement json, double defVal) {
        if( json == null || !json.isJsonPrimitive() ) {
            return defVal;
        }

        return json.getAsDouble();
    }

    public static void fetchDouble(JsonElement json, Consumer<Double> proc) {
        if( json != null && json.isJsonPrimitive() ) {
            proc.accept(json.getAsDouble());
        }
    }

    public static String getStringVal(JsonElement json) {
        if( json == null || json.isJsonNull() ) {
            return null;
        }

        if( !json.isJsonPrimitive() ) {
            throw new JsonSyntaxException("Expected value to be a primitive");
        }

        return json.getAsString();
    }

    public static String getStringVal(JsonElement json, String defVal) {
        if( json == null || !json.isJsonPrimitive() ) {
            return defVal;
        }

        return json.getAsString();
    }

    public static void fetchString(JsonElement json, Consumer<String> proc) {
        if( json != null && json.isJsonPrimitive() ) {
            proc.accept(json.getAsString());
        }
    }

    public static int getIntVal(JsonElement json) {
        requirePrimitive(json);

        return json.getAsInt();
    }

    public static int getIntVal(JsonElement json, int defVal) {
        if( json == null || !json.isJsonPrimitive() ) {
            return defVal;
        }

        return json.getAsInt();
    }

    public static void fetchInt(JsonElement json, IntConsumer proc) {
        if( json != null && json.isJsonPrimitive() ) {
            proc.accept(json.getAsInt());
        }
    }

    public static boolean getBoolVal(JsonElement json) {
        requirePrimitive(json);

        return json.getAsBoolean();
    }

    public static boolean getBoolVal(JsonElement json, boolean defVal) {
        if( json == null || !json.isJsonPrimitive() ) {
            return defVal;
        }

        return json.getAsBoolean();
    }

    public static void fetchBool(JsonElement json, Consumer<Boolean> proc) {
        if( json != null && json.isJsonPrimitive() ) {
            proc.accept(json.getAsBoolean());
        }
    }

    public static ResourceLocation getLocation(JsonElement json) {
        if( json != null && json.isJsonObject() ) {
            JsonObject jObj = json.getAsJsonObject();
            String namespace = getStringVal(jObj.get("namespace"), "minecraft");
            String path = getStringVal(jObj.get("path"), null);
            if( path != null ) {
                return new ResourceLocation(namespace, path);
            } else {
                throw new JsonSyntaxException("JSON-Object needs at least an element named \"path\" for a Resource Location");
            }
        }

        requirePrimitive(json);

        return new ResourceLocation(json.getAsString());
    }

    public static ResourceLocation getLocation(JsonElement json, ResourceLocation defVal) {
        if( json != null && json.isJsonObject() ) {
            JsonObject jObj = json.getAsJsonObject();
            String namespace = getStringVal(jObj.get("namespace"), "minecraft");
            String path = getStringVal(jObj.get("path"), null);
            if( path != null ) {
                return new ResourceLocation(namespace, path);
            } else {
                return defVal;
            }
        }

        if( json == null || !json.isJsonPrimitive() ) {
            return defVal;
        }

        return new ResourceLocation(json.getAsString());
    }

    private static void requirePrimitive(JsonElement json) {
        if( json == null || json.isJsonNull() ) {
            throw new JsonSyntaxException("Json cannot be null");
        }

        if( !json.isJsonPrimitive() ) {
            throw new JsonSyntaxException("Expected value to needs be a primitive");
        }
    }

    public static int[] getIntArray(JsonElement json) {
        return getIntArray(json, FULL_ARRAY_RANGE);
    }

    public static int[] getIntArray(JsonElement json, Range<Integer> requiredSize) {
        requirePrimitiveArray(json, requiredSize);

        return GSON.fromJson(json, int[].class);
    }

    public static int[] getIntArray(JsonElement json, int[] defVal) {
        return getIntArray(json, defVal, FULL_ARRAY_RANGE);
    }

    public static int[] getIntArray(JsonElement json, int[] defVal, Range<Integer> requiredSize) {
        if( !isPrimitiveArray(json, requiredSize) ) {
            return defVal;
        }

        return GSON.fromJson(json, int[].class);
    }

    public static void fetchIntArray(JsonElement json, Consumer<int[]> proc) {
        fetchIntArray(json, proc, FULL_ARRAY_RANGE);
    }

    public static void fetchIntArray(JsonElement json, Consumer<int[]> proc, Range<Integer> requiredSize) {
        if( isPrimitiveArray(json, requiredSize) ) {
            proc.accept(GSON.fromJson(json, int[].class));
        }
    }

    public static double[] getDoubleArray(JsonElement json) {
        return getDoubleArray(json, FULL_ARRAY_RANGE);
    }

    public static double[] getDoubleArray(JsonElement json, Range<Integer> requiredSize) {
        requirePrimitiveArray(json, requiredSize);

        return GSON.fromJson(json, double[].class);
    }

    public static double[] getDoubleArray(JsonElement json, double[] defVal) {
        return getDoubleArray(json, defVal, FULL_ARRAY_RANGE);
    }

    public static double[] getDoubleArray(JsonElement json, double[] defVal, Range<Integer> requiredSize) {
        if( !isPrimitiveArray(json, requiredSize) ) {
            return defVal;
        }

        return GSON.fromJson(json, double[].class);
    }

    public static void fetchDoubleArray(JsonElement json, Consumer<double[]> proc) {
        fetchDoubleArray(json, proc, FULL_ARRAY_RANGE);
    }

    public static void fetchDoubleArray(JsonElement json, Consumer<double[]> proc, Range<Integer> requiredSize) {
        if( isPrimitiveArray(json, requiredSize) ) {
            proc.accept(GSON.fromJson(json, double[].class));
        }
    }

    public static float[] getFloatArray(JsonElement json) {
        return getFloatArray(json, FULL_ARRAY_RANGE);
    }

    public static float[] getFloatArray(JsonElement json, Range<Integer> requiredSize) {
        requirePrimitiveArray(json, requiredSize);

        return GSON.fromJson(json, float[].class);
    }

    public static float[] getFloatArray(JsonElement json, float[] defVal) {
        return getFloatArray(json, defVal, FULL_ARRAY_RANGE);
    }

    public static float[] getFloatArray(JsonElement json, float[] defVal, Range<Integer> requiredSize) {
        if( !isPrimitiveArray(json, requiredSize) ) {
            return defVal;
        }

        return GSON.fromJson(json, float[].class);
    }

    public static void fetchFloatArray(JsonElement json, Consumer<float[]> proc) {
        fetchFloatArray(json, proc, FULL_ARRAY_RANGE);
    }

    public static void fetchFloatArray(JsonElement json, Consumer<float[]> proc, Range<Integer> requiredSize) {
        if( isPrimitiveArray(json, requiredSize) ) {
            proc.accept(GSON.fromJson(json, float[].class));
        }
    }

    public static String[] getStringArray(JsonElement json) {
        return getStringArray(json, FULL_ARRAY_RANGE);
    }

    public static String[] getStringArray(JsonElement json, Range<Integer> requiredSize) {
        requirePrimitiveArray(json, requiredSize);

        return GSON.fromJson(json, String[].class);
    }

    public static String[] getStringArray(JsonElement json, String[] defVal) {
        return getStringArray(json, defVal, FULL_ARRAY_RANGE);
    }

    public static String[] getStringArray(JsonElement json, String[] defVal, Range<Integer> requiredSize) {
        if( !isPrimitiveArray(json, requiredSize) ) {
            return defVal;
        }

        return GSON.fromJson(json, String[].class);
    }

    public static void fetchStringArray(JsonElement json, Consumer<String[]> proc) {
        fetchStringArray(json, proc, FULL_ARRAY_RANGE);
    }

    public static void fetchStringArray(JsonElement json, Consumer<String[]> proc, Range<Integer> requiredSize) {
        if( isPrimitiveArray(json, requiredSize) ) {
            proc.accept(GSON.fromJson(json, String[].class));
        }
    }

    private static boolean isPrimitiveArray(JsonElement json, Range<Integer> requiredSize) {
        if( json == null || !json.isJsonArray() ) {
            return false;
        }

        JsonArray arr = json.getAsJsonArray();
        return requiredSize.contains(arr.size()) && (arr.size() <= 0 || arr.get(0).isJsonPrimitive());
    }

    private static JsonArray requireArray(JsonElement json, Range<Integer> requiredSize) {
        if( json == null || json.isJsonNull() ) {
            throw new JsonSyntaxException("Json cannot be null");
        }

        if( !json.isJsonArray() ) {
            throw new JsonSyntaxException("Expected value needs to be an array");
        }

        JsonArray arr = json.getAsJsonArray();
        if( !requiredSize.contains(arr.size()) ) {
            int min = requiredSize.getMinimum();
            int max = requiredSize.getMaximum();
            throw new JsonSyntaxException("Expected array's size needs to be " + (min == max ? Integer.toString(min) : String.format("between %d and %d", min, max)) + "elements big");
        }
        return arr;
    }

    private static void requirePrimitiveArray(JsonElement json, Range<Integer> requiredSize) {
        JsonArray arr = requireArray(json, requiredSize);

        if( arr.size() > 0 && !arr.get(0).isJsonPrimitive() ) {
            throw new JsonSyntaxException("Expected array needs to contain primitive values");
        }
    }

    public static JsonObject addDefaultJsonProperty(JsonObject jobj, String name, String val) {
        if( !jobj.has(name) ) { jobj.addProperty(name, val); }

        return jobj;
    }

    public static JsonObject addDefaultJsonProperty(JsonObject jobj, String name, Boolean val) {
        if( !jobj.has(name) ) { jobj.addProperty(name, val); }

        return jobj;
    }

    public static JsonObject addDefaultJsonProperty(JsonObject jobj, String name, Character val) {
        if( !jobj.has(name) ) { jobj.addProperty(name, val); }

        return jobj;
    }

    public static JsonObject addDefaultJsonProperty(JsonObject jobj, String name, Number val) {
        if( !jobj.has(name) ) { jobj.addProperty(name, val); }

        return jobj;
    }

    public static JsonObject addDefaultJsonProperty(JsonObject jobj, String name, String[] val) {
        if( !jobj.has(name) ) { addJsonProperty(jobj, name, val); }

        return jobj;
    }

    public static JsonObject addDefaultJsonProperty(JsonObject jobj, String name, Boolean[] val) {
        if( !jobj.has(name) ) { addJsonProperty(jobj, name, val); }

        return jobj;
    }

    public static JsonObject addDefaultJsonProperty(JsonObject jobj, String name, Character[] val) {
        if( !jobj.has(name) ) { addJsonProperty(jobj, name, val); }

        return jobj;
    }

    public static JsonObject addDefaultJsonProperty(JsonObject jobj, String name, Number[] val) {
        if( !jobj.has(name) ) { addJsonProperty(jobj, name, val); }

        return jobj;
    }

    public static JsonObject addDefaultJsonProperty(JsonObject jobj, String name, int[] arr) {
        if( !jobj.has(name) ) { addJsonProperty(jobj, name, arr); }

        return jobj;
    }

    public static JsonObject addDefaultJsonProperty(JsonObject jobj, String name, long[] arr) {
        if( !jobj.has(name) ) { addJsonProperty(jobj, name, arr); }

        return jobj;
    }

    public static JsonObject addDefaultJsonProperty(JsonObject jobj, String name, double[] arr) {
        if( !jobj.has(name) ) { addJsonProperty(jobj, name, arr); }

        return jobj;
    }

    public static JsonObject addDefaultJsonProperty(JsonObject jobj, String name, byte[] arr) {
        if( !jobj.has(name) ) { addJsonProperty(jobj, name, arr); }

        return jobj;
    }

    public static JsonObject addDefaultJsonProperty(JsonObject jobj, String name, short[] arr) {
        if( !jobj.has(name) ) { addJsonProperty(jobj, name, arr); }

        return jobj;
    }

    public static JsonObject addDefaultJsonProperty(JsonObject jobj, String name, float[] arr) {
        if( !jobj.has(name) ) { addJsonProperty(jobj, name, arr); }

        return jobj;
    }

    private static Number[] convertNArray(Object arr) {
        if( arr instanceof int[] )    return Arrays.stream((int[])    arr).mapToObj(e -> (Number) e).toArray(Number[]::new);
        if( arr instanceof long[] )   return Arrays.stream((long[])   arr).mapToObj(e -> (Number) e).toArray(Number[]::new);
        if( arr instanceof double[] ) return Arrays.stream((double[]) arr).mapToObj(e -> (Number) e).toArray(Number[]::new);
        if( arr instanceof byte[] )  { byte[] na = (byte[])   arr; return IntStream.range(0, na.length).mapToObj(i -> (Number) na[i]).toArray(Number[]::new); }
        if( arr instanceof short[] ) { short[] na = (short[]) arr; return IntStream.range(0, na.length).mapToObj(i -> (Number) na[i]).toArray(Number[]::new); }
        if( arr instanceof float[] ) { float[] na = (float[]) arr; return IntStream.range(0, na.length).mapToObj(i -> (Number) na[i]).toArray(Number[]::new); }

        throw new IllegalArgumentException("The given array does not hold numeric values!");
    }

    public static void addJsonProperty(JsonObject jobj, String name, String val) {
        jobj.addProperty(name, val);
    }

    public static void addJsonProperty(JsonObject jobj, String name, Boolean val) {
        jobj.addProperty(name, val);
    }

    public static void addJsonProperty(JsonObject jobj, String name, Character val) {
        jobj.addProperty(name, val);
    }

    public static void addJsonProperty(JsonObject jobj, String name, Number val) {
        jobj.addProperty(name, val);
    }

    public static void addJsonProperty(JsonObject jobj, String name, int[] arr) {
        addJsonProperty(jobj, name, convertNArray(arr));
    }

    public static void addJsonProperty(JsonObject jobj, String name, long[] arr) {
        addJsonProperty(jobj, name, convertNArray(arr));
    }

    public static void addJsonProperty(JsonObject jobj, String name, double[] arr) {
        addJsonProperty(jobj, name, convertNArray(arr));
    }

    public static void addJsonProperty(JsonObject jobj, String name, byte[] arr) {
        addJsonProperty(jobj, name, convertNArray(arr));
    }

    public static void addJsonProperty(JsonObject jobj, String name, short[] arr) {
        addJsonProperty(jobj, name, convertNArray(arr));
    }

    public static void addJsonProperty(JsonObject jobj, String name, float[] arr) {
        addJsonProperty(jobj, name, convertNArray(arr));
    }

    public static void addJsonProperty(JsonObject jobj, String name, Number[] arr) {
        JsonArray jarr = new JsonArray();
        for( Number i : arr ) jarr.add(i);
        jobj.add(name, jarr);
    }

    public static void addJsonProperty(JsonObject jobj, String name, String[] arr) {
        JsonArray jarr = new JsonArray();
        for( String i : arr ) jarr.add(i);
        jobj.add(name, jarr);
    }

    public static void addJsonProperty(JsonObject jobj, String name, Boolean[] arr) {
        JsonArray jarr = new JsonArray();
        for( Boolean i : arr ) jarr.add(i);
        jobj.add(name, jarr);
    }

    public static void addJsonProperty(JsonObject jobj, String name, Character[] arr) {
        JsonArray jarr = new JsonArray();
        for( Character i : arr ) jarr.add(i);
        jobj.add(name, jarr);
    }

    public static JsonObject deepCopy(JsonObject obj) {
        return JsonUtils.GSON.fromJson(JsonUtils.GSON.toJson(obj), JsonObject.class);
    }
}
