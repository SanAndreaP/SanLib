/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.sapmanpack.lib;

import java.util.regex.Pattern;

/**
 * Utility class for tasks and methods regarding UUIDs
 */
public final class UuidUtils
{
    private static final Pattern UUID_PTRN = Pattern.compile("[a-f0-9]{8}\\-[a-f0-9]{4}\\-4[a-f0-9]{3}\\-[89ab][a-f0-9]{3}\\-[a-f0-9]{12}", Pattern.CASE_INSENSITIVE);

    /**
     * Checks whether or not the String represents and can be parsed to an UUID
     * @param uuid the String representing the UUID
     * @return true, if the String represents an UUID, false otherwise
     */
    public static boolean isStringUuid(String uuid) {
        return UUID_PTRN.matcher(uuid).matches();
    }
}
