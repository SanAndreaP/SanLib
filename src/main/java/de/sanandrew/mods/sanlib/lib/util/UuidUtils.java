////////////////////////////////////////////////////////////////////////////////
// This file is subject to the terms and conditions defined in the             /
// file '.github/LICENSE.md', which is part of this source code package.       /
////////////////////////////////////////////////////////////////////////////////

package de.sanandrew.mods.sanlib.lib.util;

import java.util.UUID;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * Utility class for tasks and methods regarding UUIDs
 */
@SuppressWarnings("unused")
public final class UuidUtils
{
    /**
     * An empty UUID for use as a "null" UUID.
     */
    public static final UUID EMPTY_UUID = new UUID(0, 0);

    private static final Pattern UUID_PTRN = Pattern.compile("[a-f0-9]{8}-[a-f0-9]{4}-4[a-f0-9]{3}-[89ab][a-f0-9]{3}-[a-f0-9]{12}", Pattern.CASE_INSENSITIVE);

    /**
     * Checks whether or not the String represents and can be parsed to an UUID
     * @param uuid the String representing the UUID
     * @return true, if the String represents an UUID, false otherwise
     */
    public static boolean isStringUuid(String uuid) {
        return UUID_PTRN.matcher(uuid).matches();
    }

    /**
     * Compares two objects if both can be represented as UUIDs and have an equal value.<br>
     * Before comparison, both objects will be converted into an UUID instance:
     * <ul>
     *     <li>If one of the values is a string and represents a valid UUID, it is converted via {@link UUID#fromString(String)}</li>
     *     <li>If one of the values is a byte array, it is converted via {@link UUID#nameUUIDFromBytes(byte[])}</li>
     *     <li>If one of the values is an UUID instance, it is simply casted</li>
     *     <li>If one of the values cannot be converted, {@code null} will be used instead</li>
     * </ul>
     * After conversion, both converted values are compared for their equality with eachother.<br>
     * They're considered equal if and only if both are {@code null} or the {@link UUID#equals(Object)} method on one of the instances returns {@code true} for the other instance.
     * @param uuid1 The first object
     * @param uuid2 The second object
     * @return true, if both objects are UUIDs and are equal, or both are null, false otherwise
     */
    public static boolean areUuidsEqual(Object uuid1, Object uuid2) {
        Function<Object, UUID> getUUID = (obj) -> {
            if( obj instanceof String && isStringUuid(obj.toString()) ) {
                return UUID.fromString(obj.toString());
            } else if( obj instanceof UUID ) {
                return (UUID) obj;
            } else if( obj instanceof byte[] ) {
                return UUID.nameUUIDFromBytes((byte[]) obj);
            }

            return null;
        };

        UUID uuidInst1 = getUUID.apply(uuid1);
        UUID uuidInst2 = getUUID.apply(uuid2);

        //noinspection ObjectEquality
        return uuidInst1 == uuidInst2 || (uuidInst1 != null && uuidInst1.equals(uuidInst2));
    }
}
