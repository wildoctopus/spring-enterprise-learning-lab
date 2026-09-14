package com.example.lifecycle.features;

import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * A small example of explicit bean registration for a type owned by the JDK
 * or an external library rather than by this application.
 */
@Configuration
public class ConfigurationBeanDemoConfiguration {

    @Bean
    Clock demoClock() {
        return Clock.systemUTC();
    }

    @Bean
    ConfiguredGreetingService configuredGreetingService(Clock demoClock) {
        return new ConfiguredGreetingService(demoClock);
    }

    public record ConfiguredGreetingService(Clock clock) {
    }
}