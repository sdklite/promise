package com.sdklite.promise;

import static com.sdklite.promise.Internal.setTimeout;
import static com.sdklite.promise.Internal.size;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Represents the eventual completion (or failure) of an asynchronous operation,
 * and its resulting value.
 * 
 * @author johnsonlee
 *
 * @param <V>
 *            The type of value
 */
public class Promise<V> implements Thenable<V> {

    /**
     * Returns a single {@link Promise} that resolves when all of the promises
     * in the array argument have resolved or when the array argument contains
     * no promises. It rejects with the reason of the first promise that
     * rejects.
     * 
     * @param iterable
     *            An array.
     * @return
     *         <ul>
     *         <li>An already resolved {@link Promise} if the iterable passed is
     *         empty.</li>
     *         <li>An asynchronously resolved {@link Promise} if the iterable
     *         passed contains no promises.</li>
     *         <li>A pending {@link Promise} in all other cases. This returned
     *         promise is then resolved/rejected asynchronously (as soon as the
     *         stack is empty) when all the promises in the given iterable have
     *         resolved, or if any of the promises reject.</li>
     *         </ul>
     */
    public static Promise<Object[]> all(final Object... iterable) {
        return all(Arrays.asList(iterable));
    }

    /**
     * Returns a single {@link Promise} that resolves when all of the promises
     * in the array argument have resolved or when the array argument contains
     * no promises. It rejects with the reason of the first promise that
     * rejects.
     * 
     * @param type
     *            The the value component type of returned {@link Promise}.
     * @param iterable
     *            An array.
     * @return
     *         <ul>
     *         <li>An already resolved {@link Promise} if the iterable passed is
     *         empty.</li>
     *         <li>An asynchronously resolved {@link Promise} if the iterable
     *         passed contains no promises.</li>
     *         <li>A pending {@link Promise} in all other cases. This returned
     *         promise is then resolved/rejected asynchronously (as soon as the
     *         stack is empty) when all the promises in the given iterable have
     *         resolved, or if any of the promises reject.</li>
     *         </ul>
     */
    public static <T> Promise<T[]> all(final Class<T> type, final Object... iterable) {
        return all(type, Arrays.asList(iterable));
    }

    /**
     * Returns a single {@link Promise} that resolves when all of the promises
     * in the iterable argument have resolved or when the iterable argument
     * contains no promises. It rejects with the reason of the first promise
     * that rejects.
     * 
     * @param iterable
     *            An iterable object such as a Collection.
     * @return
     *         <ul>
     *         <li>An already resolved {@link Promise} if the iterable passed is
     *         empty.</li>
     *         <li>An asynchronously resolved {@link Promise} if the iterable
     *         passed contains no promises.</li>
     *         <li>A pending {@link Promise} in all other cases. This returned
     *         promise is then resolved/rejected asynchronously (as soon as the
     *         stack is empty) when all the promises in the given iterable have
     *         resolved, or if any of the promises reject.</li>
     *         </ul>
     */
    public static Promise<Object[]> all(final Iterable<?> iterable) {
        return all(Object.class, iterable);
    }

    /**
     * Returns a single {@link Promise} that resolves when all of the promises
     * in the iterable argument have resolved or when the iterable argument
     * contains no promises. It rejects with the reason of the first promise
     * that rejects.
     * 
     * @param type
     *            The the value component type of returned {@link Promise}.
     * @param args
     *            An iterable object such as an Array or Collection.
     * @return
     *         <ul>
     *         <li>An already resolved {@link Promise} if the iterable passed is
     *         empty.</li>
     *         <li>An asynchronously resolved {@link Promise} if the iterable
     *         passed contains no promises.</li>
     *         <li>A pending {@link Promise} in all other cases. This returned
     *         promise is then resolved/rejected asynchronously (as soon as the
     *         stack is empty) when all the promises in the given iterable have
     *         resolved, or if any of the promises reject.</li>
     *         </ul>
     */
    public static <T> Promise<T[]> all(final Class<T> type, final Iterable<?> args) {
        if (null == args) {
            return Promise.resolve();
        }

        final Iterator<?> i = args.iterator();
        if (!i.hasNext()) {
            return Promise.resolve((T[]) Array.newInstance(type, 0));
        }

        final int n = size(args);
        final Promise<T[]> promise = new Promise<T[]>();
        final AtomicInteger index = new AtomicInteger(0);
        final AtomicInteger counter = new AtomicInteger(n);
        final T[] results = (T[]) Array.newInstance(type, n);

        args.forEach(arg -> {
            final Promise<T> next = cast(arg);
            final int idx = index.getAndIncrement();

            next.then(v -> {
                try {
                    results[idx] = v;

                    if (0 == counter.decrementAndGet()) {
                        promise._resolve(results);
                    }
                } catch (final Throwable t) {
                    promise._reject(t);
                }
            }, e -> promise._reject(e));
        });

        return promise;
    }

    /**
     * Returns a {@link Promise} object that is resolved with {@code null} value
     * 
     * @return a {@link Promise} that is resolved with {@code null} value
     */
    public static <T> Promise<T> resolve() {
        return new Promise<T>((resolve, reject) -> resolve.accept((T) null));
    }

    /**
     * Returns a {@link Promise} object that is resolved with the specific value
     * 
     * @param value
     *            The to be resolved
     * @return a {@link Promise} that is resolved with the specific value
     */
    public static <T> Promise<T> resolve(final T value) {
        return new Promise<T>((resolve, reject) -> resolve.accept(value));
    }

    /**
     * Returns a {@link Promise} instance with the specified thenable
     * 
     * @param thenable
     *            The thenable to resolve
     * @return a {@link Promise} instance
     */
    public static <T> Promise<T> resolve(final Thenable<T> thenable) {
        if (thenable instanceof Promise) {
            return (Promise<T>) thenable;
        }

        final Promise<T> p = new Promise<T>();
        p._resolve(thenable);
        return p;
    }

    /**
     * Returns a Promise object that is rejected with the given reason.
     * 
     * @param reason
     *            The reason why this Promise rejected.
     * @return A Promise that is rejected with the given reason.
     */
    public static <T> Promise<T> reject(final Throwable reason) {
        return new Promise<T>((resolve, reject) -> reject.accept(reason));
    }

    private final Queue<Subscriber<V, ?>> subscribers = new ConcurrentLinkedQueue<Subscriber<V, ?>>();

    private volatile V value;
    private volatile Throwable reason;
    private volatile AtomicReference<State> state = new AtomicReference<State>(State.PENDING);

    /**
     * Default constructor
     */
    public Promise() {
    }

    /**
     * Create an instance with an executor function
     * 
     * @param executor
     *            The executor function
     */
    public Promise(final Executor<Consumer<V>, Consumer<Throwable>> executor) {
        try {
            executor.accept(v -> setTimeout(() -> _resolve(v)), e -> setTimeout(() -> _reject(e)));
        } catch (final Throwable e) {
            _reject(e);
        }
    }

    @Override
    public synchronized Promise<V> then(final Consumer<V> onFulfilled, final Consumer<Throwable> onRejected) {
        final Promise<V> next = new Promise<V>();

        switch (this.state.get()) {
        case FULFILLED:
            setTimeout(() -> _resolve(next, onFulfilled, this.value));
            break;
        case REJECTED:
            setTimeout(() -> _reject(next, onRejected, this.reason));
            break;
        default:
            this.subscribers.offer(new ConsumerSubscriber<>(onFulfilled, onRejected, next));
            break;
        }

        return next;
    }

    @Override
    public synchronized <R> Promise<R> then(final Function<V, R> onFulfilled, final Function<Throwable, R> onRejected) {
        final Promise<R> next = new Promise<R>();

        switch (this.state.get()) {
        case FULFILLED:
            setTimeout(() -> _resolve(next, onFulfilled, this.value));
            break;
        case REJECTED:
            setTimeout(() -> _reject(next, onRejected, this.reason));
            break;
        default:
            this.subscribers.offer(new FunctionSubscriber<V, R>(onFulfilled, onRejected, next));
            break;
        }

        return next;
    }

    private void _reject(final Throwable e) {
        if (this.state.compareAndSet(State.PENDING, State.REJECTED)) {
            this.reason = e;

            while (!this.subscribers.isEmpty()) {
                final Subscriber<V, ?> subscriber = this.subscribers.poll();

                if (subscriber instanceof ConsumerSubscriber) {
                    final ConsumerSubscriber<V> consumer = (ConsumerSubscriber<V>) subscriber;
                    _reject(consumer.next, consumer.onRejected, this.reason);
                } else if (subscriber instanceof Function) {
                    final FunctionSubscriber<V, ?> function = (FunctionSubscriber<V, ?>) subscriber;
                    _reject(function.next, function.onRejected, this.reason);
                }
            }
        }
    }

    private void _resolve(final V value) {
        if (value == this) {
            _reject(new TypeException("Self resolution"));
            return;
        }

        if (this.state.compareAndSet(State.PENDING, State.FULFILLED)) {
            this.value = value;

            while (!this.subscribers.isEmpty()) {
                final Subscriber<V, ?> subscriber = this.subscribers.poll();

                if (subscriber instanceof ConsumerSubscriber) {
                    final ConsumerSubscriber<V> consumer = (ConsumerSubscriber<V>) subscriber;
                    _resolve(consumer.next, consumer.onFulfilled, this.value);
                } else if (subscriber instanceof Function) {
                    final FunctionSubscriber<V, ?> function = (FunctionSubscriber<V, ?>) subscriber;
                    _resolve(function.next, function.onFulfilled, this.value);
                }
            }
        }
    }

    private void _resolve(final Thenable<V> thenable) {
        if (thenable == this) {
            _reject(new TypeException("Self resolution"));
            return;
        }

        if (null == thenable) {
            _resolve((V) null);
            return;
        }

        final AtomicBoolean notrun = new AtomicBoolean(true);

        try {
            thenable.then(v -> {
                if (notrun.compareAndSet(true, false)) {
                    _resolve(v);
                }
            } , e -> {
                if (notrun.compareAndSet(true, false)) {
                    _reject(e);
                }
            });
        } catch (final Throwable e) {
            if (notrun.compareAndSet(true, false)) {
                _reject(e);
            }
        }
    }

    private static <V> void _reject(final Promise next, final Consumer<Throwable> onRejected, final Throwable reason) {
        try {
            if (null != onRejected) {
                onRejected.accept(reason);
                next._resolve(null);
            } else {
                next._reject(reason);
            }
        } catch (final Throwable e) {
            next._reject(e);
        }
    }

    private static <V> void _resolve(final Promise next, final Consumer<V> onFulfilled, final V value) {
        try {
            if (null != onFulfilled) {
                onFulfilled.accept(value);
                next._resolve(null);
            } else {
                next._resolve(value);
            }
        } catch (final Throwable e) {
            next._reject(e);
        }
    }

    private static <R> void _reject(final Promise next, final Function<Throwable, R> onRejected, final Throwable reason) {
        try {
            if (null != onRejected) {
                next._resolve(onRejected.apply(reason));
            } else {
                next._reject(reason);
            }
        } catch (final Throwable e) {
            next._reject(e);
        }
    }

    private static <V, R> void _resolve(final Promise next, final Function<V, R> onFulfilled, final V value) {
        try {
            if (null != onFulfilled) {
                next._resolve(onFulfilled.apply(value));
            } else {
                next._resolve(value);
            }
        } catch (final Throwable e) {
            next._reject(e);
        }
    }

    private static <T> Promise<T> cast(final Object value) {
        return (Promise<T>) (value instanceof Promise ? value : resolve(value));
    }

}
