package de.sanandrew.mods.sanlib.lib.client.gui;

import com.google.gson.JsonObject;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

public class ScrollAreaGuiElement
        implements IGuiElement
{
    @Override
    public void render(GuiScreen gui, float partTicks, int x, int y, int mouseX, int mouseY, JsonObject data) {

    }

    @Override
    public int getHeight() {
        return 0;
    }

    private static final class BakedData
    {
        private ResourceLocation texture;
        private int width;
        private int height;
        private int[] margin;
//        private
    }
}
