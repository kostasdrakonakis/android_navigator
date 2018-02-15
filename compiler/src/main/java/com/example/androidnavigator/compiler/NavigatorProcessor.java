package com.example.androidnavigator.compiler;

import com.example.androidnavigator.annotation.Navigate;
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

import static com.example.androidnavigator.compiler.Constants.ACTIVITY;
import static com.example.androidnavigator.compiler.Constants.CLASS;
import static com.example.androidnavigator.compiler.Constants.GENERATED_CLASS_NAME;
import static com.example.androidnavigator.compiler.Constants.INTENT;
import static com.example.androidnavigator.compiler.Constants.INTENT_CLASS;
import static com.example.androidnavigator.compiler.Constants.METHOD_PREFIX;
import static com.example.androidnavigator.compiler.Constants.NEW_INTENT_STATEMENT;
import static com.example.androidnavigator.compiler.Constants.START_ACTIVITY_INTENT;

@AutoService(Processor.class)
public class NavigatorProcessor extends AbstractProcessor {

    private static final String PROJECT_DIR = System.getProperty("user.dir") +
            File.separator + "app" +
            File.separator + "src" +
            File.separator + "main" +
            File.separator + "AndroidManifest.xml";

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
            for (Element element : roundEnvironment.getElementsAnnotatedWith(Navigate.class)) {

                if (element.getKind() != ElementKind.CLASS) {
                    messager.printMessage(Diagnostic.Kind.ERROR, "Can be applied to class.");
                    return true;
                }
                TypeElement typeElement = (TypeElement) element;

                Navigate navigate = element.getAnnotation(Navigate.class);
                String activity = typeElement.getSimpleName().toString();
                String packageName = elements.getPackageOf(typeElement)
                        .getQualifiedName().toString();

                activitiesMap.put(activity, new AnnotationData(navigate.value(), packageName));
            }

            // 2- Generate a class
            TypeSpec.Builder navigatorClass = TypeSpec
                    .classBuilder(GENERATED_CLASS_NAME)
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL);

            for (Map.Entry<String, AnnotationData> element : activitiesMap.entrySet()) {
                AnnotationData annotationData = element.getValue();
                String activityName = element.getKey();
                String packageName = annotationData.getPackageName();
                String[] values = annotationData.getValues();
                ClassName activityClass = ClassName.get(packageName, activityName);
                MethodSpec.Builder builder = MethodSpec
                        .methodBuilder(METHOD_PREFIX + activityName)
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .returns(TypeName.VOID)
                        .addParameter(ACTIVITY, "activity");
                if (values.length > 0) {
                    builder.addStatement(
                            NEW_INTENT_STATEMENT,
                            INTENT_CLASS,
                            "activity",
                            activityClass + ".class");

                    for (String value : values) {
                        String[] splitValues = value.split(":");
                        String type = splitValues[0];
                        String parameter = splitValues[1];
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
                        builder.addStatement("intent.putExtra(" + constName + ", " + parameter + ")");
                    }
                    builder.addStatement(START_ACTIVITY_INTENT);
                } else {
                    builder.addStatement(INTENT, INTENT_CLASS, "activity", activityClass + CLASS);
                }

                MethodSpec intentMethod = builder.build();
                navigatorClass.addMethod(intentMethod);
            }

            File manifest = new File(PROJECT_DIR);
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = builderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(manifest);
            document.getDocumentElement().normalize();

            org.w3c.dom.Element element = document.getDocumentElement();
            String packageName = element.getAttribute("package");

            // 3- Write generated class to a file
            JavaFile.builder(packageName, navigatorClass.build()).build().writeTo(filer);
        } catch (IOException | ParserConfigurationException | SAXException e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return ImmutableSet.of(Navigate.class.getCanonicalName());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    private Class getClassFromType(String type) {
        switch (type) {
            case "String":
                return String.class;
            case "int":
                return int.class;
            case "boolean":
                return boolean.class;
            case "byte":
                return byte.class;
            case "short":
                return short.class;
            case "long":
                return long.class;
            case "char":
                return char.class;
            case "float":
                return float.class;
            case "double":
                return double.class;
            case "boolean[]":
                return boolean[].class;
            case "byte[]":
                return byte[].class;
            case "char[]":
                return char[].class;
            case "CharSequence[]":
                return CharSequence[].class;
            case "CharSequence":
                return CharSequence.class;
            case "long[]":
                return long[].class;
            case "int[]":
                return int[].class;
            case "String[]":
                return String[].class;
            case "short[]":
                return short[].class;
            case "Serializable":
                return Serializable.class;
            default:
                return null;
        }
    }
}
