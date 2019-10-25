package de.sanandrew.mods.sanlib.lib.client.gui.element;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.lib.ColorObj;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.IGuiElement;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.Range;

public class TextField
        implements IGuiElement
{
    public static final ResourceLocation ID = new ResourceLocation("textfield");

    public BakedData data;

    @Override
    public void bakeData(IGui gui, JsonObject data) {
        if( this.data == null ) {
            this.data = new BakedData();
            this.data.size = JsonUtils.getIntArray(data.get("size"), Range.is(2));
            this.data.backgroundColor = MiscUtils.hexToInt(JsonUtils.getStringVal(data.get("backgroundColor"), "0xFF000000"));
            this.data.borderColor = MiscUtils.hexToInt(JsonUtils.getStringVal(data.get("borderColor"), "0xFF808080"));
            JsonElement lbl = data.get("text");
            if( lbl != null ) {
                this.data.text = JsonUtils.GSON.fromJson(lbl, GuiElementInst.class);
                this.data.text.get().bakeData(gui, this.data.text.data);
            }
        }
    }

    @Override
    public void render(IGui gui, float partTicks, int x, int y, int mouseX, int mouseY, JsonObject data) {

    }

    @Override
    public int getWidth() {
        return 0;
    }

    @Override
    public int getHeight() {
        return 0;
    }

    public static final class BakedData
    {
        public int[] size;
        public int borderColor;
        public int backgroundColor;

        public GuiElementInst text;
    }
}
