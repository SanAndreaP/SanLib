////////////////////////////////////////////////////////////////////////////////
// This file is subject to the terms and conditions defined in the             /
// file '.github/LICENSE.md', which is part of this source code package.       /
////////////////////////////////////////////////////////////////////////////////

package de.sanandrew.mods.sanlib.lib.client.gui.element;

import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nonnull;
import java.util.Map;

@SuppressWarnings({"unused", "UnusedReturnValue", "java:S1172", "java:S1104"})
public class DynamicText
    extends Text
{
    public static final ResourceLocation ID = new ResourceLocation("dynamic_text");

    protected String key;

    public DynamicText(@Nonnull ITextComponent text, boolean shadow, int wrapWidth, int lineHeight, FontRenderer fontRenderer, Map<String, Integer> colors, String key) {
        super(text, shadow, wrapWidth, lineHeight, fontRenderer, colors);

        this.key = key;
    }

    @Override
    public ITextComponent getDynamicText(IGui gui, ITextComponent originalText) {
        return ((IGuiDynamicText) gui).getText(this.key, originalText);
    }

    public interface IGuiDynamicText
    {
        ITextComponent getText(String key, ITextComponent originalText);
    }

    public static class Builder
            extends Text.Builder
    {
        protected final String key;

        public Builder(ITextComponent text, String key) {
            super(text);

            this.key = key;
        }

        @Override
        public DynamicText get(IGui gui) {
            super.sanitize(gui);

            return new DynamicText(this.text, this.shadow, this.wrapWidth, this.lineHeight, this.fontRenderer, this.colors, this.key);
        }

        protected static Builder buildFromJson(IGui gui, JsonObject data) {
            Text.Builder sb = Text.Builder.buildFromJson(gui, data);

            return IBuilder.copyValues(sb, new Builder(sb.text, JsonUtils.getStringVal(data.get("key"))));
        }

        public static DynamicText fromJson(IGui gui, JsonObject data) {
            return buildFromJson(gui, data).get(gui);
        }
    }
}
