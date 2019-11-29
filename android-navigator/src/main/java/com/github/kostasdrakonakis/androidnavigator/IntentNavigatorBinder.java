package com.github.kostasdrakonakis.androidnavigator;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.CheckResult;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.Map;

public final class IntentNavigatorBinder {

    private static final String TAG = IntentNavigatorBinder.class.getSimpleName();
    private static boolean debug;

    private static final Map<Class<?>, Constructor> CONSTRUCTOR_MAP = new LinkedHashMap<>();

    private IntentNavigatorBinder() {
        throw new UnsupportedOperationException("No instances allowed");
    }

    @UiThread
    public static void bind(@NonNull Activity activity) {
        createBinder(activity);
    }

    public static void setDebug(boolean debug) {
        IntentNavigatorBinder.debug = debug;
    }

    private static void createBinder(@NonNull Activity activity) {
        Class<?> targetClass = activity.getClass();
        try {
            Constructor constructor = getConstructorForClass(targetClass);
            if (constructor != null) {
                constructor.newInstance(activity);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @CheckResult
    @UiThread
    private static Constructor getConstructorForClass(Class<?> cls) {
        Constructor<?> bindingConstructor = CONSTRUCTOR_MAP.get(cls);
        if (bindingConstructor != null) {
            if (debug) Log.d(TAG, "Returned constructor from Cache.");
            return bindingConstructor;
        }

        String clsName = cls.getPackage().getName() + "." + cls.getSimpleName();
        if (clsName.startsWith("android.") || clsName.startsWith("java.")) {
            if (debug) Log.d(TAG, "Reached framework class. No further.");
            return null;
        }

        try {
            Class<?> bindingClass =
                    cls.getClassLoader().loadClass(clsName + "_INTENT_PROPERTY_BINDING");
            if (debug) Log.d(TAG, "Binding Class: " + bindingClass);

            bindingConstructor = bindingClass.getConstructor(cls);
            if (debug) Log.d(TAG, "Loaded binding class and constructor.");
        } catch (ClassNotFoundException e) {
            return null;
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Unable to find binding constructor for " + clsName, e);
        }
        CONSTRUCTOR_MAP.put(cls, bindingConstructor);
        return bindingConstructor;
    }
}
