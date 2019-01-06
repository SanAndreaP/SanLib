/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.sanlib.lib.client.gui;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import de.sanandrew.mods.sanlib.lib.client.util.GuiUtils;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.resource.IResourceType;

import java.util.function.Predicate;

public class RectangleGuiElement
        implements IGuiElement
{
    static final ResourceLocation ID = new ResourceLocation("rectangle");

    private BakedData data;

    @Override
    public void render(GuiScreen gui, float partTicks, int x, int y, int mouseX, int mouseY, JsonObject data) {
        if( this.data == null ) {
            this.data = new BakedData();
            this.data.width = JsonUtils.getIntVal(data.get("width"));
            this.data.height = JsonUtils.getIntVal(data.get("height"));
            String colorStart = JsonUtils.getStringVal(data.get("colorStart"), "0xFFFFFFFF");
            this.data.colorStart = MiscUtils.hexToInt(colorStart);
            this.data.colorEnd = MiscUtils.hexToInt(JsonUtils.getStringVal(data.get("colorEnd"), colorStart));
            this.data.horizontal = JsonUtils.getBoolVal(data.get("horizontal"), false);
        }

        GlStateManager.translate(x, y, gui.zLevel);
        GlStateManager.pushMatrix();
        if( this.data.colorStart != this.data.colorEnd ) {
            GuiUtils.drawGradientRect(0, 0, this.data.width, this.data.height, this.data.colorStart, this.data.colorEnd, this.data.horizontal);
        } else {
            Gui.drawRect(0, 0, this.data.width, this.data.height, this.data.colorStart);
        }
        GlStateManager.popMatrix();
    }

    @Override
    public int getHeight() {
        return this.data == null ? 0 : this.data.height;
    }

    private static final class BakedData
    {
        private int width;
        private int height;
        private int colorStart;
        private int colorEnd;
        private boolean horizontal;
    }
}
