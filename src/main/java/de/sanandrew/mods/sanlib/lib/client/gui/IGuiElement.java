package de.sanandrew.mods.sanlib.lib.client.gui;

import com.google.gson.JsonObject;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

public interface IGuiElement
{
    ResourceLocation getId();

    void render(GuiScreen gui, float partTicks, int x, int y, int mouseX, int mouseY, JsonObject data);
}
