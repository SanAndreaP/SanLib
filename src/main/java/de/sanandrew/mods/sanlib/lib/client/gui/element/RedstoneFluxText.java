package de.sanandrew.mods.sanlib.lib.client.gui.element;

import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import net.minecraft.util.ResourceLocation;

public class RedstoneFluxText
        extends Text
{
    public static final ResourceLocation ID = new ResourceLocation("rflux_text");

    @Override
    public void bakeData(IGui gui, JsonObject data) {
        if( !data.has("color") ) data.addProperty("color", "0xFFFFFFFF");
        if( !data.has("shadow") ) data.addProperty("shadow", true);

        super.bakeData(gui, data);
    }

    @Override
    public String getBakedText(IGui gui, JsonObject data) {
        return "";
    }

    @Override
    public void render(IGui gui, float partTicks, int x, int y, int mouseX, int mouseY, JsonObject data) {
        super.render(gui, partTicks, x, y, mouseX, mouseY, data);
    }

    @Override
    public int getHeight() {
        return super.getHeight() - 2;
    }

    @Override
    public String getDynamicText(IGui gui, String originalText) {
        RedstoneFluxBar.IGuiEnergyContainer gec = (RedstoneFluxBar.IGuiEnergyContainer) gui;
        return String.format("%d / %d RF", gec.getEnergy(), gec.getMaxEnergy());
    }
}
