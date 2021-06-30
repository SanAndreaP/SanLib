////////////////////////////////////////////////////////////////////////////////
// This file is subject to the terms and conditions defined in the             /
// file '.github/LICENSE.md', which is part of this source code package.       /
////////////////////////////////////////////////////////////////////////////////

package de.sanandrew.mods.sanlib.lib.client.gui.element;

import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.Range;

import java.util.Map;

public class GroupBox
        extends ElementParent<String>
{
    public static final ResourceLocation ID = new ResourceLocation("group_box");

    private static final String LABEL = "label";
    private static final String[] RECTS = { "top_left", "top_right", "left", "right", "bottom" };

    protected int[] size;

    @Override
    public void buildChildren(IGui gui, JsonObject data, Map<String, GuiElementInst> listToBuild) {
        String color = JsonUtils.getStringVal(data.get("frameColor"), "0x30000000");
        int thk = JsonUtils.getIntVal(data.get("frameThickness"), 1);

        GuiElementInst label = new GuiElementInst(new int[] { 4, 0 }, new Text(), data.getAsJsonObject("title")).initialize(gui);
        listToBuild.put(LABEL, label);

        int[][] coords = new int[][] {
                new int[] { 0,                  4,                  3,            thk },
                new int[] { 0,                  4,                  0,            thk }, // x and width calculated in update()
                new int[] { 0,                  this.size[1] - thk, this.size[0], thk },
                new int[] { 0,                  4 + thk,            thk,          this.size[1] - 4 - thk * 2 },
                new int[] { this.size[0] - thk, 4 + thk,            thk,          this.size[1] - 4 - thk * 2 }
        };
        JsonUtils.addDefaultJsonProperty(label.data, "color", "0x80000000");
        for( int i = 0; i < RECTS.length; i++ ) {
            GuiElementInst ri = new GuiElementInst(new int[] { coords[i][0], coords[i][1] }, new Rectangle()).initialize(gui);
            JsonUtils.addJsonProperty(ri.data, "size", new int[] { coords[i][2], coords[i][3] });
            JsonUtils.addJsonProperty(ri.data, "color", new String[] { color });
            listToBuild.put(RECTS[i], ri);
        }
    }

    @Override
    public void bakeData(IGui gui, JsonObject data, GuiElementInst inst) {
        this.size = JsonUtils.getIntArray(data.get("size"), Range.is(2));

        super.bakeData(gui, data, inst);
    }

    @Override
    public void tick(IGui gui, JsonObject data) {
        GuiElementInst label = this.getChild(LABEL);
        GuiElementInst rectTR = this.getChild(RECTS[1]);
        int tw = label.get(Text.class).getTextWidth(gui);

        rectTR.pos[0] = 6 + tw;
        rectTR.get(Rectangle.class).size[0] = this.size[0] - 6 - tw;

        super.tick(gui, data);
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
