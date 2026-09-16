package com.example.lifecycle;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import com.example.lifecycle.playground.PlaygroundSelection;

@SpringBootApplication
@EnableCaching
@EnableTransactionManagement
public class BeanLifecycleDemoApplication {

    // @Bean is an explicit configuration option. It is useful when the bean
    // comes from a third-party class or when lifecycle methods must be named
    // in configuration rather than annotated on the class.
    @Bean(initMethod = "customInit", destroyMethod = "customDestroy")
    LifecycleBean lifecycleBean() {
        return new LifecycleBean();
    }

    public static void main(String[] args) {
        if (PlaygroundSelection.isHelp(args)) {
            com.example.lifecycle.playground.PlaygroundGuideRunner.printCatalogAndUsage();
            return;
        }
        if (!PlaygroundSelection.isKnownTopic(args)) {
            throw new IllegalArgumentException("Unknown playground '"
                    + PlaygroundSelection.selectedTopic(args) + "'");
        }
        // Spring creates the ApplicationContext, discovers bean definitions,
        // creates eager singleton beans, and runs their initialization phases.
        ConfigurableApplicationContext context = SpringApplication.run(BeanLifecycleDemoApplication.class, args);

        if (PlaygroundSelection.includes("lifecycle", args)) {
            System.out.println("\n--- Application is ready ---");
            LifecycleBean bean = context.getBean(LifecycleBean.class);
            bean.doWork();
        }

        // The web server owns this context and closes it during graceful
        // application shutdown, which triggers @PreDestroy and destroy methods.
        // An OS kill, crash, or forceful container termination cannot guarantee them.
    }
}
