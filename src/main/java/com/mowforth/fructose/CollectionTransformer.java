package com.mowforth.fructose;

import java.util.Collection;

/**
 * CHANGEME
 */
public interface CollectionTransformer<T extends Collection<? extends Object>> {
    <U> T call(U raw) throws Exception;
}
