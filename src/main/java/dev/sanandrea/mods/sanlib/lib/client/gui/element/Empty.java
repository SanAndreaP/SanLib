package dev.sanandrea.mods.sanlib.lib.client.gui.element;

import com.google.gson.JsonObject;
import dev.sanandrea.mods.sanlib.lib.client.gui.GuiDefinition;
import dev.sanandrea.mods.sanlib.lib.client.gui.GuiElement;
import dev.sanandrea.mods.sanlib.lib.client.gui.IGui;
import dev.sanandrea.mods.sanlib.lib.util.UuidUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

@SuppressWarnings("java:S6548")
public class Empty
        extends GuiElement
{
    public static final Empty            INSTANCE = new Empty();
    public static final ResourceLocation ID       = ResourceLocation.withDefaultNamespace("empty");

    private Empty() {
        super(UuidUtils.EMPTY_UUID.toString());
    }

    @Override
    public void render(IGui gui, GuiGraphics graphics, int x, int y, double mouseX, double mouseY, float partialTicks) { /* no-op */ }

    @Override
    public void fromJson(IGui gui, GuiDefinition guiDef, JsonObject data) { /* no-op */ }
}
