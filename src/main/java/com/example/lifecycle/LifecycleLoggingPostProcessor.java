package com.example.lifecycle;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

@Component
public class LifecycleLoggingPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof LifecycleBean) {
            // This hook runs around initialization callbacks. Returning a
            // different object is how wrappers/proxies can be introduced, so
            // never return null accidentally.
            System.out.println("[PostProcessor] 6. postProcessBeforeInitialization(" + beanName + ")");
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof LifecycleBean) {
            // This is after @PostConstruct, afterPropertiesSet, and configured
            // init methods. Cross-cutting concerns commonly use this phase.
            System.out.println("[PostProcessor] 10. postProcessAfterInitialization(" + beanName + ")");
        }
        return bean;
    }
}
