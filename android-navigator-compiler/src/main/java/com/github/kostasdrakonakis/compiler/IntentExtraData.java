package com.github.kostasdrakonakis.compiler;

import com.github.kostasdrakonakis.annotation.IntentType;

class IntentExtraData {
    private String typeValue;
    private IntentType type;

    IntentExtraData(String typeValue, IntentType type) {
        this.typeValue = typeValue;
        this.type = type;
    }

    String getTypeValue() {
        return typeValue;
    }

    IntentType getType() {
        return type;
    }
}
