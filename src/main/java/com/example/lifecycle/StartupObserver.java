package com.example.lifecycle;

import com.example.lifecycle.playground.PlaygroundSelection;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Component;

@Component
public class StartupObserver implements SmartInitializingSingleton {

    private final ApplicationArguments applicationArguments;

    public StartupObserver(ApplicationArguments applicationArguments) {
        this.applicationArguments = applicationArguments;
    }

    @Override
    public void afterSingletonsInstantiated() {
        if (!PlaygroundSelection.includes("lifecycle", applicationArguments.getSourceArgs())) {
            return;
        }
        // This runs after all regular non-lazy singleton beans are initialized.
        // It is safer for cross-bean coordination than assuming other beans
        // are ready inside one bean's @PostConstruct method.
        System.out.println("[StartupObserver] All non-lazy singleton beans are initialized");
    }
}
