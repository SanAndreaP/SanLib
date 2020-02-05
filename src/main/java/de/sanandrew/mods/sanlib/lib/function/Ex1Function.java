package de.sanandrew.mods.sanlib.lib.function;

import java.util.Objects;

@SuppressWarnings("unused")
@FunctionalInterface
public interface Ex1Function<T, R, E extends Throwable>
{
    /**
     * Applies this function to the given argument.
     *
     * @param t the function argument
     * @return the function result
     */
    R apply(T t) throws E;

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
     * @see #andThen(Ex1Function)
     */
    default <V> Ex1Function<V, R, E> compose(Ex1Function<? super V, ? extends T, ? extends E> before) throws E {
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
     * @see #compose(Ex1Function)
     */
    default <V> Ex1Function<T, V, E> andThen(Ex1Function<? super R, ? extends V, ? extends E> after) throws E {
        Objects.requireNonNull(after);
        return (T t) -> after.apply(apply(t));
    }


    /**
     * Returns a function that always returns its input argument.
     *
     * @param <T> the type of the input and output objects to the function
     * @return a function that always returns its input argument
     */
    static <T> Ex1Function<T, T, Throwable> identity() {
        return t -> t;
    }
}
