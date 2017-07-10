package com.sdklite.promise;

final class ConsumerSubscriber<V> implements Subscriber<V, Void> {

    final Consumer<V> onFulfilled;
    final Consumer<Throwable> onRejected;
    final Promise<V> next;

    public ConsumerSubscriber(final Consumer<V> onFulfilled, final Consumer<Throwable> onRejected, final Promise<V> next) {
        this.onFulfilled = onFulfilled;
        this.onRejected = onRejected;
        this.next = next;
    }

}
