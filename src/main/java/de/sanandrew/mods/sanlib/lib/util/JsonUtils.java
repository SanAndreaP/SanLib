/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.sanlib.lib.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nonnull;
import java.io.Reader;
import java.util.stream.Collectors;

@SuppressWarnings({"unused", "WeakerAccess"})
public final class JsonUtils
{
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    public static <T> T fromJson(Reader reader, Class<T> clazz) {
        return net.minecraft.util.JsonUtils.fromJson(GSON, reader, clazz);
    }

    public static float getFloatVal(JsonElement json) {
        if( json == null || json.isJsonNull() ) {
            throw new JsonSyntaxException("Json cannot be null");
        }

        if( !json.isJsonPrimitive() ) {
            throw new JsonSyntaxException("Expected value to be a primitive");
        }

        return json.getAsFloat();
    }

    public static float getFloatVal(JsonElement json, float defVal) {
        if( json == null || !json.isJsonPrimitive() ) {
            return defVal;
        }

        return json.getAsFloat();
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
        if( json == null || json.isJsonNull() ) {
            throw new JsonSyntaxException("Json cannot be null");
        }

        if( !json.isJsonPrimitive() ) {
            throw new JsonSyntaxException("Expected value to be a primitive");
        }

        return json.getAsInt();
    }

    public static int getIntVal(JsonElement json, int defVal) {
        if( json == null || !json.isJsonPrimitive() ) {
            return defVal;
        }

        return json.getAsInt();
    }

    public static boolean getBoolVal(JsonElement json) {
        if( json == null || json.isJsonNull() ) {
            throw new JsonSyntaxException("Json cannot be null");
        }

        if( !json.isJsonPrimitive() ) {
            throw new JsonSyntaxException("Expected value to be a primitive");
        }

        return json.getAsBoolean();
    }

    public static boolean getBoolVal(JsonElement json, boolean defVal) {
        if( json == null || !json.isJsonPrimitive() ) {
            return defVal;
        }

        return json.getAsBoolean();
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

    @Nonnull
    public static NonNullList<ItemStack> getItemStacks(JsonElement json) {
        if( json == null || json.isJsonNull() ) {
            throw new JsonSyntaxException("Json cannot be null");
        }

        return getStacks(json);
    }

    private static ItemStack getStack(JsonObject jsonObj) {
        String itemName = net.minecraft.util.JsonUtils.getString(jsonObj, "item");
        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemName));

        if( item == null ) {
            throw new JsonParseException(String.format("Unknown item '%s'", itemName));
        }

        if( item.getHasSubtypes() && !jsonObj.has("data") ) {
            throw new JsonParseException(String.format("Missing data for item '%s'", itemName));
        }

        ItemStack stack;
        if( jsonObj.has("nbt") ) {
            try {
                NBTTagCompound nbt = JsonToNBT.getTagFromJson(GSON.toJson(jsonObj.get("nbt")));
                NBTTagCompound tmp = new NBTTagCompound();
                if( nbt.hasKey("ForgeCaps") ) {
                    tmp.setTag("ForgeCaps", nbt.getTag("ForgeCaps"));
                    nbt.removeTag("ForgeCaps");
                }

                tmp.setTag("tag", nbt);
                tmp.setString("id", itemName);
                tmp.setInteger("Count", net.minecraft.util.JsonUtils.getInt(jsonObj, "count", 1));
                tmp.setInteger("Damage", net.minecraft.util.JsonUtils.getInt(jsonObj, "data", 0));

                stack = new ItemStack(tmp);
            } catch( NBTException e ) {
                throw new JsonParseException("Invalid NBT Entry: " + e.toString());
            }
        } else {
            stack = new ItemStack(item, net.minecraft.util.JsonUtils.getInt(jsonObj, "count", 1), net.minecraft.util.JsonUtils.getInt(jsonObj, "data", 0));
        }

        if( !ItemStackUtils.isValid(stack) ) {
            throw new JsonParseException("Invalid Item: " + stack.toString());
        }

        return stack;
    }

    private static NonNullList<ItemStack> getStacks(JsonElement json) {
        NonNullList<ItemStack> items = NonNullList.create();

        if( json == null || json.isJsonNull() ) {
            throw new JsonSyntaxException("Json cannot be null");
        }

        if( json.isJsonArray() ) {
            json.getAsJsonArray().forEach(elem -> {
                if( elem != null && elem.isJsonObject() ) {
                    items.addAll(getStacks(elem));
                } else {
                    throw new JsonSyntaxException("Expcted stack to be an object");
                }
            });
        } else if( json.isJsonObject() ) {
            JsonObject jsonObj = json.getAsJsonObject();

            if( jsonObj.has("type") && MiscUtils.defIfNull(jsonObj.get("type").getAsString(), "").equals("forge:ore_dict") ) {
                String oredictName = jsonObj.get("ore").getAsString();
                items.addAll(OreDictionary.getOres(oredictName).stream().map(ItemStack::copy).collect(Collectors.toList()));
            } else {
                items.add(getStack(jsonObj));
            }
        } else {
            throw new JsonSyntaxException("Expected stack(s) to be an object or an array of objects");
        }

        return items;
    }
}
