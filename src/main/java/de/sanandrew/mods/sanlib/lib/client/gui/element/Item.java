////////////////////////////////////////////////////////////////////////////////
// This file is subject to the terms and conditions defined in the             /
// file '.github/LICENSE.md', which is part of this source code package.       /
////////////////////////////////////////////////////////////////////////////////

package de.sanandrew.mods.sanlib.lib.client.gui.element;

import com.google.gson.JsonObject;
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

    public ItemStack stack = ItemStack.EMPTY;
    public double    scale;
    public int       size;

    @Override
    public void bakeData(IGui gui, JsonObject data, GuiElementInst inst) {
        this.stack = this.getBakedStack(gui, data);
        this.scale = JsonUtils.getDoubleVal(data.get("scale"), 1.0D);
        this.size = (int) Math.round(16.0D * this.scale);
    }

    @Override
    public void render(IGui gui, float partTicks, int x, int y, int mouseX, int mouseY, JsonObject data) {
        RenderUtils.renderStackInGui(this.getDynamicStack(gui), x, y, this.scale);
    }

    @Override
    public int getWidth() {
        return this.size;
    }

    @Override
    public int getHeight() {
        return this.size;
    }

    protected ItemStack getBakedStack(IGui gui, JsonObject data) {
        return JsonUtils.getItemStack(data.get("item"));
    }

    protected ItemStack getDynamicStack(IGui gui) {
        return this.stack;
    }
}
