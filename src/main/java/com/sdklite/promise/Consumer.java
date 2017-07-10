package com.sdklite.promise;

/**
 * Represents an operation that accepts a single input argument and returns no
 * result.
 * 
 * @author johnsonlee
 *
 * @param <T>
 *            the type of the input to the operation
 */
@FunctionalInterface
public interface Consumer<T> {

    void accept(final T t) throws Throwable;

}
