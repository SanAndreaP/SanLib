////////////////////////////////////////////////////////////////////////////////
// This file is subject to the terms and conditions defined in the             /
// file '.github/LICENSE.md', which is part of this source code package.       /
////////////////////////////////////////////////////////////////////////////////

package de.sanandrew.mods.sanlib.lib.client.gui.element;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.matrix.MatrixStack;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.IGuiElement;
import de.sanandrew.mods.sanlib.lib.client.util.RenderUtils;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

@SuppressWarnings({"unused", "UnusedReturnValue", "java:S1172", "java:S1104"})
public class Item
        implements IGuiElement
{
    public static final ResourceLocation ID = new ResourceLocation("item");

    protected ItemStack stack;
    protected float     scale;
    protected int       size;

    public Item(@Nonnull ItemStack stack, float scale) {
        this.stack = stack;
        this.scale = scale;
        this.size = Math.round(16.0F * this.scale);
    }

    @Override
    public void setup(IGui gui, GuiElementInst inst) {
        this.size = (int) Math.round(16.0D * this.scale);
    }

    @Override
    public void render(IGui gui, MatrixStack stack, float partTicks, int x, int y, double mouseX, double mouseY, GuiElementInst inst) {
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

    protected ItemStack getDynamicStack(IGui gui) {
        return this.stack;
    }

    public static class Builder
    {
        @Nonnull
        protected ItemStack item;
        protected float     scale = 0.0F;

        public Builder(@Nonnull ItemStack item) {
            this.item = item;
        }

        public Builder scale(float scale) { this.scale = scale; return this; }

        public void sanitize(IGui gui) {
            if( this.scale <= 0.0000001F ) {
                this.scale = 1.0F;
            }
        }

        public Item get(IGui gui) {
            this.sanitize(gui);

            return new Item(this.item, this.scale);
        }

        protected static Builder buildFromJson(IGui gui, JsonObject data) {
            Builder b = new Builder(Ingredient.fromJson(data.get("item")).getItems()[0]);

            JsonUtils.fetchFloat(data.get("scale"), b::scale);

            return b;
        }

        public static Item fromJson(IGui gui, JsonObject data) {
            return buildFromJson(gui, data).get(gui);
        }
    }
}
