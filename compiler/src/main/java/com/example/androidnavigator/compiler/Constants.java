package com.example.androidnavigator.compiler;

import com.squareup.javapoet.ClassName;

class Constants {
    private static final String START_ACTIVITY_PREFIX = "activity.startActivity(";
    private static final String START_ACTIVITY_SUFFIX = ")";
    private static final String CLASS_SUFFIX = ".class";

    static final String GENERATED_CLASS_NAME = "Navigator";
    static final String NEW_INTENT_STATEMENT = "Intent intent = new $T($L, $L" + START_ACTIVITY_SUFFIX;
    static final String NEW_INTENT = "new $T($L, $L" + START_ACTIVITY_SUFFIX;
    static final String METHOD_PREFIX = "start";
    static final ClassName INTENT_CLASS = ClassName.get("android.content", "Intent");
    static final ClassName ACTIVITY = ClassName.get("android.app", "Activity");
    static final String INTENT = START_ACTIVITY_PREFIX + NEW_INTENT;
    static final String CLASS = CLASS_SUFFIX + START_ACTIVITY_SUFFIX;
    static final String START_ACTIVITY_INTENT = START_ACTIVITY_PREFIX + "intent" + START_ACTIVITY_SUFFIX;
    static final String PACKAGE_NAME = "com.example.androidnavigator";
}
