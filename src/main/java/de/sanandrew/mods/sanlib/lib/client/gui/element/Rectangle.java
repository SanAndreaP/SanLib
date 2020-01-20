/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.sanlib.lib.client.gui.element;

import com.google.gson.JsonObject;
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

    public BakedData data;
    protected boolean isVisible = true;

    @Override
    public void bakeData(IGui gui, JsonObject data) {
        if( this.data == null ) {
            this.data = new BakedData();
            this.data.size = JsonUtils.getIntArray(data.get("size"), Range.is(2));
            String[] colors = JsonUtils.getStringArray(data.get("color"), new String[] {"0xFFFFFFFF"}, Range.between(1, 2));
            this.data.color = new int[] {MiscUtils.hexToInt(colors[0]), MiscUtils.hexToInt(colors.length > 1 ? colors[1] : colors[0])};
            this.data.horizontal = JsonUtils.getBoolVal(data.get("horizontal"), false);
        }
    }

    @Override
    public void render(IGui gui, float partTicks, int x, int y, int mouseX, int mouseY, JsonObject data) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, 0.0D);
        if( this.data.color[0] != this.data.color[1] ) {
            GuiUtils.drawGradientRect(0, 0, this.data.size[0], this.data.size[1], this.data.color[0], this.data.color[1], this.data.horizontal);
        } else {
            Gui.drawRect(0, 0, this.data.size[0], this.data.size[1], this.data.color[0]);
        }
        GlStateManager.popMatrix();
    }

    @Override
    public int getWidth() {
        return this.data.size[0];
    }

    @Override
    public int getHeight() {
        return this.data.size[1];
    }

    @Override
    public boolean isVisible() {
        return this.isVisible;
    }

    @Override
    public void setVisible(boolean visible) {
        this.isVisible = visible;
    }

    public static final class BakedData
    {
        public int[] size;
        public int[] color;
        public boolean horizontal;
    }
}
