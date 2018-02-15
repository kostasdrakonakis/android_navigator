package com.github.kostasdrakonakis.compiler;

import com.squareup.javapoet.ClassName;

class Constants {
    static final String CLOSING_BRACKET = ")";

    private static final String START_ACTIVITY_PREFIX = "activity.startActivity(";
    private static final String CLASS_SUFFIX = ".class";
    private static final String NEW_INTENT = "new $T($L, $L" + CLOSING_BRACKET;

    static final String COMMA_SEPARATION = ", ";
    static final String GENERATED_CLASS_NAME = "IntentNavigator";
    static final String NEW_INTENT_STATEMENT = "Intent intent = new $T($L, $L" + CLOSING_BRACKET;
    static final String INTENT_PUT_EXTRA = "intent.putExtra(";
    static final String METHOD_PREFIX = "start";
    static final ClassName INTENT_CLASS = ClassName.get("android.content", "Intent");
    static final ClassName ACTIVITY = ClassName.get("android.app", "Activity");

    static final String CLASS = CLASS_SUFFIX + CLOSING_BRACKET;

    static final String START_ACTIVITY_INTENT = START_ACTIVITY_PREFIX + "intent" + CLOSING_BRACKET;
    static final String START_ACTIVITY_NEW_INTENT = START_ACTIVITY_PREFIX + NEW_INTENT;

    static final String PACKAGE_NAME = "com.github.kostasdrakonakis.androidnavigator";
}
