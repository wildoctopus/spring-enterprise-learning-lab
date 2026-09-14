package com.example.lifecycle;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.stereotype.Component;

@Component
public class StartupObserver implements SmartInitializingSingleton {

    @Override
    public void afterSingletonsInstantiated() {
        // This runs after all regular non-lazy singleton beans are initialized.
        // It is safer for cross-bean coordination than assuming other beans
        // are ready inside one bean's @PostConstruct method.
        System.out.println("[StartupObserver] All non-lazy singleton beans are initialized");
    }
}
