package com.github.kostasdrakonakis.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declare a category for Intent
 * <p>
 * Can be used in {@literal @}{@link Intent } and accepts {@link IntentCategoryType}
 *
 * <pre>
 * <code>
 * {@literal @}Intent( categories = {{@literal @}IntentCategory(IntentCategoryType.CATEGORY_DEFAULT)})
 * </code>
 * </pre>
 */
@Retention(RetentionPolicy.SOURCE)
@Documented
@Target(ElementType.TYPE)
public @interface IntentCategory {
    /**
     * {@link IntentCategoryType} property for declaring CategoryTypes for Intent
     *
     * @return {@link IntentCategoryType} property
     */
    IntentCategoryType value();
}
