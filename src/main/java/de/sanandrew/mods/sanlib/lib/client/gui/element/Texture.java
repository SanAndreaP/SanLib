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
@SuppressWarnings({"WeakerAccess", "Duplicates"})
public class Texture
        implements IGuiElement
{
    public static final ResourceLocation ID = new ResourceLocation("texture");

    public ResourceLocation texture;
    public int[] size;
    public int[] textureSize;
    public int[] uv;
    public float[] scale;
    public ColorObj color;

    @Override
    public void bakeData(IGui gui, JsonObject data, GuiElementInst inst) {
        this.texture = gui.getDefinition().getTexture(data.get("texture"));
        this.size = JsonUtils.getIntArray(data.get("size"), Range.is(2));
        this.uv = JsonUtils.getIntArray(data.get("uv"), Range.is(2));
        this.textureSize = JsonUtils.getIntArray(data.get("textureSize"), new int[] {256, 256}, Range.is(2));
        this.scale = JsonUtils.getFloatArray(data.get("scale"), new float[] {1.0F, 1.0F}, Range.is(2));
        this.color = new ColorObj(MiscUtils.hexToInt(JsonUtils.getStringVal(data.get("color"), "0xFFFFFFFF")));
    }

    @Override
    @SuppressWarnings("deprecation")
    public void render(IGui gui, MatrixStack stack, float partTicks, int x, int y, double mouseX, double mouseY, JsonObject data) {
        gui.get().getMinecraft().getTextureManager().bind(this.texture);
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
}
