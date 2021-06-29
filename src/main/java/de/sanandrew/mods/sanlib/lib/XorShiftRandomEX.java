package de.sanandrew.mods.sanlib.lib;

import java.util.Random;
import java.util.Spliterator;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.IntConsumer;
import java.util.function.LongConsumer;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.StreamSupport;

/**
 * An implementation of the {@link XorShiftRandom} RNG to the {@link Random} class
 */
@SuppressWarnings("unused")
public class XorShiftRandomEX
        extends Random
{
    private static final String BAD_SIZE = "size must be non-negative";
    private static final String BAD_RANGE = "bound must be greater than origin";

    private final XorShiftRandom xsr;

    public XorShiftRandomEX() {
        this.xsr = new XorShiftRandom();
    }

    public XorShiftRandomEX(long seed) {
        this.xsr = new XorShiftRandom(seed);
    }

    @Override
    public synchronized void setSeed(long seed) {
        this.xsr.setSeed(seed);
    }

    @Override
    protected int next(int bits) {
        return (int) (this.xsr.rng() >>> (64 - bits));
    }

    @Override
    public void nextBytes(byte[] bytes) {
        this.xsr.randomBytes(bytes);
    }

    @Override
    public int nextInt() {
        return this.xsr.randomInt();
    }

    @Override
    public int nextInt(int bound) {
        return this.xsr.randomInt(bound);
    }

    @Override
    public long nextLong() {
        return this.xsr.randomLong();
    }

    @Override
    public boolean nextBoolean() {
        return this.xsr.randomBool();
    }

    @Override
    public float nextFloat() {
        return this.xsr.randomFloat();
    }

    @Override
    public double nextDouble() {
        return this.xsr.randomDouble();
    }

    @Override
    public synchronized double nextGaussian() {
        return this.xsr.randomGaussian();
    }

    @Override
    public IntStream ints() {
        return this.ints(Long.MAX_VALUE, Integer.MAX_VALUE, 0);
    }

    @Override
    public IntStream ints(long streamSize) {
        return this.ints(streamSize, Integer.MAX_VALUE, 0);
    }

    @Override
    public IntStream ints(int randomNumberOrigin, int randomNumberBound) {
        return this.ints(Long.MAX_VALUE, randomNumberOrigin, randomNumberBound);
    }

    @Override
    public IntStream ints(long streamSize, int randomNumberOrigin, int randomNumberBound) {
        this.checkStreamNrs(streamSize, randomNumberOrigin, randomNumberBound);

        return StreamSupport.intStream(new RandomIntsSpliterator(0L, streamSize, randomNumberOrigin, randomNumberBound), false);
    }

    @Override
    public LongStream longs() {
        return this.longs(Long.MAX_VALUE, Long.MAX_VALUE, 0);
    }

    @Override
    public LongStream longs(long streamSize) {
        return this.longs(streamSize, Long.MAX_VALUE, 0);
    }

    @Override
    public LongStream longs(long randomNumberOrigin, long randomNumberBound) {
        return this.longs(Long.MAX_VALUE, randomNumberOrigin, randomNumberBound);
    }

    @Override
    public LongStream longs(long streamSize, long randomNumberOrigin, long randomNumberBound) {
        this.checkStreamNrs(streamSize, randomNumberOrigin, randomNumberBound);

        return StreamSupport.longStream(new RandomLongsSpliterator(0L, streamSize, randomNumberOrigin, randomNumberBound), false);
    }

    @Override
    public DoubleStream doubles() {
        return this.doubles(Long.MAX_VALUE, Double.MAX_VALUE, 0);
    }

    @Override
    public DoubleStream doubles(long streamSize) {
        return this.doubles(streamSize, Double.MAX_VALUE, 0);
    }

    @Override
    public DoubleStream doubles(double randomNumberOrigin, double randomNumberBound) {
        return this.doubles(Long.MAX_VALUE, randomNumberOrigin, randomNumberBound);
    }

    @Override
    public DoubleStream doubles(long streamSize, double randomNumberOrigin, double randomNumberBound) {
        this.checkStreamNrs(streamSize, randomNumberOrigin, randomNumberBound);

        return StreamSupport.doubleStream(new RandomDoublesSpliterator(0L, streamSize, randomNumberOrigin, randomNumberBound), false);
    }

    private void checkStreamNrs(long streamSize, Number origin, Number bound) {
        if( streamSize < 0L ) {
            throw new IllegalArgumentException(BAD_SIZE);
        }

        if( origin.doubleValue() >= bound.doubleValue() ) {
            throw new IllegalArgumentException(BAD_RANGE);
        }
    }

    private static abstract class RandomPrimitiveSpliterator<T>
            implements Spliterator<T>
    {
        long index;
        final long fence;
        final T origin;
        final T bound;
        private final BiFunction<T, T, T> rng;

        RandomPrimitiveSpliterator(long index, long fence, T origin, T bound, BiFunction<T, T, T> rng) {
            this.index = index;
            this.fence = fence;
            this.origin = origin;
            this.bound = bound;
            this.rng = rng;
        }

        @Override
        public int characteristics() {
            return (Spliterator.SIZED | Spliterator.SUBSIZED | Spliterator.NONNULL | Spliterator.IMMUTABLE);
        }

        @Override
        public long estimateSize() {
            return this.fence - this.index;
        }

        boolean accept(Consumer<T> consumer, boolean remainder) {
            if( consumer == null ) {
                throw new NullPointerException();
            }

            long amt = remainder ? this.estimateSize() : 1L;
            if( amt > 0 ) {
                for( long i = 0; i < amt; i++ ) {
                    consumer.accept(this.rng.apply(this.origin, this.bound));
                }
                this.index += amt;

                return true;
            }

            return false;
        }
    }

    private final class RandomIntsSpliterator
            extends RandomPrimitiveSpliterator<Integer>
            implements Spliterator.OfInt
    {
        RandomIntsSpliterator(long index, long fence, int origin, int bound) {
            super(index, fence, origin, bound, XorShiftRandomEX.this.xsr::randomIntRange);
        }

        @Override
        public RandomIntsSpliterator trySplit() {
            long i = this.index, m = (i + this.fence) >>> 1;
            return (m <= i) ? null : new RandomIntsSpliterator(i, this.index = m, this.origin, this.bound);
        }

        @Override
        public boolean tryAdvance(IntConsumer consumer) {
            return this.accept(consumer::accept, false);
        }

        @Override
        public void forEachRemaining(IntConsumer consumer) {
            this.accept(consumer::accept, true);
        }
    }

    private final class RandomLongsSpliterator
            extends RandomPrimitiveSpliterator<Long>
            implements Spliterator.OfLong
    {
        RandomLongsSpliterator(long index, long fence, long origin, long bound) {
            super(index, fence, origin, bound, XorShiftRandomEX.this.xsr::randomLongRange);
        }

        @Override
        public RandomLongsSpliterator trySplit() {
            long i = this.index, m = (i + this.fence) >>> 1;
            return (m <= i) ? null : new RandomLongsSpliterator(i, this.index = m, this.origin, this.bound);
        }

        @Override
        public boolean tryAdvance(LongConsumer consumer) {
            return this.accept(consumer::accept, false);
        }

        @Override
        public void forEachRemaining(LongConsumer consumer) {
            this.accept(consumer::accept, true);
        }
    }

    private final class RandomDoublesSpliterator
            extends RandomPrimitiveSpliterator<Double>
            implements Spliterator.OfDouble
    {
        RandomDoublesSpliterator(long index, long fence, double origin, double bound) {
            super(index, fence, origin, bound, XorShiftRandomEX.this.xsr::randomDoubleRange);
        }

        @Override
        public RandomDoublesSpliterator trySplit() {
            long i = this.index, m = (i + this.fence) >>> 1;
            return (m <= i) ? null : new RandomDoublesSpliterator(i, this.index = m, this.origin, this.bound);
        }

        @Override
        public boolean tryAdvance(DoubleConsumer consumer) {
            return this.accept(consumer::accept, false);
        }

        @Override
        public void forEachRemaining(DoubleConsumer consumer) {
            this.accept(consumer::accept, true);
        }
    }
}
