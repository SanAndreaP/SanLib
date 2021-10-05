package de.sanandrew.mods.sanlib.lib.client.gui.element;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.matrix.MatrixStack;
import de.sanandrew.mods.sanlib.lib.ColorObj;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.Range;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public class ScrollButton
        extends Texture
{
    protected int[]   uvDisabled;
    protected boolean disabled;

    public ScrollButton(ResourceLocation txLocation, int[] size, int[] textureSize, int[] uv, int[] uvDisabled, float[] scale, ColorObj color) {
        super(txLocation, size, textureSize, uv, scale, color);

        this.uvDisabled = uvDisabled;
    }

    @Override
    protected void drawRect(IGui gui, MatrixStack stack) {
        int[] uv = this.disabled ? this.uvDisabled : this.uv;
        AbstractGui.blit(stack, 0, 0, uv[0], uv[1], size[0], size[1], textureSize[0], textureSize[1]);
    }

    public static class Builder
            extends Texture.Builder
    {
        protected int[] uvDisabled;

        public Builder(int[] size) {
            super(size);
        }

        @Override
        public void sanitize(IGui gui) {
            super.sanitize(gui);

            if( this.uvDisabled == null ) {
                this.uvDisabled = new int[] {0, 0};
            }
        }

        public Builder uvDisabled(int u, int v) { this.uvDisabled = new int[] { u, v }; return this; }
        public Builder uvDisabled(int[] uv)     { this.uvDisabled = uv;                 return this; }

        @Override
        public ScrollButton get(IGui gui) {
            this.sanitize(gui);

            return new ScrollButton(this.texture, this.size, this.textureSize, this.uv, this.uvDisabled, this.scale, this.color);
        }

        public static Builder buildFromJson(IGui gui, JsonObject data) {
            Texture.Builder tb = Texture.Builder.buildFromJson(gui, data);
            Builder         b  = IBuilder.copyValues(tb, new Builder(tb.size));

            JsonUtils.fetchIntArray(data.get("uvDisabled"), b::uvDisabled, Range.is(2));

            return b;
        }

        public static ScrollButton fromJson(IGui gui, JsonObject data) {
            return buildFromJson(gui, data).get(gui);
        }
    }
}
