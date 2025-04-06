package dev.sanandrea.mods.sanlib.lib.client.gui.element.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.sanandrea.mods.sanlib.lib.client.gui.GuiDefinition;
import dev.sanandrea.mods.sanlib.lib.util.JsonUtils;
import dev.sanandrea.mods.sanlib.lib.util.MiscUtils;
import net.minecraft.resources.ResourceLocation;

public class TextureData
        extends StatedData<TextureData.TextureDef>
{
    public static final String JSON_TEXTURE_WIDTH  = "textureWidth";
    public static final String JSON_TEXTURE_HEIGHT = "textureHeight";

    public final int textureWidth;
    public final int textureHeight;

    public TextureData(TextureDef texture) {
        this(texture, null, null, 256, 256);
    }

    public TextureData(TextureDef texture, int textureWidth, int textureHeight) {
        this(texture, null, null, textureWidth, textureHeight);
    }

    public TextureData(TextureDef regular, TextureDef hover, TextureDef disabled) {
        this(regular, hover, disabled, 256, 256);
    }

    public TextureData(TextureDef regular, TextureDef hover, TextureDef disabled, int textureWidth, int textureHeight) {
        super(regular, hover, disabled, TextureDef::toJson);

        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
    }

    public static TextureData fromJson(GuiDefinition guiDef, JsonElement data) {
        final int txWidth;
        final int txHeight;
        if( data instanceof JsonObject dataObj ) {
            txWidth = JsonUtils.getIntVal(dataObj.get(JSON_TEXTURE_WIDTH), 256);
            txHeight = JsonUtils.getIntVal(dataObj.get(JSON_TEXTURE_HEIGHT), 256);
        } else {
            txWidth = 256;
            txHeight = 256;
        }

        return StatedData.fromJson(guiDef, data, (r, h, d) -> new TextureData(MiscUtils.get(r, () -> new TextureDef(guiDef.getTexture())), h, d, txWidth, txHeight),
                                   TextureDef::fromJson);
    }

    public void buildJson(JsonUtils.ObjectBuilder builder) {
        builder.value(JSON_TEXTURE_WIDTH, this.textureWidth)
               .value(JSON_TEXTURE_HEIGHT, this.textureHeight);
    }

    public record TextureDef(ResourceLocation location, int posU, int posV)
    {
        public static final String JSON_LOCATION = "location";
        public static final String JSON_U        = "u";
        public static final String JSON_V        = "v";

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
