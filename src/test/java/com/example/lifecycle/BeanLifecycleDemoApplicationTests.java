package com.example.lifecycle;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import com.example.lifecycle.features.OrderRepository;
import com.example.lifecycle.features.OrderService;
import com.example.lifecycle.features.ProductCatalog;
import com.example.lifecycle.features.TransactionProxyPlayground;
import com.example.lifecycle.features.ConfigurationBeanDemoConfiguration.ConfiguredGreetingService;
import com.example.lifecycle.features.ShippingProvider;
import java.time.Clock;

@SpringBootTest
class BeanLifecycleDemoApplicationTests {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductCatalog productCatalog;

    @Autowired
    private ShippingProvider shippingProvider;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private Clock demoClock;

    @Autowired
    private ConfiguredGreetingService configuredGreetingService;

    @Autowired
    private TransactionProxyPlayground transactionProxyPlayground;

    @Test
    void contextLoads() {
    }

    @Test
    void configurationAndBeanRegisterAndWireInfrastructureObjects() {
        assertThat(demoClock).isSameAs(configuredGreetingService.clock());
        assertThat(demoClock.getZone()).isEqualTo(java.time.ZoneOffset.UTC);
    }

    @Test
    void propertySelectsConditionalShippingProvider() {
        assertThat(shippingProvider.providerName()).isEqualTo("fast");
    }

    @Test
    void runtimeFailureRollsBackTransactionalWrite() {
        long before = orderRepository.count();

        assertThatThrownBy(() -> orderService.createOrderThenFail("Rollback User"))
                .isInstanceOf(IllegalStateException.class);

        assertThat(orderRepository.count()).isEqualTo(before);
    }

    @Test
    void cacheableSkipsSecondUnderlyingCall() {
        cacheManager.getCache("products").clear();
        int before = productCatalog.databaseCalls();

        assertThat(productCatalog.findProduct("cached-1"))
                .isEqualTo(productCatalog.findProduct("cached-1"));

        assertThat(productCatalog.databaseCalls()).isEqualTo(before + 1);
    }

    @Test
    void selfInvocationBypassesTransactionalProxy() {
        long before = orderRepository.count();

        assertThatThrownBy(() -> transactionProxyPlayground
                .selfInvocationDoesNotStartTransaction("Self invocation"))
                .isInstanceOf(IllegalStateException.class);

        assertThat(orderRepository.count()).isEqualTo(before + 1);
    }
}
