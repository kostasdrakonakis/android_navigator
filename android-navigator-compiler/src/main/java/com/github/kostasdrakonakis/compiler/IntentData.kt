package com.github.kostasdrakonakis.compiler

import com.github.kostasdrakonakis.annotation.IntentCategory
import com.github.kostasdrakonakis.annotation.IntentExtra
import com.github.kostasdrakonakis.annotation.IntentFlag
import java.util.ArrayList

internal open class IntentData(
    values: Array<IntentExtra>,
    flags: Array<IntentFlag>,
    categories: Array<IntentCategory>,
    type: String,
    packageName: String
) {
    private val typeList: MutableList<IntentExtraData>
    private val flagList: MutableList<IntentFlagData>
    private val categoryList: MutableList<IntentCategoryData>
    val packageName: String
    val type: String

    val values: List<IntentExtraData> get() = typeList

    val flags: List<IntentFlagData> get() = flagList

    val categories: List<IntentCategoryData> get() = categoryList

    init {
        typeList = ArrayList()
        flagList = ArrayList()
        categoryList = ArrayList()
        for (extra in values) {
            typeList.add(IntentExtraData(extra.parameter, extra.type))
        }
        for (flag in flags) {
            flagList.add(IntentFlagData(flag.value.name))
        }
        for (category in categories) {
            categoryList.add(IntentCategoryData(category.value.name))
        }
        this.type = type
        this.packageName = packageName
    }
}