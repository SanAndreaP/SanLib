package dev.sanandrea.mods.sanlib.lib.client.gui.element.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import dev.sanandrea.mods.sanlib.lib.ColorObj;
import dev.sanandrea.mods.sanlib.lib.client.gui.GuiDefinition;
import dev.sanandrea.mods.sanlib.lib.util.ColorUtils;
import dev.sanandrea.mods.sanlib.lib.util.JsonUtils;
import dev.sanandrea.mods.sanlib.lib.util.MiscUtils;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Function;

@SuppressWarnings("unused")
public class ColorData
        extends StatedData<Integer>
{
    public static final ColorData WHITE = new ColorData(ColorObj.WHITE.getColorInt());
    public static final ColorData BLACK = new ColorData(ColorObj.BLACK.getColorInt());

    private static final String JSON_COLORS   = "colors";
    private static final String JSON_STOP     = "stop";

    public final float stop;

    public ColorData(int color) {
        this(-1, color, null, null);
    }
    public ColorData(float stop, int color) {
        this(stop, color, null, null);
    }

    public ColorData(int color, Integer hover, Integer disabled) {
        this(-1, color, hover, disabled);
    }

    public ColorData(float stop, int regular, Integer hover, Integer disabled) {
        super(regular, hover, disabled, v -> new JsonPrimitive(MiscUtils.toHexString(v)));

        this.stop = stop;
    }

    public boolean hasStop() {
        return this.stop >= 0.0F;
    }

    public static boolean checkStop(ColorData color, Boolean prevHasStop) {
        return checkStop(color, prevHasStop, IllegalArgumentException::new);
    }

    private static boolean checkStop(ColorData color, Boolean prevHasStop, Function<String, RuntimeException> exCtor) {
        if( prevHasStop == null ) {
            return color.hasStop();
        } else if( prevHasStop != color.hasStop() ) {
            throw exCtor.apply("Colors with a 'stop' value and colors without cannot be mixed.");
        }

        return prevHasStop;
    }

    public static ColorData fromJson(GuiDefinition guiDef, JsonElement data) {
        return fromJson(guiDef, data, null);
    }

    public static ColorData fromJson(GuiDefinition guiDef, JsonElement data, final Integer defaultColor) {
        final float stop;
        if( data instanceof JsonObject dataObj ) {
            stop = JsonUtils.getFloatVal(dataObj.get(JSON_STOP), -1);
        } else {
            stop = -1;
        }

        return StatedData.fromJson(guiDef, data, "color",
                                   (r, h, d) -> new ColorData(stop, MiscUtils.get(r, defaultColor), h, d),
                                   ColorData::valueFromJson);
    }

    private static Integer valueFromJson(GuiDefinition guiDef, JsonElement data, Integer base) {
        if( data.isJsonPrimitive() ) {
            return ColorUtils.parseColorString(data.getAsString(), base);
        }

        return base;
    }

    @Override
    public void buildJson(JsonUtils.ObjectBuilder builder) {
        builder.valueIf(JSON_STOP, this.stop, this::hasStop);
    }

    public ColorData copy() {
        return new ColorData(this.stop,
                             this.regular,
                             this.hover,
                             this.disabled);
    }


    public static void loadColors(GuiDefinition guiDef, @Nonnull JsonObject data, @Nonnull List<ColorData> colors) {
        JsonElement colorData = data.get(JSON_COLORS);
        Boolean     hasStops  = null;

        if( colorData != null && !colorData.isJsonNull() ) {
            if( colorData.isJsonArray() ) {
                for( JsonElement color : colorData.getAsJsonArray() ) {
                    ColorData def = fromJson(guiDef, color);
                    hasStops = checkStop(def, hasStops, JsonParseException::new);
                    colors.add(def);
                }
            } else {
                throw new JsonParseException("'colors' value must be an array");
            }
        } else {
            colors.add(fromJson(guiDef, data));
        }
    }
}
