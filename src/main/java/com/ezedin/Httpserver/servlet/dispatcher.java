package com.ezedin.Httpserver.servlet;

import com.ezedin.Httpserver.httpserver.httpRequest;
import com.ezedin.Httpserver.servlet.annotations.customController;
import com.ezedin.Httpserver.servlet.annotations.customGet;
import com.ezedin.Httpserver.servlet.annotations.customPost;
import com.ezedin.Httpserver.servlet.annotations.customRequestBody;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

public class dispatcher {
    private final Map<String, Method> routeMethods = new HashMap<>();
    private final Map<String, Object> controllers = new HashMap<>();

    public dispatcher(String basePackage) {
        scanController(basePackage);
    }
    public Object dispatch(httpRequest request, String contentType) {
    String key = request.httpMethod().toString() +":"+ request.Path();
    Method method = routeMethods.get(key);
    if(method == null){
        return "404 Not Found";
    }
   try{
        Object controllerInstance = controllers.get(key);
        if(contentType.contains("json")){
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(request.body());
            List<Parameter> objectName=findRequestBodyParameter(method);
            Object[] args = new Object[objectName.size()];
            int i = 0;
            for(Parameter p:objectName){
                String paramName = p.getAnnotation(customRequestBody.class).value();
                Class<?> paramType = p.getType();
                JsonNode node =jsonNode.get(paramName);
                if (paramType == String.class) {
                    args[i] = node.asText();
                } else if (paramType == int.class || paramType == Integer.class) {
                    args[i] = node.asInt();
                } else {
                    args[i] = mapper.treeToValue(node, paramType);
                }
                    i++;
            }

            return method.invoke(controllerInstance, args);
        }else{
            return method.invoke(controllerInstance);
        }


    }catch (Exception e){
        return "500 Internal Server Error";
    }
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
                    String key = "GET:"+path;
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
