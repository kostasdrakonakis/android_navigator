package com.github.kostasdrakonakis.androidnavigator

import android.app.Activity
import android.util.Log
import androidx.annotation.CheckResult
import androidx.annotation.UiThread
import java.lang.reflect.Constructor
import java.lang.reflect.InvocationTargetException

object IntentNavigatorBinder {
    private val TAG = IntentNavigatorBinder::class.java.simpleName
    private var debug = false
    private val CONSTRUCTOR_MAP: MutableMap<Class<*>, Constructor<*>> = linkedMapOf()

    @JvmStatic
    @UiThread
    fun bind(activity: Activity) {
        createBinder(activity)
    }

    fun setDebug(debug: Boolean) {
        this.debug = debug
    }

    private fun createBinder(activity: Activity) {
        val targetClass: Class<*> = activity.javaClass
        try {
            val constructor = getConstructorForClass(targetClass)
            constructor?.newInstance(activity)
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: InstantiationException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        }
    }

    @CheckResult
    @UiThread
    private fun getConstructorForClass(cls: Class<*>): Constructor<*>? {
        var bindingConstructor = CONSTRUCTOR_MAP[cls]
        if (bindingConstructor != null) {
            if (debug) Log.d(TAG, "Returned constructor from Cache.")
            return bindingConstructor
        }
        val clsName = cls.getPackage()!!.name + "." + cls.simpleName
        if (clsName.startsWith("android.") || clsName.startsWith("java.")) {
            if (debug) Log.d(TAG, "Reached framework class. No further.")
            return null
        }
        try {
            val bindingClass = cls.classLoader!!.loadClass(clsName + "_INTENT_PROPERTY_BINDING")
            if (debug) Log.d(TAG, "Binding Class: $bindingClass")
            bindingConstructor = bindingClass.getConstructor(cls)
            if (debug) Log.d(TAG, "Loaded binding class and constructor.")
        } catch (e: ClassNotFoundException) {
            return null
        } catch (e: NoSuchMethodException) {
            throw RuntimeException("Unable to find binding constructor for $clsName", e)
        }
        CONSTRUCTOR_MAP[cls] = bindingConstructor
        return bindingConstructor
    }
}