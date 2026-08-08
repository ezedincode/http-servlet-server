package com.ezedin.Httpserver.Ioc;

import java.util.List;

public class ApplicationContext {
    private final BeanFactory beanFactory;

    public ApplicationContext(List<BeanDefinition> beanDefinitions) {
        this.beanFactory = new BeanFactory(beanDefinitions);
    }

    public <T> T getBean(Class<T> type) {
        return beanFactory.getBean(type);
    }
}
