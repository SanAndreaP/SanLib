/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.sanlib.lib.util;

import de.sanandrew.mods.sanlib.SanLib;
import net.minecraft.entity.player.EntityPlayer;

@SuppressWarnings("unused")
public final class PlayerUtils
{
    /**
     * Returns the clientside player. On a server, this returns {@code null}.
     * @return the clientside player
     */
    public static EntityPlayer getClientPlayer() {
        return SanLib.proxy.getClientPlayer();
    }
}
