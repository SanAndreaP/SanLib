/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.sanlib.lib.client.gui;

import com.google.gson.JsonObject;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

public class EmptyGuiElement
        implements IGuiElement
{
    @Override
    public ResourceLocation getId() {
        return null;
    }

    @Override
    public void render(GuiScreen gui, float partTicks, int x, int y, int mouseX, int mouseY, JsonObject data) { }
}
