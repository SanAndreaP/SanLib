package dev.sanandrea.mods.sanlib.lib.client.gui2.element;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import dev.sanandrea.mods.sanlib.lib.ColorObj;
import dev.sanandrea.mods.sanlib.lib.util.JsonUtils;
import dev.sanandrea.mods.sanlib.lib.util.MiscUtils;
import net.minecraft.util.text.TextFormatting;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
public class ColorDef
{
    private static final String RGB_REGEX = "\\s*(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)\\s*";
    private static final Pattern REGEX_RGB_FUNC = Pattern.compile("^rgb\\(" + RGB_REGEX + "," + RGB_REGEX + "," + RGB_REGEX + "\\)$",
                                                                  Pattern.CASE_INSENSITIVE);
    private static final Pattern REGEX_RGBA_FUNC = Pattern.compile("^rgba\\(" + RGB_REGEX + "," + RGB_REGEX + "," + RGB_REGEX + ",\\s*(1(?:\\.0+)?|0(?:\\.\\d+)?)\\s*\\)$",
                                                                   Pattern.CASE_INSENSITIVE);

    public final float stop;
    public final int   color;

    public ColorDef(int color) {
        this(-1, color);
    }

    public ColorDef(JsonObject data) {
        this(JsonUtils.getFloatVal(data.get("stop"), -1), colorFromJson(data.get("color")));
    }

    public ColorDef(float stop, int color) {
        this.stop = stop;
        this.color = color;
    }

    public static int colorFromJson(JsonElement data) {
        String colorStr = JsonUtils.getStringVal(data);

        if( MiscUtils.isEmpty(colorStr) ) {
            throw new JsonParseException("color cannot be empty");
        }

        if( colorStr.startsWith("#") || colorStr.startsWith("0x") ) {
            return MiscUtils.hexToInt(colorStr);
        } else {
            TextFormatting tf = TextFormatting.getByName(colorStr);
            if( tf != null && tf.isColor() ) {
                return MiscUtils.get(tf.getColor(), 0);
            } else if( Pattern.matches("-?\\d+", colorStr) ) {
                return Integer.getInteger(colorStr);
            } else {
                return getColorFromRgba(colorStr);
            }
        }
    }

    protected static int getColorFromRgba(String rgbaText) {
        Matcher rgbaMatcher = REGEX_RGBA_FUNC.matcher(rgbaText);
        if( !rgbaMatcher.matches() ) {
            rgbaMatcher = REGEX_RGB_FUNC.matcher(rgbaText);
            if( !rgbaMatcher.matches() ) {
                throw new JsonParseException(String.format("color value is invalid: %s", rgbaText));
            }
        }

        return new ColorObj(Integer.parseInt(rgbaMatcher.group(1)),
                                  Integer.parseInt(rgbaMatcher.group(2)),
                                  Integer.parseInt(rgbaMatcher.group(3)),
                                  rgbaMatcher.groupCount() > 3 ? Math.round(Float.parseFloat(rgbaMatcher.group(4)) * 255.0F) : 255)
                .getColorInt();
    }
}
