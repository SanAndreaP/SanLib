////////////////////////////////////////////////////////////////////////////////
// This file is subject to the terms and conditions defined in the             /
// file '.github/LICENSE.md', which is part of this source code package.       /
////////////////////////////////////////////////////////////////////////////////

package de.sanandrew.mods.sanlib.lib.client.gui.element;

import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.IGuiElement;
import de.sanandrew.mods.sanlib.lib.client.util.GuiUtils;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.Range;

@SuppressWarnings("WeakerAccess")
public class Rectangle
        implements IGuiElement
{
    public static final ResourceLocation ID = new ResourceLocation("rectangle");

    public int[] size;
    public int[] color;
    public boolean horizontal;

    @Override
    public void bakeData(IGui gui, JsonObject data, GuiElementInst inst) {
        this.size = JsonUtils.getIntArray(data.get("size"), Range.is(2));
        String[] colors = JsonUtils.getStringArray(data.get("color"), new String[] {"0xFFFFFFFF"}, Range.between(1, 2));
        this.color = new int[] {MiscUtils.hexToInt(colors[0]), MiscUtils.hexToInt(colors.length > 1 ? colors[1] : colors[0])};
        this.horizontal = JsonUtils.getBoolVal(data.get("horizontal"), false);
    }

    @Override
    public void render(IGui gui, float partTicks, int x, int y, int mouseX, int mouseY, JsonObject data) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, 0.0D);
        if( this.color[0] != this.color[1] ) {
            GuiUtils.drawGradientRect(0, 0, this.size[0], this.size[1], this.color[0], this.color[1], this.horizontal);
        } else {
            Gui.drawRect(0, 0, this.size[0], this.size[1], this.color[0]);
        }
        GlStateManager.enableBlend();
        GlStateManager.popMatrix();
    }

    @Override
    public int getWidth() {
        return this.size[0];
    }

    @Override
    public int getHeight() {
        return this.size[1];
    }
}
