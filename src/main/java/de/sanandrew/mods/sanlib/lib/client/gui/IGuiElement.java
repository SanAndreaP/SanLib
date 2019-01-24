package de.sanandrew.mods.sanlib.lib.client.gui;

import com.google.gson.JsonObject;

public interface IGuiElement
{
    void render(IGui gui, float partTicks, int x, int y, int mouseX, int mouseY, JsonObject data);

    int getHeight();
}
