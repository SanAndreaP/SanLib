////////////////////////////////////////////////////////////////////////////////
// This file is subject to the terms and conditions defined in the             /
// file '.github/LICENSE.md', which is part of this source code package.       /
////////////////////////////////////////////////////////////////////////////////

package de.sanandrew.mods.sanlib.lib.client.gui.element;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import de.sanandrew.mods.sanlib.lib.ColorObj;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.IGuiElement;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import net.minecraft.client.gui.AbstractGui;
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
@SuppressWarnings({"unused", "UnusedReturnValue", "java:S1172", "java:S1104"})
public class Texture
        implements IGuiElement
{
    public static final ResourceLocation ID = new ResourceLocation("texture");

    protected ResourceLocation txLocation;
    protected int[]            size;
    protected int[]            textureSize;
    protected int[]            uv;
    protected float[]          scale;
    protected ColorObj         color;

    public Texture(ResourceLocation txLocation, int[] size, int[] textureSize, int[] uv, float[] scale, ColorObj color) {
        this.txLocation = txLocation;
        this.size = size;
        this.textureSize = textureSize;
        this.uv = uv;
        this.scale = scale;
        this.color = color;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void render(IGui gui, MatrixStack stack, float partTicks, int x, int y, double mouseX, double mouseY, GuiElementInst inst) {
        gui.get().getMinecraft().getTextureManager().bind(this.txLocation);
        stack.pushPose();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        stack.translate(x, y, 0.0D);
        stack.scale(this.scale[0], this.scale[1], 1.0F);
        RenderSystem.color4f(this.color.fRed(), this.color.fGreen(), this.color.fBlue(), this.color.fAlpha());
        drawRect(gui, stack);
        stack.popPose();
    }

    protected void drawRect(IGui gui, MatrixStack stack) {
        AbstractGui.blit(stack, 0, 0, this.uv[0], this.uv[1], this.size[0], this.size[1], this.textureSize[0], this.textureSize[1]);
    }

    @Override
    public int getWidth() {
        return this.size[0];
    }

    @Override
    public int getHeight() {
        return this.size[1];
    }

    public static class Builder
    {
        protected ResourceLocation texture;
        protected int[] size;
        protected int[] textureSize;
        protected int[] uv;
        protected float[] scale;
        protected ColorObj color;

        public Builder(int width, int height) {
            this(new int[] { width, height });
        }

        public Builder(int[] size) {
            this.size = size;
        }

        public Builder texture(ResourceLocation texture) { this.texture = texture;                               return this; }
        public Builder textureSize(int[] size)           { this.textureSize = size;                              return this; }
        public Builder uv(int[] uv)                      { this.uv = uv;                                         return this; }
        public Builder scale(float[] scale)              { this.scale = scale;                                   return this; }
        public Builder color(int color)                  { this.color = new ColorObj(color);                     return this; }

        public Builder textureSize(int width, int height) { return this.textureSize(new int[] {width, height}); }
        public Builder uv(int u, int v)                   { return this.uv(new int[] {u, v}); }
        public Builder scale(float x, float y)            { return this.scale(new float[] {x, y}); }
        public Builder scale(float scale)                 { return this.scale(new float[] {scale, scale}); }
        public Builder color(String color)                { return this.color(MiscUtils.hexToInt(color)); }

        protected void sanitize(IGui gui) {
            if( this.texture == null ) {
                this.texture = gui.getDefinition().getTexture(null);
            }

            if( this.textureSize == null ) {
                this.textureSize = new int[] {256, 256};
            }

            if( this.uv == null ) {
                this.uv = new int[] {0, 0};
            }

            if( this.scale == null ) {
                this.scale = new float[] {1.0F, 1.0F};
            }

            if( this.color == null ) {
                this.color = new ColorObj(0xFFFFFFFF);
            }
        }

        public Texture get(IGui gui) {
            this.sanitize(gui);

            return new Texture(this.texture, this.size, this.textureSize, this.uv, this.scale, this.color);
        }

        protected static Builder buildFromJson(IGui gui, JsonObject data) {
            Builder b = new Builder(JsonUtils.getIntArray(data.get("size"), Range.is(2)))
                    .texture(gui.getDefinition().getTexture(data.get("texture")))
                    .uv(JsonUtils.getIntArray(data.get("uv"), Range.is(2)));

            JsonUtils.fetchIntArray(data.get("textureSize"), b::textureSize, Range.is(2));
            JsonUtils.fetchFloatArray(data.get("scale"), b::scale, Range.is(2));
            JsonUtils.fetchString(data.get("color"), b::color);

            return b;
        }

        public static Texture fromJson(IGui gui, JsonObject data) {
            return buildFromJson(gui, data).get(gui);
        }
    }
}
