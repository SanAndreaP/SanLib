package de.sanandrew.mods.sanlib.lib.client.gui;

import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;

import java.io.IOException;

public interface IGuiElement
{
    void bakeData(IGui gui, JsonObject data);

    default void update(IGui gui, JsonObject data) {}

    void render(IGui gui, float partTicks, int x, int y, int mouseX, int mouseY, JsonObject data);

    default void handleMouseInput(IGui gui) throws IOException { }

    int getHeight();
}
