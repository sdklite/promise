package com.sdklite.promise;

/**
 * Represents a function that is passed with the arguments resolve and reject.
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
