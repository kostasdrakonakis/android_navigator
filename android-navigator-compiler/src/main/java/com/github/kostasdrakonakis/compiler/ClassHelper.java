package com.github.kostasdrakonakis.compiler;

import com.github.kostasdrakonakis.annotation.IntentType;

import java.io.Serializable;

class ClassHelper {
    static Class getClassFromType(IntentType type) {
        switch (type) {
            case STRING:
                return String.class;
            case INT:
                return int.class;
            case BOOLEAN:
                return boolean.class;
            case BYTE:
                return byte.class;
            case SHORT:
                return short.class;
            case LONG:
                return long.class;
            case CHAR:
                return char.class;
            case FLOAT:
                return float.class;
            case DOUBLE:
                return double.class;
            case BOOLEAN_ARRAY:
                return boolean[].class;
            case BYTE_ARRAY:
                return byte[].class;
            case CHAR_ARRAY:
                return char[].class;
            case CHAR_SEQUENCE_ARRAY:
                return CharSequence[].class;
            case CHAR_SEQUENCE:
                return CharSequence.class;
            case LONG_ARRAY:
                return long[].class;
            case INT_ARRAY:
                return int[].class;
            case STRING_ARRAY:
                return String[].class;
            case SHORT_ARRAY:
                return short[].class;
            case SERIALIZABLE:
                return Serializable.class;
            case BUNDLE:
            case PARCELABLE:
            default:
                return null;
        }
    }

    static String getIntentExtraFromClass(String classType, String value) {
        if (classType.equals(String.class.getName())) {
            return "getStringExtra(\"" + value + "\")";
        } else if (classType.equals(int.class.getName())) {
            return "getIntExtra(\"" + value + "\", 0)";
        } else if (classType.equals(boolean.class.getName())) {
            return "getBooleanExtra(\"" + value + "\", false)";
        } else if (classType.equals(byte.class.getName())) {
            return "getByteExtra(\"" + value + "\", 0)";
        } else if (classType.equals(short.class.getName())) {
            return "getShortExtra(\"" + value + "\", 0)";
        } else if (classType.equals(long.class.getName())) {
            return "getLongExtra(\"" + value + "\", 0L)";
        } else if (classType.equals(char.class.getName())) {
            return "getCharExtra(\"" + value + "\", '\\u0000')";
        } else if (classType.equals(float.class.getName())) {
            return "getFloatExtra(\"" + value + "\", 0.0f)";
        } else if (classType.equals(double.class.getName())) {
            return "getDoubleExtra(\"" + value + "\", 0.0d)";
        } else if (classType.equals(boolean[].class.getName())) {
            return "getBooleanArrayExtra(\"" + value + "\")";
        } else if (classType.equals(byte[].class.getName())) {
            return "getByteArrayExtra(\"" + value + "\")";
        } else if (classType.equals(char[].class.getName())) {
            return "getCharArrayExtra(\"" + value + "\")";
        } else if (classType.equals(CharSequence[].class.getName())) {
            return "getCharSequenceArrayExtra(\"" + value + "\")";
        } else if (classType.equals(CharSequence.class.getName())) {
            return "getCharSequenceExtra(\"" + value + "\")";
        } else if (classType.equals(long[].class.getName())) {
            return "getLongArrayExtra(\"" + value + "\")";
        } else if (classType.equals(int[].class.getName())) {
            return "getIntArrayExtra(\"" + value + "\")";
        } else if (classType.equals(String[].class.getName())) {
            return "getStringArrayExtra(\"" + value + "\")";
        } else if (classType.equals(short[].class.getName())) {
            return "getShortArrayExtra(\"" + value + "\")";
        } else if (classType.equals(Serializable.class.getName())) {
            return "getSerializableExtra(\"" + value + "\")";
        } else {
            return null;
        }
    }
}
