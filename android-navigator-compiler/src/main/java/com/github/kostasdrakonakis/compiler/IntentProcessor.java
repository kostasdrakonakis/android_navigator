package com.github.kostasdrakonakis.compiler;

import com.github.kostasdrakonakis.annotation.Intent;
import com.github.kostasdrakonakis.annotation.IntentType;
import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableSet;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
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
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import static com.github.kostasdrakonakis.compiler.Constants.ACTIVITY;
import static com.github.kostasdrakonakis.compiler.Constants.CLASS;
import static com.github.kostasdrakonakis.compiler.Constants.CLOSING_BRACKET;
import static com.github.kostasdrakonakis.compiler.Constants.COMMA_SEPARATION;
import static com.github.kostasdrakonakis.compiler.Constants.GENERATED_CLASS_NAME;
import static com.github.kostasdrakonakis.compiler.Constants.INTENT_PUT_EXTRA;
import static com.github.kostasdrakonakis.compiler.Constants.PACKAGE_NAME;
import static com.github.kostasdrakonakis.compiler.Constants.START_ACTIVITY_NEW_INTENT;
import static com.github.kostasdrakonakis.compiler.Constants.INTENT_CLASS;
import static com.github.kostasdrakonakis.compiler.Constants.METHOD_PREFIX;
import static com.github.kostasdrakonakis.compiler.Constants.NEW_INTENT_STATEMENT;
import static com.github.kostasdrakonakis.compiler.Constants.START_ACTIVITY_INTENT;

@AutoService(Processor.class)
public class IntentProcessor extends AbstractProcessor {

    private static final String MANIFEST_DIR = System.getProperty("user.dir") +
            File.separator + "app" +
            File.separator + "src" +
            File.separator + "main" +
            File.separator + "AndroidManifest.xml";

    private static final String LIB_DIR = System.getProperty("user.dir") +
            File.separator + "android-navigator" +
            File.separator + "src" +
            File.separator + "main" +
            File.separator + "java" +
            File.separator + "com" +
            File.separator + "github" +
            File.separator + "kostasdrakonakis" +
            File.separator + "androidnavigator";

    private Filer filer;
    private Messager messager;
    private Elements elements;
    private Map<String, AnnotationData> activitiesMap;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        filer = processingEnvironment.getFiler();
        messager = processingEnvironment.getMessager();
        elements = processingEnvironment.getElementUtils();
        activitiesMap = new HashMap<>();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        try {
            // 1- Find all annotated element
            for (Element element : roundEnvironment.getElementsAnnotatedWith(Intent.class)) {

                if (element.getKind() != ElementKind.CLASS) {
                    messager.printMessage(Diagnostic.Kind.ERROR, "Can be applied to class.");
                    return true;
                }
                TypeElement typeElement = (TypeElement) element;

                Intent intent = element.getAnnotation(Intent.class);
                String activity = typeElement.getSimpleName().toString();
                String packageName = elements.getPackageOf(typeElement)
                        .getQualifiedName().toString();

                try {
                    Class activityClass = Class.forName(packageName + activity);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                activitiesMap.put(activity, new AnnotationData(intent.value(), packageName));
            }

            // 2- Generate a class
            TypeSpec.Builder navigatorClass = TypeSpec
                    .classBuilder(GENERATED_CLASS_NAME)
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL);

            for (Map.Entry<String, AnnotationData> element : activitiesMap.entrySet()) {
                AnnotationData annotationData = element.getValue();
                String activityName = element.getKey();
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


                        Class cls = getClassFromType(type);
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
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return ImmutableSet.of(Intent.class.getCanonicalName());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    private Class getClassFromType(IntentType type) {
        switch (type) {
            case STRING:
                return String.class;
            case INT:
                return int.class;
            case BOOLEAN:
                return boolean.class;
            case BYTE:
                return byte.class;
            case SHORT:
                return short.class;
            case LONG:
                return long.class;
            case CHAR:
                return char.class;
            case FLOAT:
                return float.class;
            case DOUBLE:
                return double.class;
            case BOOLEAN_ARRAY:
                return boolean[].class;
            case BYTE_ARRAY:
                return byte[].class;
            case CHAR_ARRAY:
                return char[].class;
            case CHAR_SEQUENCE_ARRAY:
                return CharSequence[].class;
            case CHAR_SEQUENCE:
                return CharSequence.class;
            case LONG_ARRAY:
                return long[].class;
            case INT_ARRAY:
                return int[].class;
            case STRING_ARRAY:
                return String[].class;
            case SHORT_ARRAY:
                return short[].class;
            case SERIALIZABLE:
                return Serializable.class;
            default:
                return null;
        }
    }
}
