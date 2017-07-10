package com.sdklite.promise;

import static org.junit.Assert.fail;

import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;

import org.junit.Test;

import com.google.gson.Gson;

public class TestPromise {

    @Test
    public void xhr_with_promise() {
        final CountDownLatch signal = new CountDownLatch(1);

        Promise.resolve("https://api.github.com").then(url -> {
            try (final InputStreamReader reader = new InputStreamReader(new URL(url).openStream())) {
                return new Gson().fromJson(reader, GitHub.class);
            }
        }).then(github -> {
            System.out.println(github);
            signal.countDown();
        }, e -> {
            e.printStackTrace();
            signal.countDown();
        });

        await(signal);
    }

    @Test
    public void promise_with_iterable() {
        final CountDownLatch signal = new CountDownLatch(1);

        Promise.all(String.class, "a", "b", "c").then(v -> {
            System.out.println(Arrays.toString(v));
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
