package com.github.kostasdrakonakis.compiler

internal class IntentPropertyData(val fieldName: String, val annotationValue: String, val fieldClass: String) {
    private var hasIntValue = false
    private var hasDoubleValue = false
    private var hasFloatValue = false
    private var hasLongValue = false
    private var hasCharValue = false
    private var hasShortValue = false
    private var hasByteValue = false
    private var hasBooleanValue = false
    var doubleDefaultValue = 0.0
        get() = if (hasDoubleValue) field else 0.0
        set(doubleDefaultValue) {
            hasDoubleValue = true
            field = doubleDefaultValue
        }
    var floatDefaultValue = 0f
        get() = if (hasFloatValue) field else 0.0f
        set(floatDefaultValue) {
            hasFloatValue = true
            field = floatDefaultValue
        }
    var longDefaultValue: Long = 0
        get() = if (hasLongValue) field else 0L
        set(longDefaultValue) {
            hasLongValue = true
            field = longDefaultValue
        }
    var charDefaultValue = 0.toChar()
        get() = if (hasCharValue) field else '\u0000'
        set(charDefaultValue) {
            hasCharValue = true
            field = charDefaultValue
        }
    var shortDefaultValue: Short = 0
        get() = if (hasShortValue) field else 0
        set(shortDefaultValue) {
            hasShortValue = true
            field = shortDefaultValue
        }
    var byteDefaultValue: Byte = 0
        get() = if (hasByteValue) field else 0
        set(byteDefaultValue) {
            hasByteValue = true
            field = byteDefaultValue
        }
    var booleanDefaultValue = false
        get() = hasBooleanValue && field
        set(booleanDefaultValue) {
            hasBooleanValue = true
            field = booleanDefaultValue
        }
    var intDefaultValue = 0
        get() = if (hasIntValue) field else 0
        set(intDefaultValue) {
            hasIntValue = true
            field = intDefaultValue
        }

}