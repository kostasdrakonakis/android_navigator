package com.github.kostasdrakonakis.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Documented
@Target(ElementType.TYPE)
public @interface IntentService {
    ServiceType value() default ServiceType.BACKGROUND;

    IntentExtra[] extras() default {};

    IntentFlag[] flags() default {};

    IntentCategory[] categories() default {};

    String type() default "";
}
