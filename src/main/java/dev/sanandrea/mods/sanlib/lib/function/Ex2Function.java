/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2016-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.sanlib.lib.function;

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
     * Returns a function that always returns its input argument.
     *
     * @param <T> the type of the input and output objects to the function
     * @return a function that always returns its input argument
     */
    static <T> Ex2Function<T, T, Throwable, Throwable> identity() {
        return t -> t;
    }
}
