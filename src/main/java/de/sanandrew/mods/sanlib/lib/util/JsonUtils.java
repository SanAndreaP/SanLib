/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.sanlib.lib.util;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.Range;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.Reader;

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

    @Nonnull
    public static ItemStack getItemStack(JsonElement json) {
        if( json == null || json.isJsonNull() ) {
            throw new JsonSyntaxException("Json cannot be null");
        }

        if( json.isJsonArray() ) {
            throw new JsonSyntaxException("Expected value to be an object, not an array");
        }

        if( !json.isJsonObject() ) {
            throw new JsonSyntaxException("Expcted value to be an object");
        }

        return getStack((JsonObject) json);
    }

//    @Nonnull
//    public static NonNullList<ItemStack> getItemStacks(JsonElement json) {
//        if( json == null || json.isJsonNull() ) {
//            throw new JsonSyntaxException("Json cannot be null");
//        }
//
//        return getStacks(json);
//    }

    private static ItemStack getStack(JsonObject jsonObj) {
        String itemName = net.minecraft.util.JsonUtils.getString(jsonObj, "item");
        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemName));

        if( item == null ) {
            throw new JsonParseException(String.format("Unknown item '%s'", itemName));
        }

        ItemStack stack;
        if( jsonObj.has("nbt") ) {
            NBTTagCompound nbt = JsonNbtReader.getTagFromJson(jsonObj.get("nbt"));
            NBTTagCompound tmp = new NBTTagCompound();
            if( nbt.hasKey("ForgeCaps") ) {
                tmp.setTag("ForgeCaps", nbt.getTag("ForgeCaps"));
                nbt.removeTag("ForgeCaps");
            }

            tmp.setTag("tag", nbt);
            tmp.setString("id", itemName);
            tmp.setInt("Count", net.minecraft.util.JsonUtils.getInt(jsonObj, "count", 1));

            stack = ItemStack.read(tmp);
        } else {
            stack = new ItemStack(item, net.minecraft.util.JsonUtils.getInt(jsonObj, "count", 1));
        }

        if( !ItemStackUtils.isValid(stack) ) {
            throw new JsonParseException("Invalid Item: " + stack.toString());
        }

        return stack;
    }

//    private static NonNullList<ItemStack> getStacks(JsonElement json) {
//        NonNullList<ItemStack> items = NonNullList.create();
//
//        if( json == null || json.isJsonNull() ) {
//            throw new JsonSyntaxException("Json cannot be null");
//        }
//
//        if( json.isJsonArray() ) {
//            json.getAsJsonArray().forEach(elem -> {
//                if( elem != null && elem.isJsonObject() ) {
//                    items.addAll(getStacks(elem));
//                } else {
//                    throw new JsonSyntaxException("Expcted stack to be an object");
//                }
//            });
//        } else if( json.isJsonObject() ) {
//            JsonObject jsonObj = json.getAsJsonObject();
//
//            if( jsonObj.has("type") && MiscUtils.defIfNull(jsonObj.get("type").getAsString(), "").equals("forge:ore_dict") ) {
//                String oredictName = jsonObj.get("ore").getAsString();
//                items.addAll(OreDictionary.getOres(oredictName).stream().map(ItemStack::copy).collect(Collectors.toList()));
//            } else {
//                items.add(getStack(jsonObj));
//            }
//        } else {
//            throw new JsonSyntaxException("Expected stack(s) to be an object or an array of objects");
//        }
//
//        return items;
//    }
}
