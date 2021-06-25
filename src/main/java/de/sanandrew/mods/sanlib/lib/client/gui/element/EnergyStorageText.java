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
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class EnergyStorageText
        extends Text
{
    public static final ResourceLocation ID = new ResourceLocation("energy_text");

    @Override
    public void bakeData(IGui gui, JsonObject data, GuiElementInst inst) {
        JsonUtils.addDefaultJsonProperty(data, "color", "0xFFFFFFFF");
        JsonUtils.addDefaultJsonProperty(data, "shadow", true);

        super.bakeData(gui, data, inst);
    }

    @Override
    public ITextComponent getBakedText(IGui gui, JsonObject data) {
        return StringTextComponent.EMPTY;
    }

    @Override
    public int getHeight() {
        return super.getHeight() - 2;
    }

    @Override
    public ITextComponent getDynamicText(IGui gui, ITextComponent originalText) {
        EnergyStorageBar.IGuiEnergyContainer gec = (EnergyStorageBar.IGuiEnergyContainer) gui;
        return new StringTextComponent(String.format("%d / %d RF", gec.getEnergy(), gec.getMaxEnergy()));
    }
}
