package com.github.kostasdrakonakis.compiler

import com.squareup.javapoet.ClassName

internal object Constants {
    internal const val ACTIVITY_FIELD = "activity"
    internal const val GET_INTENT_METHOD = "getIntent"
    internal const val GET_BUNDLE_METHOD = "getBundleExtra"
    internal const val GET_PARCELABLE_METHOD = "getParcelable"
    const val CLOSING_BRACKET = ")"
    private const val START_ACTIVITY_PREFIX = "context.startActivity("
    private const val START_SERVICE_PREFIX = "context.startService("
    private const val START_SERVICE_FOREGROUND_PREFIX = "context.startForegroundService("
    private const val CLASS_SUFFIX = ".class"
    private const val NEW_INTENT = "new \$T(\$L, \$L$CLOSING_BRACKET"
    const val COMMA_SEPARATION = ", "
    const val GENERATED_CLASS_NAME = "IntentNavigator"
    const val INTENT_PROPERTY_CLASS_SUFFIX = "_INTENT_PROPERTY_BINDING"
    const val NEW_INTENT_STATEMENT = "Intent intent = new \$T(\$L, \$L$CLOSING_BRACKET"
    const val INTENT_PUT_EXTRA = "intent.putExtra("
    const val INTENT_SET_TYPE = "intent.setType(\""
    const val METHOD_PREFIX = "start"
    val INTENT_CLASS = ClassName.get("android.content", "Intent")
    val CONTEXT = ClassName.get("android.content", "Context")
    val BUNDLE = ClassName.get("android.os", "Bundle")
    val PARCELABLE = ClassName.get("android.os", "Parcelable")
    const val BUNDLE_FIELD = "android.os.Bundle"
    const val PARCELABLE_FIELD = "android.os.Parcelable"
    const val CLASS = CLASS_SUFFIX + CLOSING_BRACKET
    const val INTENT_ADD_FLAGS = "intent.addFlags(Intent."
    const val INTENT_ADD_CATEGORY = "intent.addCategory(Intent."
    const val SERVICE_TYPE = "android.app.Service"
    const val START_ACTIVITY_INTENT = START_ACTIVITY_PREFIX + "intent" + CLOSING_BRACKET
    const val START_SERVICE_INTENT = START_SERVICE_PREFIX + "intent" + CLOSING_BRACKET
    const val START_FOREGROUND_SERVICE_INTENT = START_SERVICE_FOREGROUND_PREFIX + "intent" + CLOSING_BRACKET
    const val START_ACTIVITY_NEW_INTENT = START_ACTIVITY_PREFIX + NEW_INTENT
    const val START_SERVICE_NEW_INTENT = START_SERVICE_PREFIX + NEW_INTENT
    const val START_FOREGROUND_SERVICE_NEW_INTENT = START_SERVICE_FOREGROUND_PREFIX + NEW_INTENT
    const val PACKAGE_NAME = "com.github.kostasdrakonakis.androidnavigator"
    internal val INTENT_NAVIGATOR_CLASS = ClassName.get(PACKAGE_NAME, GENERATED_CLASS_NAME)
}