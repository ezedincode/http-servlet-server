package com.ezedin.Httpserver.Ioc;

public class BeanDefinition {
    private Class<?> beanClass;

    public BeanDefinition (Class<?> beanClass){
        this.beanClass = beanClass;
    }
    public Class<?> getBeanClass() {
        return beanClass;
    }
}
