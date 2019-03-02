package com.github.kostasdrakonakis.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declare the target Service for Intent
 * <p>
 * Accepts {@link ServiceType}, {@literal @}{@link IntentExtra},
 * {@literal @}{@link IntentFlag}, {@literal @}{@link IntentCategory}, String
 * </p>
 * <p>
 * Can be used like:
 *
 * <pre>
 * <code>
 * public class MainActivity extends AppCompatActivity {
 *      {@literal @}Override
 *       protected void onCreate(Bundle savedInstanceState) {
 *           IntentNavigator.startMyService(MainActivity.this);
 *       }
 *    }
 * </code>
 * </pre>
 * <p>
 * in Service you should use like:
 * </p>
 * <pre>
 * <code>
 *  {@literal @}IntentService(ServiceType.FOREGROUND)
 *      public class MyService extends Service {}
 * </code>
 * </pre>
 * <p>
 * You can use it with parameters like:
 * </p>
 * <pre>
 * <code>
 *  {@literal @}IntentService(value = {
 *         {@literal @}IntentExtra(type = IntentType.INT, parameter = "id"),
 *         {@literal @}IntentExtra(type = IntentType.STRING, parameter = "name")
 *      }, flags = {
 *         {@literal @}IntentFlag(IntentFlagType.FLAG_ACTIVITY_CLEAR_TOP)
 *      }
 * )
 * </code>
 * </pre>
 */
@Retention(RetentionPolicy.SOURCE)
@Documented
@Target(ElementType.TYPE)
public @interface IntentService {
    /**
     * {@link ServiceType} property to declare ServiceType
     *
     * @return the {@link ServiceType} value
     */
    ServiceType value() default ServiceType.BACKGROUND;

    /**
     * {@literal @}{@link IntentExtra} property to declare extras for Intent
     *
     * @return the {@literal @}{@link IntentExtra} extra
     */
    IntentExtra[] extras() default {};

    /**
     * {@literal @}{@link IntentFlag} property to declare flags for Intent
     *
     * @return the {@literal @}{@link IntentFlag} flag
     */
    IntentFlag[] flags() default {};

    /**
     * {@literal @}{@link IntentCategory} property to declare categories for Intent
     *
     * @return the {@literal @}{@link IntentCategory} category
     */
    IntentCategory[] categories() default {};

    /**
     * String property to declare types for Intent
     *
     * @return the String type
     */
    String type() default "";
}
