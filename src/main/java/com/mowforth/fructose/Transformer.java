package com.mowforth.fructose;


/**
 * CHANGEME
 */
public interface Transformer<T> {
    T call(Object raw) throws Exception;
}
