////////////////////////////////////////////////////////////////////////////////
// This file is subject to the terms and conditions defined in the             /
// file '.github/LICENSE.md', which is part of this source code package.       /
////////////////////////////////////////////////////////////////////////////////

package de.sanandrew.mods.sanlib.lib.client.gui.element;

import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import org.apache.commons.lang3.Range;

import java.util.function.BiFunction;
import java.util.function.Function;

@SuppressWarnings({"unused", "UnusedReturnValue", "java:S1172"})
public class GroupBox
        extends ElementParent<String>
{
    public static final ResourceLocation ID = new ResourceLocation("group_box");

    private static final String LABEL = "label";
    private static final String[] RECTS = { "top_left", "top_right", "left", "right", "bottom" };

    protected int[] size;
    protected int   fThickness;
    protected int   color;

    public GroupBox(int[] size, int frameThickness, int color, Text label, IGui gui) {
        this.size = size;
        this.fThickness = frameThickness;
        this.color = color;

        this.put(LABEL, new GuiElementInst(new int[] { 4, 0 }, label).initialize(gui));
    }

    protected void setupLabel(GuiElementInst label) {
        GuiElementInst rectTR = this.get(RECTS[1]);
        int tw = label.get(Text.class).getWidth();

        rectTR.pos[0] = 6 + tw;
        rectTR.get(Rectangle.class).size[0] = this.size[0] - 6 - tw;
    }

    @Override
    public void setup(IGui gui, GuiElementInst inst) {
        GuiElementInst lbl = this.get(LABEL);

        int[][] coords = new int[][] {
                new int[] { 0,                              4,                              3,               this.fThickness },
                new int[] { 0,                              4,                              0,               this.fThickness }, // x and width calculated in setupLabel()
                new int[] { 0,                              this.size[1] - this.fThickness, this.size[0],    this.fThickness },
                new int[] { 0,                              4 + this.fThickness,            this.fThickness, this.size[1] - 4 - this.fThickness * 2 },
                new int[] { this.size[0] - this.fThickness, 4 + this.fThickness,            this.fThickness, this.size[1] - 4 - this.fThickness * 2 }
        };

        for( int i = 0; i < RECTS.length; i++ ) {
            Rectangle rc = new Rectangle.Builder(new int[] {coords[i][2], coords[i][3]}).color(this.color).get(gui);

            GuiElementInst ri = new GuiElementInst(new int[] { coords[i][0], coords[i][1] }, rc).initialize(gui);
            this.put(RECTS[i], ri);
        }

        super.setup(gui, inst);

        lbl.get(Text.class).setOnTextChange(this::setupLabel);

        this.setupLabel(lbl);
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
            implements IBuilder<GroupBox>
    {
        public final int[] size;

        protected int  fThickness;
        protected int  color;
        protected Text label;

        public Builder(int width, int height) {
            this(new int[] {width, height});
        }

        public Builder(int[] size) {
            this.size = size;
            this.color = 0x30000000;
            this.fThickness = 1;
        }

        public Builder color(int color)               { this.color = color;          return this; }
        public Builder frameThickness(int thickness)  { this.fThickness = thickness; return this; }
        public Builder label(Text txt)                { this.label = txt;     return this; }

        public Builder color(String color)        { return this.color(MiscUtils.hexToInt(color)); }

        @Override
        public void sanitize(IGui gui) {
            if( this.label == null ) {
                this.label = new Text.Builder(StringTextComponent.EMPTY).shadow(false).color(0x80000000).get(gui);
            }
        }

        @Override
        public GroupBox get(IGui gui) {
            this.sanitize(gui);

            return new GroupBox(this.size, this.fThickness, this.color, this.label, gui);
        }

        protected Text loadLabel(IGui gui, JsonObject lblData) {
            return Text.Builder.buildFromJson(gui, JsonUtils.addDefaultJsonProperty(lblData, "color", "0x80000000")).get(gui);
        }

        public static Builder buildFromJson(IGui gui, JsonObject data) {
            return buildFromJson(gui, data, b -> b::loadLabel);
        }

        public static Builder buildFromJson(IGui gui, JsonObject data, Function<Builder, BiFunction<IGui, JsonObject, Text>> loadLabelFunc) {
            Builder b = new Builder(JsonUtils.getIntArray(data.get("size"), Range.is(2)));

            JsonUtils.fetchString(data.get("frameColor"), b::color);
            JsonUtils.fetchInt(data.get("frameThickness"), b::frameThickness);

            b.label(MiscUtils.apply(loadLabelFunc, lcf -> MiscUtils.apply(lcf.apply(b), f -> f.apply(gui, data.getAsJsonObject("title")))));

            return b;
        }

        public static GroupBox fromJson(IGui gui, JsonObject data) {
            return buildFromJson(gui, data).get(gui);
        }
    }
}
