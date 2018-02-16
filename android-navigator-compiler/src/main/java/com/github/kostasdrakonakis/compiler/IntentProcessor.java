package com.github.kostasdrakonakis.compiler;

import com.github.kostasdrakonakis.annotation.Intent;
import com.github.kostasdrakonakis.annotation.IntentProperty;
import com.github.kostasdrakonakis.annotation.IntentType;
import com.google.auto.service.AutoService;
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
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

import static com.github.kostasdrakonakis.compiler.Constants.ACTIVITY;
import static com.github.kostasdrakonakis.compiler.Constants.CLASS;
import static com.github.kostasdrakonakis.compiler.Constants.CLOSING_BRACKET;
import static com.github.kostasdrakonakis.compiler.Constants.COMMA_SEPARATION;
import static com.github.kostasdrakonakis.compiler.Constants.GENERATED_CLASS_NAME;
import static com.github.kostasdrakonakis.compiler.Constants.INTENT_CLASS;
import static com.github.kostasdrakonakis.compiler.Constants.INTENT_PROPERTY_CLASS_SUFFIX;
import static com.github.kostasdrakonakis.compiler.Constants.INTENT_PUT_EXTRA;
import static com.github.kostasdrakonakis.compiler.Constants.METHOD_PREFIX;
import static com.github.kostasdrakonakis.compiler.Constants.NEW_INTENT_STATEMENT;
import static com.github.kostasdrakonakis.compiler.Constants.PACKAGE_NAME;
import static com.github.kostasdrakonakis.compiler.Constants.START_ACTIVITY_INTENT;
import static com.github.kostasdrakonakis.compiler.Constants.START_ACTIVITY_NEW_INTENT;

@AutoService(Processor.class)
public class IntentProcessor extends AbstractProcessor {

    private Filer filer;
    private Messager messager;
    private Elements elements;
    private Map<String, AnnotationData> activitiesMap;
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
        intentPropertiesMap = new HashMap<>();
        extraDataMap = new HashMap<>();
        fields = new ArrayList<>();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        try {

            for (Element element : roundEnvironment.getElementsAnnotatedWith(Intent.class)) {

                if (element.getKind() != ElementKind.CLASS) {
                    messager.printMessage(
                            Diagnostic.Kind.ERROR, "@Intent must be applied to class.");
                    return true;
                }
                TypeElement typeElement = (TypeElement) element;

                Intent intent = element.getAnnotation(Intent.class);
                String activity = typeElement.getSimpleName().toString();
                String packageName = elements.getPackageOf(typeElement)
                        .getQualifiedName().toString();
                activitiesMap.put(activity, new AnnotationData(intent.value(), packageName));
            }


            for (Element element : roundEnvironment.getElementsAnnotatedWith(IntentProperty.class)) {

                if (element.getKind() != ElementKind.FIELD) {
                    messager.printMessage(
                            Diagnostic.Kind.ERROR, "@IntentProperty must be applied to fields.");
                    return true;
                }
                TypeElement typeElement = (TypeElement) element.getEnclosingElement();
                TypeMirror mirror = element.asType();
                IntentProperty intentProperty = element.getAnnotation(IntentProperty.class);

                String activity = typeElement.getSimpleName().toString();
                String packageName = elements.getPackageOf(typeElement)
                        .getQualifiedName().toString();

                fields.add(new IntentPropertyData(
                        element.getSimpleName().toString(),
                        intentProperty.value(),
                        mirror.toString()));
                intentPropertiesMap.put(activity, packageName);
            }

            TypeSpec.Builder navigatorClass = TypeSpec
                    .classBuilder(GENERATED_CLASS_NAME)
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL);

            for (Map.Entry<String, AnnotationData> element : activitiesMap.entrySet()) {
                String activityName = element.getKey();
                AnnotationData annotationData = element.getValue();

                String packageName = annotationData.getPackageName();
                List<IntentExtraData> values = annotationData.getValues();

                ClassName activityClass = ClassName.get(packageName, activityName);
                MethodSpec.Builder builder = MethodSpec
                        .methodBuilder(METHOD_PREFIX + activityName)
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .returns(TypeName.VOID)
                        .addParameter(ACTIVITY, "activity");

                if (values.size() > 0) {
                    builder.addStatement(
                            NEW_INTENT_STATEMENT,
                            INTENT_CLASS,
                            "activity",
                            activityClass + ".class");

                    for (IntentExtraData data : values) {

                        IntentType type = data.getType();
                        String parameter = data.getParameter();
                        String constName = "EXTRA_"
                                + activityName.toUpperCase()
                                + "_" + parameter.toUpperCase();

                        extraDataMap.put(parameter, constName);

                        Class cls = ClassHelper.getClassFromType(type);
                        if (cls == null) {
                            throw new IllegalArgumentException("Unknown type: " + type);
                        }

                        FieldSpec fieldSpec = FieldSpec.builder(String.class, constName)
                                .addModifiers(Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC)
                                .initializer("$S", constName)
                                .build();
                        navigatorClass.addField(fieldSpec);

                        builder.addParameter(cls, parameter);
                        builder.addStatement(INTENT_PUT_EXTRA
                                + constName
                                + COMMA_SEPARATION
                                + parameter
                                + CLOSING_BRACKET);
                    }
                    builder.addStatement(START_ACTIVITY_INTENT);
                } else {
                    builder.addStatement(START_ACTIVITY_NEW_INTENT,
                            INTENT_CLASS,
                            "activity",
                            activityClass + CLASS);
                }

                MethodSpec intentMethod = builder.build();
                navigatorClass.addMethod(intentMethod);
            }

            JavaFile.builder(PACKAGE_NAME, navigatorClass.build()).build().writeTo(filer);

            for (Map.Entry<String, String> mapElement : intentPropertiesMap.entrySet()) {
                String activityName = mapElement.getKey();
                String packageName = mapElement.getValue();
                ClassName activityClass = ClassName.get(packageName, activityName);

                TypeSpec.Builder intentPropertyClass = TypeSpec
                        .classBuilder(activityName + INTENT_PROPERTY_CLASS_SUFFIX)
                        .addModifiers(Modifier.PUBLIC);

                MethodSpec.Builder intentPropertyConstructorBuilder =
                        MethodSpec.constructorBuilder().addParameter(activityClass, "activity");

                for (IntentPropertyData data : fields) {
                    String fieldClass = data.getFieldClass();
                    String fieldName = data.getFieldName();
                    String parameterName = data.getAnnotationValue();

                    intentPropertyConstructorBuilder.addStatement("activity."
                            + fieldName
                            + " = activity.getIntent()."
                            + ClassHelper.getIntentExtraFromClass(
                            fieldClass, extraDataMap.get(parameterName)));
                }

                MethodSpec intentPropertyConstructor = intentPropertyConstructorBuilder.build();
                intentPropertyClass.addMethod(intentPropertyConstructor);

                JavaFile.builder(PACKAGE_NAME, intentPropertyClass.build()).build().writeTo(filer);
            }

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
        return annotations;
    }
}
