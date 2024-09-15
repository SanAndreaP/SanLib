package dev.sanandrea.mods.sanlib.lib.client.gui.element;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.serialization.DataResult;
import dev.sanandrea.mods.sanlib.lib.ColorObj;
import dev.sanandrea.mods.sanlib.lib.util.ColorUtils;
import dev.sanandrea.mods.sanlib.lib.util.JsonUtils;
import dev.sanandrea.mods.sanlib.lib.util.MiscUtils;
import net.minecraft.network.chat.TextColor;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
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
 *                 <li>color name (for named colors, see {@link net.minecraft.ChatFormatting})</li>
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
    public final float stop;
    public final StatedColor   color;

    public ColorDef(int color) {
        this(-1, color);
    }

    public ColorDef(ColorObj color) {
        this(color.getColorInt());
    }

    public ColorDef(JsonObject data, Integer defaultColor) {
        this(JsonUtils.getFloatVal(data.get("stop"), -1), getColorFromJson(data.get("color"), defaultColor).color);
    }

    public ColorDef(float stop, int color) {
        this.stop = stop;
        this.color = color;
    }

    public boolean hasStop() {
        return this.stop >= 0.0F;
    }

    public static ColorDef getColorFromJson(JsonElement data, Integer defaultColor) {
        String colorStr = JsonUtils.getStringVal(data);

        if( MiscUtils.isEmpty(colorStr) ) {
            if( defaultColor == null ) {
                throw new JsonParseException("color cannot be empty");
            } else {
                return new ColorDef(defaultColor);
            }
        }

        if( colorStr.startsWith("#") || colorStr.startsWith("0x") ) {
            return new ColorDef(MiscUtils.hexToInt(colorStr));
        } else {
            DataResult<TextColor> tf = TextColor.parseColor(colorStr);
            if( tf.isSuccess() ) {
                return new ColorDef(tf.getOrThrow().getValue());
            } else if( Pattern.matches("-?\\d+", colorStr) ) {
                return new ColorDef(Integer.parseInt(colorStr));
            } else {
                return new ColorDef(ColorUtils.getColorFromRgba(colorStr));
            }
        }
    }

    public static boolean checkStop(ColorDef color, Boolean prevHasStop) {
        return checkStop(color, prevHasStop, IllegalArgumentException::new);
    }

    private static boolean checkStop(ColorDef color, Boolean prevHasStop, Function<String, RuntimeException> exCtor) {
        if( prevHasStop == null ) {
            return color.hasStop();
        } else if( prevHasStop != color.hasStop() ) {
            throw exCtor.apply("Colors with a 'stop' value and colors without cannot be mixed.");
        }

        return prevHasStop;
    }

    public static void loadColors(@Nonnull JsonObject data, @Nonnull List<ColorDef> colors, Integer defaultColor) {
        JsonElement colorData = data.get("colors");
        Boolean hasStops = null;

        if( colorData != null && !colorData.isJsonNull() ) {
            if( colorData.isJsonArray() ) {
                for( JsonElement color : colorData.getAsJsonArray() ) {
                    ColorDef def = loadColor(color, true, defaultColor);
                    hasStops = checkStop(def, hasStops, JsonParseException::new);
                    colors.add(def);
                }
            } else {
                throw new JsonParseException("'colors' value must be an array");
            }
        } else {
            colors.add(ColorDef.getColorFromJson(data.get("color"), defaultColor));
        }
    }

    @Deprecated
    public static void loadKeyedColors(@Nonnull JsonObject data, @Nonnull Map<String, Integer> colors, String defaultKey, Integer defaultColor) {
        JsonElement colorData = data.get("colors");

        if( colorData != null && !colorData.isJsonNull() ) {
            if( colorData.isJsonObject() ) {
                for( Map.Entry<String, JsonElement> color : colorData.getAsJsonObject().entrySet() ) {
                    ColorDef def = loadColor(color.getValue(), false, defaultColor);
                    colors.put(color.getKey(), def.color);
                }
            } else {
                throw new JsonParseException("'colors' value must be an object");
            }
        } else {
            colors.put(defaultKey, ColorDef.getColorFromJson(data.get("color"), defaultColor).color);
        }
    }

    @Nonnull
    public static ColorDef loadColor(@Nonnull JsonElement color, boolean allowStop, Integer defaultColor) {
        ColorDef def;
        if( color.isJsonObject() ) {
            JsonObject colorObj = color.getAsJsonObject();
            if( !allowStop && colorObj.has("stop") ) {
                throw new JsonParseException("color does not support 'stop' value");
            }
            def = new ColorDef(colorObj, defaultColor);
        } else if( color.isJsonPrimitive() ) {
            def = ColorDef.getColorFromJson(color, defaultColor);
        } else {
            throw new JsonParseException("color is an invalid type, must be either an ARGB hex string (# or 0x as prefix, e.g. 0xFFFF0000), a 'TextFormatting' color name or a color object.");
        }

        return def;
    }

    public record StatedColor(int defaultColor, int hoverColor, int disabledColor)
    {
        public StatedColor(int color) {
            this(color, color, color);
        }

        public static StatedColor getShadowColor(StatedColor base) {
            return new StatedColor(ColorUtils.getShadowColor(base.defaultColor),
                                   ColorUtils.getShadowColor(base.hoverColor),
                                   ColorUtils.getShadowColor(base.disabledColor));
        }

        public static StatedColor getBorderColor(StatedColor base) {
            
        }

        public int getColor(boolean disabled, boolean hovering) {
            if( disabled ) {
                return this.disabledColor;
            }

            return hovering ? this.hoverColor : this.defaultColor;
        }


    }
}
