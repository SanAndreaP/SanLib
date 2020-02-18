////////////////////////////////////////////////////////////////////////////////
// This file is subject to the terms and conditions defined in the             /
// file '.github/LICENSE.md', which is part of this source code package.       /
////////////////////////////////////////////////////////////////////////////////

package de.sanandrew.mods.sanlib.lib.client.gui.element;

import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.sanlib.lib.util.LangUtils;
import net.minecraft.util.ResourceLocation;

@SuppressWarnings("WeakerAccess")
public class DynamicText
    extends Text
{
    public static final ResourceLocation ID = new ResourceLocation("dynamic_text");

    public String key;

    @Override
    public void bakeData(IGui gui, JsonObject data, GuiElementInst inst) {
        if( this.key == null ) {
            this.key = JsonUtils.getStringVal(data.get("key"));
        }

        super.bakeData(gui, data, inst);
    }

    @Override
    public String getBakedText(IGui gui, JsonObject data) {
        return data.has("text") ? LangUtils.translate(JsonUtils.getStringVal(data.get("text"))) : "";
    }

    @Override
    public String getDynamicText(IGui gui, String originalText) {
        return ((IGuiDynamicText) gui).getText(this.key, originalText);
    }

    public interface IGuiDynamicText
    {
        String getText(String key, String originalText);
    }
}
