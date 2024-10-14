/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2016-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.sanlib.lib;

import java.util.Objects;

/**
 * A class that contains color values
 */
@SuppressWarnings("unused")
public final class ColorObj
{
    public static final ColorObj WHITE = fromARGB(0xFFFFFFFF);
    public static final ColorObj BLACK = fromARGB(0xFF000000);

    private final int r;
    private final int g;
    private final int b;
    private final int a;

    public static ColorObj fromRGB(int red, int green, int blue) {return fromRGBA(red, green, blue, 255);}

    public static ColorObj fromRGB(float red, float green, float blue) {return fromRGBA(red, green, blue, 1.0F);}

    public static ColorObj fromRGBA(int red, int green, int blue, int alpha) {return new ColorObj(red, green, blue, alpha);}

    public static ColorObj fromRGBA(float red, float green, float blue, float alpha) {return new ColorObj(red, green, blue, alpha);}

    public static ColorObj fromRGB(int rgb) {return fromARGB(rgb | 0xFF000000);}

    /**
     * Creates a new instance with an integer as its color value.<br> The integer can be written as hexadecimal number, {@code 0xAARRGGBB}, whereas A
     * is alpha, R is red, G is green and B is blue.<br> If you have integers of format 0xRRGGBB, then it's recommended to add the desired alpha with
     * a bitwise OR, like: {@code clr | 0xAA000000}.
     *
     * @param argb The color value as integer
     */
    public static ColorObj fromARGB(int argb) {return new ColorObj((argb >> 16) & 0xFF, (argb >> 8) & 0xFF, argb & 0xFF, (argb >> 24) & 0xFF);}

    public static ColorObj fromHSL(float hue, float saturation, float luminance) {return fromHSLA(hue, saturation, luminance, 1.0F);}

    /**
     * Calculates the RGB values from the passed HSL values. This overrides the RGB components from this color object with the new colors.
     *
     * @param hue        The new hue value, from {@code 0.0F} - {@code 360.0F}
     * @param saturation The new saturation value, from {@code 0.0F} - {@code 1.0F}
     * @param luminance  The new luminance value, from {@code 0.0F} - {@code 1.0F}
     * @param alpha      The alpha value, from  {@code 0.0F} - {@code 1.0F}
     */
    public static ColorObj fromHSLA(float hue, float saturation, float luminance, float alpha) {
        hue %= 360.0F;

        float c = (1.0F - Math.abs(2.0F * luminance - 1.0F)) * saturation;
        float h = (hue / 60.0F);
        float x = c * (1.0F - Math.abs((h % 2.0F) - 1.0F));
        float m = luminance - c / 2.0F;

        float[] rgb = new float[] { 0, 0, 0 };
        if( h < 1.0F ) {
            rgb = new float[] { c, x, 0 };
        } else if( h < 2.0F ) {
            rgb = new float[] { x, c, 0 };
        } else if( h < 3.0F ) {
            rgb = new float[] { 0, c, x };
        } else if( h < 4.0F ) {
            rgb = new float[] { 0, x, c };
        } else if( h < 5.0F ) {
            rgb = new float[] { x, 0, c };
        } else if( h < 6.0F ) {
            rgb = new float[] { c, 0, x };
        }

        rgb = new float[] { (rgb[0] + m) * 255.0F, (rgb[1] + m) * 255.0F, (rgb[2] + m) * 255.0F };

        return new ColorObj(Math.round(rgb[0]), Math.round(rgb[1]), Math.round(rgb[2]), Math.round(alpha * 255.0F));
    }

    public ColorObj copy() {return new ColorObj(this.r, this.g, this.b, this.a);}

    public ColorObj copyWithRed(int red) {return new ColorObj(red, this.g, this.b, this.a);}

    public ColorObj copyWithRed(float red) {return new ColorObj(Math.round(red * 255.0F), this.g, this.b, this.a);}

    public ColorObj copyWithGreen(int green) {return new ColorObj(this.r, green, this.b, this.a);}

    public ColorObj copyWithGreen(float green) {return new ColorObj(this.r, Math.round(green * 255.0F), this.b, this.a);}

    public ColorObj copyWithBlue(int blue) {return new ColorObj(this.r, this.g, blue, this.a);}

    public ColorObj copyWithBlue(float blue) {return new ColorObj(this.r, this.g, Math.round(blue * 255.0F), this.a);}

    public ColorObj copyWithAlpha(int alpha) {return new ColorObj(this.r, this.g, this.b, alpha);}

    public ColorObj copyWithAlpha(float alpha) {return new ColorObj(this.r, this.g, this.b, Math.round(alpha * 255.0F));}

    /**
     * Creates a new instance with each color component as integer value.<br> Each value has a range of {@code 0 - 255}. Anything lower / higher will
     * be adjusted.
     *
     * @param red   The value for the red color component
     * @param green The value for the green color component
     * @param blue  The value for the blue color component
     * @param alpha The value for the alpha color component (transparency)
     */
    private ColorObj(int red, int green, int blue, int alpha) {
        this.r = Math.clamp(red, 0, 255);
        this.g = Math.clamp(green, 0, 255);
        this.b = Math.clamp(blue, 0, 255);
        this.a = Math.clamp(alpha, 0, 255);
    }

    /**
     * Creates a new instance with each color component as floating point value.<br> Each value has a range of {@code 0.0F - 1.0F}. Anything lower /
     * higher will be adjusted.
     *
     * @param red   The value for the red color component
     * @param green The value for the green color component
     * @param blue  The value for the blue color component
     * @param alpha The value for the alpha color component (transparency)
     */
    @SuppressWarnings("java:S3358")
    private ColorObj(float red, float green, float blue, float alpha) {
        this(Math.round(red * 255), Math.round(green * 255), Math.round(blue * 255), Math.round(alpha * 255));
    }

    /**
     * Getter for the red color component
     *
     * @return The red color value
     */
    public int red() { return this.r; }

    /**
     * Getter for the red color component
     *
     * @return The red color value as floating point number
     */
    public float fRed() { return this.r / 255.0F; }

    /**
     * Getter for the green color component
     *
     * @return The green color value
     */
    public int green() { return this.g; }

    /**
     * Getter for the green color component
     *
     * @return The green color value as floating point number
     */
    public float fGreen() { return this.g / 255.0F; }

    /**
     * Getter for the blue color component
     *
     * @return The blue color value
     */
    public int blue() { return this.b; }

    /**
     * Getter for the blue color component
     *
     * @return The blue color value as floating point number
     */
    public float fBlue() { return this.b / 255.0F; }

    /**
     * Getter for the alpha component (transparency)
     *
     * @return The alpha value
     */
    public int alpha() { return this.a; }

    /**
     * Getter for the alpha component (transparency)
     *
     * @return The alpha value as floating point number
     */
    public float fAlpha() { return this.a / 255.0F; }

    /**
     * Returns the color object as an integer. As a hexadecimal number, it looks like {@code 0xAARRGGBB}, whereas A is alpha, R is red, G is green and
     * B is blue.
     *
     * @return this color object as integer
     */
    public int getColorInt() { return (this.a << 24) | (this.r << 16) | (this.g << 8) | this.b; }

    /**
     * Converts this color object into the HSL colorspace.
     *
     * @return a floating point array containing the hue, saturation and luminance values, in this order.
     */
    public float[] convertToHSL() {
        float[] hsl = new float[3];
        int     min = Math.min(this.r, Math.min(this.g, this.b));
        int     max = Math.max(this.r, Math.max(this.g, this.b));

        hsl[2] = (min + max) / 510.0F;

        if( min == max ) {
            hsl[0] = 0.0F;
            hsl[1] = 0.0F;
        } else {
            if( hsl[2] < 0.5F ) {
                hsl[1] = (max - min) / (float) (max + min);
            } else {
                hsl[1] = (max - min) / (510.0F - max - min);
            }

            if( max == this.r ) {
                hsl[0] = ((this.g - this.b) / (float) (max - min)) * 60.0F;
            } else if( max == this.g ) {
                hsl[0] = (2.0F + (this.b - this.r) / (float) (max - min)) * 60.0F;
            } else {
                hsl[0] = (4.0F + (this.r - this.g) / (float) (max - min)) * 60.0F;
            }

            if( hsl[0] < 0.0F ) {
                hsl[0] += 360.0F;
            }
        }

        return hsl;
    }

    public float getPerceptiveLuminance() {
        return ((0.299f * this.r) + (0.587f * this.g) + (0.114f * this.b)) / 255.0F;
    }

    @Override
    @SuppressWarnings("NonFinalFieldReferenceInEquals")
    public boolean equals(Object obj) {
        if( obj instanceof ColorObj colorObj ) {
            return this.equals(colorObj, this.a == 255);
        } else {
            return super.equals(obj);
        }
    }

    public boolean equals(ColorObj clr, boolean checkAlpha) {
        return this.r == clr.r && this.g == clr.g && this.b == clr.b && (!checkAlpha || this.a == clr.a);
    }

    @Override
    public int hashCode() {
        return Objects.hash(r, g, b, a);
    }
}
