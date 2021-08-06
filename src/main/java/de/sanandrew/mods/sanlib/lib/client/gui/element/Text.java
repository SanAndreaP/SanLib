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
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
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
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.commons.lang3.mutable.MutableInt;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

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

@SuppressWarnings("java:S1104")
public class Text
        implements IGuiElement
{
    public static final ResourceLocation ID = new ResourceLocation("text");
    
    public ITextComponent bakedText;
    public int            color;
    public Map<String, Integer> colors;
    public boolean              shadow;
    public int                  wrapWidth;
    public FontRenderer         fontRenderer;
    public int                  lineHeight;

    protected String                 defaultColor;
    protected int                    currWidth;
    protected int                    currHeight;
    protected GuiElementInst.Justify justify;

    private final List<ITextProperties> renderedLines = new ArrayList<>();

    private static final String DEFAULT_COLOR = "default";

    @Override
    public void bakeData(IGui gui, JsonObject data, GuiElementInst inst) {
        this.justify = inst.getAlignmentH();

        this.colors = new HashMap<>();
        this.defaultColor = null;
        if( data.has("color") ) {
            JsonElement clrData = data.get("color");
            if( clrData.isJsonObject() ) {
                this.bakeColorObj(clrData.getAsJsonObject());
            } else if( clrData.isJsonPrimitive() ) {
                int clr = MiscUtils.hexToInt(clrData.getAsString());
                this.defaultColor = DEFAULT_COLOR;
                this.colors.put(this.defaultColor, clr);
                this.color = clr;
            } else {
                this.bakeDefaultColor();
            }
        } else {
            this.bakeDefaultColor();
        }

        this.bakedText = this.getBakedText(gui, data);
        this.shadow = JsonUtils.getBoolVal(data.get("shadow"), false);
        this.wrapWidth = JsonUtils.getIntVal(data.get("wrapWidth"), 0);
        this.lineHeight = JsonUtils.getIntVal(data.get("lineHeight"), 9);

        JsonElement cstFont = data.get("font");
        if( cstFont == null ) {
            this.fontRenderer = new Font("standard").get(gui.get());
        } else {
            this.fontRenderer = JsonUtils.GSON.fromJson(cstFont, Font.class).get(gui.get(), data.getAsJsonObject("glyphProvider"));
        }

        this.currWidth = this.getTextWidth(gui);
        this.currHeight = this.fontRenderer.getSplitter()
                                           .splitLines(this.bakedText, this.wrapWidth <= 0 ? Integer.MAX_VALUE : this.wrapWidth, this.bakedText.getStyle())
                                           .size()
                          * this.lineHeight;
        if( this.shadow ) {
            this.currHeight += 1;
        }
    }

    private void bakeColorObj(JsonObject clrData) {
        for( Map.Entry<String, JsonElement> o : clrData.entrySet() ) {
            String key = o.getKey();
            if( key.equalsIgnoreCase(DEFAULT_COLOR) || this.defaultColor == null ) {
                this.defaultColor = key;
            }

            this.colors.put(key, MiscUtils.hexToInt(o.getValue().getAsString()));
        }

        this.color = this.colors.get(this.defaultColor);
    }

    private void bakeDefaultColor() {
        this.defaultColor = DEFAULT_COLOR;
        this.colors.put(this.defaultColor, 0xFF000000);
        this.color = 0xFF000000;
    }

    @SuppressWarnings("unused")
    public ITextComponent getBakedText(IGui gui, JsonObject data) {
        return new TranslationTextComponent(data.get("text").getAsString());
    }

    @SuppressWarnings("unused")
    public ITextComponent getDynamicText(IGui gui, ITextComponent originalText) {
        return originalText;
    }

    public int getTextWidth(IGui gui) {
        ITextComponent tc = this.getDynamicText(gui, this.bakedText);
        int maxWidth = this.wrapWidth;
        if( maxWidth > 0 ) {
            if( this.justify == GuiElementInst.Justify.JUSTIFY ) {
                return maxWidth;
            }
        } else {
            maxWidth = Integer.MAX_VALUE;
        }

        return this.fontRenderer.getSplitter().splitLines(tc, maxWidth, Style.EMPTY)
                                .stream().map(tp -> this.fontRenderer.width(tp))
                                .reduce(Integer::max).orElse(0);
    }

    protected void updateSize(IGui gui) {
        this.currWidth = this.getTextWidth(gui);
        this.currHeight = this.renderedLines.size() * this.lineHeight;
    }

    @Override
    public void tick(IGui gui, JsonObject data) {
        this.renderedLines.clear();
        ITextComponent s = this.getDynamicText(gui, this.bakedText);

        this.renderedLines.addAll(Arrays.asList(this.fontRenderer.getSplitter()
                                                .splitLines(s, this.wrapWidth > 0 ? this.wrapWidth : Integer.MAX_VALUE, s.getStyle())
                                                .toArray(new ITextProperties[0])));

        this.updateSize(gui);
    }

    @Override
    public void renderTick(IGui gui, MatrixStack stack, float partTicks, int x, int y, double mouseX, double mouseY, JsonObject data) {
        this.updateSize(gui);
    }

    @Override
    public void render(IGui gui, MatrixStack stack, float partTicks, int x, int y, double mouseX, double mouseY, JsonObject data) {
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
                this.fontRenderer.drawShadow(stack, irp, mx.getValue(), y, this.color);
            } else {
                this.fontRenderer.draw(stack, irp, mx.getValue(), y, this.color);
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
                    this.fontRenderer.drawShadow(stack, irp, mx.getValue(), y, this.color);
                } else {
                    this.fontRenderer.draw(stack, irp, mx.getValue(), y, this.color);
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
            colorId = this.defaultColor;
        }

        this.color = this.colors.get(colorId);
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
}
