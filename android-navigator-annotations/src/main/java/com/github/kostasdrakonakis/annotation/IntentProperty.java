package com.github.kostasdrakonakis.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Binds a data member with Intent value
 * <p>
 * Can be used like:
 * </p>
 *
 * <pre>
 * <code>
 * {@literal @}IntentProperty(value = "id", intDefaultValue = 1)
 *     public int myId;
 *    {@literal @}IntentProperty("name")
 *     String name;
 *
 *     {@literal @}Override
 *      protected void onCreate(Bundle savedInstanceState) {
 *         IntentNavigatorBinder.bind(this);
 *         TextView textView = findViewById(R.id.main_text);
 *
 *         if (myId &#62; 0 &amp;&amp; !isEmpty(title)) {
 *             String text = "Id: " + myId + " Title: " + title;
 *             textView.setText(text);
 *         }
 *      }
 * </code>
 * </pre>
 */
@Retention(RetentionPolicy.SOURCE)
@Documented
@Target(ElementType.FIELD)
public @interface IntentProperty {
    /**
     * Declares the name of the Intent extra property to bind with. Must be same with {@literal @}{@link IntentExtra#parameter()}
     *
     * @return the name of the Intent extra property
     */
    String value();

    /**
     * Declares the default value for extra type of int. Default value = 0
     *
     * @return the default value
     */
    int intDefaultValue() default 0;

    /**
     * Declares the default value for extra type of double. Default value = 0.0d
     *
     * @return the default value
     */
    double doubleDefaultValue() default 0.0d;

    /**
     * Declares the default value for extra type of float. Default value = 0.0f
     *
     * @return the default value
     */
    float floatDefaultValue() default 0.0f;

    /**
     * Declares the default value for extra type of long. Default value = 0L
     *
     * @return the default value
     */
    long longDefaultValue() default 0L;

    /**
     * Declares the default value for extra type of char. Default value = '\u0000'
     *
     * @return the default value
     */
    char charDefaultValue() default '\u0000';

    /**
     * Declares the default value for extra type of short. Default value = 0
     *
     * @return the default value
     */
    short shortDefaultValue() default 0;

    /**
     * Declares the default value for extra type of byte. Default value = 0
     *
     * @return the default value
     */
    byte byteDefaultValue() default 0;

    /**
     * Declares the default value for extra type of boolean. Default value = false
     *
     * @return the default value
     */
    boolean booleanDefaultValue() default false;
}
