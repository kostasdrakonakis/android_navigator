package com.github.kostasdrakonakis.compiler;

import com.squareup.javapoet.ClassName;

class Constants {
    static final String CLOSING_BRACKET = ")";

    private static final String START_ACTIVITY_PREFIX = "context.startActivity(";
    private static final String START_SERVICE_PREFIX = "context.startService(";
    private static final String START_SERVICE_FOREGROUND_PREFIX = "context.startForegroundService(";
    private static final String CLASS_SUFFIX = ".class";
    private static final String NEW_INTENT = "new $T($L, $L" + CLOSING_BRACKET;

    static final String COMMA_SEPARATION = ", ";
    static final String GENERATED_CLASS_NAME = "IntentNavigator";
    static final String INTENT_PROPERTY_CLASS_SUFFIX = "_INTENT_PROPERTY_BINDING";
    static final String NEW_INTENT_STATEMENT = "Intent intent = new $T($L, $L" + CLOSING_BRACKET;
    static final String INTENT_PUT_EXTRA = "intent.putExtra(";
    static final String INTENT_SET_TYPE = "intent.setType(\"";
    static final String METHOD_PREFIX = "start";
    static final ClassName INTENT_CLASS = ClassName.get("android.content", "Intent");
    static final ClassName CONTEXT = ClassName.get("android.content", "Context");
    static final ClassName BUNDLE = ClassName.get("android.os", "Bundle");
    static final ClassName PARCELABLE = ClassName.get("android.os", "Parcelable");
    static final String BUNDLE_FIELD = "android.os.Bundle";
    static final String PARCELABLE_FIELD = "android.os.Parcelable";

    static final String CLASS = CLASS_SUFFIX + CLOSING_BRACKET;
    static final String INTENT_ADD_FLAGS = "intent.addFlags(Intent.";
    static final String INTENT_ADD_CATEGORY = "intent.addCategory(Intent.";
    static final String SERVICE_TYPE = "android.app.Service";

    static final String START_ACTIVITY_INTENT = START_ACTIVITY_PREFIX + "intent" + CLOSING_BRACKET;
    static final String START_SERVICE_INTENT = START_SERVICE_PREFIX + "intent" + CLOSING_BRACKET;
    static final String START_FOREGROUND_SERVICE_INTENT = START_SERVICE_FOREGROUND_PREFIX + "intent" + CLOSING_BRACKET;
    static final String START_ACTIVITY_NEW_INTENT = START_ACTIVITY_PREFIX + NEW_INTENT;
    static final String START_SERVICE_NEW_INTENT = START_SERVICE_PREFIX + NEW_INTENT;
    static final String START_FOREGROUND_SERVICE_NEW_INTENT = START_SERVICE_FOREGROUND_PREFIX + NEW_INTENT;

    static final String PACKAGE_NAME = "com.github.kostasdrakonakis.androidnavigator";
}
