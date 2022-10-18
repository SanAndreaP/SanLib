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
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.function.BiFunction;
import java.util.function.Supplier;

@SuppressWarnings({"unused", "UnusedReturnValue", "java:S1172", "java:S1104"})
public class Item
        implements IGuiElement
{
    public static final ResourceLocation ID = new ResourceLocation("item");

    protected ItemStack item;
    protected float     scale;
    protected int       size;
    protected boolean doMouseOver;

    private   Supplier<ItemStack> itemSupplier = () -> item;

    private ItemStack cachedItem;

    public Item(@Nonnull ItemStack item, float scale) {
        this(item, scale, false);
    }

    public Item(@Nonnull ItemStack item, float scale, boolean doMouseOver) {
        this.item = item;
        this.cachedItem = item;
        this.scale = scale;
        this.size = Math.round(16.0F * this.scale);
        this.doMouseOver = doMouseOver;
    }

    public void setItemSupplier(@Nonnull Supplier<ItemStack> supplier) {
        this.itemSupplier = supplier;
    }

    @Override
    public void setup(IGui gui, GuiElementInst inst) {
        this.size = (int) Math.round(16.0D * this.scale);
        this.cachedItem = this.itemSupplier.get();
    }

    @Override
    public void tick(IGui gui, GuiElementInst inst) {
        this.cachedItem = this.itemSupplier.get();
    }

    @Override
    public void render(IGui gui, MatrixStack stack, float partTicks, int x, int y, double mouseX, double mouseY, GuiElementInst inst) {
        RenderUtils.renderStackInGui(this.getDynamicStack(gui), stack, x, y, this.scale);

        if( doMouseOver && IGuiElement.isHovering(gui, x, y, mouseX, mouseY, this.size, this.size) ) {
            RenderSystem.disableDepthTest();
            RenderSystem.colorMask(true, true, true, false);
            AbstractGui.fill(stack, x, y, x + this.size, y + this.size, 0x80FFFFFF);
            RenderSystem.colorMask(true, true, true, true);
            RenderSystem.enableDepthTest();
            RenderSystem.enableBlend();
        }
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
        return this.cachedItem;
    }

    public static class Builder
            implements IBuilder<Item>
    {
        @Nonnull
        public final ItemStack item;

        protected float scale = 1.0F;
        protected boolean doMouseOver = false;

        public Builder(@Nonnull ItemStack item) {
            this.item = item;
        }

        public Builder scale(float scale) { this.scale = scale; return this; }
        public Builder doMouseOver(boolean doMouseOver) { this.doMouseOver = doMouseOver; return this; }

        @Override
        public void sanitize(IGui gui) {
            if( this.scale <= 0.0000001F ) {
                this.scale = 1.0F;
            }
        }

        @Override
        public Item get(IGui gui) {
            this.sanitize(gui);

            return new Item(this.item, this.scale, this.doMouseOver);
        }

        protected static ItemStack loadItem(IGui gui, JsonObject data) {
            return data.has("item") ? Ingredient.fromJson(data.get("item")).getItems()[0] : null;
        }

        public static Builder buildFromJson(IGui gui, JsonObject data) {
            return buildFromJson(gui, data, Builder::loadItem);
        }

        public static Builder buildFromJson(IGui gui, JsonObject data, BiFunction<IGui, JsonObject, ItemStack> loadItemFunc) {
            Builder b = new Builder(loadItemFunc.apply(gui, data));

            JsonUtils.fetchFloat(data.get("scale"), b::scale);
            JsonUtils.fetchBool(data.get("doMouseOver"), b::doMouseOver);

            return b;
        }

        public static Item fromJson(IGui gui, JsonObject data) {
            return buildFromJson(gui, data).get(gui);
        }
    }
}
