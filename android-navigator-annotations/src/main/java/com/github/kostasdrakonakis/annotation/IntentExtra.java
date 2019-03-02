package com.github.kostasdrakonakis.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declare an IntentExtra
 * <p>
 * Can be used in {@literal @}{@link Intent } and accepts {@link IntentType} and String parameters
 *
 * <pre>
 * <code>
 *  {@literal @}Intent(value = {
 *         {@literal @}IntentExtra(type = IntentType.INT, parameter = "id"),
 *         {@literal @}IntentExtra(type = IntentType.STRING, parameter = "name"),
 *         {@literal @}IntentExtra(type = IntentType.CHAR, parameter = "mine"),
 *         {@literal @}IntentExtra(type = IntentType.STRING, parameter = "title")
 *      })
 * </code>
 * </pre>
 */
@Retention(RetentionPolicy.SOURCE)
@Documented
@Target(ElementType.TYPE)
public @interface IntentExtra {
    /**
     * Declares {@link IntentType} property for {@link IntentExtra}
     *
     * @return the {@link IntentType} property
     */
    IntentType type();

    /**
     * Declares String property name for {@link IntentExtra}
     *
     * @return the String property name
     */
    String parameter();
}
