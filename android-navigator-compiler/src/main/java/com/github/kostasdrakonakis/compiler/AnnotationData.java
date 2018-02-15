package com.github.kostasdrakonakis.compiler;

import com.github.kostasdrakonakis.annotation.IntentExtra;

import java.util.ArrayList;
import java.util.List;

class AnnotationData {
    private List<IntentExtraData> typeList;
    private String packageName;

    AnnotationData(IntentExtra[] values, String packageName) {
        typeList = new ArrayList<>();
        for (IntentExtra extra : values) {
            typeList.add(new IntentExtraData(extra.typeValue(), extra.type()));
        }
        this.packageName = packageName;
    }

    List<IntentExtraData> getValues() {
        return typeList;
    }

    String getPackageName() {
        return packageName;
    }
}
