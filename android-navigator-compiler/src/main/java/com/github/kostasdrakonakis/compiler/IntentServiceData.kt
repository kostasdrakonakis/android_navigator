package com.github.kostasdrakonakis.compiler

import com.github.kostasdrakonakis.annotation.IntentCategory
import com.github.kostasdrakonakis.annotation.IntentExtra
import com.github.kostasdrakonakis.annotation.IntentFlag
import com.github.kostasdrakonakis.annotation.ServiceType

internal class IntentServiceData(values: Array<IntentExtra>,
                                 flags: Array<IntentFlag>,
                                 categories: Array<IntentCategory>,
                                 type: String?,
                                 packageName: String?, val serviceType: ServiceType) : IntentData(values, flags, categories, type!!, packageName!!)