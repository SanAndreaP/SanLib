package de.sanandrew.mods.sanlib.lib.function;

import javax.annotation.Nonnull;
import java.util.function.Function;

@FunctionalInterface
public interface TriFunction<T, U, V, R>
{
    R apply(T t, U u, V v);

    default <X> TriFunction<T, U, V, X> andThen(@Nonnull Function<? super R, ? extends X> after) {
        return (T t, U u, V v) -> after.apply(apply(t, u, v));
    }
}
