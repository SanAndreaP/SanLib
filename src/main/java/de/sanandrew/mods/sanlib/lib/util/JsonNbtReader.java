/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.sanlib.lib.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTPrimitive;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.nbt.NBTTagLongArray;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.common.util.Constants;

import java.util.regex.Pattern;

final class JsonNbtReader
{
    static NBTTagCompound getTagFromJson(JsonElement elem) {
        if( !elem.isJsonObject() ) {
            throw new JsonParseException("NBT element must be an object!");
        }

        JsonObject obj = elem.getAsJsonObject();
        NBTTagCompound nbt = new NBTTagCompound();
        obj.entrySet().forEach(entry -> {
            JsonElement entryElem = entry.getValue();
            if( entryElem.isJsonObject() ) {
                nbt.setTag(entry.getKey(), getTagFromJson(entryElem));
            } else if( entryElem.isJsonPrimitive() ) {
                nbt.setTag(entry.getKey(), getTyped(entryElem.getAsJsonPrimitive()));
            } else if( entryElem.isJsonArray() ) {
                nbt.setTag(entry.getKey(), getArray(entryElem.getAsJsonArray()));
            } else {
                throw new JsonParseException("Unable to identify type for JSON-Object. Is it null?");
            }
        });

        return nbt;
    }

    static NBTBase getArray(JsonArray array) {
        if( array.size() > 0 ) {
            JsonElement firstElem = array.get(0);
            NBTPrimitive firstPrimitive = getPrimitive(firstElem);
            if( firstPrimitive != null ) {
                if( firstPrimitive.getId() == Constants.NBT.TAG_BYTE ) {
                    byte[] bytes = new byte[array.size()];
                    bytes[0] = firstPrimitive.getByte();

                    for( int i = 1, max = bytes.length; i < max; i++ ) {
                        bytes[i] = getPrimitive(array.get(i), Constants.NBT.TAG_BYTE).getByte();
                    }

                    return new NBTTagByteArray(bytes);
                } else if( firstPrimitive.getId() == Constants.NBT.TAG_INT ) {
                    int[] ints = new int[array.size()];
                    ints[0] = firstPrimitive.getInt();

                    for( int i = 1, max = ints.length; i < max; i++ ) {
                        ints[i] = getPrimitive(array.get(i), Constants.NBT.TAG_INT).getInt();
                    }

                    return new NBTTagIntArray(ints);
                } else if( firstPrimitive.getId() == Constants.NBT.TAG_LONG ) {
                    long[] longs = new long[array.size()];
                    longs[0] = firstPrimitive.getLong();

                    for( int i = 1, max = longs.length; i < max; i++ ) {
                        longs[i] = getPrimitive(array.get(i), Constants.NBT.TAG_LONG).getLong();
                    }

                    return new NBTTagLongArray(longs);
                }
            }
            if( firstElem.isJsonPrimitive() ) {
                NBTBase firstTyped = getTyped(firstElem.getAsJsonPrimitive());
                NBTTagList list = new NBTTagList();
                list.appendTag(firstTyped);

                for( int i = 1, max = array.size(); i < max; i++ ) {
                    JsonElement arrElem = array.get(i);
                    if( arrElem.isJsonPrimitive() ) {
                        NBTBase arrElemTyped = getTyped(arrElem.getAsJsonPrimitive());
                        if( arrElemTyped.getId() == firstTyped.getId() ) {
                            list.appendTag(arrElemTyped);
                        } else {
                            throw new JsonParseException("Cannot add mismatching NBT types to NBT list");
                        }
                    } else {
                        throw new JsonParseException("Cannot add mismatching NBT types to NBT list");
                    }
                }

                return list;
            }
            if( firstElem.isJsonObject() ) {
                NBTTagCompound firstNBT = getTagFromJson(firstElem);
                NBTTagList list = new NBTTagList();
                list.appendTag(firstNBT);

                for( int i = 1, max = array.size(); i < max; i++ ) {
                    JsonElement arrElem = array.get(i);
                    if( arrElem.isJsonObject() ) {
                        list.appendTag(getTagFromJson(arrElem));
                    } else {
                        throw new JsonParseException("Cannot add mismatching NBT types to NBT list");
                    }
                }

                return list;
            }
            if( firstElem.isJsonArray() ) {
                NBTBase firstArray = getArray(firstElem.getAsJsonArray());
                NBTTagList list = new NBTTagList();
                list.appendTag(firstArray);

                for( int i = 1, max = array.size(); i < max; i++ ) {
                    JsonElement arrElem = array.get(i);
                    if( arrElem.isJsonArray() ) {
                        NBTBase arrElemArray = getArray(arrElem.getAsJsonArray());
                        if( arrElemArray.getId() == firstArray.getId() ) {
                            list.appendTag(arrElemArray);
                        } else {
                            throw new JsonParseException("Cannot add mismatching NBT types to NBT list");
                        }
                    } else {
                        throw new JsonParseException("Cannot add mismatching NBT types to NBT list");
                    }
                }
            }

            throw new JsonParseException("Unable to identify array type");
        } else {
            throw new JsonParseException("Cannot parse an empty array");
        }
    }

    static NBTPrimitive getPrimitive(JsonElement elem) {
        if( elem.isJsonPrimitive() ) {
            return (NBTPrimitive) getTyped(elem.getAsJsonPrimitive());
        }

        return null;
    }

    static NBTPrimitive getPrimitive(JsonElement elem, int typeId) {
        if( elem.isJsonPrimitive() ) {
            NBTBase nbt = getTyped(elem.getAsJsonPrimitive());
            if( nbt.getId() == typeId ) {
                return (NBTPrimitive) nbt;
            } else {
                throw new JsonParseException(String.format("The array cannot contain mixed types! Expected a type with NBT-ID %d here.", typeId));
            }
        } else {
            throw new JsonParseException(String.format("The array cannot contain mixed types! Expected a type with NBT-ID %d here.", typeId));
        }
    }

    static NBTBase getTyped(JsonPrimitive primitive) {
        if( primitive.isNumber() ) {
            Number nbr = primitive.getAsNumber();
            if( nbr instanceof Float ) {
                return new NBTTagFloat(nbr.floatValue());
            } else if( nbr instanceof Byte ) {
                return new NBTTagByte(nbr.byteValue());
            } else if( nbr instanceof Short ) {
                return new NBTTagShort(nbr.shortValue());
            } else if( nbr instanceof Long ) {
                return new NBTTagLong(nbr.longValue());
            } else if( nbr instanceof Integer ) {
                return new NBTTagInt(nbr.intValue());
            } else if( nbr instanceof Double ) {
                return new NBTTagDouble(nbr.doubleValue());
            } else {
                throw new JsonParseException("Cannot parse unknown number type!");
            }
        } else if( primitive.isBoolean() ) {
            return new NBTTagByte((byte) (primitive.getAsBoolean() ? 1 : 0));
        } else if( primitive.isString() ) {
            return tryGetTypedFromString(primitive.getAsString());
        }

        throw new JsonParseException("Cannot parse unknown primitive!");
    }

    private static final Pattern DOUBLE_PATTERN_NOSUFFIX = Pattern.compile("[-+]?(?:[0-9]+[.]|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?", Pattern.CASE_INSENSITIVE);
    private static final Pattern DOUBLE_PATTERN = Pattern.compile("[-+]?(?:[0-9]+[.]?|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?d", Pattern.CASE_INSENSITIVE);
    private static final Pattern FLOAT_PATTERN = Pattern.compile("[-+]?(?:[0-9]+[.]?|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?f", Pattern.CASE_INSENSITIVE);
    private static final Pattern BYTE_PATTERN = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)b", Pattern.CASE_INSENSITIVE);
    private static final Pattern LONG_PATTERN = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)l", Pattern.CASE_INSENSITIVE);
    private static final Pattern SHORT_PATTERN = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)s", Pattern.CASE_INSENSITIVE);
    private static final Pattern INT_PATTERN = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)");

    static NBTBase tryGetTypedFromString(String s) {
        if( FLOAT_PATTERN.matcher(s).matches() ) {
            return new NBTTagFloat(Float.parseFloat(s.substring(0, s.length() - 1)));
        } else if( BYTE_PATTERN.matcher(s).matches() ) {
            return new NBTTagByte(Byte.parseByte(s.substring(0, s.length() - 1)));
        } else if( SHORT_PATTERN.matcher(s).matches() ) {
            return new NBTTagShort(Short.parseShort(s.substring(0, s.length() - 1)));
        } else if( LONG_PATTERN.matcher(s).matches() ) {
            return new NBTTagLong(Long.parseLong(s.substring(0, s.length() - 1)));
        } else if( INT_PATTERN.matcher(s).matches() ) {
            return new NBTTagInt(Integer.parseInt(s));
        } else if( DOUBLE_PATTERN.matcher(s).matches() ) {
            return new NBTTagDouble(Double.parseDouble(s.substring(0, s.length() - 1)));
        } else if( DOUBLE_PATTERN_NOSUFFIX.matcher(s).matches() ) {
            return new NBTTagDouble(Double.parseDouble(s));
        } else {
            return new NBTTagString(s);
        }
    }
}
