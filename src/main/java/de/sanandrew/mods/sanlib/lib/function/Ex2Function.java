package de.sanandrew.mods.sanlib.lib.function;

import java.util.Objects;

@SuppressWarnings("unused")
@FunctionalInterface
public interface Ex2Function<T, R, E1 extends Throwable, E2 extends Throwable>
{
    /**
     * Applies this function to the given argument.
     *
     * @param t the function argument
     * @return the function result
     */
    R apply(T t) throws E1, E2;

    /**
     * Returns a composed function that first applies the {@code before}
     * function to its input, and then applies this function to the result.
     * If evaluation of either function throws an exception, it is relayed to
     * the caller of the composed function.
     *
     * @param <V> the type of input to the {@code before} function, and to the
     *           composed function
     * @param before the function to apply before this function is applied
     * @return a composed function that first applies the {@code before}
     * function and then applies this function
     * @throws NullPointerException if before is null
     *
     * @see #andThen(Ex2Function)
     */
    default <V> Ex2Function<V, R, E1, E2> compose(Ex2Function<? super V, ? extends T, ? extends E1, ? extends E2> before) throws E1, E2 {
        Objects.requireNonNull(before);
        return (V v) -> apply(before.apply(v));
    }

    /**
     * Returns a composed function that first applies this function to
     * its input, and then applies the {@code after} function to the result.
     * If evaluation of either function throws an exception, it is relayed to
     * the caller of the composed function.
     *
     * @param <V> the type of output of the {@code after} function, and of the
     *           composed function
     * @param after the function to apply after this function is applied
     * @return a composed function that first applies this function and then
     * applies the {@code after} function
     * @throws NullPointerException if after is null
     *
     * @see #compose(Ex2Function)
     */
    default <V> Ex2Function<T, V, E1, E2> andThen(Ex2Function<? super R, ? extends V, ? extends E1, ? extends E2> after) throws E1, E2 {
        Objects.requireNonNull(after);
        return (T t) -> after.apply(apply(t));
    }


    /**
     * Returns a function that always returns its input argument.
     *
     * @param <T> the type of the input and output objects to the function
     * @return a function that always returns its input argument
     */
    static <T> Ex2Function<T, T, Throwable, Throwable> identity() {
        return t -> t;
    }
}
