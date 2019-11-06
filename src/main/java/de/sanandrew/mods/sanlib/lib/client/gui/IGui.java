/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
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
