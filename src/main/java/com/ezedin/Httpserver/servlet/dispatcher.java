package com.ezedin.Httpserver.servlet;

import com.ezedin.Httpserver.httpserver.httpRequest;
import com.ezedin.Httpserver.httpserver.models.HttpMethod;
import com.ezedin.Httpserver.servlet.annotations.customController;
import com.ezedin.Httpserver.servlet.annotations.customGet;
import com.ezedin.Httpserver.servlet.annotations.customPost;
import com.ezedin.Httpserver.servlet.annotations.customResponseBody;
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
    private final String basePackage;

    public dispatcher(String basePackage) {
        this.basePackage = basePackage;
        scanController(this.basePackage);
    }
    public Object dispatch(httpRequest request, String contentType) {
    String key = request.httpMethod().toString() +":"+ request.Path();
        System.out.println(key);
    Method method = routeMethods.get(key);
        System.out.println(method + "methode");
        System.out.println(controllers.get(key));
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
                String paramName = p.getAnnotation(customResponseBody.class).value();
                Class<?> paramType = p.getType();
                System.out.println("param name: "+paramName);
                JsonNode node =jsonNode.get(paramName);
                System.out.println(node);
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
        System.out.println(e.getMessage());
        return "500 Internal Server Error test";
    }
    }
    private List<Parameter> findRequestBodyParameter(Method method) {
        List<Parameter> paramList = new ArrayList<>();
        for (Parameter param : method.getParameters()) {
            if (param.isAnnotationPresent(customResponseBody.class)) {
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
        System.out.println(routeMethods);
        System.out.println(controllers);
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
