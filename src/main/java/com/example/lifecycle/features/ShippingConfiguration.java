package com.example.lifecycle.features;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ShippingConfiguration {

    // Conditions are evaluated while Spring builds the bean definition graph.
    // The fast integration is enabled only when the property explicitly asks
    // for it. This is useful for environment-specific infrastructure.
    @Bean
    @ConditionalOnProperty(prefix = "feature", name = "shipping-provider", havingValue = "fast")
    ShippingProvider fastShippingProvider() {
        return new FastShippingProvider();
    }

    // This is a fallback, not a second competing bean. It is selected only if
    // no other ShippingProvider exists, which keeps injection unambiguous.
    @Bean
    @ConditionalOnMissingBean(ShippingProvider.class)
    ShippingProvider defaultShippingProvider() {
        return new DefaultShippingProvider();
    }
}
