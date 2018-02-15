package com.example.androidnavigator.compiler;

public class AnnotationData {
    private String[] values;
    private String packageName;

    public AnnotationData(String[] values, String packageName) {
        this.values = values;
        this.packageName = packageName;
    }

    public String[] getValues() {
        return values;
    }

    public String getPackageName() {
        return packageName;
    }
}
