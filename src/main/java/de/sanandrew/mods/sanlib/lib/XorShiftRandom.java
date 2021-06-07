////////////////////////////////////////////////////////////////////////////////
// This file is subject to the terms and conditions defined in the             /
// file '.github/LICENSE.md', which is part of this source code package.       /
////////////////////////////////////////////////////////////////////////////////

package de.sanandrew.mods.sanlib.lib;

import net.minecraft.util.math.vector.Vector3d;

import java.util.concurrent.atomic.AtomicLong;

/**
 * A faster, more reliable pseudorandom number generator than {@link java.util.Random}.
 * This class uses a 64-bit seed, which is modified by multiple XOR and bit-shift operations (thus the name "XorShift").<br>
 * The idea came from dmurphy747, <a href="https://dmurphy747.wordpress.com/2011/03/23/xorshift-vs-random-performance-in-java/">XORShift vs Random performance in Java</a>.
 * Almost everything works like {@link java.util.Random}, although it will return different results.
 */
@SuppressWarnings({"unused"})
public final class XorShiftRandom
{
    private long seed;
    private double nextGaussian;
    private boolean haveNextGaussian;

    private static final double DOUBLE_UNIT = 0x1.0p-53;
    private static final String BAD_BOUND = "bound must be positive";
    private static final long MASK = (1L << 48) - 1;
    private static final AtomicLong SEED_UNIQUIFIER = new AtomicLong(8682522807148012L);

    /**
     * Creates a new instance of this RNG with a random seed based off the system clock.
     */
    public XorShiftRandom() {
        this.setSeed(seedUniquifier() ^ System.nanoTime());
    }

    /**
     * Creates a new instance of this RNG with a predetermined seed.
     * @param seed The predetermined seed
     * @see #setSeed(long)
     */
    public XorShiftRandom(long seed) {
        this.setSeed(seed);
    }

    /**
     * sets the new seed for this RNG instance. If {@code seed} is 0, the seed will be {@code 0xdeadbeef}, since the algorithm doesn't work with a 0 seed.
     * @param seed The new seed
     */
    public void setSeed(long seed) {
        this.seed = seed == 0 ? 0xdeadbeef : seed;
        this.haveNextGaussian = false;
    }

    /**
     * from {@link java.util.Random}<br>
     * <i>L'Ecuyer, "Tables of Linear Congruential Generators of
     * Different Sizes and Good Lattice Structure", 1999</i>
     */
    private static long seedUniquifier() {
        while( true ) {
            long current = SEED_UNIQUIFIER.get();
            long next = current * 0x285D320AD33FDB5L;
            if( SEED_UNIQUIFIER.compareAndSet(current, next) ) {
                return next;
            }
        }
    }

    /**
     * The algorithm used to get the next random seed.
     * @return the new randomized seed.
     */
    private long rng() {
        this.seed ^= (this.seed << 21);
        this.seed ^= (this.seed >>> 35);
        this.seed ^= (this.seed << 4);

        return this.seed;
    }

    /**
     * Calculates a new randomized 64-bit number
     * @see java.util.Random#nextLong()
     * @return the new randomized long number
     */
    public long randomLong() {
        return ((this.rng() & 0xFFFFFFFF00000000L) | (this.rng() >>> 32));
    }

    /**
     * Calculates a new randomized 32-bit number
     * @see java.util.Random#nextInt()
     * @return the new randomized int number
     */
    public int randomInt() {
        return (int) (rng() >>> 32);
    }

    /**
     * Calculates a new randomized 32-bit number, whereas the range is from 0 (inclusive) to {@code bound} (exclusive).
     * @see java.util.Random#nextInt(int bound)
     * @param bound The upper bound (exclusive)
     * @return the new randomized int number
     * @throws IllegalArgumentException if the bound is smaller than 1
     */
    public int randomInt(int bound) {
        if( bound <= 0 ) {
            throw new IllegalArgumentException(BAD_BOUND);
        }

        return (int) (rng() >>> 33) % bound;
    }

    /**
     * Calculates a new randomized 16-bit number
     * @return the new randomized short number
     */
    public short randomShort() {
        return (short) (rng() >>> 48);
    }

    /**
     * Calculates a new randomized 8-bit number
     * @return the new randomized byte number
     */
    public byte randomByte() {
        return (byte) (rng() >>> 56);
    }

    /**
     * Calculates a new randomized 4-bit number
     * @return the new randomized nibble number
     */
    public byte randomNibble() {
        return (byte) (rng() >>> 60);
    }

    /**
     * Calculates a new randomized boolean value
     * @see java.util.Random#nextBoolean()
     * @return the new randomized boolean value
     */
    public boolean randomBool() {
        return (rng() >>> 63) == 1;
    }

    /**
     * Calculates a new randomized 64-bit floating point number within the range of 0.0 (included) to 1.0 (excluded)
     * @see java.util.Random#nextDouble()
     * @return the new randomized double value
     */
    public double randomDouble() {
        return (rng() >>> 11) * DOUBLE_UNIT;
    }

    /**
     * Calculates a new randomized 32-bit floating point number within the range of 0.0 (included) to 1.0 (excluded)
     * @see java.util.Random#nextFloat()
     * @return the new randomized float value
     */
    public float randomFloat() {
        return (rng() >>> 40) / (float) (1 << 24);
    }


    /**
     * Calculates a new randomized 64-bit, gaussian distributed floating point number
     * @see java.util.Random#nextGaussian()
     * @return the new randomized double value
     */
    public double randomGaussian() {
        // See Knuth, ACP, Section 3.4.1 Algorithm C.
        if( this.haveNextGaussian ) {
            this.haveNextGaussian = false;
            return this.nextGaussian;
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
            this.nextGaussian = nextNr * multiplier;
            this.haveNextGaussian = true;
            return currNr * multiplier;
        }
    }

    public Vector3d randomVector(Vector3d from, Vector3d to) {
        return new Vector3d(this.randomDouble() * (to.x - from.x) + from.x,
                            this.randomDouble() * (to.y - from.y) + from.y,
                            this.randomDouble() * (to.z - from.z) + from.z);
    }
}
