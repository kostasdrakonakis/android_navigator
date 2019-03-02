package com.github.kostasdrakonakis.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declare the target Activity for Intent
 * <p>
 * If used without parameters it generates simple intent like:
 *
 * <pre>
 * <code>
 * public class MainActivity extends AppCompatActivity {
 *      {@literal @}Override
 *       protected void onCreate(Bundle savedInstanceState) {
 *           IntentNavigator.startSecondActivity(MainActivity.this);
 *       }
 *    }
 * </code>
 * </pre>
 * <p>
 * in another Activity you should use like:
 *
 * <pre>
 * <code>
 *  {@literal @}Intent
 *      public class SecondActivity extends AppCompatActivity {}
 * </code>
 * </pre>
 * <p>
 * You can use it with parameters like:
 *
 * <pre>
 * <code>
 *  {@literal @}Intent(value = {
 *         {@literal @}IntentExtra(type = IntentType.INT, parameter = "id"),
 *         {@literal @}IntentExtra(type = IntentType.STRING, parameter = "name"),
 *         {@literal @}IntentExtra(type = IntentType.CHAR, parameter = "mine"),
 *         {@literal @}IntentExtra(type = IntentType.STRING, parameter = "title")
 *      }, categories = {
 *         {@literal @}IntentCategory(IntentCategoryType.CATEGORY_DEFAULT)
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
public @interface Intent {
    /**
     * {@literal @}{@link IntentExtra} property to declare extras for Intent
     *
     * @return the {@literal @}{@link IntentExtra} property
     */
    IntentExtra[] value() default {};

    /**
     * String property to declare types for Intent
     *
     * @return the type property
     */
    String type() default "";

    /**
     * {@literal @}{@link IntentFlag} property to declare flags for Intent
     *
     * @return the {@literal @}{@link IntentFlag} property
     */
    IntentFlag[] flags() default {};

    /**
     * {@literal @}{@link IntentCategory} property to declare category for Intent
     *
     * @return the {@literal @}{@link IntentCategory} property
     */
    IntentCategory[] categories() default {};
}
