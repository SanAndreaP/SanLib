package dev.sanandrea.mods.sanlib.lib.client.gui2.element;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.matrix.MatrixStack;
import dev.sanandrea.mods.sanlib.lib.client.gui2.GuiDefinition;
import dev.sanandrea.mods.sanlib.lib.client.gui2.GuiElement;
import dev.sanandrea.mods.sanlib.lib.client.gui2.IGui;
import net.minecraft.util.ResourceLocation;

public class Empty
        extends GuiElement
{
    public static final ResourceLocation ID = new ResourceLocation("empty");

    @Override
    public void render(IGui gui, MatrixStack matrixStack, int x, int y, double mouseX, double mouseY, float partialTicks) { /* no-op */ }

    @Override
    public void fromJson(IGui gui, GuiDefinition guiDef, JsonObject data) { /* no-op */ }
}
