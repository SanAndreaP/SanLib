////////////////////////////////////////////////////////////////////////////////
// This file is subject to the terms and conditions defined in the             /
// file '.github/LICENSE.md', which is part of this source code package.       /
////////////////////////////////////////////////////////////////////////////////

package de.sanandrew.mods.sanlib.lib.client.gui.element;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
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
import java.util.HashMap;
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
    
    public String               text;
    public int                  color;
    public Map<String, Integer> colors;
    public boolean              shadow;
    public int                  wrapWidth;
    public FontRenderer         fontRenderer;
    public int                  lineHeight;

    protected String                 defaultColor;
    protected int                    currWidth;
    protected int                    currHeight;
    protected GuiElementInst.Justify justify;

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
                this.defaultColor = "default";
                this.colors.put(this.defaultColor, 0xFF000000);
                this.color = 0xFF000000;
            }
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
        this.currHeight = this.wrapWidth <= 0
                          ? this.text.split("\n").length * this.lineHeight
                          : this.fontRenderer.listFormattedStringToWidth(this.text, this.wrapWidth).size() * this.lineHeight;
        if( this.shadow ) {
            this.currHeight += 1;
        }
    }

    public String getBakedText(IGui gui, JsonObject data) {
        return LangUtils.translate(JsonUtils.getStringVal(data.get("text")));
    }

    public String getDynamicText(IGui gui, String originalText) {
        return originalText;
    }

    public int getTextWidth(IGui gui) {
        if( this.wrapWidth > 0 ) {
            if( this.justify == GuiElementInst.Justify.JUSTIFY ) {
                return this.wrapWidth;
            } else {
                return this.fontRenderer.listFormattedStringToWidth(this.getDynamicText(gui, this.text), this.wrapWidth)
                                             .stream().map(s -> this.fontRenderer.getStringWidth(s)).reduce(0, Math::max, Math::max);
            }
        }

        return Arrays.stream(this.getDynamicText(gui, this.text).split("\n")).map(s -> this.fontRenderer.getStringWidth(s))
                     .reduce(0, Math::max, Math::max);
    }

    @Override
    public void render(IGui gui, float partTicks, int x, int y, int mouseX, int mouseY, JsonObject data) {
        String s = this.getDynamicText(gui, this.text);

        this.currWidth = this.getTextWidth(gui);
        String[] ln;
        if( this.wrapWidth > 0 ) {
            ln = this.fontRenderer.listFormattedStringToWidth(s, this.wrapWidth).toArray(new String[0]);
        } else {
            ln = s.split("\n");
        }
        this.currHeight = ln.length * this.lineHeight;

        for( String sln : ln ) {
            this.renderLine(sln, x, y);
            y += this.lineHeight;
        }
    }

    private void renderLine(String s, int x, int y) {
        switch( this.justify ) {
            case JUSTIFY:
                if( this.wrapWidth > 0 ) {
                    String[] words = s.split("\\s");
                    float spaceDist = this.wrapWidth;
                    int[] wordWidths = new int[words.length];

                    for( int i = 0; i < words.length; i++ ) {
                        wordWidths[i] = this.fontRenderer.getStringWidth(words[i]);
                        spaceDist -= wordWidths[i];
                    }

                    spaceDist /= words.length - 1;
                    for( int i = 0; i < words.length; i++ ) {
                        this.fontRenderer.drawString(words[i], x, y, this.color, this.shadow);
                        x += wordWidths[i] + spaceDist;
                    }

                    return;
                }
            case LEFT:
                break;
            case CENTER:
                x += (this.currWidth - this.fontRenderer.getStringWidth(s)) / 2;
                break;
            case RIGHT:
                x += this.currWidth - this.fontRenderer.getStringWidth(s);
        }

        this.fontRenderer.drawString(s, x, y, this.color, this.shadow);
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

}
