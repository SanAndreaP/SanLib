////////////////////////////////////////////////////////////////////////////////
// This file is subject to the terms and conditions defined in the             /
// file '.github/LICENSE.md', which is part of this source code package.       /
////////////////////////////////////////////////////////////////////////////////

package de.sanandrew.mods.sanlib.lib.client.gui.element;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.IGuiElement;
import de.sanandrew.mods.sanlib.lib.client.util.GuiUtils;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.Range;

@SuppressWarnings({"unused", "UnusedReturnValue", "java:S1172", "java:S1104"})
public class Rectangle
        implements IGuiElement
{
    public static final ResourceLocation ID = new ResourceLocation("rectangle");

    protected int[]   size;
    protected int[]   color;
    protected boolean hGradient;

    public Rectangle(int[] size, int[] color, boolean hGradient) {
        this.size = size;
        this.color = color;
        this.hGradient = hGradient;
    }

    @Override
    public void render(IGui gui, MatrixStack stack, float partTicks, int x, int y, double mouseX, double mouseY, GuiElementInst inst) {
        stack.pushPose();
        stack.translate(x, y, 0.0D);
        if( this.color[0] != this.color[1] ) {
            GuiUtils.drawGradient(stack, 0, 0, this.size[0], this.size[1], this.color[0], this.color[1], this.hGradient);
        } else {
            AbstractGui.fill(stack, 0, 0, this.size[0], this.size[1], this.color[0]);
        }
        RenderSystem.enableBlend();
        stack.popPose();
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
            implements IBuilder<Rectangle>
    {
        public final int[] size;

        protected int[]   color;
        protected boolean hGradient = false;

        public Builder(int[] size) {
            this.size = size;
            this.color = new int[] {0xFF000000, 0xFF000000};
        }

        public Builder color(int firstColor, int secondColor) { this.color = new int[] {firstColor, secondColor}; return this; }
        public Builder horizontalGradient(boolean hGradient)  { this.hGradient = hGradient;                       return this; }

        public Builder color(int color)                             { return this.color(color, color); }
        public Builder color(int[] colors)                          { return this.color(colors[0], colors[colors.length > 1 ? 1 : 0]); }
        public Builder color(String color)                          { return this.color(MiscUtils.hexToInt(color)); }
        public Builder color(String firstColor, String secondColor) { return this.color(MiscUtils.hexToInt(firstColor), MiscUtils.hexToInt(secondColor)); }
        public Builder color(String[] colors)                       { return this.color(MiscUtils.hexToInt(colors[0]), MiscUtils.hexToInt(colors[colors.length > 1 ? 1 : 0])); }

        @Override
        public void sanitize(IGui gui) {
            // noop
        }

        @Override
        public Rectangle get(IGui gui) {
            return new Rectangle(this.size, this.color, this.hGradient);
        }

        public static Builder buildFromJson(IGui gui, JsonObject data) {
            Builder b = new Builder(JsonUtils.getIntArray(data.get("size"), Range.is(2)));

            JsonUtils.fetchStringArray(data.get("color"), b::color, Range.between(1, 2));
            JsonUtils.fetchBool(data.get("horizontalGradient"), b::horizontalGradient);

            return b;
        }

        public static Rectangle fromJson(IGui gui, JsonObject data) {
            return buildFromJson(gui, data).get(gui);
        }
    }
}
