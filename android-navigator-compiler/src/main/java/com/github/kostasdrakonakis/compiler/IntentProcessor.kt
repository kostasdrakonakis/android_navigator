package com.github.kostasdrakonakis.compiler

import com.github.kostasdrakonakis.annotation.Intent
import com.github.kostasdrakonakis.annotation.IntentProperty
import com.github.kostasdrakonakis.annotation.IntentService
import com.github.kostasdrakonakis.annotation.IntentType
import com.github.kostasdrakonakis.annotation.ServiceType
import com.github.kostasdrakonakis.compiler.extension.error
import com.github.kostasdrakonakis.compiler.util.ClassHelper
import com.github.kostasdrakonakis.compiler.util.TypeUtil
import com.google.auto.service.AutoService
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.FieldSpec
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeSpec
import net.ltgt.gradle.incap.IncrementalAnnotationProcessor
import net.ltgt.gradle.incap.IncrementalAnnotationProcessorType
import java.io.IOException
import java.io.Serializable
import java.util.Locale
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Filer
import javax.annotation.processing.FilerException
import javax.annotation.processing.Messager
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.ElementKind
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements

@AutoService(Processor::class)
@IncrementalAnnotationProcessor(IncrementalAnnotationProcessorType.AGGREGATING)
class IntentProcessor : AbstractProcessor() {

    private lateinit var filer: Filer
    private lateinit var messager: Messager
    private lateinit var elements: Elements
    private val activitiesMap: MutableMap<String, IntentData> = hashMapOf()
    private val servicesMap: MutableMap<String, IntentServiceData> = hashMapOf()
    private val intentPropertiesMap: MutableMap<String, String> = hashMapOf()
    private val extraDataMap: MutableMap<String, String> = hashMapOf()
    private val fields: MutableList<IntentPropertyData> = arrayListOf()
    private val intentPropertyFiles: MutableList<JavaFile> = arrayListOf()

    @Synchronized
    override fun init(processingEnvironment: ProcessingEnvironment) {
        super.init(processingEnvironment)
        filer = processingEnvironment.filer
        messager = processingEnvironment.messager
        elements = processingEnvironment.elementUtils
    }

    override fun process(p0: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {
        if (parseIntentElements(roundEnv)) return true
        if (parseIntentServiceElements(roundEnv)) return true
        if (parseIntentPropertyElements(roundEnv)) return true
        val navigatorClass = TypeSpec
            .classBuilder(Constants.GENERATED_CLASS_NAME)
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
        val constructor = MethodSpec.constructorBuilder()
            .addModifiers(Modifier.PRIVATE)
            .addStatement("throw new UnsupportedOperationException(\"No instances\")")
            .build()
        navigatorClass.addMethod(constructor)
        generateActivities(navigatorClass)
        generateServices(navigatorClass)
        generateIntentPropertiesFile()
        if (roundEnv.processingOver()) {
            try {
                JavaFile.builder(Constants.PACKAGE_NAME, navigatorClass.build()).build().writeTo(filer)
                for (intentPropertyFile in intentPropertyFiles) {
                    intentPropertyFile.writeTo(filer)
                }
            } catch (ex: FilerException) {
            } catch (e: IOException) {
            }
        }
        return false
    }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latestSupported()
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return linkedSetOf(
            Intent::class.java.canonicalName,
            IntentProperty::class.java.canonicalName,
            IntentService::class.java.canonicalName
        )
    }

    private fun parseIntentElements(roundEnvironment: RoundEnvironment): Boolean {
        for (element in roundEnvironment.getElementsAnnotatedWith(Intent::class.java)) {
            if (element.kind != ElementKind.CLASS) {
                error("@Intent must be applied to class.")
                return true
            }
            val typeElement = element as TypeElement
            val intent = element.getAnnotation(Intent::class.java)
            val activity = typeElement.simpleName.toString()
            val packageName = elements.getPackageOf(typeElement).qualifiedName.toString()
            activitiesMap[activity] = IntentData(intent.value,
                intent.flags,
                intent.categories,
                intent.type,
                packageName)
        }
        return false
    }

    private fun parseIntentServiceElements(roundEnvironment: RoundEnvironment): Boolean {
        for (element in roundEnvironment.getElementsAnnotatedWith(IntentService::class.java)) {
            if (element.kind != ElementKind.CLASS) {
                error("@IntentService must be applied to class.")
                return true
            }
            val typeElement = element as TypeElement
            val mirror = element.asType()
            val intentService = element.getAnnotation(IntentService::class.java)
            val service = typeElement.simpleName.toString()
            val packageName = elements.getPackageOf(typeElement)
                .qualifiedName.toString()
            val foundService = ClassName.get(packageName, service)

            // Verify that extends from Service
            if (!TypeUtil.extendsFromType(mirror, Constants.SERVICE_TYPE)) {
                error("Class found: $foundService")
                error("IntentService annotation must be used in Service class")
                return true
            }
            servicesMap[service] = IntentServiceData(intentService.extras,
                intentService.flags,
                intentService.categories,
                intentService.type,
                packageName,
                intentService.value)
        }
        return false
    }

    private fun parseIntentPropertyElements(roundEnvironment: RoundEnvironment): Boolean {
        for (element in roundEnvironment.getElementsAnnotatedWith(IntentProperty::class.java)) {
            if (element.kind != ElementKind.FIELD) {
                error("@IntentProperty must be applied to fields.")
                return true
            }
            val typeElement = element.enclosingElement as TypeElement
            val returnType = element.asType()
            val fieldName = element.simpleName.toString()
            val returnTypeString = returnType.toString()
            val modifiers = element.modifiers
            if (modifiers.contains(Modifier.PRIVATE)
                || modifiers.contains(Modifier.FINAL)
                || modifiers.contains(Modifier.NATIVE)) {
                error("@IntentProperty must be applied to public, protected, package-private fields only.")
                return true
            }
            val intentProperty = element.getAnnotation(IntentProperty::class.java)
            val activity = typeElement.simpleName.toString()
            val packageName = elements.getPackageOf(typeElement).qualifiedName.toString()
            val intentPropertyData = IntentPropertyData(fieldName, intentProperty.value, returnTypeString)
            for (elementMirror in element.annotationMirrors) {
                val map = elementMirror.elementValues
                val shouldCheckValue = map.keys.size > 1
                if (!shouldCheckValue) continue
                var isBooleanSet = false
                var isByteSet = false
                var isCharSet = false
                var isFloatSet = false
                var isLongSet = false
                var isShortSet = false
                var isIntSet = false
                var isDoubleSet = false
                for (executableElement in map.keys) {
                    val keyName = executableElement.simpleName.toString()
                    isBooleanSet = "booleanDefaultValue" == keyName
                    isByteSet = "byteDefaultValue" == keyName
                    isCharSet = "charDefaultValue" == keyName
                    isFloatSet = "floatDefaultValue" == keyName
                    isLongSet = "longDefaultValue" == keyName
                    isShortSet = "shortDefaultValue" == keyName
                    isIntSet = "intDefaultValue" == keyName
                    isDoubleSet = "doubleDefaultValue" == keyName
                }
                when {
                    isBooleanSet -> {
                        val booleanDefaultValue: Boolean = intentProperty.booleanDefaultValue
                        if (returnTypeString == Boolean::class.javaPrimitiveType!!.name) {
                            intentPropertyData.booleanDefaultValue = booleanDefaultValue
                        } else {
                            error("booleanDefaultValue can be applied to boolean fields only.")
                            return true
                        }
                    }
                    isByteSet -> {
                        val byteDefaultValue: Byte = intentProperty.byteDefaultValue
                        if (returnTypeString == Byte::class.javaPrimitiveType!!.name) {
                            intentPropertyData.byteDefaultValue = byteDefaultValue
                        } else {
                            error("byteDefaultValue can be applied to byte fields only.")
                            return true
                        }
                    }
                    isCharSet -> {
                        val charDefaultValue: Char = intentProperty.charDefaultValue
                        if (returnTypeString == Char::class.javaPrimitiveType!!.name) {
                            intentPropertyData.charDefaultValue = charDefaultValue
                        } else {
                            error("charDefaultValue can be applied to char fields only.")
                            return true
                        }
                    }
                    isFloatSet -> {
                        val floatDefaultValue: Float = intentProperty.floatDefaultValue
                        if (returnTypeString == Float::class.javaPrimitiveType!!.name) {
                            intentPropertyData.floatDefaultValue = floatDefaultValue
                        } else {
                            error("floatDefaultValue can be applied to float fields only.")
                            return true
                        }
                    }
                    isLongSet -> {
                        val longDefaultValue: Long = intentProperty.longDefaultValue
                        if (returnTypeString == Long::class.javaPrimitiveType!!.name) {
                            intentPropertyData.longDefaultValue = longDefaultValue
                        } else {
                            error("longDefaultValue can be applied to long fields only.")
                            return true
                        }
                    }
                    isShortSet -> {
                        val shortDefaultValue: Short = intentProperty.shortDefaultValue
                        if (returnTypeString == Short::class.javaPrimitiveType!!.name) {
                            intentPropertyData.shortDefaultValue = shortDefaultValue
                        } else {
                            error("shortDefaultValue can be applied to short fields only.")
                            return true
                        }
                    }
                    isIntSet -> {
                        val intDefaultValue: Int = intentProperty.intDefaultValue
                        if (returnTypeString == Int::class.javaPrimitiveType!!.name) {
                            intentPropertyData.intDefaultValue = intDefaultValue
                        } else {
                            error("intDefaultValue can be applied to int fields only.")
                            return true
                        }
                    }
                    isDoubleSet -> {
                        val doubleDefaultValue: Double = intentProperty.doubleDefaultValue
                        if (returnTypeString == Double::class.javaPrimitiveType!!.name) {
                            intentPropertyData.doubleDefaultValue = doubleDefaultValue
                        } else {
                            error("doubleDefaultValue can be applied to double fields only.")
                            return true
                        }
                    }
                }
            }
            fields.add(intentPropertyData)
            intentPropertiesMap[activity] = packageName
        }
        return false
    }

    private fun generateActivities(navigatorClass: TypeSpec.Builder) {
        for ((activityName, intentData) in activitiesMap) {
            val packageName = intentData.packageName
            val values = intentData.values
            val flags = intentData.flags
            val categories = intentData.categories
            val intentType = intentData.type
            val activityClass = ClassName.get(packageName, activityName)
            val builder = MethodSpec
                .methodBuilder(Constants.METHOD_PREFIX + activityName)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(TypeName.VOID)
                .addParameter(Constants.CONTEXT, "context")
            if (values.isNotEmpty() || flags.isNotEmpty() || categories.isNotEmpty() || intentType.isNotEmpty()) {
                builder.addStatement(Constants.NEW_INTENT_STATEMENT,
                    Constants.INTENT_CLASS,
                    "context", "$activityClass.class")
                if (flags.isNotEmpty()) {
                    for (data in flags) {
                        builder.addStatement(Constants.INTENT_ADD_FLAGS
                            + data.flag
                            + Constants.CLOSING_BRACKET)
                    }
                }
                if (categories.isNotEmpty()) {
                    for (data in categories) {
                        builder.addStatement(Constants.INTENT_ADD_CATEGORY
                            + data.category
                            + Constants.CLOSING_BRACKET)
                    }
                }
                if (values.isNotEmpty()) {
                    for (data in values) {
                        val type = data.type
                        val parameter = data.parameter
                        val constName = ("EXTRA_"
                            + activityName.toUpperCase(Locale.getDefault())
                            + "_" + parameter.toUpperCase(Locale.getDefault()))
                        extraDataMap[parameter] = constName
                        val fieldSpec = FieldSpec.builder(String::class.java, constName)
                            .addModifiers(Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC)
                            .initializer("\$S", constName)
                            .build()
                        navigatorClass.addField(fieldSpec)
                        when (type) {
                            IntentType.BUNDLE -> {
                                builder.addParameter(Constants.BUNDLE, parameter)
                            }
                            IntentType.PARCELABLE -> {
                                builder.addParameter(Constants.PARCELABLE, parameter)
                            }
                            else -> {
                                val cls = ClassHelper.getClassFromType(type)
                                    ?: throw IllegalArgumentException("Unknown type: $type")
                                builder.addParameter(cls, parameter)
                            }
                        }
                        builder.addStatement(Constants.INTENT_PUT_EXTRA
                            + constName
                            + Constants.COMMA_SEPARATION
                            + parameter
                            + Constants.CLOSING_BRACKET)
                    }
                }
                if (intentType.isNotEmpty()) {
                    builder.addStatement(Constants.INTENT_SET_TYPE + intentType + "\"" + Constants.CLOSING_BRACKET)
                }
                builder.addStatement(Constants.START_ACTIVITY_INTENT)
            } else {
                builder.addStatement(Constants.START_ACTIVITY_NEW_INTENT,
                    Constants.INTENT_CLASS,
                    "context", activityClass.toString() + Constants.CLASS)
            }
            val intentMethod = builder.build()
            navigatorClass.addMethod(intentMethod)
        }
    }

    private fun generateServices(navigatorClass: TypeSpec.Builder) {
        for ((serviceName, intentData) in servicesMap) {
            val packageName = intentData.packageName
            val values = intentData.values
            val flags = intentData.flags
            val categories = intentData.categories
            val serviceType = intentData.serviceType
            val intentType = intentData.type
            val serviceClass = ClassName.get(packageName, serviceName)
            val builder = MethodSpec
                .methodBuilder(Constants.METHOD_PREFIX + serviceName)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(TypeName.VOID)
                .addParameter(Constants.CONTEXT, "context")
            if (values.isNotEmpty() || flags.isNotEmpty() || categories.isNotEmpty() || intentType.isNotEmpty()) {
                builder.addStatement(
                    Constants.NEW_INTENT_STATEMENT,
                    Constants.INTENT_CLASS,
                    "context", "$serviceClass.class")
                if (flags.isNotEmpty()) {
                    for (data in flags) {
                        builder.addStatement(Constants.INTENT_ADD_FLAGS
                            + data.flag
                            + Constants.CLOSING_BRACKET)
                    }
                }
                if (categories.isNotEmpty()) {
                    for (data in categories) {
                        builder.addStatement(Constants.INTENT_ADD_CATEGORY
                            + data.category
                            + Constants.CLOSING_BRACKET)
                    }
                }
                if (values.isNotEmpty()) {
                    for (data in values) {
                        val type = data.type
                        val parameter = data.parameter
                        val constName = ("EXTRA_"
                            + serviceName.toUpperCase(Locale.getDefault())
                            + "_" + parameter.toUpperCase(Locale.getDefault()))
                        extraDataMap[parameter] = constName
                        val fieldSpec = FieldSpec.builder(String::class.java, constName)
                            .addModifiers(Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC)
                            .initializer("\$S", constName)
                            .build()
                        navigatorClass.addField(fieldSpec)
                        when (type) {
                            IntentType.BUNDLE -> builder.addParameter(Constants.BUNDLE, parameter)
                            IntentType.PARCELABLE -> builder.addParameter(Constants.PARCELABLE, parameter)
                            else -> {
                                val cls = ClassHelper.getClassFromType(type)
                                    ?: throw java.lang.IllegalArgumentException("Unknown type: $type")
                                builder.addParameter(cls, parameter)
                            }
                        }
                        builder.addStatement(Constants.INTENT_PUT_EXTRA
                            + constName
                            + Constants.COMMA_SEPARATION
                            + parameter
                            + Constants.CLOSING_BRACKET)
                    }
                }
                if (intentType.isNotEmpty()) {
                    builder.addStatement(Constants.INTENT_SET_TYPE + intentType + "\"" + Constants.CLOSING_BRACKET)
                }
                when (serviceType) {
                    ServiceType.FOREGROUND -> builder.addStatement(Constants.START_FOREGROUND_SERVICE_INTENT)
                    ServiceType.BACKGROUND -> builder.addStatement(Constants.START_SERVICE_INTENT)
                    else -> {
                    }
                }
            } else {
                when (serviceType) {
                    ServiceType.FOREGROUND -> builder.addStatement(Constants.START_FOREGROUND_SERVICE_NEW_INTENT,
                        Constants.INTENT_CLASS,
                        "context", serviceClass.toString() + Constants.CLASS)
                    ServiceType.BACKGROUND -> builder.addStatement(Constants.START_SERVICE_NEW_INTENT,
                        Constants.INTENT_CLASS,
                        "context", serviceClass.toString() + Constants.CLASS)
                    else -> {
                    }
                }
            }
            val intentMethod = builder.build()
            navigatorClass.addMethod(intentMethod)
        }
    }

    private fun generateIntentPropertiesFile() {
        for ((activityName, packageName) in intentPropertiesMap) {
            val propertyClass = TypeSpec
                .classBuilder(activityName + Constants.INTENT_PROPERTY_CLASS_SUFFIX)
                .addModifiers(Modifier.FINAL)
                .addModifiers(Modifier.PUBLIC)
            val emptyConstructor = MethodSpec.constructorBuilder()
            emptyConstructor.addModifiers(Modifier.PRIVATE)
            emptyConstructor.addStatement("throw new UnsupportedOperationException(\"No instances\")")
            val activityConstructor = MethodSpec.constructorBuilder()
                .addParameter(ClassName.get(packageName, activityName), Constants.ACTIVITY_FIELD)
            for (field in fields) {
                val fieldClass = field.fieldClass
                val fieldName = field.fieldName
                val parameterName = field.annotationValue
                val item = extraDataMap[parameterName]
                when (fieldClass) {
                    Constants.BUNDLE_FIELD -> {
                        activityConstructor.addStatement("\$N.\$N = \$N.\$N().\$N(\$T.\$N)",
                            Constants.ACTIVITY_FIELD,
                            fieldName,
                            Constants.ACTIVITY_FIELD,
                            Constants.GET_INTENT_METHOD,
                            Constants.GET_BUNDLE_METHOD,
                            Constants.INTENT_NAVIGATOR_CLASS,
                            item
                        )
                    }
                    Constants.PARCELABLE_FIELD -> {
                        activityConstructor.addStatement("\$N.\$N = \$N.\$N().\$N(\$T.\$N)",
                            Constants.ACTIVITY_FIELD,
                            fieldName,
                            Constants.ACTIVITY_FIELD,
                            Constants.GET_INTENT_METHOD,
                            Constants.GET_PARCELABLE_METHOD,
                            Constants.INTENT_NAVIGATOR_CLASS,
                            item
                        )
                    }
                    else -> {
                        when (fieldClass) {
                            String::class.java.name -> {
                                activityConstructor.addStatement("\$N.\$N = \$N.\$N().getStringExtra(\$T.\$N)",
                                    Constants.ACTIVITY_FIELD,
                                    fieldName,
                                    Constants.ACTIVITY_FIELD,
                                    Constants.GET_INTENT_METHOD,
                                    Constants.INTENT_NAVIGATOR_CLASS,
                                    item
                                )
                            }
                            Int::class.javaPrimitiveType?.name -> {
                                activityConstructor.addStatement("\$N.\$N = \$N.\$N().getIntExtra(\$T.\$N, \$L)",
                                    Constants.ACTIVITY_FIELD,
                                    fieldName,
                                    Constants.ACTIVITY_FIELD,
                                    Constants.GET_INTENT_METHOD,
                                    Constants.INTENT_NAVIGATOR_CLASS,
                                    item,
                                    field.intDefaultValue
                                )
                            }
                            Boolean::class.javaPrimitiveType?.name -> {
                                activityConstructor.addStatement("\$N.\$N = \$N.\$N().getBooleanExtra(\$T.\$N, \$L)",
                                    Constants.ACTIVITY_FIELD,
                                    fieldName,
                                    Constants.ACTIVITY_FIELD,
                                    Constants.GET_INTENT_METHOD,
                                    Constants.INTENT_NAVIGATOR_CLASS,
                                    item,
                                    field.booleanDefaultValue
                                )
                            }
                            Byte::class.javaPrimitiveType?.name -> {
                                activityConstructor.addStatement("\$N.\$N = \$N.\$N().getByteExtra(\$T.\$N, \$L)",
                                    Constants.ACTIVITY_FIELD,
                                    fieldName,
                                    Constants.ACTIVITY_FIELD,
                                    Constants.GET_INTENT_METHOD,
                                    Constants.INTENT_NAVIGATOR_CLASS,
                                    item,
                                    field.byteDefaultValue
                                )
                            }
                            Short::class.javaPrimitiveType?.name -> {
                                activityConstructor.addStatement("\$N.\$N = \$N.\$N().getShortExtra(\$T.\$N, \$L)",
                                    Constants.ACTIVITY_FIELD,
                                    fieldName,
                                    Constants.ACTIVITY_FIELD,
                                    Constants.GET_INTENT_METHOD,
                                    Constants.INTENT_NAVIGATOR_CLASS,
                                    item,
                                    field.shortDefaultValue
                                )
                            }
                            Long::class.javaPrimitiveType?.name -> {
                                activityConstructor.addStatement("\$N.\$N = \$N.\$N().getLongExtra(\$T.\$N, \$LL)",
                                    Constants.ACTIVITY_FIELD,
                                    fieldName,
                                    Constants.ACTIVITY_FIELD,
                                    Constants.GET_INTENT_METHOD,
                                    Constants.INTENT_NAVIGATOR_CLASS,
                                    item,
                                    field.longDefaultValue
                                )
                            }
                            Char::class.javaPrimitiveType?.name -> {
                                activityConstructor.addStatement("\$N.\$N = \$N.\$N().getCharExtra(\$T.\$N, \'\$L\')",
                                    Constants.ACTIVITY_FIELD,
                                    fieldName,
                                    Constants.ACTIVITY_FIELD,
                                    Constants.GET_INTENT_METHOD,
                                    Constants.INTENT_NAVIGATOR_CLASS,
                                    item,
                                    field.charDefaultValue
                                )
                            }
                            Float::class.javaPrimitiveType?.name -> {
                                activityConstructor.addStatement("\$N.\$N = \$N.\$N().getFloatExtra(\$T.\$N, \$Lf)",
                                    Constants.ACTIVITY_FIELD,
                                    fieldName,
                                    Constants.ACTIVITY_FIELD,
                                    Constants.GET_INTENT_METHOD,
                                    Constants.INTENT_NAVIGATOR_CLASS,
                                    item,
                                    field.floatDefaultValue
                                )
                            }
                            Double::class.javaPrimitiveType?.name -> {
                                activityConstructor.addStatement("\$N.\$N = \$N.\$N().getDoubleExtra(\$T.\$N, \$Ld)",
                                    Constants.ACTIVITY_FIELD,
                                    fieldName,
                                    Constants.ACTIVITY_FIELD,
                                    Constants.GET_INTENT_METHOD,
                                    Constants.INTENT_NAVIGATOR_CLASS,
                                    item,
                                    field.doubleDefaultValue
                                )
                            }
                            BooleanArray::class.java.name -> {
                                activityConstructor.addStatement("\$N.\$N = \$N.\$N().getBooleanArrayExtra(\$T.\$N)",
                                    Constants.ACTIVITY_FIELD,
                                    fieldName,
                                    Constants.ACTIVITY_FIELD,
                                    Constants.GET_INTENT_METHOD,
                                    Constants.INTENT_NAVIGATOR_CLASS,
                                    item
                                )
                            }
                            ByteArray::class.java.name -> {
                                activityConstructor.addStatement("\$N.\$N = \$N.\$N().getByteArrayExtra(\$T.\$N)",
                                    Constants.ACTIVITY_FIELD,
                                    fieldName,
                                    Constants.ACTIVITY_FIELD,
                                    Constants.GET_INTENT_METHOD,
                                    Constants.INTENT_NAVIGATOR_CLASS,
                                    item
                                )
                            }
                            CharArray::class.java.name -> {
                                activityConstructor.addStatement("\$N.\$N = \$N.\$N().getCharArrayExtra(\$T.\$N)",
                                    Constants.ACTIVITY_FIELD,
                                    fieldName,
                                    Constants.ACTIVITY_FIELD,
                                    Constants.GET_INTENT_METHOD,
                                    Constants.INTENT_NAVIGATOR_CLASS,
                                    item
                                )
                            }
                            Array<CharSequence>::class.java.name -> {
                                activityConstructor.addStatement("\$N.\$N = \$N.\$N().getCharSequenceArrayExtra(\$T.\$N)",
                                    Constants.ACTIVITY_FIELD,
                                    fieldName,
                                    Constants.ACTIVITY_FIELD,
                                    Constants.GET_INTENT_METHOD,
                                    Constants.INTENT_NAVIGATOR_CLASS,
                                    item
                                )
                            }
                            CharSequence::class.java.name -> {
                                activityConstructor.addStatement("\$N.\$N = \$N.\$N().getCharSequenceExtra(\$T.\$N)",
                                    Constants.ACTIVITY_FIELD,
                                    fieldName,
                                    Constants.ACTIVITY_FIELD,
                                    Constants.GET_INTENT_METHOD,
                                    Constants.INTENT_NAVIGATOR_CLASS,
                                    item
                                )
                            }
                            LongArray::class.java.name -> {
                                activityConstructor.addStatement("\$N.\$N = \$N.\$N().getLongArrayExtra(\$T.\$N)",
                                    Constants.ACTIVITY_FIELD,
                                    fieldName,
                                    Constants.ACTIVITY_FIELD,
                                    Constants.GET_INTENT_METHOD,
                                    Constants.INTENT_NAVIGATOR_CLASS,
                                    item
                                )
                            }
                            IntArray::class.java.name -> {
                                activityConstructor.addStatement("\$N.\$N = \$N.\$N().getIntArrayExtra(\$T.\$N)",
                                    Constants.ACTIVITY_FIELD,
                                    fieldName,
                                    Constants.ACTIVITY_FIELD,
                                    Constants.GET_INTENT_METHOD,
                                    Constants.INTENT_NAVIGATOR_CLASS,
                                    item
                                )
                            }
                            Array<String>::class.java.name -> {
                                activityConstructor.addStatement("\$N.\$N = \$N.\$N().getStringArrayExtra(\$T.\$N)",
                                    Constants.ACTIVITY_FIELD,
                                    fieldName,
                                    Constants.ACTIVITY_FIELD,
                                    Constants.GET_INTENT_METHOD,
                                    Constants.INTENT_NAVIGATOR_CLASS,
                                    item
                                )
                            }
                            ShortArray::class.java.name -> {
                                activityConstructor.addStatement("\$N.\$N = \$N.\$N().getShortArrayExtra(\$T.\$N)",
                                    Constants.ACTIVITY_FIELD,
                                    fieldName,
                                    Constants.ACTIVITY_FIELD,
                                    Constants.GET_INTENT_METHOD,
                                    Constants.INTENT_NAVIGATOR_CLASS,
                                    item
                                )
                            }
                            Serializable::class.java.name -> {
                                activityConstructor.addStatement("\$N.\$N = \$N.\$N().getSerializableExtra(\$T.\$N)",
                                    Constants.ACTIVITY_FIELD,
                                    fieldName,
                                    Constants.ACTIVITY_FIELD,
                                    Constants.GET_INTENT_METHOD,
                                    Constants.INTENT_NAVIGATOR_CLASS,
                                    item
                                )
                            }
                            else -> {
                            }
                        }
                    }
                }
            }
            propertyClass.addMethod(emptyConstructor.build())
            propertyClass.addMethod(activityConstructor.build())
            intentPropertyFiles.add(JavaFile.builder(packageName, propertyClass.build()).build())
        }
    }

    private fun error(error: String) {
        messager.error(error)
    }
}