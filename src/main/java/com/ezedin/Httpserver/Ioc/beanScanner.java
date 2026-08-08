package com.ezedin.Httpserver.Ioc;

import com.ezedin.Httpserver.servlet.annotations.customController;
import com.ezedin.Httpserver.servlet.annotations.customGet;
import com.ezedin.Httpserver.servlet.annotations.customPost;
import com.ezedin.Httpserver.servlet.annotations.customRequestBody;
import com.ezedin.Httpserver.servlet.annotations.customService;
import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class beanScanner {
    private final Map<String, Method> controllerMethods = new HashMap<>();
    private final Map<String, Object> controllers = new HashMap<>();
    private List<BeanDefinition> beanDefinitions = new ArrayList<>();

    List<Class<? extends Annotation>> annotations = List.of(
            customService.class,
            customController.class
    );

    public Map<String, Method> getControllerMethods() {
        return controllerMethods;
    }

    public Map<String, Object> getControllers() {
        return controllers;
    }

    public beanScanner(String basePackage) {
        scanController(basePackage);
    }

    private List<Parameter> findRequestBodyParameter(Method method) {
        List<Parameter> paramList = new ArrayList<>();
        for (Parameter param : method.getParameters()) {
            if (param.isAnnotationPresent(customRequestBody.class)) {
                paramList.add(param);
            }
        }
        return paramList;
    }

    private void scanController(String basePackage) {
        Reflections reflections = new Reflections(basePackage);
        Set<Class<?>> classList = reflections.getTypesAnnotatedWith(customController.class);
        for (Class<?> clazz : classList) {
            if (!clazz.isAnnotationPresent(customController.class)) continue;
            Object instance = null;
            for (Method method : clazz.getDeclaredMethods()) {
                if (method.isAnnotationPresent(customGet.class)) {
                    if (instance == null) instance = createInstance(clazz);
                    String path = method.getAnnotation(customGet.class).value();
                    String key = "GET:" + path;
                    controllerMethods.put(key, method);
                    controllers.put(key, instance);
                }
                if (method.isAnnotationPresent(customPost.class)) {
                    if (instance == null) instance = createInstance(clazz);

                    String path = method.getAnnotation(customPost.class).value();
                    String key = "POST:" + path;
                    controllerMethods.put(key, method);
                    controllers.put(key, instance);
                }
            }
        }
    }

    private void scanBean(String basePackage) {
        Reflections reflections = new Reflections(basePackage);
        beanDefinitions = List.copyOf(annotations.stream()
                .flatMap(annotation -> reflections.getTypesAnnotatedWith(annotation).stream())
                .distinct()
                .map(BeanDefinition::new)
                .toList());
    }

    private Object createInstance(Class<?> cls) {
        try {
            Constructor<?> constructor = cls.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
