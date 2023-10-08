/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2016-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.sanlib.lib.client.gui.element;

import com.google.gson.JsonObject;
import dev.sanandrea.mods.sanlib.lib.client.gui.GuiElementInst;
import dev.sanandrea.mods.sanlib.lib.client.gui.IGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import javax.annotation.Nonnull;
import java.util.Map;

public class ScreenTitle
    extends Text
{
    public static final ResourceLocation ID = new ResourceLocation("screen_title");

    @Nonnull
    protected ITextComponent guiText = StringTextComponent.EMPTY;

    public ScreenTitle(boolean shadow, int wrapWidth, int lineHeight, FontRenderer fontRenderer, Map<String, Integer> colors, boolean bordered) {
        super(StringTextComponent.EMPTY, shadow, wrapWidth, lineHeight, fontRenderer, colors, bordered);
    }

    @Override
    public void setup(IGui gui, GuiElementInst inst) {
        this.guiText = gui.get().getTitle();

        super.setup(gui, inst);
    }

    @Override
    public ITextComponent getDynamicText(IGui gui, ITextComponent originalText) {
        return this.guiText;
    }

    public static class Builder
            extends Text.Builder
    {
        public Builder() {
            super(StringTextComponent.EMPTY);
        }

        @Override
        public ScreenTitle get(IGui gui) {
            super.sanitize(gui);

            return new ScreenTitle(this.shadow, this.wrapWidth, this.lineHeight, this.fontRenderer, this.colors, this.bordered);
        }

        public static Builder buildFromJson(IGui gui, JsonObject data) {
            return IBuilder.copyValues(Text.Builder.buildFromJson(gui, data), new Builder());
        }

        public static ScreenTitle fromJson(IGui gui, JsonObject data) {
            return buildFromJson(gui, data).get(gui);
        }
    }
}
