/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.sanlib.lib.client;

/**
 * A class that contains color values
 */
@SuppressWarnings("unused")
public final class ColorObj
{
    private int r;
    private int g;
    private int b;
    private int a;

    /**
     * Creates a new instance with an integer as its color value.<br>
     * The integer can be written as hexadecimal number, {@code 0xAARRGGBB}, where as A is alpha, R is red, G is green and B is blue.<br>
     * If you have integers of format 0xRRGGBB, then it's recommended to add the desired alpha with a bitwise OR, like: {@code clr | 0xAA000000}.
     * @param argb The color value as integer
     */
    public ColorObj(int argb) {
        this.r = (argb >> 16) & 0xFF;
        this.g = (argb >> 8) & 0xFF;
        this.b = (argb) & 0xFF;
        this.a = (argb >> 24) & 0xFF;
    }

    /**
     * Creates a new instance with each color component as integer value.<br>
     * Each value has a range of {@code 0 - 255}. Anything lower / higher will be adjusted.
     * @param red The value for the red color component
     * @param green The value for the green color component
     * @param blue The value for the blue color component
     * @param alpha The value for the alpha color component (transparency)
     */
    public ColorObj(int red, int green, int blue, int alpha) {
        this.r = red < 0 ? 0 : red > 255 ? 255 : red;
        this.g = green < 0 ? 0 : green > 255 ? 255 : green;
        this.b = blue < 0 ? 0 : blue > 255 ? 255 : blue;
        this.a = alpha < 0 ? 0 : alpha > 255 ? 255 : alpha;
    }

    /**
     * Creates a new instance with each color component as floating point value.<br>
     * Each value has a range of {@code 0.0F - 1.0F}. Anything lower / higher will be adjusted.
     * @param red The value for the red color component
     * @param green The value for the green color component
     * @param blue The value for the blue color component
     * @param alpha The value for the alpha color component (transparency)
     */
    public ColorObj(float red, float green, float blue, float alpha) {
        this.r = (int) (red < 0.0F ? 0.0F : red > 1.0F ? 255.0F : red * 255.0F);
        this.g = (int) (green < 0.0F ? 0.0F : green > 1.0F ? 255.0F : green * 255.0F);
        this.b = (int) (blue < 0.0F ? 0.0F : blue > 1.0F ? 255.0F : blue * 255.0F);
        this.a = (int) (alpha < 0.0F ? 0.0F : alpha > 1.0F ? 255.0F : alpha * 255.0F);
    }

    /**
     * Getter for the red color component
     * @return The red color value
     */
    public int red() {
        return this.r;
    }

    /**
     * Getter for the red color component
     * @return The red color value as floating point number
     */
    public float fRed() {
        return this.r / 255.0F;
    }

    /**
     * Getter for the green color component
     * @return The green color value
     */
    public int green() {
        return this.g;
    }
    /**
     * Getter for the green color component
     * @return The green color value as floating point number
     */
    public float fGreen() {
        return this.g / 255.0F;
    }

    /**
     * Getter for the blue color component
     * @return The blue color value
     */
    public int blue() {
        return this.b;
    }

    /**
     * Getter for the blue color component
     * @return The blue color value as floating point number
     */
    public float fBlue() {
        return this.b / 255.0F;
    }

    /**
     * Getter for the alpha component (transparency)
     * @return The alpha value
     */
    public int alpha() {
        return this.a;
    }

    /**
     * Getter for the alpha component (transparency)
     * @return The alpha value as floating point number
     */
    public Float fAlpha() {
        return this.a / 255.0F;
    }

    /**
     * Setter for the red color component
     * @param red The red color value
     */
    public void setRed(int red) {
        this.r = red < 0 ? 0 : red > 255 ? 255 : red;
    }

    /**
     * Setter for the green color component
     * @param green The green color value
     */
    public void setGreen(int green) {
        this.g = green < 0 ? 0 : green > 255 ? 255 : green;
    }

    /**
     * Setter for the blue color component
     * @param blue The blue color value
     */
    public void setBlue(int blue) {
        this.b = blue < 0 ? 0 : blue > 255 ? 255 : blue;
    }

    /**
     * Setter for the alpha component (transparency)
     * @param alpha The alpha value
     */
    public void setAlpha(int alpha) {
        this.a = alpha < 0 ? 0 : alpha > 255 ? 255 : alpha;
    }

    @Override
    @SuppressWarnings("NonFinalFieldReferenceInEquals")
    public boolean equals(Object obj) {
        if( obj instanceof ColorObj ) {
            return this.equals((ColorObj) obj, this.a == 255);
        } else {
            return super.equals(obj);
        }
    }

    public boolean equals(ColorObj clr, boolean checkAlpha) {
        return this.r == clr.r && this.g == clr.g && this.b == clr.b && (!checkAlpha || this.a == clr.a);
    }
}
