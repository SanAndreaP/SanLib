package de.sanandrew.mods.sanlib.lib.client.gui;

import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.lib.ColorObj;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.Range;

public class ScrollAreaGuiElement
        implements IGuiElement
{
    BakedData data;

    @Override
    public void render(GuiScreen gui, float partTicks, int x, int y, int mouseX, int mouseY, JsonObject data) {
        if( this.data == null ) {
            this.data = new BakedData();
            this.data.texture = new ResourceLocation(data.get("texture").getAsString());
            this.data.size = JsonUtils.getIntArray(data.get("size"), Range.is(2));
            this.data.uv = JsonUtils.getIntArray(data.get("uv"), Range.is(2));
            this.data.margin = JsonUtils.getIntArray(data.get("size"), new int[] {0}, Range.between(1, 4));
            this.data.textureSize = JsonUtils.getIntArray(data.get("textureSize"), new int[] {256, 256}, Range.is(2));
            this.data.scale = JsonUtils.getDoubleArray(data.get("scale"), new double[] {1.0D, 1.0D}, Range.is(2));
            this.data.tintColor = new ColorObj(MiscUtils.hexToInt(JsonUtils.getStringVal(data.get("color"), "0xFFFFFFFF")));
            this.data.forceAlpha = JsonUtils.getBoolVal(data.get("forceAlpha"), false);
        }

    }

    @Override
    public int getHeight() {
        return 0;
    }

    private static final class BakedData
    {
        private ResourceLocation texture;
        private int[] uv;
        private int[] size;
        private int[] margin;
        private int[] scrollPos;
        private int[] scrollUV;
        private int[] scrollUVDisabled;
        private boolean rasterized;

        private IGuiElement[] elements;
    }
}
