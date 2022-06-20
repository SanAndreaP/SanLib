////////////////////////////////////////////////////////////////////////////////
// This file is subject to the terms and conditions defined in the             /
// file '.github/LICENSE.md', which is part of this source code package.       /
////////////////////////////////////////////////////////////////////////////////

package de.sanandrew.mods.sanlib.lib.client.gui.element;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import de.sanandrew.mods.sanlib.lib.ColorObj;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;

import javax.annotation.Nonnull;
import java.util.Locale;
import java.util.function.ToDoubleFunction;

@SuppressWarnings({"unused", "UnusedReturnValue", "java:S1172", "java:S1104"})
public class ProgressBar
        extends Texture
{
    public static final ResourceLocation ID = new ResourceLocation("progress_bar");

    protected final Direction direction;
    protected final boolean smooth;

    protected ToDoubleFunction<IGui> getPercentFunc = g -> 0.0D;

    public ProgressBar(ResourceLocation txLocation, int[] size, int[] textureSize, int[] uv, float[] scale, ColorObj color, Direction direction, boolean smooth) {
        super(txLocation, size, textureSize, uv, scale, color);

        this.direction = direction;
        this.smooth = smooth;
    }

    public void setPercentFunc(@Nonnull ToDoubleFunction<IGui> func) {
        this.getPercentFunc = func;
    }

    @Override
    protected void drawRect(IGui gui, MatrixStack stack) {
        float energyPerc = (float) Math.max(0, Math.min(this.getPercentFunc.applyAsDouble(gui), 1.0D));


        if( this.smooth ) {
            float w = !this.direction.vertical ? this.size[0] * energyPerc : this.size[0];
            float h = this.direction.vertical ? this.size[1] * energyPerc : this.size[1];
            float x = this.direction == Direction.RIGHT_TO_LEFT ? this.size[0] - w : 0;
            float y = this.direction == Direction.BOTTOM_TO_TOP ? this.size[1] - h : 0;

            smoothBlit(stack, x, y, this.uv[0] + x, this.uv[1] + y, w, h, this.textureSize[0], this.textureSize[1]);
        } else {
            int w = !this.direction.vertical ? MathHelper.ceil(this.size[0] * energyPerc) : this.size[0];
            int h = this.direction.vertical ? MathHelper.ceil(this.size[1] * energyPerc) : this.size[1];
            int x = this.direction == Direction.RIGHT_TO_LEFT ? this.size[0] - w : 0;
            int y = this.direction == Direction.BOTTOM_TO_TOP ? this.size[1] - h : 0;

            AbstractGui.blit(stack, x, y, this.uv[0] + (float) x, this.uv[1] + (float) y, w, h, this.textureSize[0], this.textureSize[1]);
        }
    }

    private static void smoothBlit(MatrixStack stack, float x, float y, float u, float v, float w, float h, int tW, int tH) {
        Matrix4f pose = stack.last().pose();

        float xw = x + w;
        float yh = y + h;

        float ut = u / tW;
        float uwt = (u + w) / tW;
        float vt = v / tH;
        float vht = (v + h) / tH;

        BufferBuilder builder = Tessellator.getInstance().getBuilder();
        builder.begin(7, DefaultVertexFormats.POSITION_TEX);
        builder.vertex(pose, x, yh, 0).uv(ut, vht).endVertex();
        builder.vertex(pose, xw, yh, 0).uv(uwt, vht).endVertex();
        builder.vertex(pose, xw, y, 0).uv(uwt, vt).endVertex();
        builder.vertex(pose, x, y, 0).uv(ut, vt).endVertex();
        builder.end();
        RenderSystem.enableAlphaTest();
        WorldVertexBufferUploader.end(builder);
    }

    public static class Builder
            extends Texture.Builder
    {
        protected Direction direction;
        protected boolean smooth = false;

        public Builder(int[] size) {
            super(size);
        }

        public Builder direction(Direction direction) { this.direction = direction;                       return this; }
        public Builder direction(String direction)    { this.direction = Direction.fromString(direction); return this; }
        public Builder smooth(boolean smooth)         { this.smooth = smooth;                             return this; }

        @Override
        public void sanitize(IGui gui) {
            super.sanitize(gui);

            if( this.direction == null ) {
                this.direction = Direction.BOTTOM_TO_TOP;
            }
        }

        @Override
        public ProgressBar get(IGui gui) {
            this.sanitize(gui);

            return new ProgressBar(this.texture, this.size, this.textureSize, this.uv, this.scale, this.color, this.direction, this.smooth);
        }

        public static Builder buildFromJson(IGui gui, JsonObject data) {
            Texture.Builder tb = Texture.Builder.buildFromJson(gui, data);
            Builder b = IBuilder.copyValues(tb, new Builder(tb.size));

            MiscUtils.accept(JsonUtils.getStringVal(data.get("direction")), b::direction);
            MiscUtils.accept(JsonUtils.getBoolVal(data.get("smooth"), false), b::smooth);

            return b;
        }

        public static ProgressBar fromJson(IGui gui, JsonObject data) {
            return buildFromJson(gui, data).get(gui);
        }
    }

    public enum Direction
    {
        BOTTOM_TO_TOP(true),
        TOP_TO_BOTTOM(true),
        LEFT_TO_RIGHT(false),
        RIGHT_TO_LEFT(false);

        public final boolean vertical;

        Direction(boolean vertical) {
            this.vertical = vertical;
        }

        public static Direction fromString(String s) {
            return Direction.valueOf(s.toUpperCase(Locale.ROOT));
        }
    }
}
