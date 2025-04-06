/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2016-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.sanlib.lib.util;

import com.mojang.serialization.DataResult;
import dev.sanandrea.mods.sanlib.lib.ColorObj;
import net.minecraft.network.chat.TextColor;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A utility class for colors
 */
@SuppressWarnings("unused")
public final class ColorUtils
{
    private static final String  RGB_REGEX       = "\\s*(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)\\s*";
    private static final String  A_REGEX         = "\\s*(1(?:\\.0+)?|0(?:\\.\\d+)?)\\s*";
    private static final Pattern REGEX_RGB_FUNC  = Pattern.compile("^rgb\\(" + RGB_REGEX + "," + RGB_REGEX + "," + RGB_REGEX + "\\)$",
                                                                   Pattern.CASE_INSENSITIVE);
    private static final Pattern REGEX_RGBA_FUNC = Pattern.compile("^rgba\\(" + RGB_REGEX + "," + RGB_REGEX + "," + RGB_REGEX + "," + A_REGEX + "\\)$",
                                                                   Pattern.CASE_INSENSITIVE);

    private ColorUtils() { }

    /**
     * Gets the average color from an image. The alpha value of each pixel is used for the color weightness, thus the more transparent the pixel is,
     * the less it is counted towards the average
     *
     * @param is The image as InputStream
     *
     * @return the average color
     *
     * @throws java.io.IOException when the InputStream cannot be read as an image
     */
    public static ColorObj getAverageColor(InputStream is) throws IOException {
        return getAverageColor(is, null);
    }

    /**
     * Gets the average color from an image. The alpha value of each pixel is used for the color weightness, thus the more transparent the pixel is,
     * the less it is counted towards the average. The mask color ({@code maskClr}) will determine which pixels will not be counted towards the
     * average.
     *
     * @param is      The image as InputStream
     * @param maskClr The color to be masked / ignored; Can be {@code null} for no mask color
     *
     * @return the average color
     *
     * @throws java.io.IOException when the InputStream cannot be read as an image
     */
    public static ColorObj getAverageColor(InputStream is, ColorObj maskClr) throws IOException {
        // read location as BufferedImage from InputStream
        BufferedImage bi = ImageIO.read(is);

        // holds the added RGB values of the whole location and pixel counter
        double red   = 0.0D;
        double green = 0.0D;
        double blue  = 0.0D;
        double count = 0.0D;
        for( int x = 0; x < bi.getWidth(); x++ ) {          // loop through the pixels
            for( int y = 0; y < bi.getHeight(); y++ ) {
                ColorObj color = ColorObj.fromARGB(bi.getRGB(x, y));

                if( color.equals(maskClr) ) {
                    continue;
                }

                red += color.red() * color.fAlpha();              // add RGB from the pixel to the RGB storage variables, increase pixel counter
                green += color.green() * color.fAlpha();
                blue += color.blue() * color.fAlpha();
                count += color.fAlpha();
            }
        }

        if( count > 0.0D ) {
            int avgRed   = (int) (red / count);       // calculating the average of each channel
            int avgGreen = (int) (green / count);
            int avgBlue  = (int) (blue / count);

            return ColorObj.fromRGBA(avgRed, avgGreen, avgBlue, 255); // return combined RGB channels
        } else {
            return ColorObj.BLACK;
        }
    }

    public static ColorObj getColorFromRgba(String rgbaText) {
        Matcher rgbaMatcher = REGEX_RGBA_FUNC.matcher(rgbaText);
        if( !rgbaMatcher.matches() ) {
            rgbaMatcher = REGEX_RGB_FUNC.matcher(rgbaText);
            if( !rgbaMatcher.matches() ) {
                return null;
            }
        }

        return ColorObj.fromRGBA(Integer.parseInt(rgbaMatcher.group(1)),
                                 Integer.parseInt(rgbaMatcher.group(2)),
                                 Integer.parseInt(rgbaMatcher.group(3)),
                                 rgbaMatcher.groupCount() > 3 ? Math.round(Float.parseFloat(rgbaMatcher.group(4)) * 255.0F) : 255);
    }

    public static ColorObj getShadowColor(ColorObj baseColor) {
        return ColorObj.fromRGBA(baseColor.fRed() * 0.25F, baseColor.fGreen() * 0.25F, baseColor.fBlue() * 0.25F, baseColor.fAlpha());
    }

    public static int getShadowColor(int baseColor) {
        return getShadowColor(ColorObj.fromARGB(baseColor)).getColorInt();
    }

    public static ColorObj getBorderColor(ColorObj baseColor) {
        return baseColor.getPerceptiveLuminance() > 0.5F ? ColorObj.BLACK : ColorObj.WHITE;
    }

    public static int getBorderColor(int baseColor) {
        return getBorderColor(ColorObj.fromARGB(baseColor)).getColorInt();
    }

    public static Integer parseColorString(String colorStr, Integer defaultColor) {
        if( MiscUtils.isEmpty(colorStr) ) {
            return defaultColor;
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
                return MiscUtils.apply(ColorUtils.getColorFromRgba(colorStr), ColorObj::getColorInt);
            }
        }
    }
}
