package com.mowforth.fructose;

import com.google.common.collect.ImmutableList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CHANGEME
 */
public class CollectionTransformers {
    public static final Map<Class<?>, CollectionTransformer<?>> COMMON =
            new HashMap<Class<?>, CollectionTransformer<?>>();

    static {
        COMMON.put(
                List.class,
                new CollectionTransformer<List<?>>() {
                    @Override
                    public <U> List<? extends U> call(U raw) throws Exception {
                        return ImmutableList.of(raw);
                    }
                }
        );
    }
}
