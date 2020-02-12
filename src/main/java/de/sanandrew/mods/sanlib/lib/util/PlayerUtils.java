////////////////////////////////////////////////////////////////////////////////
// This file is subject to the terms and conditions defined in the             /
// file '.github/LICENSE.md', which is part of this source code package.       /
////////////////////////////////////////////////////////////////////////////////

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
