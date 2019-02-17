package de.sanandrew.mods.sanlib.lib.client.gui;

import com.google.gson.JsonObject;

import java.io.IOException;

public interface IGuiElement
{
    void bakeData(IGui gui, JsonObject data);

    void render(IGui gui, float partTicks, int x, int y, int mouseX, int mouseY, JsonObject data);

    default boolean onMouseScroll(IGui gui, double scroll) { return false; }

    int getHeight();
}
