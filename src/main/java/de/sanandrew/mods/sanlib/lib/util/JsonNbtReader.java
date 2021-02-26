////////////////////////////////////////////////////////////////////////////////
// This file is subject to the terms and conditions defined in the             /
// file '.github/LICENSE.md', which is part of this source code package.       /
////////////////////////////////////////////////////////////////////////////////

package de.sanandrew.mods.sanlib.lib.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import net.minecraft.nbt.ByteArrayNBT;
import net.minecraft.nbt.ByteNBT;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.DoubleNBT;
import net.minecraft.nbt.FloatNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.IntArrayNBT;
import net.minecraft.nbt.IntNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.LongArrayNBT;
import net.minecraft.nbt.LongNBT;
import net.minecraft.nbt.NumberNBT;
import net.minecraft.nbt.ShortNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraftforge.common.util.Constants;

import java.util.regex.Pattern;

final class JsonNbtReader
{
    static CompoundNBT getTagFromJson(JsonElement elem) {
        if( !elem.isJsonObject() ) {
            throw new JsonParseException("NBT element must be an object!");
        }

        JsonObject obj = elem.getAsJsonObject();
        CompoundNBT nbt = new CompoundNBT();
        obj.entrySet().forEach(entry -> {
            JsonElement entryElem = entry.getValue();
            if( entryElem.isJsonObject() ) {
                nbt.put(entry.getKey(), getTagFromJson(entryElem));
            } else if( entryElem.isJsonPrimitive() ) {
                nbt.put(entry.getKey(), getTyped(entryElem.getAsJsonPrimitive()));
            } else if( entryElem.isJsonArray() ) {
                nbt.put(entry.getKey(), getArray(entryElem.getAsJsonArray()));
            } else {
                throw new JsonParseException("Unable to identify type for JSON-Object. Is it null?");
            }
        });

        return nbt;
    }

    static INBT getArray(JsonArray array) {
        if( array.size() > 0 ) {
            JsonElement firstElem      = array.get(0);
            NumberNBT   firstPrimitive = getPrimitive(firstElem);
            if( firstPrimitive != null ) {
                if( firstPrimitive.getId() == Constants.NBT.TAG_BYTE ) {
                    byte[] bytes = new byte[array.size()];
                    bytes[0] = firstPrimitive.getByte();

                    for( int i = 1, max = bytes.length; i < max; i++ ) {
                        bytes[i] = getPrimitive(array.get(i), Constants.NBT.TAG_BYTE).getByte();
                    }

                    return new ByteArrayNBT(bytes);
                } else if( firstPrimitive.getId() == Constants.NBT.TAG_INT ) {
                    int[] ints = new int[array.size()];
                    ints[0] = firstPrimitive.getInt();

                    for( int i = 1, max = ints.length; i < max; i++ ) {
                        ints[i] = getPrimitive(array.get(i), Constants.NBT.TAG_INT).getInt();
                    }

                    return new IntArrayNBT(ints);
                } else if( firstPrimitive.getId() == Constants.NBT.TAG_LONG ) {
                    long[] longs = new long[array.size()];
                    longs[0] = firstPrimitive.getLong();

                    for( int i = 1, max = longs.length; i < max; i++ ) {
                        longs[i] = getPrimitive(array.get(i), Constants.NBT.TAG_LONG).getLong();
                    }

                    return new LongArrayNBT(longs);
                }
            }
            if( firstElem.isJsonPrimitive() ) {
                INBT    firstTyped = getTyped(firstElem.getAsJsonPrimitive());
                ListNBT list       = new ListNBT();
                list.add(firstTyped);

                for( int i = 1, max = array.size(); i < max; i++ ) {
                    JsonElement arrElem = array.get(i);
                    if( arrElem.isJsonPrimitive() ) {
                        INBT arrElemTyped = getTyped(arrElem.getAsJsonPrimitive());
                        if( arrElemTyped.getId() == firstTyped.getId() ) {
                            list.add(arrElemTyped);
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
                CompoundNBT firstNBT = getTagFromJson(firstElem);
                ListNBT list = new ListNBT();
                list.add(firstNBT);

                for( int i = 1, max = array.size(); i < max; i++ ) {
                    JsonElement arrElem = array.get(i);
                    if( arrElem.isJsonObject() ) {
                        list.add(getTagFromJson(arrElem));
                    } else {
                        throw new JsonParseException("Cannot add mismatching NBT types to NBT list");
                    }
                }

                return list;
            }
            if( firstElem.isJsonArray() ) {
                INBT firstArray = getArray(firstElem.getAsJsonArray());
                ListNBT list = new ListNBT();
                list.add(firstArray);

                for( int i = 1, max = array.size(); i < max; i++ ) {
                    JsonElement arrElem = array.get(i);
                    if( arrElem.isJsonArray() ) {
                        INBT arrElemArray = getArray(arrElem.getAsJsonArray());
                        if( arrElemArray.getId() == firstArray.getId() ) {
                            list.add(arrElemArray);
                        } else {
                            throw new JsonParseException("Cannot add mismatching NBT types to NBT list");
                        }
                    } else {
                        throw new JsonParseException("Cannot add mismatching NBT types to NBT list");
                    }
                }

                return list;
            }

            throw new JsonParseException("Unable to identify array type");
        } else {
            throw new JsonParseException("Cannot parse an empty array");
        }
    }

    static NumberNBT getPrimitive(JsonElement elem) {
        if( elem.isJsonPrimitive() ) {
            return (NumberNBT) getTyped(elem.getAsJsonPrimitive());
        }

        return null;
    }

    static NumberNBT getPrimitive(JsonElement elem, int typeId) {
        if( elem.isJsonPrimitive() ) {
            INBT nbt = getTyped(elem.getAsJsonPrimitive());
            if( nbt.getId() == typeId ) {
                return (NumberNBT) nbt;
            } else {
                throw new JsonParseException(String.format("The array cannot contain mixed types! Expected a type with NBT-ID %d here.", typeId));
            }
        } else {
            throw new JsonParseException(String.format("The array cannot contain mixed types! Expected a type with NBT-ID %d here.", typeId));
        }
    }

    static INBT getTyped(JsonPrimitive primitive) {
        if( primitive.isNumber() ) {
            Number nbr = primitive.getAsNumber();
            if( nbr instanceof Float ) {
                return FloatNBT.valueOf(nbr.floatValue());
            } else if( nbr instanceof Byte ) {
                return ByteNBT.valueOf(nbr.byteValue());
            } else if( nbr instanceof Short ) {
                return ShortNBT.valueOf(nbr.shortValue());
            } else if( nbr instanceof Long ) {
                return LongNBT.valueOf(nbr.longValue());
            } else if( nbr instanceof Integer ) {
                return IntNBT.valueOf(nbr.intValue());
            } else if( nbr instanceof Double ) {
                return DoubleNBT.valueOf(nbr.doubleValue());
            } else {
                throw new JsonParseException("Cannot parse unknown number type!");
            }
        } else if( primitive.isBoolean() ) {
            return ByteNBT.valueOf((byte) (primitive.getAsBoolean() ? 1 : 0));
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

    static INBT tryGetTypedFromString(String s) {
        if( FLOAT_PATTERN.matcher(s).matches() ) {
            return FloatNBT.valueOf(Float.parseFloat(s.substring(0, s.length() - 1)));
        } else if( BYTE_PATTERN.matcher(s).matches() ) {
            return ByteNBT.valueOf(Byte.parseByte(s.substring(0, s.length() - 1)));
        } else if( SHORT_PATTERN.matcher(s).matches() ) {
            return ShortNBT.valueOf(Short.parseShort(s.substring(0, s.length() - 1)));
        } else if( LONG_PATTERN.matcher(s).matches() ) {
            return LongNBT.valueOf(Long.parseLong(s.substring(0, s.length() - 1)));
        } else if( INT_PATTERN.matcher(s).matches() ) {
            return IntNBT.valueOf(Integer.parseInt(s));
        } else if( DOUBLE_PATTERN.matcher(s).matches() ) {
            return DoubleNBT.valueOf(Double.parseDouble(s.substring(0, s.length() - 1)));
        } else if( DOUBLE_PATTERN_NOSUFFIX.matcher(s).matches() ) {
            return DoubleNBT.valueOf(Double.parseDouble(s));
        } else {
            return StringNBT.valueOf(s);
        }
    }
}
