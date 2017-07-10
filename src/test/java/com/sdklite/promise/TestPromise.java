package com.sdklite.promise;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;

import org.junit.Test;

public class TestPromise {

    @Test
    public void test_resolving_an_array() {
        final CountDownLatch signal = new CountDownLatch(1);

        Promise.resolve(new int[] { 1, 2, 3 }).then(v -> {
            System.out.println(Arrays.toString(v));
            signal.countDown();
        });

        await(signal);
    }

    @Test
    public void test_resolving_another_promise() {
        final CountDownLatch signal = new CountDownLatch(1);

        final Promise<Integer> original = Promise.resolve(33);
        final Promise<Integer> cast = Promise.resolve(original);

        assertEquals(cast, original);

        cast.then(v -> {
            System.out.println(v);
            signal.countDown();
        });

        await(signal);
    }

    @Test
    public void test_resolving_thenable() {
        final CountDownLatch signal = new CountDownLatch(1);

        Promise.resolve(new Thenable<String>() {
            @Override
            public Promise<String> then(Consumer<String> onFulfilled, Consumer<Throwable> onRejected) {
                return new Promise<String>((resolve, reject) -> {
                    onFulfilled.accept("Fulfilled!@Consumer");
                });
            }

            @Override
            public <R> Promise<R> then(Function<String, R> onFulfilled, Function<Throwable, R> onRejected) {
                return new Promise<String>().then(v -> {
                    return onFulfilled.apply("Fulfilled!@Function");
                }, onRejected);
            }
        }).then(v -> {
            System.out.println(v);
            signal.countDown();
        }, e -> {
            e.printStackTrace();
            signal.countDown();
        });

        await(signal);
    }

    @Test
    public void test_resolving_thenable_and_throwing_error() {
        final CountDownLatch signal = new CountDownLatch(1);

        Promise.resolve(new Thenable<String>() {
            @Override
            public Promise<String> then(Consumer<String> onFulfilled, Consumer<Throwable> onRejected) {
                throw new TypeException("Throwing@Consumer");
            }

            @Override
            public <R> Promise<R> then(Function<String, R> onFulfilled, Function<Throwable, R> onRejected) {
                throw new TypeException("Throwing@Function");
            }
        }).then(v -> {
            System.out.println(v);
            signal.countDown();
        }, e -> {
            e.printStackTrace();
            signal.countDown();
        });

        await(signal);
    }

    @Test
    public void test_resolving_all_with_string_array() {
        final CountDownLatch signal = new CountDownLatch(1);

        Promise.all(String.class, "a", "b", "c").then(v -> {
            System.out.println(Arrays.toString(v));
            signal.countDown();
        });

        await(signal);
    }

    @Test
    public void test_resolving_all_with_mixed_values() {
        final CountDownLatch signal = new CountDownLatch(1);

        Promise.all(Object.class, "a", "b", "c", Promise.resolve("d"), 1).then(v -> {
            System.out.println(Arrays.toString(v));
            signal.countDown();
        }, e -> {
            e.printStackTrace();
            signal.countDown();
        });

        await(signal);
    }

    static void await(final CountDownLatch signal) {
        try {
            signal.await();
        } catch (final InterruptedException e) {
            fail(e.getLocalizedMessage());
        }
    }

}
