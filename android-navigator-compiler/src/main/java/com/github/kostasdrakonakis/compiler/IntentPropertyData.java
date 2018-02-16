package com.github.kostasdrakonakis.compiler;

class IntentPropertyData {
    private String fieldName;
    private String annotationValue;
    private String fieldClass;

    IntentPropertyData(String fieldName,
                       String annotationValue,
                       String fieldClass) {

        this.fieldName = fieldName;
        this.annotationValue = annotationValue;
        this.fieldClass = fieldClass;
    }

    String getFieldName() {
        return fieldName;
    }

    String getAnnotationValue() {
        return annotationValue;
    }

    String getFieldClass() {
        return fieldClass;
    }
}
