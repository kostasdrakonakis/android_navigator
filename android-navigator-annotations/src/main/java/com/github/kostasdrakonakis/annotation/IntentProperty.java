package com.github.kostasdrakonakis.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Documented
@Target(ElementType.FIELD)
public @interface IntentProperty {
    String value();

    int intDefaultValue() default 0;

    double doubleDefaultValue() default 0.0d;

    float floatDefaultValue() default 0.0f;

    long longDefaultValue() default 0L;

    char charDefaultValue() default '\u0000';

    short shortDefaultValue() default 0;

    byte byteDefaultValue() default 0;

    boolean booleanDefaultValue() default false;
}
