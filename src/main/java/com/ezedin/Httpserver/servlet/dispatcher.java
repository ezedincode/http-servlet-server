package com.ezedin.Httpserver.servlet;

import org.reflections.Reflections;

import java.lang.reflect.Method;
import java.util.Set;

public class dispatcher {
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
                if(method.isAnnotationPresent(customGet.class)){}
            }
        }
    }
}
