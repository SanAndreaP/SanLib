package de.sanandrew.mods.sanlib.lib.client.gui.element;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.matrix.MatrixStack;
import de.sanandrew.mods.sanlib.lib.ColorObj;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.Range;

@SuppressWarnings("unused")
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
        protected void sanitize(IGui gui) {
            super.sanitize(gui);

            if( this.uvDisabled == null ) {
                this.uvDisabled = new int[] {0, 0};
            }
        }

        public Builder uvDisabled(int u, int v) { this.uvDisabled = new int[] { u, v }; return this; }
        public Builder uvDisabled(int[] uv)     { this.uvDisabled = uv;                return this; }

        @Override
        public ScrollButton get(IGui gui) {
            this.sanitize(gui);

            return new ScrollButton(this.texture, this.size, this.textureSize, this.uv, this.uvDisabled, this.scale, this.color);
        }

        protected static Builder buildFromJson(IGui gui, JsonObject data) {
            Texture.Builder          tb = Texture.Builder.buildFromJson(gui, data);
            Builder b  = new Builder(tb.size).uvDisabled(JsonUtils.getIntArray(data.get("uvDisabled"), Range.is(2)));

            b.texture = tb.texture;
            b.textureSize = tb.textureSize;
            b.uv = tb.uv;
            b.scale = tb.scale;
            b.color = tb.color;

            return b;
        }

        public static ScrollButton fromJson(IGui gui, JsonObject data) {
            return buildFromJson(gui, data).get(gui);
        }
    }
}
