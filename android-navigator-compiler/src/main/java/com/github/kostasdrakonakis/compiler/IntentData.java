package com.github.kostasdrakonakis.compiler;

import com.github.kostasdrakonakis.annotation.IntentCategory;
import com.github.kostasdrakonakis.annotation.IntentExtra;
import com.github.kostasdrakonakis.annotation.IntentFlag;

import java.util.ArrayList;
import java.util.List;

class IntentData {
    private List<IntentExtraData> typeList;
    private List<IntentFlagData> flagList;
    private List<IntentCategoryData> categoryList;
    private String packageName;

    IntentData(IntentExtra[] values,
               IntentFlag[] flags,
               IntentCategory[] categories,
               String packageName) {

        typeList = new ArrayList<>();
        flagList = new ArrayList<>();
        categoryList = new ArrayList<>();
        for (IntentExtra extra : values) {
            typeList.add(new IntentExtraData(extra.parameter(), extra.type()));
        }
        for (IntentFlag flag : flags) {
            flagList.add(new IntentFlagData(flag.value().name()));
        }
        for (IntentCategory category : categories) {
            categoryList.add(new IntentCategoryData(category.value().name()));
        }
        this.packageName = packageName;
    }

    protected List<IntentExtraData> getValues() {
        return typeList;
    }

    protected List<IntentFlagData> getFlags() {
        return flagList;
    }

    protected List<IntentCategoryData> getCategories() {
        return categoryList;
    }

    protected String getPackageName() {
        return packageName;
    }
}
