/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2016-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.sanlib.lib.client.gui;

import net.minecraft.client.gui.screens.Screen;

@SuppressWarnings("UnusedReturnValue")
public interface IGui
{
    Screen get();

    GuiDefinition getDefinition();

    int getPosX();

    int getPosY();
}
