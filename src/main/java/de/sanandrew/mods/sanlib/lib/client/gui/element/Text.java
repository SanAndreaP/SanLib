////////////////////////////////////////////////////////////////////////////////
// This file is subject to the terms and conditions defined in the             /
// file '.github/LICENSE.md', which is part of this source code package.       /
////////////////////////////////////////////////////////////////////////////////

package de.sanandrew.mods.sanlib.lib.client.gui.element;

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
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TranslationTextComponent;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
@SuppressWarnings("WeakerAccess")
public class Text
        implements IGuiElement
{
    public static final ResourceLocation ID = new ResourceLocation("text");
    
    public ITextComponent text;
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

    private       ITextComponent       prevTxt;
    private final List<ITextProperties> renderedLines = new ArrayList<>();

    @Override
    public void bakeData(IGui gui, JsonObject data, GuiElementInst inst) {
        this.justify = inst.getAlignmentH();

        this.colors = new HashMap<>();
        this.defaultColor = null;
        if( data.has("color") ) {
            JsonElement clrData = data.get("color");
            if( clrData.isJsonObject() ) {
                for( Map.Entry<String, JsonElement> o : clrData.getAsJsonObject().entrySet() ) {
                    String key = o.getKey();
                    if( key.equalsIgnoreCase("default") || this.defaultColor == null ) {
                        this.defaultColor = key;
                    }

                    this.colors.put(key, MiscUtils.hexToInt(o.getValue().getAsString()));
                }

                this.color = this.colors.get(this.defaultColor);
            } else if( clrData.isJsonPrimitive() ) {
                int clr = MiscUtils.hexToInt(clrData.getAsString());
                this.defaultColor = "default";
                this.colors.put(this.defaultColor, clr);
                this.color = clr;
            } else {
                this.bakeDefaultColor();
            }
        } else {
            this.bakeDefaultColor();
        }

        this.text = getBakedText(gui, data);
        this.shadow = JsonUtils.getBoolVal(data.get("shadow"), false);
        this.wrapWidth = JsonUtils.getIntVal(data.get("wrapWidth"), 0);
        this.lineHeight = JsonUtils.getIntVal(data.get("lineHeight"), 9);

        JsonElement cstFont = data.get("font");
        if( cstFont == null ) {
            this.fontRenderer = new Font("standard").get(gui.get());
        } else {
            this.fontRenderer = JsonUtils.GSON.fromJson(cstFont, Font.class).get(gui.get());
        }

        this.currWidth = this.getTextWidth(gui);
        this.currHeight = this.fontRenderer.getSplitter()
                                           .splitLines(this.text, this.wrapWidth <= 0 ? Integer.MAX_VALUE : this.wrapWidth, Style.EMPTY)
                                           .size()
                          * this.lineHeight;
        if( this.shadow ) {
            this.currHeight += 1;
        }
    }

    private void bakeDefaultColor() {
        this.defaultColor = "default";
        this.colors.put(this.defaultColor, 0xFF000000);
        this.color = 0xFF000000;
    }

    public ITextComponent getBakedText(IGui gui, JsonObject data) {
        return new TranslationTextComponent(data.get("text").getAsString());
    }

    public ITextComponent getDynamicText(IGui gui, ITextComponent originalText) {
        return originalText;
    }

    public int getTextWidth(IGui gui) {
        ITextComponent text = this.getDynamicText(gui, this.text);
        int maxWidth = this.wrapWidth;
        if( maxWidth > 0 ) {
            if( this.justify == GuiElementInst.Justify.JUSTIFY ) {
                return maxWidth;
            }
        } else {
            maxWidth = Integer.MAX_VALUE;
        }

        return this.fontRenderer.getSplitter().splitLines(text, maxWidth, Style.EMPTY)
                                .stream().map(tp -> this.fontRenderer.width(tp))
                                .reduce(Integer::max).orElse(0);
    }

    @Override
    public void update(IGui gui, JsonObject data) {
        this.renderedLines.clear();
        ITextComponent s = this.getDynamicText(gui, this.text);

        this.currWidth = this.getTextWidth(gui);
        ITextProperties[] ln = this.fontRenderer.getSplitter()
                                                .splitLines(s, this.wrapWidth > 0 ? this.wrapWidth : Integer.MAX_VALUE, Style.EMPTY)
                                                .toArray(new ITextProperties[0]);
        this.currHeight = ln.length * this.lineHeight;

        this.renderedLines.addAll(Arrays.asList(ln));
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
                    String[] words = s.getString().split("\\s");
                    float spaceDist = this.wrapWidth;
                    int[] wordWidths = new int[words.length];

                    for( int i = 0; i < words.length; i++ ) {
                        wordWidths[i] = this.fontRenderer.width(words[i]);
                        spaceDist -= wordWidths[i];
                    }

                    spaceDist /= words.length - 1;
                    for( int i = 0; i < words.length; i++ ) {
                        if( this.shadow ) {
                            this.fontRenderer.drawShadow(stack, words[i], x, y, this.color);
                        } else {
                            this.fontRenderer.draw(stack, words[i], x, y, this.color);
                        }
                        x += wordWidths[i] + spaceDist;
                    }

                    return;
                }
            case LEFT:
                break;
            case CENTER:
                x += (this.currWidth - this.fontRenderer.width(s)) / 2;
                break;
            case RIGHT:
                x += this.currWidth - this.fontRenderer.width(s);
        }

        if( this.shadow ) {
            this.fontRenderer.drawShadow(stack, s.getString(), x, y, this.color);
        } else {
            this.fontRenderer.draw(stack, s.getString(), x, y, this.color);
        }
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

    @SuppressWarnings("WeakerAccess")
    public static final class Font
    {
        public String texture;
        public Boolean unicode;

        public WeakReference<FontRenderer> frInst;

        private static FontRenderer frGalactic;

        @SuppressWarnings("unused")
        public Font() { }

        public Font(String tx) {
            this.texture = tx;
        }

        public FontRenderer get(Screen gui) {
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
                    net.minecraft.client.gui.fonts.Font f = getMcFont(gui.getMinecraft(), new ResourceLocation(this.texture));
                    this.frInst = new WeakReference<>(new FontRenderer(r -> f));
                }

                return fr;
            }
        }

        private static net.minecraft.client.gui.fonts.Font getMcFont(Minecraft mc, ResourceLocation rl) {
            return new net.minecraft.client.gui.fonts.Font(mc.textureManager, rl);
        }

        @Override
        @SuppressWarnings("NonFinalFieldReferencedInHashCode")
        public int hashCode() {
            return Objects.hash(this.texture, this.unicode);
        }
    }

    @Override
    public boolean forceRenderUpdate(IGui gui) {
        ITextComponent s = this.getDynamicText(gui, this.text);

        if( !s.getString().equals(this.prevTxt.getString()) ) {
            this.prevTxt = s;
            return true;
        } else {
            return false;
        }
    }
}
