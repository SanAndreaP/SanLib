////////////////////////////////////////////////////////////////////////////////
// This file is subject to the terms and conditions defined in the             /
// file '.github/LICENSE.md', which is part of this source code package.       /
////////////////////////////////////////////////////////////////////////////////

package de.sanandrew.mods.sanlib.lib.client.gui;

import net.minecraft.client.gui.screen.Screen;

@SuppressWarnings("UnusedReturnValue")
public interface IGui
{
    Screen get();

    GuiDefinition getDefinition();

    int getScreenPosX();

    int getScreenPosY();
}
