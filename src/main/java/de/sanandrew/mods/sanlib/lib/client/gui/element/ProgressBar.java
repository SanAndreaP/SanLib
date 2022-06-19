////////////////////////////////////////////////////////////////////////////////
// This file is subject to the terms and conditions defined in the             /
// file '.github/LICENSE.md', which is part of this source code package.       /
////////////////////////////////////////////////////////////////////////////////

package de.sanandrew.mods.sanlib.lib.client.gui.element;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.matrix.MatrixStack;
import de.sanandrew.mods.sanlib.lib.ColorObj;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

import javax.annotation.Nonnull;
import java.util.Locale;
import java.util.function.ToDoubleFunction;

@SuppressWarnings({"unused", "UnusedReturnValue", "java:S1172", "java:S1104"})
public class ProgressBar
        extends Texture
{
    public static final ResourceLocation ID = new ResourceLocation("progress_bar");

    protected final Direction direction;

    protected ToDoubleFunction<IGui> getPercentFunc = g -> 0.0D;

    public ProgressBar(ResourceLocation txLocation, int[] size, int[] textureSize, int[] uv, float[] scale, ColorObj color, Direction direction) {
        super(txLocation, size, textureSize, uv, scale, color);

        this.direction = direction;
    }

    public void setPercentFunc(@Nonnull ToDoubleFunction<IGui> func) {
        this.getPercentFunc = func;
    }

    @Override
    protected void drawRect(IGui gui, MatrixStack stack) {
        double energyPerc = this.getPercentFunc.applyAsDouble(gui);
        int    energyBarY = Math.max(0, Math.min(this.size[1], MathHelper.ceil((1.0D - energyPerc) * this.size[1])));

        AbstractGui.blit(stack, 0, energyBarY, this.uv[0], this.uv[1] + (float) energyBarY, this.size[0], this.size[1] - energyBarY, this.textureSize[0], this.textureSize[1]);
    }

    public static class Builder
            extends Texture.Builder
    {
        protected Direction direction;

        public Builder(int[] size) {
            super(size);
        }

        public Builder direction(Direction direction) { this.direction = direction; return this; }
        public Builder direction(String direction) { this.direction = Direction.fromString(direction); return this; }

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

            return new ProgressBar(this.texture, this.size, this.textureSize, this.uv, this.scale, this.color, this.direction);
        }

        public static Builder buildFromJson(IGui gui, JsonObject data) {
            Texture.Builder tb = Texture.Builder.buildFromJson(gui, data);
            Builder b = IBuilder.copyValues(tb, new Builder(tb.size));

            MiscUtils.accept(JsonUtils.getStringVal(data.get("direction")), b::direction);

            return b;
        }

        public static ProgressBar fromJson(IGui gui, JsonObject data) {
            return buildFromJson(gui, data).get(gui);
        }
    }

    public enum Direction
    {
        BOTTOM_TO_TOP,
        TOP_TO_BOTTOM,
        LEFT_TO_RIGHT,
        RIGHT_TO_LEFT;

        public static Direction fromString(String s) {
            return Direction.valueOf(s.toUpperCase(Locale.ROOT));
        }
    }
}
