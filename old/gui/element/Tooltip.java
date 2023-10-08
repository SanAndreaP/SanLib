/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2016-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.sanlib.lib.client.gui.element;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.blaze3d.matrix.MatrixStack;
import dev.sanandrea.mods.sanlib.lib.client.gui.GuiDefinition;
import dev.sanandrea.mods.sanlib.lib.client.gui.GuiElementInst;
import dev.sanandrea.mods.sanlib.lib.client.gui.IGui;
import dev.sanandrea.mods.sanlib.lib.client.gui.IGuiElement;
import dev.sanandrea.mods.sanlib.lib.client.util.GuiUtils;
import dev.sanandrea.mods.sanlib.lib.util.JsonUtils;
import dev.sanandrea.mods.sanlib.lib.util.MiscUtils;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.commons.lang3.Range;

import javax.annotation.Nonnull;
import java.util.function.BiFunction;
import java.util.function.Function;

@SuppressWarnings({"unused", "UnusedReturnValue", "java:S1172", "java:S1104"})
public class Tooltip
        extends ElementParent<String>
{
    public static final ResourceLocation ID = new ResourceLocation("tooltip");

    public static final String CONTENT = "content";

    protected int[] mouseOverSize;
    protected int   backgroundColor;
    protected int borderTopColor;
    protected int borderBottomColor;
    protected int[] padding; // top - right - bottom - left

    protected String visibleForId;
    protected GuiElementInst visibleFor;

    public Tooltip(int[] mouseOverSize, int backgroundColor, int borderTopColor, int borderBottomColor, int[] padding, String visibleForId, GuiElementInst content) {
        this.mouseOverSize = mouseOverSize;
        this.backgroundColor = backgroundColor;
        this.borderTopColor = borderTopColor;
        this.borderBottomColor = borderBottomColor;
        this.padding = adjustPadding(padding);
        this.visibleForId = visibleForId;

        this.add(content);
    }

    @Override
    public void put(String id, @Nonnull GuiElementInst child) {
        super.put(CONTENT, child);
    }

    public void add(@Nonnull GuiElementInst child) {
        super.put(CONTENT, child);
    }

    @Override
    public void setup(IGui gui, GuiElementInst inst) {
        this.visibleFor = this.visibleForId != null ? gui.getDefinition().getElementById(this.visibleForId) : null;

        super.setup(gui, inst);
    }

    @Override
    public void render(IGui gui, MatrixStack stack, float partTicks, int x, int y, double mouseX, double mouseY, GuiElementInst inst) {
        if( IGuiElement.isHovering(gui, x, y, mouseX, mouseY, this.mouseOverSize[0], this.mouseOverSize[1]) ) {
            double locMouseX = mouseX - gui.getScreenPosX();
            double locMouseY = mouseY - gui.getScreenPosY();
            int xPos = (int) locMouseX + 12;
            int yPos = (int) locMouseY - 12;

            GuiElementInst contentInst = this.get(CONTENT);
            IGuiElement contentElem = contentInst.get();

            contentElem.renderTick(gui, stack, partTicks, xPos, yPos, mouseX, mouseY, contentInst);

            int width = contentElem.getWidth() + this.padding[3] + this.padding[1];
            int height = contentElem.getHeight() + this.padding[0] + this.padding[2];

            if( mouseX + width + 16 > gui.get().width ) {
                xPos -= width + 28;
            }

            stack.pushPose();
            stack.translate(0.0D, 0.0D, 400.0D);
            AbstractGui.fill(stack, xPos - 1,         yPos - 2,          xPos + width + 1, yPos - 1,          this.backgroundColor); // top
            AbstractGui.fill(stack, xPos - 1,         yPos + height + 1, xPos + width + 1, yPos + height + 2, this.backgroundColor); // bottom
            AbstractGui.fill(stack, xPos - 1,         yPos - 1,          xPos + width + 1, yPos + height + 1, this.backgroundColor); // center
            AbstractGui.fill(stack, xPos - 2,         yPos - 1,          xPos - 1,         yPos + height + 1, this.backgroundColor); // left
            AbstractGui.fill(stack, xPos + width + 1, yPos - 1,          xPos + width + 2, yPos + height + 1, this.backgroundColor); // right

            AbstractGui.fill(stack, xPos - 1, yPos - 1,      xPos + width + 1, yPos,              this.borderTopColor);
            AbstractGui.fill(stack, xPos - 1, yPos + height, xPos + width + 1, yPos + height + 1, this.borderBottomColor);

            GuiUtils.drawGradient(stack, xPos - 1, yPos, 1, height, this.borderTopColor, this.borderBottomColor, true);
            GuiUtils.drawGradient(stack, xPos + width, yPos, 1, height, this.borderTopColor, this.borderBottomColor, true);

            GuiDefinition.renderElement(gui, stack, xPos + this.padding[0], yPos + this.padding[3], mouseX, mouseY, partTicks, contentInst, false);

            stack.popPose();
        }
    }

    @Override
    public int getWidth() {
        return 0;
    }

    @Override
    public int getHeight() {
        return 0;
    }

    @Override
    public boolean isVisible() {
        return (this.visibleFor == null && this.visibleForId == null) || (this.visibleFor != null && this.visibleFor.isVisible());
    }

    public static class Builder
            implements IGuiElement.IBuilder<Tooltip>
    {
        public final int[] mouseOverSize;

        protected int   backgroundColor;
        protected int   borderTopColor;
        protected int   borderBottomColor;
        protected int[] padding;

        protected String visibleForId;

        protected GuiElementInst content;

        public Builder(int[] mouseOverSize) {
            this.mouseOverSize = mouseOverSize;

            this.backgroundColor = 0xF0100010;
            this.borderTopColor = 0x505000FF;
            this.borderBottomColor = 0x5028007F;
        }

        public Builder backgroundColor(int color)      { this.backgroundColor = color;                       return this; }
        public Builder backgroundColor(String color)   { this.backgroundColor = MiscUtils.hexToInt(color);   return this; }
        public Builder borderTopColor(int color)       { this.borderTopColor = color;                        return this; }
        public Builder borderTopColor(String color)    { this.borderTopColor = MiscUtils.hexToInt(color);    return this; }
        public Builder borderBottomColor(int color)    { this.borderBottomColor = color;                     return this; }
        public Builder borderBottomColor(String color) { this.borderBottomColor = MiscUtils.hexToInt(color); return this; }
        public Builder padding(int[] padding)          { this.padding = padding;                             return this; }
        public Builder visibleFor(String id)           { this.visibleForId = id;                             return this; }
        public Builder content(GuiElementInst content) { this.content = content;                             return this; }

        public Builder padding(int all)                                  { return this.padding(new int[] {all}); }
        public Builder padding(int topBottom, int leftRight)             { return this.padding(new int[] {topBottom, leftRight}); }
        public Builder padding(int top, int leftRight, int bottom)       { return this.padding(new int[] {top, leftRight, bottom}); }
        public Builder padding(int top, int right, int bottom, int left) { return this.padding(new int[] {top, right, bottom, left}); }

        @Override
        public void sanitize(IGui gui) {
            if( this.padding == null ) {
                this.padding = new int[] {2};
            }
        }

        @Override
        public Tooltip get(IGui gui) {
            this.sanitize(gui);

            return new Tooltip(this.mouseOverSize, this.backgroundColor, this.borderTopColor, this.borderBottomColor, this.padding, this.visibleForId,
                               this.content.initialize(gui));
        }

        protected GuiElementInst loadContent(IGui gui, JsonObject data) {
            if( data.has(CONTENT) ) {
                return JsonUtils.GSON.fromJson(data.get(CONTENT), GuiElementInst.class);
            } else if( data.has("text") ) {
                return new GuiElementInst(new Text.Builder(new TranslationTextComponent(data.get("text").getAsString())).color(0xFFFFFFFF).shadow(true).get(gui));
            } else {
                throw new JsonParseException("No data property called \"content\" or \"text\" has been found.");
            }
        }

        public static Builder buildFromJson(IGui gui, JsonObject data) {
            return buildFromJson(gui, data, b -> b::loadContent);
        }

        public static Builder buildFromJson(IGui gui, JsonObject data, Function<Builder, BiFunction<IGui, JsonObject, GuiElementInst>> loadContentFunc) {
            Builder b = new Builder(JsonUtils.getIntArray(data.get("size"), Range.is(2)));

            JsonUtils.fetchString(data.get("backgroundColor"), b::backgroundColor);
            JsonUtils.fetchString(data.get("borderTopColor"), b::borderTopColor);
            JsonUtils.fetchString(data.get("borderBottomColor"), b::borderBottomColor);
            JsonUtils.fetchIntArray(data.get("padding"), b::padding, Range.between(0, 4));
            JsonUtils.fetchString(data.get("for"), b::visibleFor);

            GuiElementInst content = MiscUtils.apply(loadContentFunc, lcf -> MiscUtils.apply(lcf.apply(b), f -> f.apply(gui, data)));
            if( content != null ) {
                b.content(content);
            }

            return b;
        }

        public static Tooltip fromJson(IGui gui, JsonObject data) {
            return buildFromJson(gui, data).get(gui);
        }
    }
}
