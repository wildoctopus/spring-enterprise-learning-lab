package com.example.lifecycle;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.context.ResourceLoaderAware;

public class LifecycleBean implements BeanNameAware, ApplicationContextAware,
        EnvironmentAware, ResourceLoaderAware, InitializingBean, DisposableBean {

    private String beanName;
    private ApplicationContext applicationContext;
    private Environment environment;
    private ResourceLoader resourceLoader;

    public LifecycleBean() {
        // Phase 1: construction. Keep this cheap: dependencies are not yet
        // injected and the application context is not available here.
        log("1. Constructor");
    }

    @Override
    public void setBeanName(String name) {
        // Phase 2: Spring gives the bean its registered name. Use this only
        // when the name is genuinely needed.
        this.beanName = name;
        log("2. BeanNameAware.setBeanName(" + name + ")");
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        // Phase 2: this is a framework integration escape hatch. Constructor
        // injection is usually clearer for normal application dependencies.
        this.applicationContext = applicationContext;
        log("3. ApplicationContextAware.setApplicationContext(...)");
    }

    @Override
    public void setEnvironment(Environment environment) {
        // Phase 2: for business configuration, prefer @ConfigurationProperties
        // or @Value so configuration remains explicit and testable.
        this.environment = environment;
        log("4. EnvironmentAware.setEnvironment(...)");
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        // Phase 2: useful for framework/resource integration. Do not load
        // remote or expensive resources during this callback.
        this.resourceLoader = resourceLoader;
        log("5. ResourceLoaderAware.setResourceLoader(...)");
    }

    @PostConstruct
    void postConstruct() {
        // Phase 3: properties are injected. Keep this short and local: validate
        // values or build cheap in-memory state.
        log("7. @PostConstruct");
    }

    @Override
    public void afterPropertiesSet() {
        // Phase 3 alternative: Spring-specific equivalent of @PostConstruct.
        // Use it only when implementing a Spring lifecycle contract is useful.
        log("8. InitializingBean.afterPropertiesSet()");
    }

    void customInit() {
        // Phase 3 alternative: useful for classes you cannot modify, such as
        // third-party library types configured through @Bean.
        log("9. Custom init method");
    }

    @Override
    public void destroy() {
        // Shutdown phase: release resources owned by this bean. Cleanup should
        // be best effort because remote services may already be unavailable.
        log("12. DisposableBean.destroy()");
    }

    @PreDestroy
    void preDestroy() {
        // Shutdown phase: preferred annotation for cleanup owned by this bean.
        log("11. @PreDestroy");
    }

    void customDestroy() {
        // Shutdown alternative: configured by @Bean(destroyMethod = ...).
        log("13. Custom destroy method");
    }

    public void doWork() {
        // This runs after initialization and demonstrates that the aware
        // callback objects are now available for use.
        String appName = environment.getProperty("spring.application.name");
        Resource resource = resourceLoader.getResource("classpath:application.properties");
        boolean present = applicationContext.containsBean(beanName);
        log("Business method: app=" + appName + ", configExists=" + resource.exists()
                + ", contextContainsThisBean=" + present);
    }

    private void log(String message) {
        System.out.println("[LifecycleBean] " + message);
    }
}
