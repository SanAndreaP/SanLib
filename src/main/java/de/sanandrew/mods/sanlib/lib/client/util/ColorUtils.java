/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.sanlib.lib.client.util;

import de.sanandrew.mods.sanlib.lib.ColorObj;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

/**
 * An utility class for colors
 */
@SuppressWarnings("unused")
public final class ColorUtils
{
    /**
     * Gets the average color from an image.
     * The alpha value of each pixel is used for the color weightness, thus the more transparent the pixel is,
     * the less it is counted towards the average
     * @param is The image as InputStream
     * @return the average color
     * @throws java.io.IOException when the InputStream cannot be read as an image
     */
    public static ColorObj getAverageColor(InputStream is) throws IOException {
        return getAverageColor(is, null);
    }

    /**
     * Gets the average color from an image.
     * The alpha value of each pixel is used for the color weightness, thus the more transparent the pixel is,
     * the less it is counted towards the average.
     * The mask color ({@code maskClr}) will determine which pixels will not be counted towards the average.
     *
     * @param is      The image as InputStream
     * @param maskClr The color to be masked / ignored; Can be {@code null} for no mask color
     * @return the average color
     * @throws java.io.IOException when the InputStream cannot be read as an image
     */
    public static ColorObj getAverageColor(InputStream is, ColorObj maskClr) throws IOException {
        // read texture as BufferedImage from InputStream
        BufferedImage bi = ImageIO.read(is);

        // holds the added RGB values of the whole texture and pixel counter
        double red = 0.0D;
        double green = 0.0D;
        double blue = 0.0D;
        double count = 0.0D;
        for( int x = 0; x < bi.getWidth(); x++ ) {          // loop through the pixels
            for( int y = 0; y < bi.getHeight(); y++ ) {
                ColorObj color = new ColorObj(bi.getRGB(x, y));

                if( maskClr != null ) {
                    if( color.equals(maskClr) ) {
                        continue;
                    }
                }

                red += color.red() * color.fAlpha();              // add RGB from the pixel to the RGB storage variables, increase pixel counter
                green += color.green() * color.fAlpha();
                blue += color.blue() * color.fAlpha();
                count += color.fAlpha();
            }
        }

        int avgRed = (int) (red / count);       // calculating the average of each channel
        int avgGreen = (int) (green / count);
        int avgBlue = (int) (blue / count);

        return new ColorObj(avgRed, avgGreen, avgBlue, 255); // return combined RGB channels
    }
}
