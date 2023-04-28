/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2016-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.sanlib.lib.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

@SuppressWarnings("unused")
public final class PlayerUtils
{
    /**
     * Returns the clientside player. On a server, this returns {@code null}.
     * @return the clientside player
     */
//    public static EntityPlayer getClientPlayer() {
//        return SanLib.proxy.getClientPlayer();
//    }

    public static ItemStack getHeldItemOfType(PlayerEntity player, Item type) {
        ItemStack heldStack = player.getMainHandItem();
        if( !ItemStackUtils.isItem(heldStack, type) ) {
            return player.getOffhandItem();
        }

        return heldStack;
    }
}
