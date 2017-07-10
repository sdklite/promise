package com.sdklite.promise;

final class FunctionSubscriber<V, R> implements Subscriber<V, R> {

    final Function<V, R> onFulfilled;
    final Function<Throwable, R> onRejected;
    final Promise<?> next;

    public FunctionSubscriber(final Function<V, R> onFulfilled, final Function<Throwable, R> onRejected, final Promise<?> next) {
        this.onFulfilled = onFulfilled;
        this.onRejected = onRejected;
        this.next = next;
    }

}
