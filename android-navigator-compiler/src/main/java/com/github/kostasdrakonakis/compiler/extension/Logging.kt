package com.github.kostasdrakonakis.compiler.extension

import javax.annotation.processing.Messager
import javax.tools.Diagnostic

fun Messager.error(error: String) {
    this.printMessage(Diagnostic.Kind.ERROR, error)
}

fun Messager.warning(warning: String) {
    this.printMessage(Diagnostic.Kind.WARNING, warning)
}