package com.github.kostasdrakonakis.compiler.util

import com.github.kostasdrakonakis.annotation.IntentType
import java.io.Serializable

internal object ClassHelper {
    fun getClassFromType(type: IntentType?): Class<*>? {
        return when (type) {
            IntentType.STRING -> String::class.java
            IntentType.INT -> Int::class.javaPrimitiveType
            IntentType.BOOLEAN -> Boolean::class.javaPrimitiveType
            IntentType.BYTE -> Byte::class.javaPrimitiveType
            IntentType.SHORT -> Short::class.javaPrimitiveType
            IntentType.LONG -> Long::class.javaPrimitiveType
            IntentType.CHAR -> Char::class.javaPrimitiveType
            IntentType.FLOAT -> Float::class.javaPrimitiveType
            IntentType.DOUBLE -> Double::class.javaPrimitiveType
            IntentType.BOOLEAN_ARRAY -> BooleanArray::class.java
            IntentType.BYTE_ARRAY -> ByteArray::class.java
            IntentType.CHAR_ARRAY -> CharArray::class.java
            IntentType.CHAR_SEQUENCE_ARRAY -> Array<CharSequence>::class.java
            IntentType.CHAR_SEQUENCE -> CharSequence::class.java
            IntentType.LONG_ARRAY -> LongArray::class.java
            IntentType.INT_ARRAY -> IntArray::class.java
            IntentType.STRING_ARRAY -> Array<String>::class.java
            IntentType.SHORT_ARRAY -> ShortArray::class.java
            IntentType.SERIALIZABLE -> Serializable::class.java
            IntentType.BUNDLE, IntentType.PARCELABLE -> null
            else -> null
        }
    }
}