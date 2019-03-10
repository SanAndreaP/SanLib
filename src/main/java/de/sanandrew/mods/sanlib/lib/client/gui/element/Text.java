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
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import java.lang.ref.WeakReference;
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
public class Text
        implements IGuiElement
{
    public static final ResourceLocation ID = new ResourceLocation("text");

    private BakedData data;

    @Override
    public void bakeData(IGui gui, JsonObject data) {
        if( this.data == null ) {
            this.data = new BakedData();
            this.data.text = getBakedText(gui, data);
            this.data.color = MiscUtils.hexToInt(JsonUtils.getStringVal(data.get("color"), "0xFF000000"));
            this.data.shadow = JsonUtils.getBoolVal(data.get("shadow"), false);
            this.data.wrapWidth = JsonUtils.getIntVal(data.get("wrapWidth"), 0);
            this.data.justifyRight = JsonUtils.getBoolVal(data.get("justifyRight"), false);

            JsonElement cstFont = data.get("font");
            if( cstFont == null ) {
                this.data.fontRenderer = new Font("standard").get(gui.get());
            } else {
                this.data.fontRenderer = JsonUtils.GSON.fromJson(cstFont, Font.class).get(gui.get());
            }
            this.data.height = this.data.wrapWidth <= 0 ? this.data.fontRenderer.FONT_HEIGHT : this.data.fontRenderer.getWordWrappedHeight(this.data.text, this.data.wrapWidth);
            if( this.data.shadow ) {
                this.data.height += 1;
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
        return this.data.wrapWidth > 0 ? this.data.wrapWidth : this.data.fontRenderer.getStringWidth(this.getDynamicText(gui, this.data.text));
    }

    @Override
    public void render(IGui gui, float partTicks, int x, int y, int mouseX, int mouseY, JsonObject data) {
        String s = this.getDynamicText(gui, this.data.text);
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0D, 0.0D, gui.getZLevel());

        if( this.data.wrapWidth > 0 ) {
            boolean origBidi = this.data.fontRenderer.getBidiFlag();
            if( this.data.justifyRight ) {
                x -= this.data.wrapWidth;
                this.data.fontRenderer.setBidiFlag(true);
            }
            if( this.data.shadow ) {
                int sdColor = (this.data.color & 0x00FCFCFC) >> 2 | this.data.color & 0xFF000000;
                this.data.fontRenderer.drawSplitString(s, x + 1, y + 1, sdColor, this.data.wrapWidth);
            }
            this.data.fontRenderer.drawSplitString(s, x, y, this.data.color, this.data.wrapWidth);
            if( this.data.justifyRight ) {
                this.data.fontRenderer.setBidiFlag(origBidi);
            }
        } else {
            if( this.data.justifyRight ) {
                x -= this.data.fontRenderer.getStringWidth(s);
            }
            this.data.fontRenderer.drawString(s, x, y, this.data.color, this.data.shadow);
        }
        GlStateManager.popMatrix();
    }

    @Override
    public int getHeight() {
        return this.data == null ? 0 : this.data.height;
    }

    private static final class BakedData
    {
        private String text;
        private int color;
        private boolean shadow;
        private int wrapWidth;
        private FontRenderer fontRenderer;
        private int height;
        private boolean justifyRight;
    }

    @SuppressWarnings("WeakerAccess")
    private static final class Font
    {
        String texture;
        Boolean unicode;
        Boolean bidirectional;

        private WeakReference<FontRenderer> frInst;

        @SuppressWarnings("unused")
        public Font() { }

        Font(String tx) {
            this.texture = tx;
        }

        FontRenderer get(GuiScreen gui) {
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
