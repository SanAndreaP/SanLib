/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.sanlib.lib.client.gui;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.sanlib.lib.util.LangUtils;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

import java.lang.ref.WeakReference;
import java.util.Objects;
import java.util.WeakHashMap;

public class TextGuiElement
        implements IGuiElement
{
    private static final ResourceLocation ID = new ResourceLocation("text");

    private static final WeakHashMap<TextGuiElement, Font> FONTS = new WeakHashMap<>();

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public void render(GuiScreen gui, float partTicks, int x, int y, int mouseX, int mouseY, JsonObject data) {
        String txt = LangUtils.translate(data.get("languageKey").getAsString());
        Font font = FONTS.get(this);
        if( font == null ) {
            JsonElement cstFont = data.get("font");
            if( cstFont == null ) {
                font = new Font("standard");
            } else {
                font = JsonUtils.GSON.fromJson(cstFont, Font.class);
            }
            FONTS.put(this, font);
        }

//        font.get(gui).drawString(txt, x, y, )
    }

    private static final class Font
    {
        String texture;
        Boolean unicode;
        Boolean bidirectional;

        private WeakReference<FontRenderer> frInst;

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
