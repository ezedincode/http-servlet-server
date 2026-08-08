package com.ezedin.Httpserver.Ioc;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
todo
1.handle circular dependency
2.handle interface and spring class like String .. dependency -now this class work only for class dependency
 */
public class BeanFactory {
    private final List<BeanDefinition> beanDefinitions;
    private final ApplicationContext applicationContext;
    private final Map<Class<?>, Object> beanDefinitionMap = new HashMap<Class<?>, Object>();

    public BeanFactory(List<BeanDefinition> beanFactory) {
        this.beanDefinitions = beanFactory;
        feeder();
        applicationContext = new ApplicationContext(beanDefinitionMap);
    }

    public void feeder() {
        for (BeanDefinition beanDefinition : beanDefinitions) {
            createBeans(beanDefinition.getBeanClass());
        }
    }

    public void createBeans(Class<?> bean) {
        if (beanDefinitionMap.containsKey(bean)) {
            return;
        }
        if (bean.getDeclaredConstructors().length != 1) {
            throw new IllegalStateException();
        }
        Constructor<?>[] constructors = bean.getDeclaredConstructors();
        Class<?>[] parameterTypes = constructors[0].getParameterTypes();
        Object[] newParameterTypes = new Object[parameterTypes.length];
        int i = 0;
        for (Class<?> parameter : parameterTypes) {
            if (beanDefinitionMap.containsKey(parameter)) {
                newParameterTypes[i] = beanDefinitionMap.get(parameter);
                i++;
                continue;
            }
            createBeans(parameter);
            newParameterTypes[i] = beanDefinitionMap.get(parameter);
            i++;
        }
        Object obj = createInstance(newParameterTypes, bean);
        beanDefinitionMap.put(bean, obj);

    }

    private Object createInstance(Object[] parameters, Class<?> bean) {
        try {
            Object[] args = new Object[parameters.length];
            Class<?>[] dependencies  = new Class[parameters.length];
            int i = 0;
            for (Object parameter : parameters) {
                args[i] = parameter;
                dependencies [i] = parameter.getClass();
                i++;
            }
            Constructor<?> constructor = bean.getDeclaredConstructor(dependencies );
            constructor.setAccessible(true);

            return constructor.newInstance(args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
