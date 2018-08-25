package com.github.kostasdrakonakis.compiler;

class IntentPropertyData {
    private String fieldName;
    private String annotationValue;
    private String fieldClass;

    private boolean hasIntValue;
    private boolean hasDoubleValue;
    private boolean hasFloatValue;
    private boolean hasLongValue;
    private boolean hasCharValue;
    private boolean hasShortValue;
    private boolean hasByteValue;
    private boolean hasBooleanValue;

    private double doubleDefaultValue;
    private float floatDefaultValue;
    private long longDefaultValue;
    private char charDefaultValue;
    private short shortDefaultValue;
    private byte byteDefaultValue;
    private boolean booleanDefaultValue;
    private int intDefaultValue;

    IntentPropertyData(String fieldName, String annotationValue, String fieldClass) {
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

    public int getIntDefaultValue() {
        return hasIntValue ? intDefaultValue : 0;
    }

    public double getDoubleDefaultValue() {
        return hasDoubleValue ? doubleDefaultValue : 0.0d;
    }

    public float getFloatDefaultValue() {
        return hasFloatValue ? floatDefaultValue : 0.0f;
    }

    public long getLongDefaultValue() {
        return hasLongValue ? longDefaultValue : 0L;
    }

    public char getCharDefaultValue() {
        return hasCharValue ? charDefaultValue : '\u0000';
    }

    public short getShortDefaultValue() {
        return hasShortValue ? shortDefaultValue : 0;
    }

    public byte getByteDefaultValue() {
        return hasByteValue ? byteDefaultValue : 0;
    }

    public boolean getBooleanDefaultValue() {
        return hasBooleanValue && booleanDefaultValue;
    }

    public void setIntDefaultValue(int intDefaultValue) {
        hasIntValue = true;
        this.intDefaultValue = intDefaultValue;
    }

    public void setDoubleDefaultValue(double doubleDefaultValue) {
        hasDoubleValue = true;
        this.doubleDefaultValue = doubleDefaultValue;
    }

    public void setFloatDefaultValue(float floatDefaultValue) {
        hasFloatValue = true;
        this.floatDefaultValue = floatDefaultValue;
    }

    public void setLongDefaultValue(long longDefaultValue) {
        hasLongValue = true;
        this.longDefaultValue = longDefaultValue;
    }

    public void setCharDefaultValue(char charDefaultValue) {
        hasCharValue = true;
        this.charDefaultValue = charDefaultValue;
    }

    public void setShortDefaultValue(short shortDefaultValue) {
        hasShortValue = true;
        this.shortDefaultValue = shortDefaultValue;
    }

    public void setByteDefaultValue(byte byteDefaultValue) {
        hasByteValue = true;
        this.byteDefaultValue = byteDefaultValue;
    }

    public void setBooleanDefaultValue(boolean booleanDefaultValue) {
        hasBooleanValue = true;
        this.booleanDefaultValue = booleanDefaultValue;
    }
}
