package com.mowforth.fructose.sample;

import com.mowforth.fructose.Key;
import static com.mowforth.fructose.sample.SampleKeys.*;

/**
 * CHANGEME
 */
public class FooClass {

    private final int value;

    public FooClass(@Key(FOO) int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }
}
