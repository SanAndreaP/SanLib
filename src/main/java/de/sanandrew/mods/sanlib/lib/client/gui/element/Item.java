////////////////////////////////////////////////////////////////////////////////
// This file is subject to the terms and conditions defined in the             /
// file '.github/LICENSE.md', which is part of this source code package.       /
////////////////////////////////////////////////////////////////////////////////

package de.sanandrew.mods.sanlib.lib.client.gui.element;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.IGuiElement;
import de.sanandrew.mods.sanlib.lib.client.util.RenderUtils;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

@SuppressWarnings({ "WeakerAccess", "unused" })
public class Item
        implements IGuiElement
{
    public static final ResourceLocation ID = new ResourceLocation("item");

    public ItemStack item = ItemStack.EMPTY;
    public float     scale;
    public int       size;

    @Override
    public void bakeData(IGui gui, JsonObject data, GuiElementInst inst) {
        this.item = this.getBakedItem(gui, data);
        this.scale = JsonUtils.getFloatVal(data.get("scale"), 1.0F);
        this.size = (int) Math.round(16.0D * this.scale);
    }

    @Override
    public void render(IGui gui, MatrixStack stack, float partTicks, int x, int y, double mouseX, double mouseY, JsonObject data) {
        RenderUtils.renderStackInGui(this.getDynamicStack(gui), stack, x, y, this.scale);
    }

    @Override
    public int getWidth() {
        return this.size;
    }

    @Override
    public int getHeight() {
        return this.size;
    }

    protected ItemStack getBakedItem(IGui gui, JsonObject data) {
        return JsonUtils.getItemStack(data.get("item"));
    }

    protected ItemStack getDynamicStack(IGui gui) {
        return this.item;
    }
}
