package com.github.kostasdrakonakis.compiler.util

import javax.lang.model.element.TypeElement
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.TypeKind
import javax.lang.model.type.TypeMirror

object TypeUtil {

    fun extendsFromType(typeMirror: TypeMirror, otherType: String): Boolean {
        if (isTypeEqual(typeMirror, otherType)) {
            return true
        }
        if (typeMirror.kind != TypeKind.DECLARED) {
            return false
        }
        val declaredType = typeMirror as DeclaredType
        val typeArguments = declaredType.typeArguments
        if (typeArguments.size > 0) {
            val typeString = StringBuilder(declaredType.asElement().toString())
            typeString.append('<')
            for (i in typeArguments.indices) {
                if (i > 0) {
                    typeString.append(',')
                }
                typeString.append('?')
            }
            typeString.append('>')
            if (typeString.toString() == otherType) {
                return true
            }
        }
        val element = declaredType.asElement() as? TypeElement ?: return false
        val superType = element.superclass
        if (extendsFromType(superType, otherType)) {
            return true
        }
        for (interfaceType in element.interfaces) {
            if (extendsFromType(interfaceType, otherType)) {
                return true
            }
        }
        return false
    }

    private fun isTypeEqual(typeMirror: TypeMirror, otherType: String): Boolean {
        return otherType == typeMirror.toString()
    }
}