package com.ezedin.Httpserver.servlet;

import com.ezedin.Httpserver.Ioc.beanScanner;
import com.ezedin.Httpserver.httpserver.httpRequest;
import com.ezedin.Httpserver.servlet.annotations.customRequestBody;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class dispatcher {
    private final Map<String, Method> routeMethods;
    private final Map<String, Object> controllers;

    public dispatcher(beanScanner scan) {
        routeMethods = scan.getControllerMethods();
        controllers = scan.getControllers();
    }

    public Object dispatch(httpRequest request, String contentType) {
        String key = request.httpMethod().toString() + ":" + request.Path();
        Method method = routeMethods.get(key);
        if (method == null) {
            return "404 Not Found";
        }
        try {
            Object controllerInstance = controllers.get(key);
            if (contentType.contains("json")) {
              ObjectMapper mapper = new ObjectMapper();
              JsonNode jsonNode = mapper.readTree(request.body());
              List<Parameter> objectName = findRequestBodyParameter(method);
              Object[] args = new Object[objectName.size()];
              int i = 0;
              for (Parameter p : objectName) {
                  String paramName = p.getAnnotation(customRequestBody.class).value();
                  Class<?> paramType = p.getType();
                  JsonNode node = jsonNode.get(paramName);
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
            } else {
                return method.invoke(controllerInstance);
            }


        } catch (Exception e) {
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

}
