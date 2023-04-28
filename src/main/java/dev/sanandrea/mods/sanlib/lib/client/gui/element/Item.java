/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2016-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.sanlib.lib.client.gui.element;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.sanandrea.mods.sanlib.lib.client.gui.GuiElementInst;
import dev.sanandrea.mods.sanlib.lib.client.gui.IGui;
import dev.sanandrea.mods.sanlib.lib.client.gui.IGuiElement;
import dev.sanandrea.mods.sanlib.lib.client.util.RenderUtils;
import dev.sanandrea.mods.sanlib.lib.util.JsonUtils;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.Locale;
import java.util.function.BiFunction;
import java.util.function.Supplier;

@SuppressWarnings({"unused", "UnusedReturnValue", "java:S1172", "java:S1104"})
public class Item
        implements IGuiElement
{
    public static final ResourceLocation ID = new ResourceLocation("item");

    protected ItemStack item;
    protected float     scale;
    protected int           size;
    protected MouseOverType mouseOverType;
    protected FontRenderer overlayFont;

    private   Supplier<ItemStack> itemSupplier = () -> item;

    private ItemStack cachedItem;

    public Item(@Nonnull ItemStack item, float scale) {
        this(item, scale, MouseOverType.NONE);
    }

    public Item(@Nonnull ItemStack item, float scale, MouseOverType mouseOverType) {
        this(item, scale, mouseOverType, null);
    }

    public Item(@Nonnull ItemStack item, float scale, MouseOverType mouseOverType, FontRenderer overlayFont) {
        this.item = item;
        this.cachedItem = item;
        this.scale = scale;
        this.size = Math.round(16.0F * this.scale);
        this.mouseOverType = mouseOverType;
        this.overlayFont = overlayFont;
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
        RenderUtils.renderGuiItem(this.getDynamicStack(gui), stack, x, y, this.scale, this.overlayFont);

        if( this.mouseOverType.isHovering(gui, x, y, mouseX, mouseY, this.size, this.size) ) {
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

        protected float         scale         = 1.0F;
        protected MouseOverType mouseOverType = MouseOverType.NONE;
        protected FontRenderer fontRenderer = null;

        public Builder(@Nonnull ItemStack item) {
            this.item = item;
        }

        public Builder scale(float scale)                 { this.scale = scale;                                       return this; }
        public Builder mouseOver(MouseOverType mouseOver) { this.mouseOverType = mouseOver;                           return this; }
        public Builder mouseOver(String mouseOver)        { this.mouseOverType = MouseOverType.fromString(mouseOver); return this; }
        public Builder font(FontRenderer fontRenderer)    { this.fontRenderer = fontRenderer;                         return this; }
        public Builder font(IGui gui, Text.Font font)                       { return this.font(font.get(gui.get())); }
        public Builder font(IGui gui, Text.Font font, JsonObject glyphData) { return this.font(font.get(gui.get(), glyphData)); }

        @Override
        public void sanitize(IGui gui) {
            if( this.scale <= 0.0000001F ) {
                this.scale = 1.0F;
            }
        }

        @Override
        public Item get(IGui gui) {
            this.sanitize(gui);

            return new Item(this.item, this.scale, this.mouseOverType, this.fontRenderer);
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
            JsonUtils.fetchBool(data.get("doMouseOver"), dmo ->  b.mouseOver(Boolean.TRUE.equals(dmo) ? MouseOverType.VANILLA : MouseOverType.NONE));
            JsonUtils.fetchString(data.get("mouseOverType"), b::mouseOver);

            JsonElement cstFont = data.get("overlayFont");
            if( cstFont != null ) {
                if( cstFont.isJsonPrimitive() ) {
                    b.font(gui, new Text.Font(cstFont.getAsString()));
                } else {
                    b.font(gui, JsonUtils.GSON.fromJson(cstFont, Text.Font.class), data.getAsJsonObject("glyphProvider"));
                }
            }

            return b;
        }

        public static Item fromJson(IGui gui, JsonObject data) {
            return buildFromJson(gui, data).get(gui);
        }
    }

    public enum MouseOverType
    {
        NONE(),
        EXACT(0),
        VANILLA(1);

        private final int border;

        MouseOverType() {
            this(0);
        }

        MouseOverType(int border) {
            this.border = border;
        }

        public static MouseOverType fromString(String s) {
            return MouseOverType.valueOf(s.toUpperCase(Locale.ROOT));
        }

        public boolean isHovering(IGui gui, int x, int y, double mouseX, double mouseY, int width, int height) {
            return this != NONE && IGuiElement.isHovering(gui, x - border, y - border, mouseX, mouseY, width + border * 2, height + border * 2);
        }
    }
}
