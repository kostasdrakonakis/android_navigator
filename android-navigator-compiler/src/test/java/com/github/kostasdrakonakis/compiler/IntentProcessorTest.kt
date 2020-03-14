package com.github.kostasdrakonakis.compiler

import com.google.testing.compile.JavaFileObjects
import com.google.testing.compile.JavaSourcesSubject.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import javax.tools.StandardLocation

@RunWith(JUnit4::class)
class IntentProcessorTest {

    @Test
    fun verifyIntentNavigator() {
        assertThat(JavaFileObjects.forResource("test/MyActivity.java"))
            .processedWith(IntentProcessor())
            .compilesWithoutError()
            .and()
            .generatesFileNamed(StandardLocation.SOURCE_OUTPUT, "com.github.kostasdrakonakis.androidnavigator", "IntentNavigator.java")
    }

    @Test
    fun verifyIntentPropertiesFile() {
        assertThat(JavaFileObjects.forResource("test/MyActivity.java"))
            .processedWith(IntentProcessor())
            .compilesWithoutError()
            .and()
            .generatesFileNamed(StandardLocation.SOURCE_OUTPUT, "test", "MyActivity_INTENT_PROPERTY_BINDING.java")
    }
}