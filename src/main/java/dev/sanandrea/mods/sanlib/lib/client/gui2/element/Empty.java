package dev.sanandrea.mods.sanlib.lib.client.gui2.element;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.matrix.MatrixStack;
import dev.sanandrea.mods.sanlib.lib.client.gui2.GuiDefinition;
import dev.sanandrea.mods.sanlib.lib.client.gui2.GuiElement;
import dev.sanandrea.mods.sanlib.lib.client.gui2.IGui;
import dev.sanandrea.mods.sanlib.lib.util.UuidUtils;
import net.minecraft.util.ResourceLocation;

@SuppressWarnings("java:S6548")
public class Empty
        extends GuiElement
{
    public static final Empty INSTANCE = new Empty();
    public static final ResourceLocation ID = new ResourceLocation("empty");

    private Empty() {
        super(UuidUtils.EMPTY_UUID.toString());
    }

    @Override
    public void render(IGui gui, MatrixStack matrixStack, int x, int y, double mouseX, double mouseY, float partialTicks) { /* no-op */ }

    @Override
    public void fromJson(IGui gui, GuiDefinition guiDef, JsonObject data) { /* no-op */ }
}
