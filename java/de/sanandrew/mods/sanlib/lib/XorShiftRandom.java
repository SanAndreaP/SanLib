/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.sanlib.lib;

@SuppressWarnings({"unused", "WeakerAccess"})
public final class XorShiftRandom
{
    private long x;
    private static final double DOUBLE_UNIT = 0x1.0p-53;
    private static final String BAD_BOUND = "bound must be positive";

    public XorShiftRandom() {
        this(System.nanoTime());
    }

    public XorShiftRandom(long seed) {
        this.x = seed==0 ? 0xdeadbeef : seed;
    }

    public long randomLong() {
        this.x ^= (this.x << 21);
        this.x ^= (this.x >>> 35);
        this.x ^= (this.x << 4);
        return this.x;
    }

    public int randomInt() {
        long l = this.randomLong();
        return (int) ((l & 0xFFFFFFFFL) ^ ((l >> 32) & 0xFFFFFFFFL));
    }

    public int randomInt(int bound) {
        if( bound <= 0 ) {
            throw new IllegalArgumentException(BAD_BOUND);
        }

        return (randomInt() & Integer.MAX_VALUE) % bound;
    }

    public short randomShort() {
        long l = this.randomLong();
        l = ((l & 0xFFFFFFFFL) ^ ((l >> 32) & 0xFFFFFFFFL));
        return (short) ((l & 0xFFFF) ^ ((l >> 16) & 0xFFFF));
    }

    public byte randomByte() {
        long l = this.randomLong();
        l = ((l & 0xFFFFFFFFL) ^ ((l >> 32) & 0xFFFFFFFFL));
        l = ((l & 0xFFFF) ^ ((l >> 16) & 0xFFFF));
        return (byte) ((l & 0xFF) ^ ((l >> 8) & 0xFF));
    }

    public byte randomNibble() {
        long l = this.randomLong();
        l = ((l & 0xFFFFFFFFL) ^ ((l >> 32) & 0xFFFFFFFFL));
        l = ((l & 0xFFFF) ^ ((l >> 16) & 0xFFFF));
        l = ((l & 0xFF) ^ ((l >> 8) & 0xFF));
        return (byte) ((l & 0xF) ^ ((l >> 4) & 0xF));
    }

    public boolean randomBool() {
        long l = this.randomLong();
        l = ((l & 0xFFFFFFFFL) ^ ((l >> 32) & 0xFFFFFFFFL));
        l = ((l & 0xFFFF) ^ ((l >> 16) & 0xFFFF));
        l = ((l & 0xFF) ^ ((l >> 8) & 0xFF));
        l = ((l & 0xF) ^ ((l >> 4) & 0xF));
        return ((l & 1) ^ ((l >> 1) & 1)) == 1;
    }

    public double randomDouble() {
        return (this.randomLong() & 0x1FFFFFFFFFFFFFL) * DOUBLE_UNIT;
    }

    public float randomFloat() {
        return (this.randomInt() & 0xFFFFFF) / (float) 0x1000000;
    }

    private double nextNextGaussian;
    private boolean haveNextNextGaussian = false;
    public double nextGaussian() {
        // See Knuth, ACP, Section 3.4.1 Algorithm C.
        if( this.haveNextNextGaussian ) {
            this.haveNextNextGaussian = false;
            return this.nextNextGaussian;
        } else {
            double currNr;
            double nextNr;
            double s;

            do {
                currNr = 2 * randomDouble() - 1;
                nextNr = 2 * randomDouble() - 1;
                s = currNr * currNr + nextNr * nextNr;
            } while( s >= 1 || s == 0 );

            double multiplier = StrictMath.sqrt(-2 * StrictMath.log(s) / s);
            this.nextNextGaussian = nextNr * multiplier;
            this.haveNextNextGaussian = true;
            return currNr * multiplier;
        }
    }
}
