package com.sdklite.promise;

/**
 * Represents the object which has a {@code then} method
 * 
 * @author johnsonlee
 *
 * @param <V>
 *            The type of value
 */
public interface Thenable<V> {

    default Promise<V> then(final Consumer<V> onFulfilled) {
        return this.then(onFulfilled, null);
    }

    default <R> Promise<R> then(final Function<V, R> onFulfilled) {
        return this.then(onFulfilled, null);
    }

    Promise<V> then(final Consumer<V> onFulfilled, final Consumer<Throwable> onRejected);

    <R> Promise<R> then(final Function<V, R> onFulfilled, final Function<Throwable, R> onRejected);

}
