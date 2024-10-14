package dev.sanandrea.mods.sanlib.lib.client.gui.element;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.sanandrea.mods.sanlib.lib.client.gui.GuiDefinition;
import dev.sanandrea.mods.sanlib.lib.util.JsonUtils;
import dev.sanandrea.mods.sanlib.lib.util.MiscUtils;
import net.minecraft.resources.ResourceLocation;

public record TextureData(TextureDef regular, TextureDef hover, TextureDef disabled, int textureWidth, int textureHeight)
{
    public static final String JSON_LOCATION       = "location";
    public static final String JSON_REGULAR        = "regular";
    public static final String JSON_HOVER          = "hover";
    public static final String JSON_DISABLED       = "disabled";
    public static final String JSON_U              = "u";
    public static final String JSON_V              = "v";
    public static final String JSON_TEXTURE_WIDTH  = "textureWidth";
    public static final String JSON_TEXTURE_HEIGHT = "textureHeight";

    public TextureData(TextureDef texture) {
        this(texture, texture, texture, 256, 256);
    }

    public TextureData(TextureDef texture, int textureWidth, int textureHeight) {
        this(texture, texture, texture, textureWidth, textureHeight);
    }

    public TextureData(TextureDef regular, TextureDef hover, TextureDef disabled) {
        this(regular, hover, disabled, 256, 256);
    }

    public static TextureData fromJson(GuiDefinition guiDef, JsonElement data) {
        if( data == null || data.isJsonPrimitive() ) {
            return new TextureData(TextureDef.fromJson(guiDef, data));
        } else {
            JsonObject dataObj = data.getAsJsonObject();

            TextureDef regular;
            if( dataObj.has(JSON_REGULAR) ) {
                regular = TextureDef.fromJson(guiDef, dataObj.get(JSON_REGULAR));
            } else {
                regular = TextureDef.fromJson(guiDef, data);
            }

            TextureDef hover;
            if( dataObj.has(JSON_HOVER) ) {
                hover = TextureDef.fromJson(guiDef, dataObj.get(JSON_HOVER), regular);
            } else {
                hover = regular;
            }

            TextureDef disabled;
            if( dataObj.has(JSON_DISABLED) ) {
                disabled = TextureDef.fromJson(guiDef, dataObj.get(JSON_DISABLED), regular);
            } else {
                disabled = regular;
            }

            return new TextureData(regular, hover, disabled,
                                   JsonUtils.getIntVal(dataObj.get(JSON_TEXTURE_WIDTH), 256),
                                   JsonUtils.getIntVal(dataObj.get(JSON_TEXTURE_HEIGHT), 256));
        }
    }

    public JsonObject toJson() {
        return JsonUtils.ObjectBuilder.create()
                                      .value(JSON_REGULAR, this.regular.toJson())
                                      .value(JSON_HOVER, this.hover.toJson())
                                      .value(JSON_DISABLED, this.disabled.toJson())
                                      .value(JSON_TEXTURE_WIDTH, this.textureWidth)
                                      .value(JSON_TEXTURE_HEIGHT, this.textureHeight)
                                      .get();
    }

    public TextureDef getTexture(boolean isDisabled, boolean isHovering) {
        TextureDef data = isHovering ? this.hover : this.regular;

        return isDisabled ? this.disabled : data;
    }

    public record TextureDef(ResourceLocation location, int posU, int posV)
    {
        public TextureDef(ResourceLocation location) {
            this(location, 0, 0);
        }

        public TextureDef(String location) {
            this(ResourceLocation.parse(location));
        }

        public TextureDef(String location, int posU, int posV) {
            this(ResourceLocation.parse(location), posU, posV);
        }

        public TextureDef copy() {
            return new TextureDef(this.location, this.posU, this.posV);
        }

        public TextureDef copyWith(ResourceLocation texture) {
            return new TextureDef(texture, this.posU, this.posV);
        }

        public static TextureDef fromJson(GuiDefinition guiDef, JsonElement data) {
            return fromJson(guiDef, data, null);
        }

        public static TextureDef fromJson(GuiDefinition guiDef, JsonElement data, TextureDef base) {
            if( data == null || data.isJsonPrimitive() ) {
                return new TextureDef(guiDef.getTexture(data, MiscUtils.apply(base, TextureDef::location)),
                                      MiscUtils.apply(base, TextureDef::posU, 0),
                                      MiscUtils.apply(base, TextureDef::posV, 0));
            } else {
                JsonObject dataObj = data.getAsJsonObject();
                return new TextureDef(guiDef.getTexture(dataObj.get(JSON_LOCATION), MiscUtils.apply(base, TextureDef::location)),
                                      JsonUtils.getIntVal(dataObj.get(JSON_U), MiscUtils.apply(base, TextureDef::posU, 0)),
                                      JsonUtils.getIntVal(dataObj.get(JSON_V), MiscUtils.apply(base, TextureDef::posV, 0)));
            }
        }

        public JsonObject toJson() {
            return JsonUtils.ObjectBuilder.create()
                                          .value(JSON_LOCATION, this.location)
                                          .value(JSON_U, this.posU)
                                          .value(JSON_V, this.posV)
                                          .get();
        }
    }
}
