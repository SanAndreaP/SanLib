/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.sanlib.lib.client.gui;

import com.google.gson.JsonObject;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.resource.IResourceType;

import java.util.function.Predicate;

public class EmptyGuiElement
        implements IGuiElement
{
    @Override
    public void render(GuiScreen gui, float partTicks, int x, int y, int mouseX, int mouseY, JsonObject data) { }

    @Override
    public int getHeight() {
        return 0;
    }
}
