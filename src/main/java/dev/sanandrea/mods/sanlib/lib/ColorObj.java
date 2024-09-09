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
    public static final ColorObj WHITE = new ColorObj(0xFFFFFFFF);
    public static final ColorObj BLACK = new ColorObj(0xFF000000);

    private int r;
    private int g;
    private int b;
    private int a;


    /**
     * Creates a new color object from the passed HSLA values.
     *
     * @param hue        The hue value, from {@code 0.0F} - {@code 360.0F}
     * @param saturation The saturation value, from {@code 0.0F} - {@code 1.0F}
     * @param luminance  The luminance value, from {@code 0.0F} - {@code 1.0F}
     * @param alpha      The alpha value, from  {@code 0.0F} - {@code 1.0F}
     */
    public static ColorObj fromHSLA(float hue, float saturation, float luminance, float alpha) {
        ColorObj obj = new ColorObj();
        obj.calcAndSetRgbFromHsl(hue, saturation, luminance);

        if( alpha < 0.0F ) {
            obj.a = 0;
        } else {
            obj.a = alpha > 1.0F ? 255 : (int) (alpha * 255.0F);
        }

        return obj;
    }

    public static ColorObj fromRGB(int rgb) {
        return new ColorObj(rgb | 0xFF000000);
    }

    private ColorObj() {}

    /**
     * Creates a new instance with an integer as its color value.<br> The integer can be written as hexadecimal number, {@code 0xAARRGGBB}, where as A is alpha, R is red, G is
     * green and B is blue.<br> If you have integers of format 0xRRGGBB, then it's recommended to add the desired alpha with a bitwise OR, like: {@code clr | 0xAA000000}.
     *
     * @param argb The color value as integer
     */
    public ColorObj(int argb) {
        this.a = (argb >> 24) & 0xFF;
        this.r = (argb >> 16) & 0xFF;
        this.g = (argb >> 8) & 0xFF;
        this.b = argb & 0xFF;
    }

    /**
     * Creates a new instance with each color component as integer value.<br> Each value has a range of {@code 0 - 255}. Anything lower / higher will be adjusted.
     *
     * @param red   The value for the red color component
     * @param green The value for the green color component
     * @param blue  The value for the blue color component
     * @param alpha The value for the alpha color component (transparency)
     */
    public ColorObj(int red, int green, int blue, int alpha) {
        this.setRed(red);
        this.setGreen(green);
        this.setBlue(blue);
        this.setAlpha(alpha);
    }

    /**
     * Creates a new instance with each color component as floating point value.<br> Each value has a range of {@code 0.0F - 1.0F}. Anything lower / higher will be adjusted.
     *
     * @param red   The value for the red color component
     * @param green The value for the green color component
     * @param blue  The value for the blue color component
     * @param alpha The value for the alpha color component (transparency)
     */
    @SuppressWarnings("java:S3358")
    public ColorObj(float red, float green, float blue, float alpha) {
        this.r = (int) (red < 0.0F ? 0.0F : (red > 1.0F ? 255.0F : red * 255.0F));
        this.g = (int) (green < 0.0F ? 0.0F : (green > 1.0F ? 255.0F : green * 255.0F));
        this.b = (int) (blue < 0.0F ? 0.0F : (blue > 1.0F ? 255.0F : blue * 255.0F));
        this.a = (int) (alpha < 0.0F ? 0.0F : (alpha > 1.0F ? 255.0F : alpha * 255.0F));
    }

    /**
     * Creates a new instance with all color components from the passed color object.<br> This will basically be an independent copy of the original.
     *
     * @param orig the color object to be copied
     */
    public ColorObj(ColorObj orig) {
        this.r = orig.r;
        this.g = orig.g;
        this.b = orig.b;
        this.a = orig.a;
    }

    /**
     * Getter for the red color component
     *
     * @return The red color value
     */
    public int red() {
        return this.r;
    }

    /**
     * Getter for the red color component
     *
     * @return The red color value as floating point number
     */
    public float fRed() {
        return this.r / 255.0F;
    }

    /**
     * Getter for the green color component
     *
     * @return The green color value
     */
    public int green() {
        return this.g;
    }

    /**
     * Getter for the green color component
     *
     * @return The green color value as floating point number
     */
    public float fGreen() {
        return this.g / 255.0F;
    }

    /**
     * Getter for the blue color component
     *
     * @return The blue color value
     */
    public int blue() {
        return this.b;
    }

    /**
     * Getter for the blue color component
     *
     * @return The blue color value as floating point number
     */
    public float fBlue() {
        return this.b / 255.0F;
    }

    /**
     * Getter for the alpha component (transparency)
     *
     * @return The alpha value
     */
    public int alpha() {
        return this.a;
    }

    /**
     * Getter for the alpha component (transparency)
     *
     * @return The alpha value as floating point number
     */
    public float fAlpha() {
        return this.a / 255.0F;
    }

    public float fAlpha(float min) {
        return Math.max(min, this.a / 255.0F);
    }

    /**
     * Setter for the red color component
     *
     * @param red The red color value
     */
    public void setRed(int red) {
        this.r = Math.clamp(red, 0, 255);
    }

    /**
     * Setter for the green color component
     *
     * @param green The green color value
     */
    public void setGreen(int green) {
        this.g = Math.clamp(green, 0, 255);
    }

    /**
     * Setter for the blue color component
     *
     * @param blue The blue color value
     */
    public void setBlue(int blue) {
        this.b = Math.clamp(blue, 0, 255);
    }

    /**
     * Setter for the alpha component (transparency)
     *
     * @param alpha The alpha value
     */
    public void setAlpha(int alpha) {
        this.a = Math.clamp(alpha, 0, 255);
    }

    /**
     * Returns the color object as an integer. As a hexadecimal number, it looks like {@code 0xAARRGGBB}, where as A is alpha, R is red, G is green and B is blue.
     *
     * @return this color object as integer
     */
    public int getColorInt() {
        return (this.a << 24) | (this.r << 16) | (this.g << 8) | this.b;
    }

    /**
     * Converts this color object into the HSL colorspace.
     *
     * @return a floating point array containing the hue, saturation and luminance values, in this order.
     */
    public float[] calcHSL() {
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

    /**
     * Calculates the RGB values from the passed HSL values. This overrides the RGB components from this color object with the new colors.
     *
     * @param hue        The new hue value, from {@code 0.0F} - {@code 360.0F}
     * @param saturation The new saturation value, from {@code 0.0F} - {@code 1.0F}
     * @param luminance  The new luminance value, from {@code 0.0F} - {@code 1.0F}
     */
    public void calcAndSetRgbFromHsl(float hue, float saturation, float luminance) {
        hue %= 360.0F;

        float c = (1.0F - Math.abs(2.0F * luminance - 1.0F)) * saturation;
        float x = c * (1.0F - Math.abs((hue / 60.0F) % 2.0F - 1.0F));
        float m = luminance - c / 2.0F;

        float[] rgb = new float[] { 0, 0, 0 };
        if( hue < 60.0F ) {
            rgb = new float[] { c, x, 0 };
        } else if( hue < 120.0F ) {
            rgb = new float[] { x, c, 0 };
        } else if( hue < 180.0F ) {
            rgb = new float[] { 0, c, x };
        } else if( hue < 240.0F ) {
            rgb = new float[] { 0, x, c };
        } else if( hue < 300.0F ) {
            rgb = new float[] { x, 0, c };
        } else if( hue < 360.0F ) {
            rgb = new float[] { c, 0, x };
        }

        rgb = new float[] { (rgb[0] + m) * 255.0F, (rgb[1] + m) * 255.0F, (rgb[2] + m) * 255.0F };

        this.r = Math.round(rgb[0]);
        this.g = Math.round(rgb[1]);
        this.b = Math.round(rgb[2]);
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
