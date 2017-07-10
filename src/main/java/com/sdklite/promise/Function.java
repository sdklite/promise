package com.sdklite.promise;

/**
 * Represents a function that accepts one argument and produces a result.
 * 
 * @author johnsonlee
 *
 * @param <T>
 *            the type of the input to the function
 * @param <R>
 *            the type of the result of the function
 */
@FunctionalInterface
public interface Function<T, R> {

    R apply(final T t) throws Throwable;

}
