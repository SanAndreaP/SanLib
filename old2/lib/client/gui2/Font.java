package dev.sanandrea.mods.sanlib.lib.client.gui2;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import dev.sanandrea.mods.sanlib.lib.util.JsonUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.fonts.providers.DefaultGlyphProvider;
import net.minecraft.client.gui.fonts.providers.IGlyphProvider;
import net.minecraft.client.gui.fonts.providers.TextureGlyphProvider;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.ResourceLocation;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public final class Font
{
    public static final String STANDARD_FONT = "standard";

    private final String texture;
    private final JsonObject glyphProviderData;

    private WeakReference<FontRenderer> frInst;
    private static FontRenderer frGalactic;

    public Font(String tx) {
        this(tx, null);
    }

    public Font(String tx, JsonObject gpd) {
        this.texture = tx;
        this.glyphProviderData = gpd;
    }

    @Override
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

    private static void initGalactic(Minecraft mc) {
        if( frGalactic == null ) {
            net.minecraft.client.gui.fonts.Font f = getMcFont(mc, Minecraft.ALT_FONT);
            frGalactic = new FontRenderer(r -> f);
        }
    }

    public FontRenderer get(Minecraft mc) {
        if( STANDARD_FONT.equals(this.texture) ) {
            return mc.font;
        } else if( "galactic".equals(this.texture) ) {
            initGalactic(mc);

            return frGalactic;
        } else {
            FontRenderer fr = this.frInst == null ? null : this.frInst.get();
            if( fr == null ) {
                IGlyphProvider p = null;
                if( this.glyphProviderData != null ) {
                    p = TextureGlyphProvider.Factory.fromJson(this.glyphProviderData).create(mc.getResourceManager());
                }

                net.minecraft.client.gui.fonts.Font f = getMcFont(mc, new ResourceLocation(this.texture), p);
                this.frInst = new WeakReference<>(new FontRenderer(r -> f));
            }

            return fr;
        }
    }

    public static net.minecraft.client.gui.fonts.Font getMcFont(Minecraft mc, ResourceLocation rl, IGlyphProvider glyphProvider) {
        net.minecraft.client.gui.fonts.Font f = new net.minecraft.client.gui.fonts.Font(mc.textureManager, rl);
        f.reload(new ArrayList<>(Collections.singleton(glyphProvider)));

        return f;
    }

    public static net.minecraft.client.gui.fonts.Font getMcFont(Minecraft mc, ResourceLocation rl) {
        return getMcFont(mc, rl, new DefaultGlyphProvider());
    }

    public static Font loadFont(JsonObject data) {
        JsonElement cstFont = data.get("font");

        if( cstFont == null ) {
            return new Font(STANDARD_FONT);
        } else if( cstFont.isJsonPrimitive() ) {
            return new Font(cstFont.getAsString());
        } else if( cstFont.isJsonObject() ) {
            JsonObject cstFontObj = (JsonObject) cstFont;
            return new Font(JsonUtils.getStringVal(cstFontObj.get("texture"), STANDARD_FONT), cstFontObj.getAsJsonObject("glyphProvider"));
        } else {
            throw new JsonParseException("font must either be an object or a string");
        }
    }
}
