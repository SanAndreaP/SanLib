package dev.sanandrea.mods.sanlib.lib.client.gui2.element;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import dev.sanandrea.mods.sanlib.lib.ColorObj;
import dev.sanandrea.mods.sanlib.lib.util.JsonUtils;
import dev.sanandrea.mods.sanlib.lib.util.MiscUtils;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An object defining a color.
 * See the 'color' value below if this is represented as a simple string.<br>
 * Following JSON values are available:
 * <table valign="top" border="0" cellpadding="2">
 *     <tr>
 *         <th>Name</th>
 *         <th>Mandatory</th>
 *         <th>Type</th>
 *         <th>Description</th>
 *     </tr>
 *     <tr>
 *         <td>color</td>
 *         <td>yes</td>
 *         <td>string</td>
 *         <td>either one of the following formats are supported:
 *             <ul style="margin-top:0;margin-bottom:0">
 *                 <li>hexadecimal format {@code #AARRGGBB} or {@code 0xAARRGGBB}</li>
 *                 <li>color name (for named colors, see {@link net.minecraft.util.text.TextFormatting})</li>
 *                 <li>rgb(a) functions:
 *                     <ul style="margin-top:0;margin-bottom">
 *                         <li>{@code rgb([0-255], [0-255], [0-255])}</li>
 *                         <li>{@code rgba([0-255], [0-255], [0-255], [0.0-1.0])}</li>
 *                     </ul>
 *                 </li>
 *             </ul>
 *         </td>
 *     </tr>
 *     <tr>
 *         <td>stop</td>
 *         <td>no</td>
 *         <td>float</td>
 *         <td>determines the stop position of the color within the gradient as a percentage.<br>
 *             {@code (0.0 -> 0% = start of the gradient; 1.0 -> 100% = end of the gradient)}<br>
 *             In a gradient, you must provide a start and end color, if stops are provided.<br>
 *             If no stops are provided, colors are evenly distributed within a gradient.</td>
 *     </tr>
 * </table>
 */
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
        this(JsonUtils.getFloatVal(data.get("stop"), -1), getColorFromJson(data.get("color")));
    }

    public ColorDef(float stop, int color) {
        this.stop = stop;
        this.color = color;
    }

    public boolean hasStop() {
        return this.stop >= 0.0F;
    }

    public static int getColorFromJson(JsonElement data) {
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

    public static void loadColors(@Nonnull JsonObject data, @Nonnull List<ColorDef> colors) {
        JsonElement colorData = data.get("colors");
        Boolean hasStops = null;

        if( colorData != null && !colorData.isJsonNull() ) {
            if( colorData.isJsonArray() ) {
                for( JsonElement color : colorData.getAsJsonArray() ) {
                    ColorDef def = loadColor(color, true);

                    if( hasStops == null ) {
                        hasStops = def.hasStop();
                    } else if( hasStops != def.hasStop() ) {
                        throw new JsonParseException("Colors containing the 'stop' value and colors without it cannot be mixed.");
                    }

                    colors.add(def);
                }
            } else {
                throw new JsonParseException("'colors' value must be an array");
            }
        } else {
            colors.add(new ColorDef(ColorDef.getColorFromJson(data.get("color"))));
        }
    }

    @Nonnull
    public static ColorDef loadColor(@Nonnull JsonElement color, boolean allowStop) {
        ColorDef def;
        if( color.isJsonObject() ) {
            JsonObject colorObj = color.getAsJsonObject();
            if( !allowStop && colorObj.has("stop") ) {
                throw new JsonParseException("color does not support 'stop' value");
            }
            def = new ColorDef(colorObj);
        } else if( color.isJsonPrimitive() ) {
            def = new ColorDef(ColorDef.getColorFromJson(color));
        } else {
            throw new JsonParseException("color is an invalid type, must be either an ARGB hex string (# or 0x as prefix, e.g. 0xFFFF0000), a 'TextFormatting' color name or a color object.");
        }

        return def;
    }
}
