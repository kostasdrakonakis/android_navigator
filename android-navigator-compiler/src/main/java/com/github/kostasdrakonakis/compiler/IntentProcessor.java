package com.github.kostasdrakonakis.compiler;

import com.github.kostasdrakonakis.annotation.Intent;
import com.github.kostasdrakonakis.annotation.IntentProperty;
import com.github.kostasdrakonakis.annotation.IntentService;
import com.github.kostasdrakonakis.annotation.IntentType;
import com.github.kostasdrakonakis.annotation.ServiceType;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

import static com.github.kostasdrakonakis.compiler.Constants.BUNDLE;
import static com.github.kostasdrakonakis.compiler.Constants.BUNDLE_FIELD;
import static com.github.kostasdrakonakis.compiler.Constants.CLASS;
import static com.github.kostasdrakonakis.compiler.Constants.CLOSING_BRACKET;
import static com.github.kostasdrakonakis.compiler.Constants.COMMA_SEPARATION;
import static com.github.kostasdrakonakis.compiler.Constants.CONTEXT;
import static com.github.kostasdrakonakis.compiler.Constants.GENERATED_CLASS_NAME;
import static com.github.kostasdrakonakis.compiler.Constants.INTENT_ADD_CATEGORY;
import static com.github.kostasdrakonakis.compiler.Constants.INTENT_ADD_FLAGS;
import static com.github.kostasdrakonakis.compiler.Constants.INTENT_CLASS;
import static com.github.kostasdrakonakis.compiler.Constants.INTENT_PROPERTY_CLASS_SUFFIX;
import static com.github.kostasdrakonakis.compiler.Constants.INTENT_PUT_EXTRA;
import static com.github.kostasdrakonakis.compiler.Constants.INTENT_SET_TYPE;
import static com.github.kostasdrakonakis.compiler.Constants.METHOD_PREFIX;
import static com.github.kostasdrakonakis.compiler.Constants.NEW_INTENT_STATEMENT;
import static com.github.kostasdrakonakis.compiler.Constants.PACKAGE_NAME;
import static com.github.kostasdrakonakis.compiler.Constants.PARCELABLE;
import static com.github.kostasdrakonakis.compiler.Constants.PARCELABLE_FIELD;
import static com.github.kostasdrakonakis.compiler.Constants.START_ACTIVITY_INTENT;
import static com.github.kostasdrakonakis.compiler.Constants.START_ACTIVITY_NEW_INTENT;
import static com.github.kostasdrakonakis.compiler.Constants.START_FOREGROUND_SERVICE_INTENT;
import static com.github.kostasdrakonakis.compiler.Constants.START_FOREGROUND_SERVICE_NEW_INTENT;
import static com.github.kostasdrakonakis.compiler.Constants.START_SERVICE_INTENT;
import static com.github.kostasdrakonakis.compiler.Constants.START_SERVICE_NEW_INTENT;

public class IntentProcessor extends AbstractProcessor {

    private static final String SERVICE_TYPE = "android.app.Service";
    private Filer filer;
    private Messager messager;
    private Elements elements;
    private Map<String, IntentData> activitiesMap;
    private Map<String, IntentServiceData> servicesMap;
    private Map<String, String> intentPropertiesMap;
    private Map<String, String> extraDataMap;
    private List<IntentPropertyData> fields;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        filer = processingEnvironment.getFiler();
        messager = processingEnvironment.getMessager();
        elements = processingEnvironment.getElementUtils();
        activitiesMap = new HashMap<>();
        servicesMap = new HashMap<>();
        intentPropertiesMap = new HashMap<>();
        extraDataMap = new HashMap<>();
        fields = new ArrayList<>();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        try {

            if (parseIntentElements(roundEnvironment)) return true;

            if (parseIntentServiceElements(roundEnvironment)) return true;

            if (parseIntentPropertyElements(roundEnvironment)) return true;

            TypeSpec.Builder navigatorClass = TypeSpec
                    .classBuilder(GENERATED_CLASS_NAME)
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL);

            MethodSpec constructor = MethodSpec.constructorBuilder()
                    .addModifiers(Modifier.PRIVATE)
                    .addStatement("throw new UnsupportedOperationException(\"No instances\")")
                    .build();

            navigatorClass.addMethod(constructor);

            generateActivities(navigatorClass);

            generateServices(navigatorClass);

            JavaFile.builder(PACKAGE_NAME, navigatorClass.build()).build().writeTo(filer);

            generateIntentPropertiesFile();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        for (Class<? extends Annotation> annotation : getSupportedAnnotations()) {
            types.add(annotation.getCanonicalName());
        }
        return types;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    private Set<Class<? extends Annotation>> getSupportedAnnotations() {
        Set<Class<? extends Annotation>> annotations = new LinkedHashSet<>();
        annotations.add(Intent.class);
        annotations.add(IntentProperty.class);
        annotations.add(IntentService.class);
        return annotations;
    }

    private boolean parseIntentElements(RoundEnvironment roundEnvironment) {
        for (Element element : roundEnvironment.getElementsAnnotatedWith(Intent.class)) {

            if (element.getKind() != ElementKind.CLASS) {
                printError("@Intent must be applied to class.");
                return true;
            }
            TypeElement typeElement = (TypeElement) element;

            Intent intent = element.getAnnotation(Intent.class);
            String activity = typeElement.getSimpleName().toString();
            String packageName = elements.getPackageOf(typeElement)
                    .getQualifiedName().toString();
            activitiesMap.put(activity, new IntentData(intent.value(),
                    intent.flags(),
                    intent.categories(),
                    intent.type(),
                    packageName));
        }
        return false;
    }

    private boolean parseIntentServiceElements(RoundEnvironment roundEnvironment) {
        for (Element element : roundEnvironment.getElementsAnnotatedWith(IntentService.class)) {

            if (element.getKind() != ElementKind.CLASS) {
                printError("@IntentService must be applied to class.");
                return true;
            }
            TypeElement typeElement = (TypeElement) element;
            TypeMirror mirror = element.asType();

            IntentService intentService = element.getAnnotation(IntentService.class);
            String service = typeElement.getSimpleName().toString();
            String packageName = elements.getPackageOf(typeElement)
                    .getQualifiedName().toString();
            ClassName foundService = ClassName.get(packageName, service);

            // Verify that extends from Service
            if (!extendsFromType(mirror, SERVICE_TYPE)) {
                printError("Class found: " + foundService);
                printError("IntentService annotation must be used in Service class");
                return true;
            }

            servicesMap.put(service, new IntentServiceData(intentService.extras(),
                    intentService.flags(),
                    intentService.categories(),
                    intentService.type(),
                    packageName,
                    intentService.value()));
        }
        return false;
    }

    private boolean parseIntentPropertyElements(RoundEnvironment roundEnvironment) {
        for (Element element : roundEnvironment.getElementsAnnotatedWith(IntentProperty.class)) {

            if (element.getKind() != ElementKind.FIELD) {
                printError("@IntentProperty must be applied to fields.");
                return true;
            }
            TypeElement typeElement = (TypeElement) element.getEnclosingElement();
            TypeMirror mirror = element.asType();
            Set<Modifier> modifiers = element.getModifiers();
            if (modifiers.contains(Modifier.PRIVATE)
                    || modifiers.contains(Modifier.FINAL)
                    || modifiers.contains(Modifier.NATIVE)) {

                printError("@IntentProperty must be applied to public, protected, package-private fields only.");
                return true;
            }
            IntentProperty intentProperty = element.getAnnotation(IntentProperty.class);

            String activity = typeElement.getSimpleName().toString();
            String packageName = elements.getPackageOf(typeElement).getQualifiedName().toString();

            IntentPropertyData intentPropertyData = new IntentPropertyData(
                    element.getSimpleName().toString(), intentProperty.value(), mirror.toString());

            for (AnnotationMirror elementMirror : element.getAnnotationMirrors()) {
                Map<? extends ExecutableElement, ? extends AnnotationValue> map =
                        elementMirror.getElementValues();

                boolean shouldCheckValue = map.keySet().size() > 1;
                if (!shouldCheckValue) continue;

                String classType = mirror.toString();
                boolean isBooleanSet = false;
                boolean isByteSet = false;
                boolean isCharSet = false;
                boolean isFloatSet = false;
                boolean isLongSet = false;
                boolean isShortSet = false;
                boolean isIntSet = false;
                boolean isDoubleSet = false;

                for (ExecutableElement executableElement : map.keySet()) {
                    String keyName = executableElement.getSimpleName().toString();
                    isBooleanSet = "booleanDefaultValue".equals(keyName);
                    isByteSet = "byteDefaultValue".equals(keyName);
                    isCharSet = "charDefaultValue".equals(keyName);
                    isFloatSet = "floatDefaultValue".equals(keyName);
                    isLongSet = "longDefaultValue".equals(keyName);
                    isShortSet = "shortDefaultValue".equals(keyName);
                    isIntSet = "intDefaultValue".equals(keyName);
                    isDoubleSet = "doubleDefaultValue".equals(keyName);
                }

                if (isBooleanSet) {
                    boolean booleanDefaultValue = intentProperty.booleanDefaultValue();
                    if (classType.equals(boolean.class.getName())) {
                        intentPropertyData.setBooleanDefaultValue(booleanDefaultValue);
                    } else {
                        printError("booleanDefaultValue can be applied to boolean fields only.");
                        return true;
                    }
                } else if (isByteSet) {
                    byte byteDefaultValue = intentProperty.byteDefaultValue();
                    if (classType.equals(byte.class.getName())) {
                        intentPropertyData.setByteDefaultValue(byteDefaultValue);
                    } else {
                        printError("byteDefaultValue can be applied to byte fields only.");
                        return true;
                    }
                } else if (isCharSet) {
                    char charDefaultValue = intentProperty.charDefaultValue();
                    if (classType.equals(char.class.getName())) {
                        intentPropertyData.setCharDefaultValue(charDefaultValue);
                    } else {
                        printError("charDefaultValue can be applied to char fields only.");
                        return true;
                    }
                } else if (isFloatSet) {
                    float floatDefaultValue = intentProperty.floatDefaultValue();
                    if (classType.equals(float.class.getName())) {
                        intentPropertyData.setFloatDefaultValue(floatDefaultValue);
                    } else {
                        printError("floatDefaultValue can be applied to float fields only.");
                        return true;
                    }
                } else if (isLongSet) {
                    long longDefaultValue = intentProperty.longDefaultValue();
                    if (classType.equals(long.class.getName())) {
                        intentPropertyData.setLongDefaultValue(longDefaultValue);
                    } else {
                        printError("longDefaultValue can be applied to long fields only.");
                        return true;
                    }
                } else if (isShortSet) {
                    short shortDefaultValue = intentProperty.shortDefaultValue();
                    if (classType.equals(short.class.getName())) {
                        intentPropertyData.setShortDefaultValue(shortDefaultValue);
                    } else {
                        printError("shortDefaultValue can be applied to short fields only.");
                        return true;
                    }
                } else if (isIntSet) {
                    int intDefaultValue = intentProperty.intDefaultValue();
                    if (classType.equals(int.class.getName())) {
                        intentPropertyData.setIntDefaultValue(intDefaultValue);
                    } else {
                        printError("intDefaultValue can be applied to int fields only.");
                        return true;
                    }
                } else if (isDoubleSet) {
                    double doubleDefaultValue = intentProperty.doubleDefaultValue();
                    if (classType.equals(double.class.getName())) {
                        intentPropertyData.setDoubleDefaultValue(doubleDefaultValue);
                    } else {
                        printError("doubleDefaultValue can be applied to double fields only.");
                        return true;
                    }
                }

            }

            fields.add(intentPropertyData);
            intentPropertiesMap.put(activity, packageName);
        }
        return false;
    }

    private void generateActivities(TypeSpec.Builder navigatorClass) {
        for (Map.Entry<String, IntentData> element : activitiesMap.entrySet()) {
            String activityName = element.getKey();
            IntentData intentData = element.getValue();

            String packageName = intentData.getPackageName();
            List<IntentExtraData> values = intentData.getValues();
            List<IntentFlagData> flags = intentData.getFlags();
            List<IntentCategoryData> categories = intentData.getCategories();
            String intentType = intentData.getType();

            ClassName activityClass = ClassName.get(packageName, activityName);
            MethodSpec.Builder builder = MethodSpec
                    .methodBuilder(METHOD_PREFIX + activityName)
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .returns(TypeName.VOID)
                    .addParameter(CONTEXT, "context");

            if (values.size() > 0
                    || flags.size() > 0
                    || categories.size() > 0
                    || !isEmpty(intentType)) {

                builder.addStatement(NEW_INTENT_STATEMENT,
                        INTENT_CLASS,
                        "context",
                        activityClass + ".class");

                if (flags.size() > 0) {
                    for (IntentFlagData data : flags) {
                        builder.addStatement(INTENT_ADD_FLAGS
                                + data.getFlag()
                                + CLOSING_BRACKET);
                    }
                }

                if (categories.size() > 0) {
                    for (IntentCategoryData data : categories) {
                        builder.addStatement(INTENT_ADD_CATEGORY
                                + data.getCategory()
                                + CLOSING_BRACKET);
                    }
                }

                if (values.size() > 0) {
                    for (IntentExtraData data : values) {

                        IntentType type = data.getType();
                        String parameter = data.getParameter();
                        String constName = "EXTRA_"
                                + activityName.toUpperCase()
                                + "_" + parameter.toUpperCase();

                        extraDataMap.put(parameter, constName);

                        FieldSpec fieldSpec = FieldSpec.builder(String.class, constName)
                                .addModifiers(Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC)
                                .initializer("$S", constName)
                                .build();
                        navigatorClass.addField(fieldSpec);

                        if (type == IntentType.BUNDLE) {
                            builder.addParameter(BUNDLE, parameter);
                        } else if (type == IntentType.PARCELABLE) {
                            builder.addParameter(PARCELABLE, parameter);
                        } else {
                            Class cls = ClassHelper.getClassFromType(type);
                            if (cls == null) {
                                throw new IllegalArgumentException("Unknown type: " + type);
                            }

                            builder.addParameter(cls, parameter);
                        }

                        builder.addStatement(INTENT_PUT_EXTRA
                                + constName
                                + COMMA_SEPARATION
                                + parameter
                                + CLOSING_BRACKET);
                    }
                }

                if (!isEmpty(intentType)) {
                    builder.addStatement(INTENT_SET_TYPE + intentType + "\"" + CLOSING_BRACKET);
                }

                builder.addStatement(START_ACTIVITY_INTENT);
            } else {
                builder.addStatement(START_ACTIVITY_NEW_INTENT,
                        INTENT_CLASS,
                        "context",
                        activityClass + CLASS);
            }

            MethodSpec intentMethod = builder.build();
            navigatorClass.addMethod(intentMethod);
        }
    }

    private void generateServices(TypeSpec.Builder navigatorClass) {
        for (Map.Entry<String, IntentServiceData> element : servicesMap.entrySet()) {
            String serviceName = element.getKey();
            IntentServiceData intentData = element.getValue();

            String packageName = intentData.getPackageName();
            List<IntentExtraData> values = intentData.getValues();
            List<IntentFlagData> flags = intentData.getFlags();
            List<IntentCategoryData> categories = intentData.getCategories();
            ServiceType serviceType = intentData.getServiceType();
            String intentType = intentData.getType();

            ClassName serviceClass = ClassName.get(packageName, serviceName);
            MethodSpec.Builder builder = MethodSpec
                    .methodBuilder(METHOD_PREFIX + serviceName)
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .returns(TypeName.VOID)
                    .addParameter(CONTEXT, "context");

            if (values.size() > 0
                    || flags.size() > 0
                    || categories.size() > 0
                    || !isEmpty(intentType)) {

                builder.addStatement(
                        NEW_INTENT_STATEMENT,
                        INTENT_CLASS,
                        "context",
                        serviceClass + ".class");

                if (flags.size() > 0) {
                    for (IntentFlagData data : flags) {
                        builder.addStatement(INTENT_ADD_FLAGS
                                + data.getFlag()
                                + CLOSING_BRACKET);
                    }
                }

                if (categories.size() > 0) {
                    for (IntentCategoryData data : categories) {
                        builder.addStatement(INTENT_ADD_CATEGORY
                                + data.getCategory()
                                + CLOSING_BRACKET);
                    }
                }

                if (values.size() > 0) {
                    for (IntentExtraData data : values) {

                        IntentType type = data.getType();
                        String parameter = data.getParameter();
                        String constName = "EXTRA_"
                                + serviceName.toUpperCase()
                                + "_" + parameter.toUpperCase();

                        extraDataMap.put(parameter, constName);

                        FieldSpec fieldSpec = FieldSpec.builder(String.class, constName)
                                .addModifiers(Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC)
                                .initializer("$S", constName)
                                .build();
                        navigatorClass.addField(fieldSpec);

                        if (type == IntentType.BUNDLE) {
                            builder.addParameter(BUNDLE, parameter);
                        } else if (type == IntentType.PARCELABLE) {
                            builder.addParameter(PARCELABLE, parameter);
                        } else {
                            Class cls = ClassHelper.getClassFromType(type);
                            if (cls == null) {
                                throw new IllegalArgumentException("Unknown type: " + type);
                            }

                            builder.addParameter(cls, parameter);
                        }

                        builder.addStatement(INTENT_PUT_EXTRA
                                + constName
                                + COMMA_SEPARATION
                                + parameter
                                + CLOSING_BRACKET);
                    }
                }

                if (!isEmpty(intentType)) {
                    builder.addStatement(INTENT_SET_TYPE + intentType + "\"" + CLOSING_BRACKET);
                }
                switch (serviceType) {
                    case FOREGROUND:
                        builder.addStatement(START_FOREGROUND_SERVICE_INTENT);
                        break;
                    case BACKGROUND:
                        builder.addStatement(START_SERVICE_INTENT);
                        break;
                }
            } else {
                switch (serviceType) {
                    case FOREGROUND:
                        builder.addStatement(START_FOREGROUND_SERVICE_NEW_INTENT,
                                INTENT_CLASS,
                                "context",
                                serviceClass + CLASS);
                        break;
                    case BACKGROUND:
                        builder.addStatement(START_SERVICE_NEW_INTENT,
                                INTENT_CLASS,
                                "context",
                                serviceClass + CLASS);
                        break;
                }
            }

            MethodSpec intentMethod = builder.build();
            navigatorClass.addMethod(intentMethod);
        }
    }

    private void generateIntentPropertiesFile() throws IOException {
        for (Map.Entry<String, String> mapElement : intentPropertiesMap.entrySet()) {
            String activityName = mapElement.getKey();
            String packageName = mapElement.getValue();
            ClassName activityClass = ClassName.get(packageName, activityName);

            TypeSpec.Builder intentPropertyClass = TypeSpec
                    .classBuilder(activityName + INTENT_PROPERTY_CLASS_SUFFIX)
                    .addModifiers(Modifier.FINAL)
                    .addModifiers(Modifier.PUBLIC);

            MethodSpec.Builder emptyBuilder = MethodSpec.constructorBuilder();
            emptyBuilder.addModifiers(Modifier.PRIVATE);
            emptyBuilder.addStatement("throw new UnsupportedOperationException(\"No instances\")");

            MethodSpec.Builder intentPropertyBuilder =
                    MethodSpec.constructorBuilder().addParameter(activityClass, "activity");

            for (IntentPropertyData data : fields) {
                String fieldClass = data.getFieldClass();
                String fieldName = data.getFieldName();
                String parameterName = data.getAnnotationValue();

                switch (fieldClass) {
                    case BUNDLE_FIELD:
                        intentPropertyBuilder.addStatement("activity."
                                + fieldName
                                + " = activity.getIntent().getBundleExtra(\""
                                + extraDataMap.get(parameterName) + "\")");
                        break;
                    case PARCELABLE_FIELD:
                        intentPropertyBuilder.addStatement("activity."
                                + fieldName
                                + " = activity.getIntent().getParcelable(\""
                                + extraDataMap.get(parameterName) + "\")");
                        break;
                    default:
                        intentPropertyBuilder.addStatement("activity."
                                + fieldName
                                + " = activity.getIntent()."
                                + ClassHelper.getIntentExtraFromClass(
                                fieldClass, extraDataMap.get(parameterName), data));
                        break;
                }
            }

            MethodSpec emptyConstructor = emptyBuilder.build();
            intentPropertyClass.addMethod(emptyConstructor);

            MethodSpec intentPropertyConstructor = intentPropertyBuilder.build();
            intentPropertyClass.addMethod(intentPropertyConstructor);

            JavaFile.builder(packageName, intentPropertyClass.build()).build().writeTo(filer);
        }
    }

    private static boolean isEmpty(CharSequence str) {
        return str == null || str.length() == 0;
    }

    private static boolean extendsFromType(TypeMirror typeMirror, String otherType) {
        if (isTypeEqual(typeMirror, otherType)) {
            return true;
        }
        if (typeMirror.getKind() != TypeKind.DECLARED) {
            return false;
        }
        DeclaredType declaredType = (DeclaredType) typeMirror;
        List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();
        if (typeArguments.size() > 0) {
            StringBuilder typeString = new StringBuilder(declaredType.asElement().toString());
            typeString.append('<');
            for (int i = 0; i < typeArguments.size(); i++) {
                if (i > 0) {
                    typeString.append(',');
                }
                typeString.append('?');
            }
            typeString.append('>');
            if (typeString.toString().equals(otherType)) {
                return true;
            }
        }
        Element element = declaredType.asElement();
        if (!(element instanceof TypeElement)) {
            return false;
        }
        TypeElement typeElement = (TypeElement) element;
        TypeMirror superType = typeElement.getSuperclass();
        if (extendsFromType(superType, otherType)) {
            return true;
        }
        for (TypeMirror interfaceType : typeElement.getInterfaces()) {
            if (extendsFromType(interfaceType, otherType)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isTypeEqual(TypeMirror typeMirror, String otherType) {
        return otherType.equals(typeMirror.toString());
    }

    private void printError(String message) {
        messager.printMessage(Diagnostic.Kind.ERROR, message);
    }
}
