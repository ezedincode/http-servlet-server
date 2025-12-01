package com.ezedin.Httpserver.servlet;

import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class dispatcher {
    private final Map<String, Method> routeMethods = new HashMap<>();
    private final Map<String, Object> controllers = new HashMap<>();

    public dispatcher(String basePackage) {
        scanController(basePackage);
    }
    public String dispatch(String httpMethod,String path){

    return "";
    }
    private void scanController(String basePackage){
        Reflections reflections = new Reflections(basePackage);
        Set<Class<?>> classList = reflections.getTypesAnnotatedWith(customController.class);
        for(Class<?> clazz : classList){
            if(!clazz.isAnnotationPresent(customController.class)) continue;
            Object instance = null;
            for(Method method : clazz.getDeclaredMethods()){
                if(method.isAnnotationPresent(customGet.class)){
                    if(instance == null) instance = createInstance(clazz);
                    String path = method.getAnnotation(customGet.class).value();
                    String key = "GET: "+path;
                    routeMethods.put(key,method);
                    controllers.put(key,instance);
                }
                if (method.isAnnotationPresent(customPost.class)) {
                    if (instance == null) instance = createInstance(clazz);

                    String path = method.getAnnotation(customPost.class).value();
                    String key = "POST:" + path;
                    routeMethods.put(key, method);
                    controllers.put(key, instance);
                }
            }
        }
    }
    private Object createInstance(Class<?> cls){
        try{
            Constructor<?> constructor = cls.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }
}
