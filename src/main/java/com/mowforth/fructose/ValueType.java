package com.mowforth.fructose;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Collection;

/**
 * CHANGEME
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ValueType {
	Class<?> value();
    Class<? extends Collection> collectionType() default NoCollection.class;
}
