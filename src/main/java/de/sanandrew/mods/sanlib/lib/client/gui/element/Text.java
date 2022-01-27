////////////////////////////////////////////////////////////////////////////////
// This file is subject to the terms and conditions defined in the             /
// file '.github/LICENSE.md', which is part of this source code package.       /
////////////////////////////////////////////////////////////////////////////////

package de.sanandrew.mods.sanlib.lib.client.gui.element;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.matrix.MatrixStack;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.IGuiElement;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.fonts.providers.DefaultGlyphProvider;
import net.minecraft.client.gui.fonts.providers.IGlyphProvider;
import net.minecraft.client.gui.fonts.providers.TextureGlyphProvider;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.commons.lang3.mutable.MutableInt;

import javax.annotation.Nonnull;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * JSON format:
 * <pre>
&#123;
  "type": "texture",              -- type of element: "text"
  "pos": [0, 0],                  -- relative position as [x, y] coordinates
  "data": &#123;
    "text": "sanlib.test"         -- The string to be rendered, either plain text or a language key (which will be translated when rendered) can be used
    "color": "0xFF000000",        -- the color (and transparency) of the text as hexadecimal number string (optional, default: "0xFF000000")
    "shadow": false,              -- Wether or not the text should cast a shadow (optional, default: false)
    "wrapWidth": 166,             -- Maximum width a text can have until it is wrapped to a new line, values smaller than 1 will disable wrapping (optional, default: 0)
    "font": &#123;                -- custom font (optional, default: "font": &#123; "texture": "standard" &#125;)
      "texture": "galactic",         -- either font name or custom texture resource location, font names can be ("standard" (regular MC font) or "galactic" (standard galactic alphabet))
      "unicode": true,               -- wether or not to use unicode (optional, default: dependent on MC settings)
      "bidirectional": false,        -- wether or not the Unicode Bidirectional Algorithm should be run before rendering any string (optional, default: dependent on MC settings)
    &#125;
  &#125;
&#125;
 * </pre>
 */

@SuppressWarnings({"unused", "java:S1172", "java:S1104", "UnusedReturnValue"})
public class Text
        implements IGuiElement
{
    public static final ResourceLocation ID = new ResourceLocation("text");
    protected static final String DEFAULT_COLOR = "default";

    @Nonnull
    protected final ITextComponent bakedText;
    protected int                  currColor;
    protected Map<String, Integer> colors;
    protected boolean              shadow;
    protected int                  wrapWidth;
    protected FontRenderer         fontRenderer;
    protected int                  lineHeight;

    protected int                    currWidth;
    protected int                    currHeight;
    protected GuiElementInst.Justify justify;

    private final List<ITextProperties> renderedLines = new ArrayList<>();
    private ITextComponent prevText;

    private Consumer<GuiElementInst> onTextChange;

    public Text(@Nonnull ITextComponent text, boolean shadow, int wrapWidth, int lineHeight, FontRenderer fontRenderer, Map<String, Integer> colors) {
        this.bakedText = text;
        this.shadow = shadow;
        this.wrapWidth = wrapWidth;
        this.lineHeight = lineHeight;
        this.fontRenderer = fontRenderer;
        this.colors = colors;
        this.currColor = colors.getOrDefault(DEFAULT_COLOR, 0xFF000000);
    }

    @Override
    public void setup(IGui gui, GuiElementInst inst) {
        this.justify = inst.getAlignmentH();

        this.buildLines(gui, inst);
    }

    public ITextComponent getDynamicText(IGui gui, ITextComponent originalText) {
        return originalText;
    }

    public void setOnTextChange(Consumer<GuiElementInst> onTextChange) {
        this.onTextChange = onTextChange;
    }

    protected void buildLines(IGui gui, GuiElementInst inst) {
        ITextComponent s = this.getDynamicText(gui, this.bakedText);
        if( !s.equals(this.prevText) ) {
            this.renderedLines.clear();

            this.renderedLines.addAll(Arrays.asList(this.fontRenderer.getSplitter()
                                                                     .splitLines(s, this.wrapWidth > 0 ? this.wrapWidth : Integer.MAX_VALUE, s.getStyle())
                                                                     .toArray(new ITextProperties[0])));

            this.currWidth = this.justify == GuiElementInst.Justify.JUSTIFY && this.wrapWidth > 0
                             ? this.wrapWidth
                             : this.renderedLines.stream().map(l -> this.fontRenderer.width(l)).max(Integer::compareTo).orElse(0);

            this.currHeight = Math.max(1, this.renderedLines.size()) * this.lineHeight;

            if( this.shadow ) {
                this.currHeight += 1;
            }
            this.currHeight -= 2;

            this.prevText = s;

            if( this.onTextChange != null ) {
                this.onTextChange.accept(inst);
            }
        }
    }

    @Override
    public void tick(IGui gui, GuiElementInst inst) {
        this.buildLines(gui, inst);
    }

    @Override
    public void render(IGui gui, MatrixStack stack, float partTicks, int x, int y, double mouseX, double mouseY, GuiElementInst inst) {
        for( ITextProperties sln : this.renderedLines ) {
            this.renderLine(stack, sln, x, y);
            y += this.lineHeight;
        }
    }

    private void renderLine(MatrixStack stack, ITextProperties s, int x, int y) {
        switch( this.justify ) {
            case JUSTIFY:
                if( this.wrapWidth > 0 ) {
                    this.renderLineJustified(stack, s, x, y);
                    return;
                }
                break;
            case LEFT:
                break;
            case CENTER:
                x += (this.currWidth - this.fontRenderer.width(s)) / 2;
                break;
            case RIGHT:
                x += this.currWidth - this.fontRenderer.width(s);
                break;
            default:
        }

        final MutableInt mx = new MutableInt(x);
        s.visit((style, str) -> {
            IReorderingProcessor irp = IReorderingProcessor.forward(str, style);
            if( this.shadow ) {
                this.fontRenderer.drawShadow(stack, irp, mx.getValue(), y, this.currColor);
            } else {
                this.fontRenderer.draw(stack, irp, mx.getValue(), y, this.currColor);
            }
            mx.add(this.fontRenderer.width(irp));

            return Optional.empty();
        }, Style.EMPTY);
    }

    private void renderLineJustified(MatrixStack stack, ITextProperties s, int x, int y) {
        final MutableInt mx = new MutableInt(x);
        s.visit((style, str) -> {
            String[] words = str.split("\\s");
            float spaceDist = this.wrapWidth;
            int[] wordWidths = new int[words.length];

            for( int i = 0; i < words.length; i++ ) {
                wordWidths[i] = this.fontRenderer.width(words[i]);
                spaceDist -= wordWidths[i];
            }

            spaceDist /= words.length - 1;

            for( int i = 0; i < words.length; i++ ) {
                IReorderingProcessor irp = IReorderingProcessor.forward(words[i], style);
                if( this.shadow ) {
                    this.fontRenderer.drawShadow(stack, irp, mx.getValue(), y, this.currColor);
                } else {
                    this.fontRenderer.draw(stack, irp, mx.getValue(), y, this.currColor);
                }
                mx.add(wordWidths[i] + spaceDist);
            }

            return Optional.empty();
        }, Style.EMPTY);
    }

    @Override
    public int getWidth() {
        return this.currWidth;
    }

    @Override
    public int getHeight() {
        return this.currHeight;
    }

    public void setColor(String colorId) {
        if( colorId == null || !this.colors.containsKey(colorId) ) {
            this.currColor = this.colors.getOrDefault(DEFAULT_COLOR, 0xFF000000);
        } else {
            this.currColor = this.colors.get(colorId);
        }
    }

    public static net.minecraft.client.gui.fonts.Font getMcFont(Minecraft mc, ResourceLocation rl) {
        return getMcFont(mc, rl, new DefaultGlyphProvider());
    }

    public static net.minecraft.client.gui.fonts.Font getMcFont(Minecraft mc, ResourceLocation rl, IGlyphProvider glyphProvider) {
        net.minecraft.client.gui.fonts.Font f = new net.minecraft.client.gui.fonts.Font(mc.textureManager, rl);
        f.reload(Lists.newArrayList(glyphProvider));

        return f;
    }

    @SuppressWarnings("WeakerAccess")
    public static final class Font
    {
        private String texture;

        private WeakReference<FontRenderer> frInst;

        private static FontRenderer frGalactic;

        @SuppressWarnings("unused")
        public Font() { }

        public Font(String tx) {
            this.texture = tx;
        }

        public FontRenderer get(Screen gui) {
            return this.get(gui, null);
        }

        @SuppressWarnings("java:S2696")
        public FontRenderer get(Screen gui, JsonObject glyphProvider) {
            if( "standard".equals(this.texture) ) {
                return gui.getMinecraft().font;
            } else if( "galactic".equals(this.texture) ) {
                if( frGalactic == null ) {
                    net.minecraft.client.gui.fonts.Font f = getMcFont(gui.getMinecraft(), Minecraft.ALT_FONT);
                    frGalactic = new FontRenderer(r -> f);
                }

                return frGalactic;
            } else {
                FontRenderer fr = this.frInst == null ? null : this.frInst.get();
                if( fr == null ) {
                    IGlyphProvider p = null;
                    if( glyphProvider != null ) {
                        p = TextureGlyphProvider.Factory.fromJson(glyphProvider).create(gui.getMinecraft().getResourceManager());
                    }

                    net.minecraft.client.gui.fonts.Font f = getMcFont(gui.getMinecraft(), new ResourceLocation(this.texture), p);
                    this.frInst = new WeakReference<>(new FontRenderer(r -> f));
                }

                return fr;
            }
        }

        @Override
        @SuppressWarnings("NonFinalFieldReferencedInHashCode")
        public int hashCode() {
            return Objects.hash(this.texture);
        }

        @Override
        public boolean equals(Object o) {
            if( this == o ) {
                return true;
            }

            if( o == null || this.getClass() != o.getClass() ) {
                return false;
            }

            Font font = (Font) o;
            return Objects.equals(this.texture, font.texture);
        }
    }

    public static class Builder
            implements IBuilder<Text>
    {
        public final ITextComponent text;

        protected boolean shadow = false;
        protected int wrapWidth = 0;
        protected int lineHeight = 9;
        protected FontRenderer fontRenderer = null;
        protected Map<String, Integer> colors = new HashMap<>();

        public Builder(ITextComponent text) {
            this.text = text;
        }

        public Builder shadow(boolean shadow)              { this.shadow = shadow;                                       return this; }
        public Builder wrapWidth(int wrapWidth)            { this.wrapWidth = wrapWidth;                                 return this; }
        public Builder lineHeight(int height)              { this.lineHeight = height;                                   return this; }
        public Builder font(FontRenderer fontRenderer)     { this.fontRenderer = fontRenderer;                           return this; }
        public Builder color(int color)                    { this.colors.clear(); this.colors.put(DEFAULT_COLOR, color); return this; }
        public Builder colors(Map<String, Integer> colors) { this.colors.clear(); this.colors.putAll(colors);            return this; }
        public Builder color(String key, int color)        { this.colors.put(key, color);                                return this; }

        public Builder font(IGui gui, Font font)                       { return this.font(font.get(gui.get())); }
        public Builder font(IGui gui, Font font, JsonObject glyphData) { return this.font(font.get(gui.get(), glyphData)); }

        @Override
        public void sanitize(IGui gui) {
            if( this.colors.isEmpty() ) {
                this.colors.put(DEFAULT_COLOR, 0xFF000000);
            } else {
                this.colors.computeIfAbsent(DEFAULT_COLOR, k -> this.colors.values().stream().findFirst().get());
            }

            if( this.fontRenderer == null ) {
                this.fontRenderer = gui.get().getMinecraft().font;
            }
        }

        @Override
        public Text get(IGui gui) {
            this.sanitize(gui);

            return new Text(this.text, this.shadow, this.wrapWidth, this.lineHeight, this.fontRenderer, this.colors);
        }

        public static Builder buildFromJson(IGui gui, JsonObject data) {
            Builder b = new Builder(data.has("text") ? new TranslationTextComponent(JsonUtils.getStringVal(data.get("text"))) : StringTextComponent.EMPTY);

            if( data.has("color") ) {
                JsonElement clr = data.get("color");
                if( clr.isJsonObject() ) {
                    for( Map.Entry<String, JsonElement> o : clr.getAsJsonObject().entrySet() ) {
                        b.color(o.getKey(), MiscUtils.hexToInt(o.getValue().getAsString()));
                    }
                } else if( clr.isJsonPrimitive() ) {
                    b.color(MiscUtils.hexToInt(clr.getAsString()));
                }
            }

            JsonUtils.fetchBool(data.get("shadow"), b::shadow);
            JsonUtils.fetchInt(data.get("wrapWidth"), b::wrapWidth);
            JsonUtils.fetchInt(data.get("lineHeight"), b::lineHeight);

            JsonElement cstFont = data.get("font");
            if( cstFont == null ) {
                b.font(gui, new Font("standard"));
            } else {
                b.font(gui, JsonUtils.GSON.fromJson(cstFont, Font.class), data.getAsJsonObject("glyphProvider"));
            }

            return b;
        }

        public static Text fromJson(IGui gui, JsonObject data) {
            return buildFromJson(gui, data).get(gui);
        }
    }
}
