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
        return this.then(onFulfilled, e -> e.printStackTrace());
    }

    default <R> Promise<R> then(final Function<V, R> onFulfilled) {
        return this.then(onFulfilled, e -> {
            e.printStackTrace();
            return null;
        });
    }

    Promise<V> then(final Consumer<V> onFulfilled, final Consumer<Throwable> onRejected);

    <R> Promise<R> then(final Function<V, R> onFulfilled, final Function<Throwable, R> onRejected);

}
