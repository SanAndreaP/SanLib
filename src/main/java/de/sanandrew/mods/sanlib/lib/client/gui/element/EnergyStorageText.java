////////////////////////////////////////////////////////////////////////////////
// This file is subject to the terms and conditions defined in the             /
// file '.github/LICENSE.md', which is part of this source code package.       /
////////////////////////////////////////////////////////////////////////////////

package de.sanandrew.mods.sanlib.lib.client.gui.element;

import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import java.util.Map;

@SuppressWarnings({"unused", "UnusedReturnValue", "java:S1172", "java:S1104"})
public class EnergyStorageText
        extends Text
{
    public static final ResourceLocation ID = new ResourceLocation("energy_text");

    public EnergyStorageText(boolean shadow, int wrapWidth, int lineHeight, FontRenderer fontRenderer, Map<String, Integer> colors) {
        super(StringTextComponent.EMPTY, shadow, wrapWidth, lineHeight, fontRenderer, colors);
    }

    @Override
    public ITextComponent getDynamicText(IGui gui, ITextComponent originalText) {
        EnergyStorageBar.IGuiEnergyContainer gec = (EnergyStorageBar.IGuiEnergyContainer) gui;
        return new StringTextComponent(String.format("%d / %d RF", gec.getEnergy(), gec.getMaxEnergy()));
    }

    public static class Builder
            extends Text.Builder
    {
        public Builder() {
            super(StringTextComponent.EMPTY);
        }

        @Override
        public void sanitize(IGui gui) {
            if( this.colors.isEmpty() ) {
                this.colors.put(DEFAULT_COLOR, 0xFFFFFFFF);
            }

            super.sanitize(gui);
        }

        @Override
        public EnergyStorageText get(IGui gui) {
            super.sanitize(gui);

            return new EnergyStorageText(this.shadow, this.wrapWidth, this.lineHeight, this.fontRenderer, this.colors);
        }

        public static Builder buildFromJson(IGui gui, JsonObject data) {
            Text.Builder sb = Text.Builder.buildFromJson(gui, data);
            Builder      db = IBuilder.copyValues(sb, new Builder());

            db.shadow = JsonUtils.getBoolVal(data.get("shadow"), true);

            return db;
        }

        public static EnergyStorageText fromJson(IGui gui, JsonObject data) {
            return buildFromJson(gui, data).get(gui);
        }
    }
}
