package de.sanandrew.mods.sanlib.lib.gui;

import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.lib.client.gui.IGuiElement;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GuiDefinition
{
    int width;
    int height;
    boolean hasSlots;

    GuiElement[] elements;
    SlotPanel[] slotPanels;

    final class GuiElement
    {
        int x;
        int y;
        boolean isBackground;
        JsonObject data;

        @SideOnly(Side.CLIENT)
        IGuiElement element;
    }

    final class Button
    {
        int index;
        int x;
        int y;
        int width;
        int height;
    }

    final class SlotPanel
    {
        String type;
        int index;
        int x;
        int y;
        int rows;
        int columns;
    }
}
