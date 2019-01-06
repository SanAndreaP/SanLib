/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.sanlib.lib.client.gui;

import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.lib.ColorObj;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.Range;

/**
 * JSON format:
 * <pre>
&#123;
  "type": "texture",                                                 -- type of element: "texture"
  "x": 0,                                                            -- relative x coordinate
  "y": 0,                                                            -- relative y coordinate
  "data": &#123;
    "location": "minecraft:textures/gui/container/inventory.png"     -- resource location of texture
    "width": 176,                                                    -- width of textured rectangle
    "height": 166,                                                   -- height of textured rectangle
    "u": 0,                                                          -- x coordinate on the texture
    "v": 0,                                                          -- y coordinate on the texture
    "textureWidth": 256,                                             -- width of the texture (optional, default: 256)
    "textureHeight": 256,                                            -- height of the texture (optional, default: 256)
    "scaleX": 1.0,                                                   -- horizontal scaling of the textured rectangle (optional, default: 1.0)
    "scaleY": 1.0,                                                   -- vertical scaling of the textured rectangle (optional, default: 1.0)
    "color": "0xFFFFFFFF",                                           -- the tint color (and transparency) of the textured rectangle as hexadecimal number string (optional, default: "0xFFFFFFFF")
    "forceAlpha": false,                                             -- wether or not the texture can be forced to use transparency, as some elements might deactivate it (optional, default: false)
  &#125;
&#125;
 * </pre>
 */
public class TextureGuiElement
        implements IGuiElement
{
    static final ResourceLocation ID = new ResourceLocation("texture");

    private BakedData data;

    @Override
    public void render(GuiScreen gui, float partTicks, int x, int y, int mouseX, int mouseY, JsonObject data) {
        if( this.data == null ) {
            this.data = new BakedData();
            this.data.location = new ResourceLocation(data.get("location").getAsString());
            this.data.size = JsonUtils.getIntArray(data.get("size"), Range.is(2));
            this.data.uv = JsonUtils.getIntArray(data.get("uv"), Range.is(2));
            this.data.textureSize = JsonUtils.getIntArray(data.get("textureSize"), new int[] {256, 256}, Range.is(2));
            this.data.scale = JsonUtils.getDoubleArray(data.get("scale"), new double[] {1.0D, 1.0D}, Range.is(2));
            this.data.tintColor = new ColorObj(MiscUtils.hexToInt(JsonUtils.getStringVal(data.get("color"), "0xFFFFFFFF")));
            this.data.forceAlpha = JsonUtils.getBoolVal(data.get("forceAlpha"), false);
        }

        gui.mc.renderEngine.bindTexture(this.data.location);
        GlStateManager.pushMatrix();
        if( this.data.forceAlpha ) {
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        }
        GlStateManager.translate(x, y, gui.zLevel);
        GlStateManager.scale(this.data.scale[0], this.data.scale[1], 1.0D);
        GlStateManager.color(this.data.tintColor.fRed(), this.data.tintColor.fGreen(), this.data.tintColor.fBlue(), this.data.tintColor.fAlpha());
        Gui.drawModalRectWithCustomSizedTexture(0, 0, this.data.uv[0], this.data.uv[1], this.data.size[0], this.data.size[1], this.data.textureSize[0], this.data.textureSize[1]);
        GlStateManager.popMatrix();
    }

    @Override
    public int getHeight() {
        return this.data == null ? 0 : this.data.size[1];
    }

    private static final class BakedData
    {
        private ResourceLocation location;
        private int[] size;
        private int[] textureSize;
        private int[] uv;
        private double[] scale;
        private ColorObj tintColor;
        private boolean forceAlpha;
    }
}
