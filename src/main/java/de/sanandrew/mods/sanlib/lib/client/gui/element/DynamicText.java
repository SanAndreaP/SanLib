////////////////////////////////////////////////////////////////////////////////
// This file is subject to the terms and conditions defined in the             /
// file '.github/LICENSE.md', which is part of this source code package.       /
////////////////////////////////////////////////////////////////////////////////

package de.sanandrew.mods.sanlib.lib.client.gui.element;

import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.function.BiFunction;

@SuppressWarnings("unused")
public class DynamicText
    extends Text
{
    public static final ResourceLocation ID = new ResourceLocation("dynamic_text");

    @Nonnull
    protected BiFunction<IGui, ITextComponent, ITextComponent> textFunc = super::getDynamicText;

    public DynamicText(@Nonnull ITextComponent text, boolean shadow, int wrapWidth, int lineHeight, FontRenderer fontRenderer, Map<String, Integer> colors) {
        super(text, shadow, wrapWidth, lineHeight, fontRenderer, colors);
    }

    public void setTextFunc(@Nonnull BiFunction<IGui, ITextComponent, ITextComponent> func) {
        this.textFunc = func;
    }

    @Override
    public ITextComponent getDynamicText(IGui gui, ITextComponent originalText) {
        return this.textFunc.apply(gui, originalText);
    }

    public static class Builder
            extends Text.Builder
    {
        public Builder(ITextComponent text) {
            super(text);
        }

        @Override
        public DynamicText get(IGui gui) {
            this.sanitize(gui);

            return new DynamicText(this.text, this.shadow, this.wrapWidth, this.lineHeight, this.fontRenderer, this.colors);
        }

        public static Builder buildFromJson(IGui gui, JsonObject data) {
            Text.Builder sb = Text.Builder.buildFromJson(gui, data);
            return IBuilder.copyValues(sb, new Builder(sb.text));
        }

        public static DynamicText fromJson(IGui gui, JsonObject data) {
            return buildFromJson(gui, data).get(gui);
        }
    }
}
