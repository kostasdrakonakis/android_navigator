package com.github.kostasdrakonakis.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declare a flag for Intent
 * <p>
 * Can be used in {@literal @}{@link IntentFlag } and accepts {@link IntentFlagType}
 *
 * <pre>
 * <code>
 * {@literal @}Intent( flags = {{@literal @}IntentFlag(IntentFlagType.FLAG_ACTIVITY_CLEAR_TOP)})
 * </code>
 * </pre>
 */
@Retention(RetentionPolicy.SOURCE)
@Documented
@Target(ElementType.TYPE)
public @interface IntentFlag {
    /**
     * {@link IntentFlagType} property for declaring Flags for Intent
     *
     * @return {@link IntentFlagType} property
     */
    IntentFlagType value();
}
