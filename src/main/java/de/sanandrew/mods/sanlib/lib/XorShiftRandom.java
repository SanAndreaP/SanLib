////////////////////////////////////////////////////////////////////////////////
// This file is subject to the terms and conditions defined in the             /
// file '.github/LICENSE.md', which is part of this source code package.       /
////////////////////////////////////////////////////////////////////////////////

package de.sanandrew.mods.sanlib.lib;

import com.google.common.util.concurrent.AtomicDouble;
import net.minecraft.util.math.vector.Vector3d;

import javax.annotation.Nonnull;
import java.util.concurrent.atomic.AtomicBoolean;
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
    private final AtomicLong   seed = new AtomicLong(0xdeadbeef);
    private final AtomicDouble        nextGaussian = new AtomicDouble();
    private final AtomicBoolean hasNextGaussian = new AtomicBoolean();

    private static final double DOUBLE_UNIT = 0x1.0p-53;
    private static final long MASK = (1L << 48) - 1;
    private static final AtomicLong SEED_UNIQUIFIER = new AtomicLong(8682522807148012L);

    static final String BAD_BOUND = "bound must be positive";

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
        if( seed == 0 ) {
            seed = 0xdeadbeef;
        }

        this.seed.set(seed);

        this.hasNextGaussian.set(false);
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
    long rng() {
        long cs = this.seed.get();

        cs ^= (cs << 21);
        cs ^= (cs >>> 35);
        cs ^= (cs << 4);

        this.seed.set(cs);

        return cs;
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
     * Calculates new randomized values for the provided byte array
     * @param bytes the array to be randomized
     * @implNote similar to {@link java.util.Random#nextBytes(byte[])}
     */
    public void randomBytes(@Nonnull byte[] bytes) {
        for( int i = 0, max = bytes.length; i < max; ) {
            for( int r = randomInt(), n = Integer.SIZE / Byte.SIZE; n > 0 && i < max; r >>= 8, n-- ) {
                bytes[i++] = (byte) (r & 0xFF);
            }
        }
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

    public int randomIntRange(int origin, int bound) {
        if( origin <= bound ) {
            return this.randomInt(bound - origin) + origin;
        }

        return this.randomInt();
    }

    public long randomLongRange(long origin, long bound) {
        if( origin <= bound ) {
            return (this.randomLong() >>> 1) % (bound - origin) + origin;
        }

        return this.randomLong();
    }

    public double randomDoubleRange(double origin, double bound) {
        double r = this.randomDouble();
        if( origin <= bound ) {
            r = (randomDouble()) * (bound - origin) + origin;
            if( r >= bound ) {
                r = Double.longBitsToDouble(Double.doubleToLongBits(r) - 1L);
            }
        }

        return r;
    }


    /**
     * Calculates a new randomized 64-bit, gaussian distributed floating point number
     * @see java.util.Random#nextGaussian()
     * @return the new randomized double value
     */
    public double randomGaussian() {
        // See Knuth, ACP, Section 3.4.1 Algorithm C.
        if( this.hasNextGaussian.get() ) {
            this.hasNextGaussian.set(false);
            return this.nextGaussian.get();
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
            this.nextGaussian.set(nextNr * multiplier);
            this.hasNextGaussian.set(true);
            return currNr * multiplier;
        }
    }

    public Vector3d randomVector(Vector3d from, Vector3d to) {
        Vector3d distVec = from.vectorTo(to);
        distVec.scale(this.randomDouble());

        return from.add(distVec);
    }
}
