package com.github.kostasdrakonakis.compiler;

import com.github.kostasdrakonakis.annotation.IntentType;

class IntentExtraData {
    private String parameter;
    private IntentType type;

    IntentExtraData(String parameter, IntentType type) {
        this.parameter = parameter;
        this.type = type;
    }

    String getParameter() {
        return parameter;
    }

    IntentType getType() {
        return type;
    }
}
