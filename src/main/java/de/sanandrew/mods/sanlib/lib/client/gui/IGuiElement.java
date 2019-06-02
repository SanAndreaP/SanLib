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

    default void mouseClicked(IGui gui, int mouseX, int mouseY, int mouseButton) throws IOException { }

    default void mouseReleased(IGui gui, int mouseX, int mouseY, int state) { }

    default void mouseClickMove(IGui gui, int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) { }

    int getWidth();

    int getHeight();
}
