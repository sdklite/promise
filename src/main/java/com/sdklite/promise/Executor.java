package com.sdklite.promise;

/**
 * Represents the executor function
 * 
 * @author johnsonlee
 *
 * @param <T>
 *            The resolve function
 * @param <U>
 *            The reject function
 */
@FunctionalInterface
public interface Executor<T, U> {

    void accept(T t, U u) throws Throwable;

}
