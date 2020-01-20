/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.sanlib.lib.client.gui.element;

import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.lib.ColorObj;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.IGuiElement;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.Range;

/**
 * JSON format:
 * <pre>
&#123;
  "type": "texture",                                                 -- type of element: "texture"
  "pos": [0, 0],                                                     -- relative position as [x, y] coordinates
  "data": &#123;
    "location": "minecraft:textures/gui/container/inventory.png"     -- resource location of texture
    "size": [176, 166],                                              -- size of textured rectangle as [width, height]
    "uv": [0, 0],                                                    -- coordinates of the texture on the texture sheet as [u, v]
    "textureSize": [256, 256],                                       -- size of the texture sheet as [width, height] (optional, default: [256, 256])
    "scale": [1.0, 1.0],                                             -- scaling of the textured rectangle as [scaleX, scaleY] (optional, default: [1.0, 1.0])
    "color": "0xFFFFFFFF",                                           -- the tint color (and transparency) of the textured rectangle as hexadecimal number string (optional, default: "0xFFFFFFFF")
    "forceAlpha": false,                                             -- wether or not the texture can be forced to use transparency, as some elements might deactivate it (optional, default: false)
  &#125;
&#125;
 * </pre>
 */
@SuppressWarnings({"WeakerAccess", "Duplicates"})
public class Texture
        implements IGuiElement
{
    public static final ResourceLocation ID = new ResourceLocation("texture");

    public BakedData data;

    protected boolean isVisible = true;

    @Override
    public void bakeData(IGui gui, JsonObject data) {
        if( this.data == null ) {
            this.data = new BakedData();
            this.data.texture = gui.getDefinition().getTexture(data.get("texture"));
            this.data.size = JsonUtils.getIntArray(data.get("size"), Range.is(2));
            this.data.uv = JsonUtils.getIntArray(data.get("uv"), Range.is(2));
            this.data.textureSize = JsonUtils.getIntArray(data.get("textureSize"), new int[] {256, 256}, Range.is(2));
            this.data.scale = JsonUtils.getDoubleArray(data.get("scale"), new double[] {1.0D, 1.0D}, Range.is(2));
            this.data.color = new ColorObj(MiscUtils.hexToInt(JsonUtils.getStringVal(data.get("color"), "0xFFFFFFFF")));
            this.data.forceAlpha = JsonUtils.getBoolVal(data.get("forceAlpha"), false);
        }
    }

    @Override
    public void render(IGui gui, float partTicks, int x, int y, int mouseX, int mouseY, JsonObject data) {
        gui.get().mc.renderEngine.bindTexture(this.data.texture);
        GlStateManager.pushMatrix();
        if( this.data.forceAlpha ) {
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        }
        GlStateManager.translate(x, y, 0.0D);
        GlStateManager.scale(this.data.scale[0], this.data.scale[1], 1.0D);
        GlStateManager.color(this.data.color.fRed(), this.data.color.fGreen(), this.data.color.fBlue(), this.data.color.fAlpha());
        drawRect(gui);
        GlStateManager.popMatrix();
    }

    protected void drawRect(IGui gui) {
        Gui.drawModalRectWithCustomSizedTexture(0, 0, this.data.uv[0], this.data.uv[1], this.data.size[0], this.data.size[1], this.data.textureSize[0], this.data.textureSize[1]);
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
        public ResourceLocation texture;
        public int[] size;
        public int[] textureSize;
        public int[] uv;
        public double[] scale;
        public ColorObj color;
        public boolean forceAlpha;
    }
}
