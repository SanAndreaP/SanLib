/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.sanlib.lib.client.gui.element;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.IGuiElement;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.sanlib.lib.util.LangUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Locale;
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

    public BakedData data;
    protected int currWidth;
    protected int currHeight;
    protected boolean isVisible = true;

    @Override
    public void bakeData(IGui gui, JsonObject data) {
        if( this.data == null ) {
            this.data = new BakedData();
            this.data.text = getBakedText(gui, data);
            this.data.color = MiscUtils.hexToInt(JsonUtils.getStringVal(data.get("color"), "0xFF000000"));
            this.data.shadow = JsonUtils.getBoolVal(data.get("shadow"), false);
            this.data.wrapWidth = JsonUtils.getIntVal(data.get("wrapWidth"), 0);
            this.data.justify = Justify.fromString(JsonUtils.getStringVal(data.get("justify"), "left"));
            this.data.lineHeight = JsonUtils.getIntVal(data.get("lineHeight"), 9);

            JsonElement cstFont = data.get("font");
            if( cstFont == null ) {
                this.data.fontRenderer = new Font("standard").get(gui.get());
            } else {
                this.data.fontRenderer = JsonUtils.GSON.fromJson(cstFont, Font.class).get(gui.get());
            }
            this.currHeight = this.data.wrapWidth <= 0
                              ? this.data.text.split("\n").length * this.data.lineHeight
                              : this.data.fontRenderer.listFormattedStringToWidth(this.data.text, this.data.wrapWidth).size() * this.data.lineHeight;
            if( this.data.shadow ) {
                this.currHeight += 1;
            }
        }
    }

    public String getBakedText(IGui gui, JsonObject data) {
        return LangUtils.translate(JsonUtils.getStringVal(data.get("text")));
    }

    public String getDynamicText(IGui gui, String originalText) {
        return originalText;
    }

    public int getTextWidth(IGui gui) {
        if( this.data.wrapWidth > 0 ) {
            if( this.data.justify == Justify.JUSTIFY ) {
                return this.data.wrapWidth;
            } else {
                return this.data.fontRenderer.listFormattedStringToWidth(this.getDynamicText(gui, this.data.text), this.data.wrapWidth)
                                             .stream().map(s -> this.data.fontRenderer.getStringWidth(s)).reduce(0, Math::max, Math::max);
            }
        }

        return Arrays.stream(this.getDynamicText(gui, this.data.text).split("\n")).map(s -> this.data.fontRenderer.getStringWidth(s))
                     .reduce(0, Math::max, Math::max);
    }

    @Override
    public void update(IGui gui, JsonObject data) {
        this.currWidth = this.getTextWidth(gui);
    }

    @Override
    public void render(IGui gui, float partTicks, int x, int y, int mouseX, int mouseY, JsonObject data) {
        String s = this.getDynamicText(gui, this.data.text);

        this.currWidth = this.getTextWidth(gui);
        String[] ln;
        if( this.data.wrapWidth > 0 ) {
            ln = this.data.fontRenderer.listFormattedStringToWidth(s, this.data.wrapWidth).toArray(new String[0]);
        } else {
            ln = s.split("\n");
        }
        this.currHeight = ln.length * this.data.lineHeight;

        for( String sln : ln ) {
            this.renderLine(sln, x, y);
            y += this.data.lineHeight;
        }
    }

    private void renderLine(String s, int x, int y) {
        switch( this.data.justify ) {
            case JUSTIFY:
                if( this.data.wrapWidth > 0 ) {
                    String[] words = s.split("\\s");
                    float spaceDist = this.data.wrapWidth;
                    int[] wordWidths = new int[words.length];
                    for( int i = 0; i < words.length; i++ ) {
                        wordWidths[i] = this.data.fontRenderer.getStringWidth(words[i]);
                        spaceDist -= wordWidths[i];
                    }
                    spaceDist /= words.length - 1;
                    for( int i = 0; i < words.length; i++ ) {
                        this.data.fontRenderer.drawString(words[i], x, y, this.data.color, this.data.shadow);
                        x += wordWidths[i] + spaceDist;
                    }
                    break;
                }
                // else fall-through
            case LEFT:
                this.data.fontRenderer.drawString(s, x, y, this.data.color, this.data.shadow);
                break;
            case RIGHT:
                this.data.fontRenderer.drawString(s, x - this.data.fontRenderer.getStringWidth(s), y, this.data.color, this.data.shadow);
                break;
            case CENTER:
                this.data.fontRenderer.drawString(s, x - (this.data.fontRenderer.getStringWidth(s) / 2.0F), y, this.data.color, this.data.shadow);
                break;
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

    @Override
    public boolean isVisible() {
        return this.isVisible;
    }

    @Override
    public void setVisible(boolean visible) {
        this.isVisible = visible;
    }

    public static final class BakedData
    {
        public String text;
        public int color;
        public boolean shadow;
        public int wrapWidth;
        public FontRenderer fontRenderer;
        public Justify justify;
        public int lineHeight;
    }

    @SuppressWarnings("WeakerAccess")
    public static final class Font
    {
        public String texture;
        public Boolean unicode;
        public Boolean bidirectional;

        public WeakReference<FontRenderer> frInst;

        @SuppressWarnings("unused")
        public Font() { }

        public Font(String tx) {
            this.texture = tx;
        }

        public FontRenderer get(GuiScreen gui) {
            if( "standard".equals(this.texture) ) {
                return gui.mc.fontRenderer;
            } else if( "galactic".equals(this.texture) ) {
                return gui.mc.standardGalacticFontRenderer;
            } else {
                FontRenderer fr = this.frInst == null ? null : this.frInst.get();
                if( fr == null ) {
                    this.frInst = new WeakReference<>(new FontRenderer(gui.mc.gameSettings, new ResourceLocation(this.texture), gui.mc.renderEngine,
                                                                       this.unicode != null ? this.unicode : gui.mc.gameSettings.language != null && gui.mc.isUnicode()));
                    fr = Objects.requireNonNull(this.frInst.get());
                    if( this.bidirectional != null ) {
                        fr.setBidiFlag(this.bidirectional);
                    } else if( gui.mc.gameSettings.language != null ) {
                        fr.setBidiFlag(gui.mc.getLanguageManager().isCurrentLanguageBidirectional());
                    }
                }

                return fr;
            }
        }

        @Override
        @SuppressWarnings("NonFinalFieldReferencedInHashCode")
        public int hashCode() {
            return Objects.hash(this.texture, this.unicode, this.bidirectional);
        }
    }

    enum Justify
    {
        LEFT,
        CENTER,
        RIGHT,
        JUSTIFY;

        static Justify fromString(String s) {
            return Justify.valueOf(s.toUpperCase(Locale.ROOT));
        }
    }
}
