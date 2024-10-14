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
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * An object defining a color. See the 'color' value below if this is represented as a simple string.<br> Following JSON values are available:
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
public record ColorData(float stop, StatedColor color)
{
    public static final ColorData WHITE = new ColorData(ColorObj.WHITE.getColorInt());
    public static final ColorData BLACK = new ColorData(ColorObj.BLACK.getColorInt());

    private static final String JSON_COLORS   = "colors";
    private static final String JSON_COLOR    = "color";
    private static final String JSON_HOVER    = "hover";
    private static final String JSON_DISABLED = "disabled";
    private static final String JSON_STOP     = "stop";

    public ColorData(int color) {
        this(-1, new StatedColor(color));
    }

    public ColorData(StatedColor color) {
        this(-1, color);
    }

    public ColorData(float stop, int color) {
        this(stop, new StatedColor(color));
    }

    public boolean hasStop() {
        return this.stop >= 0.0F;
    }

    private static int parseColorString(String colorStr, Integer defaultColor) {
        if( MiscUtils.isEmpty(colorStr) ) {
            if( defaultColor == null ) {
                throw new JsonParseException("color cannot be empty");
            } else {
                return defaultColor;
            }
        }

        if( colorStr.startsWith("#") || colorStr.startsWith("0x") ) {
            return MiscUtils.hexToInt(colorStr);
        } else {
            DataResult<TextColor> tf = TextColor.parseColor(colorStr);
            if( tf.isSuccess() ) {
                return tf.getOrThrow().getValue();
            } else if( Pattern.matches("-?\\d+", colorStr) ) {
                return Integer.parseInt(colorStr);
            } else {
                return ColorUtils.getColorFromRgba(colorStr).getColorInt();
            }
        }
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

    public static void loadColors(@Nonnull JsonObject data, @Nonnull List<ColorData> colors, StatedColor defaultColor) {
        JsonElement colorData = data.get(JSON_COLORS);
        Boolean     hasStops  = null;

        if( colorData != null && !colorData.isJsonNull() ) {
            if( colorData.isJsonArray() ) {
                for( JsonElement color : colorData.getAsJsonArray() ) {
                    ColorData def = loadColor(color, true, defaultColor);
                    hasStops = checkStop(def, hasStops, JsonParseException::new);
                    colors.add(def);
                }
            } else {
                throw new JsonParseException("'colors' value must be an array");
            }
        } else {
            colors.add(loadColor(data.get(JSON_COLOR), false, defaultColor));
        }
    }

    @Nonnull
    public static ColorData loadColor(JsonElement color, boolean allowStop, @Nonnull StatedColor defaultColor) {
        if( color == null ) {
            return new ColorData(defaultColor);
        }

        ColorData def;
        if( color.isJsonObject() ) {
            JsonObject colorObj = color.getAsJsonObject();
            if( !allowStop && colorObj.has(JSON_STOP) ) {
                throw new JsonParseException("color does not support 'stop' value");
            }
            def = ColorData.fromJson(colorObj, defaultColor);
        } else if( color.isJsonPrimitive() ) {
            def = new ColorData(parseColorString(color.getAsString(), defaultColor.regular));
        } else {
            throw new JsonParseException(
                    "color is an invalid type, must be either an ARGB hex string (# or 0x as prefix, e.g. 0xFFFF0000), a 'TextFormatting' color name or a color object.");
        }

        return def;
    }

    public int getColor(boolean disabled, boolean hovering) {
        return this.color.getColor(disabled, hovering);
    }

    public static ColorData fromJson(JsonObject data, StatedColor defaultColor) {
        float stop = JsonUtils.getFloatVal(data.get(JSON_STOP), -1);

        int color = JsonUtils.apply(data, JSON_COLOR, e -> parseColorString(e.getAsString(), defaultColor.regular),
                                    defaultColor.regular);
        int hoverColor = JsonUtils.apply(data, JSON_HOVER, e -> parseColorString(e.getAsString(), defaultColor.hoverOr(color)),
                                         defaultColor.hoverOr(color));
        int disabledColor = JsonUtils.apply(data, JSON_DISABLED, e -> parseColorString(e.getAsString(), defaultColor.disabledOr(color)),
                                            defaultColor.disabledOr(color));

        return new ColorData(stop, new StatedColor(color, hoverColor, disabledColor));
    }

    public JsonObject toJson() {
        return JsonUtils.ObjectBuilder.create()
                                      .valueIf(JSON_STOP, this.stop, this::hasStop)
                                      .value(JSON_COLOR, this.color.regular)
                                      .valueIf(JSON_HOVER, this.color.hover, () -> this.color.hover != null)
                                      .valueIf(JSON_DISABLED, this.color.disabled, () -> this.color.disabled != null)
                                      .get();
    }

    public ColorData copy() {
        return new ColorData(this.stop, this.color.copy());
    }

    public record StatedColor(int regular, Integer hover, Integer disabled)
    {
        public StatedColor(int color) {
            this(color, null, null);
        }

        public static StatedColor getShadowColor(StatedColor base) {
            return new StatedColor(ColorUtils.getShadowColor(base.regular),
                                   MiscUtils.apply(base.hover, ColorUtils::getShadowColor),
                                   MiscUtils.apply(base.disabled, ColorUtils::getShadowColor));
        }

        public static StatedColor getBorderColor(StatedColor base) {
            return new StatedColor(ColorUtils.getBorderColor(base.regular),
                                   MiscUtils.apply(base.hover, ColorUtils::getBorderColor),
                                   MiscUtils.apply(base.disabled, ColorUtils::getBorderColor));
        }

        public int getColor(boolean disabled, boolean hovering) {
            if( disabled ) {
                return MiscUtils.get(this.disabled, this.regular);
            }

            return hovering && this.hover != null ? this.hover : this.regular;
        }

        public StatedColor copy() {
            return new StatedColor(this.regular, this.hover, this.disabled);
        }

        int hoverOr(int def) {
            return MiscUtils.get(this.hover, def);
        }

        int disabledOr(int def) {
            return MiscUtils.get(this.disabled, def);
        }
    }
}
