////////////////////////////////////////////////////////////////////////////////
// This file is subject to the terms and conditions defined in the             /
// file '.github/LICENSE.md', which is part of this source code package.       /
////////////////////////////////////////////////////////////////////////////////

package de.sanandrew.mods.sanlib.lib.client.gui;

import net.minecraft.client.gui.GuiScreen;

@SuppressWarnings("UnusedReturnValue")
public interface IGui
{
    GuiScreen get();

    GuiDefinition getDefinition();

    int getScreenPosX();

    int getScreenPosY();

    default boolean performAction(IGuiElement element, int action) { return false; }
}
