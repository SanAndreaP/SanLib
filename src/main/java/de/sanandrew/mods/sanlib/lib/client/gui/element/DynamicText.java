package de.sanandrew.mods.sanlib.lib.client.gui.element;

import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import net.minecraft.util.ResourceLocation;

public class DynamicText
    extends Text
{
    public static final ResourceLocation ID = new ResourceLocation("dynamic_text");

    private String key;

    @Override
    public void bakeData(IGui gui, JsonObject data) {
        if( !(gui instanceof IGuiDynamicText) ) {
            throw new RuntimeException("Cannot use dynamic_text on a GUI which doesn't implement IGuiDynamicText");
        }

        super.bakeData(gui, data);

        if( this.key == null ) {
            this.key = JsonUtils.getStringVal(data.get("key"));
        }
    }

    @Override
    public String getDynamicText(IGui gui, String originalText) {
        return ((IGuiDynamicText) gui).getText(this.key, originalText);
    }

    public interface IGuiDynamicText
    {
        String getText(String key, String originalText);
    }
}
