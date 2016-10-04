package de.sanandrew.mods.sanlib.lib.function;

import java.util.Objects;

/**
 * Represents an operation that accepts three input arguments and returns no result.
 * This is the three-arity specialization of {@link java.util.function.Consumer}.
 * Unlike most other functional interfaces, {@code TriConsumer} is expected to operate via side-effects.
 * <p>This is a <a href="package-summary.html">functional interface</a> whose functional method is {@link #accept(Object, Object, Object)}.</p>
 * @param <T> the type of the first argument to the operation
 * @param <U> the type of the second argument to the operation
 * @param <V> the type of the third argument to the operation
 * @see java.util.function.Consumer
 */
@FunctionalInterface
public interface TriConsumer<T, U, V>
{
    void accept(T t, U u, V v);

    default TriConsumer<T, U, V> andThen(TriConsumer<? super T, ? super U, ? super V> after) {
        Objects.requireNonNull(after);

        return (l, r, s) -> {
            accept(l, r, s);
            after.accept(l, r, s);
        };
    }
}
