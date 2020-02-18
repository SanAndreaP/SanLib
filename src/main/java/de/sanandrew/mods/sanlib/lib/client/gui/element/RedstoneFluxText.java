////////////////////////////////////////////////////////////////////////////////
// This file is subject to the terms and conditions defined in the             /
// file '.github/LICENSE.md', which is part of this source code package.       /
////////////////////////////////////////////////////////////////////////////////

package de.sanandrew.mods.sanlib.lib.client.gui.element;

import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import net.minecraft.util.ResourceLocation;

public class RedstoneFluxText
        extends Text
{
    public static final ResourceLocation ID = new ResourceLocation("rflux_text");

    @Override
    public void bakeData(IGui gui, JsonObject data, GuiElementInst inst) {
        JsonUtils.addDefaultJsonProperty(data, "color", "0xFFFFFFFF");
        JsonUtils.addDefaultJsonProperty(data, "shadow", true);

        super.bakeData(gui, data, inst);
    }

    @Override
    public String getBakedText(IGui gui, JsonObject data) {
        return "";
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
