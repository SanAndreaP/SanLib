package de.sanandrew.mods.sanlib.lib.client.gui.element;

import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import net.minecraft.util.ResourceLocation;

public class RedstoneFluxLabel
        extends Label
{
    public static final ResourceLocation ID = new ResourceLocation("rflux_label");

    @Override
    public void bakeData(IGui gui, JsonObject data) {
        if( !(gui instanceof RedstoneFluxBar.IGuiEnergyContainer) ) {
            throw new RuntimeException("Cannot use rflux_label on a GUI which doesn't implement IGuiEnergyContainer");
        }

        super.bakeData(gui, data);
    }

    @Override
    public Text getTextElement(IGui gui, JsonObject data) {
        Text elem = new RedstoneFluxLabelText();
        elem.bakeData(gui, data);
        return elem;
    }

    public class RedstoneFluxLabelText
            extends Text
    {
        @Override
        public String getBakedText(IGui gui, JsonObject data) {
            return "";
        }

        @Override
        public String getDynamicText(IGui gui, String originalText) {
            RedstoneFluxBar.IGuiEnergyContainer gec = (RedstoneFluxBar.IGuiEnergyContainer) gui;
            return String.format("%d / %d RF", gec.getEnergy(), gec.getMaxEnergy());
        }
    }
}
