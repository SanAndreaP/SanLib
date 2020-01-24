package de.sanandrew.mods.sanlib.lib.client.gui.element;

import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.IGuiElement;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.Range;

@SuppressWarnings("WeakerAccess")
public class GroupBox
        implements IGuiElement
{
    public static final ResourceLocation ID = new ResourceLocation("group_box");

    protected int[]            size;
    protected GuiElementInst   text;
    protected GuiElementInst[] rects;

    private boolean visible = true;

    @Override
    public void bakeData(IGui gui, JsonObject data) {
        this.size = JsonUtils.getIntArray(data.get("size"), Range.is(2));
        String color = JsonUtils.getStringVal(data.get("frameColor"), "0x30000000");
        int thk = JsonUtils.getIntVal(data.get("frameThickness"), 1);

        this.text = new GuiElementInst();
        this.text.pos = new int[] { 4, 0 };
        this.text.element = new Text();
        this.text.data = data.getAsJsonObject("title");

        gui.getDefinition().initElement(this.text);
        JsonUtils.addDefaultJsonProperty(this.text.data, "color", "0x80000000");
        this.text.get().bakeData(gui, this.text.data);


        int tw = this.text.get(Text.class).getTextWidth(gui);
        int[][] coords = new int[][] {
                new int[] { 0,                  4,                  3,                     thk },
                new int[] { 6 + tw,             4,                  this.size[0] - 6 - tw, thk },
                new int[] { 0,                  this.size[1] - thk, this.size[0],          thk },
                new int[] { 0,                  4 + thk,            thk,                   this.size[1] - 4 - thk * 2 },
                new int[] { this.size[0] - thk, 4 + thk,            thk,                   this.size[1] - 4 - thk * 2 }
        };
        this.rects = new GuiElementInst[coords.length];

        for( int i = 0; i < coords.length; i++ ) {
            this.rects[i] = new GuiElementInst();
            this.rects[i].pos = new int[] { coords[i][0], coords[i][1] };
            this.rects[i].element = new Rectangle();

            gui.getDefinition().initElement(this.rects[i]);
            JsonUtils.addJsonProperty(this.rects[i].data, "size", new int[] { coords[i][2], coords[i][3] });
            JsonUtils.addJsonProperty(this.rects[i].data, "color", new String[] { color });
            this.rects[i].get().bakeData(gui, this.rects[i].data);
        }
    }

    @Override
    public void update(IGui gui, JsonObject data) {
        this.text.get().update(gui, this.text.data);
        for( GuiElementInst rect : rects ) {
            rect.get().update(gui, rect.data);
        }
    }

    @Override
    public void render(IGui gui, float partTicks, int x, int y, int mouseX, int mouseY, JsonObject data) {
        for( GuiElementInst rect : rects ) {
            rect.get().render(gui, partTicks, x + rect.pos[0], y + rect.pos[1], mouseX, mouseY, rect.data);
        }
        this.text.get().render(gui, partTicks, x + this.text.pos[0], y + this.text.pos[1], mouseX, mouseY, this.text.data);
    }

    @Override
    public int getWidth() {
        return this.size[0];
    }

    @Override
    public int getHeight() {
        return this.size[1];
    }

    @Override
    public boolean isVisible() {
        return this.visible;
    }

    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
