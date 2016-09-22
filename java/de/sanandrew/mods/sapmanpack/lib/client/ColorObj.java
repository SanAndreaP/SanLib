/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.sapmanpack.lib.client;

@SuppressWarnings("unused")
public final class ColorObj
{
    private int r;
    private int g;
    private int b;
    private int a;

    public ColorObj(int argb) {
        this.r = (argb >> 16) & 0xFF;
        this.g = (argb >> 8) & 0xFF;
        this.b = (argb) & 0xFF;
        this.a = (argb >> 24) & 0xFF;
    }

    public ColorObj(int r, int g, int b, int a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    public ColorObj(float r, float g, float b, float a) {
        this.r = (int) (r * 255.0F);
        this.g = (int) (g * 255.0F);
        this.b = (int) (b * 255.0F);
        this.a = (int) (a * 255.0F);
    }

    public int r() {
        return this.r;
    }

    public float fr() {
        return this.r / 255.0F;
    }

    public int g() {
        return this.g;
    }

    public float fg() {
        return this.g / 255.0F;
    }

    public int b() {
        return this.b;
    }

    public float fb() {
        return this.b / 255.0F;
    }

    public int a() {
        return this.a;
    }

    public float fa() {
        return this.a / 255.0F;
    }

    public void setRed(int r) {
        this.r = r;
    }

    public void setGreen(int g) {
        this.g = g;
    }

    public void setBlue(int b) {
        this.b = b;
    }

    public void setAlpha(int a) {
        this.a = a;
    }

    @Override
    public boolean equals(Object obj) {
        if( obj instanceof ColorObj ) {
            return this.equals((ColorObj) obj, false);
        } else {
            return super.equals(obj);
        }
    }

    public boolean equals(ColorObj clr, boolean checkAlpha) {
        return this.r == clr.r && this.g == clr.g && this.b == clr.b && (!checkAlpha || this.a == clr.a);
    }
}
