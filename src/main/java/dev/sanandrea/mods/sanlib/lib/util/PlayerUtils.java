/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2016-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.sanlib.lib.util;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

@SuppressWarnings("unused")
public final class PlayerUtils
{
    private PlayerUtils() {}

    public static ItemStack getHeldItemOfType(Player player, Item type) {
        ItemStack heldStack = player.getMainHandItem();
        if( !ItemStackUtils.isItem(heldStack, type) ) {
            return player.getOffhandItem();
        }

        return heldStack;
    }
}
