package com.sdklite.promise;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

final class Internal {

    private static final int NCPU = Runtime.getRuntime().availableProcessors();

    private static final AtomicLong COUNTER = new AtomicLong(1);

    private static final ScheduledExecutorService EXECUTOR = Executors.newScheduledThreadPool(NCPU + 1, new ThreadFactory() {
        @Override
        public Thread newThread(final Runnable r) {
            return new Thread(r, "Promise#" + COUNTER.getAndIncrement());
        }
    });

    public static void setTimeout(final Runnable runnable) {
        EXECUTOR.execute(runnable);
    }

    public static void setTimeout(final Runnable runnable, final long delay) {
        EXECUTOR.schedule(runnable, delay, TimeUnit.MILLISECONDS);
    }

    public static int size(final Iterable<?> iterable) {
        if (iterable instanceof Collection) {
            return ((Collection<?>) iterable).size();
        }

        int n = 0;

        for (final Iterator<?> i = iterable.iterator(); i.hasNext(); n++) {
            ;
        }

        return n;
    }
}
