package dev.sanandrea.mods.sanlib.lib.client.gui2.element;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.blaze3d.matrix.MatrixStack;
import dev.sanandrea.mods.sanlib.lib.client.gui2.GuiElement;
import dev.sanandrea.mods.sanlib.lib.client.gui2.IGui;
import dev.sanandrea.mods.sanlib.lib.util.JsonUtils;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class Rectangle
        extends GuiElement
{
    public static final ResourceLocation ID = new ResourceLocation("rectangle");

    protected final List<ColorDef> colors               = new ArrayList<>();
    protected boolean              isGradientHorizontal = false;

    @Override
    public void update(IGui gui) { /* no-op */ }

    @Override
    public void render(IGui gui, MatrixStack matrixStack, int x, int y, double mouseX, double mouseY, float partialTicks) { /* no-op */ }

    @Override
    public void fromJson(IGui gui, JsonObject data) {
        this.isGradientHorizontal = JsonUtils.getBoolVal(data.get("isGradientHorizontal"), false);

        JsonElement colorData = data.get("colors");
        if( colorData != null && !colorData.isJsonNull() ) {
            if( colorData.isJsonArray() ) {
                for( JsonElement color : colorData.getAsJsonArray() ) {
                    if( color.isJsonObject() ) {
                        this.colors.add(new ColorDef(color.getAsJsonObject()));
                    } else if( color.isJsonPrimitive() ) {
                        this.colors.add(new ColorDef(ColorDef.colorFromJson(color)));
                    } else {
                        throw new JsonParseException("color in 'colors' array is an invalid type");
                    }
                }
            } else {
                throw new JsonParseException("'colors' value must be an array");
            }
        } else {
            this.colors.add(new ColorDef(ColorDef.colorFromJson(data.get("color"))));
        }
    }

}
