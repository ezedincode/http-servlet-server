package com.ezedin.Httpserver.Ioc;

import java.util.HashMap;
import java.util.Map;

public class ApplicationContext {
    private final Map<Class<?>, Object> beanDefinitions = new HashMap<Class<?>, Object>();

    public ApplicationContext(Map<Class<?>, Object> beanDefinitions ) {
        this.beanDefinitions.putAll(beanDefinitions);

    }

    public Map<Class<?>, Object> getBeanDefinitions() {
        return beanDefinitions;
    }
}
