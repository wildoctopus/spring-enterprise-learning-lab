package com.example.lifecycle.features;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ShippingProvider shippingProvider;

    // Depend on interfaces and let Spring inject the selected implementation.
    // The service does not know whether shipping is fast or the fallback.
    public OrderService(OrderRepository orderRepository, ShippingProvider shippingProvider) {
        this.orderRepository = orderRepository;
        this.shippingProvider = shippingProvider;
    }

    @Transactional
    public void createOrder(String customerName) {
        // Both writes in this method share one transaction. If a runtime
        // exception escapes, Spring rolls back the database work.
        orderRepository.insert(customerName);
        System.out.println("[DI] Selected shipping provider: " + shippingProvider.providerName());
    }

    @Transactional
    public void createOrderThenFail(String customerName) {
        orderRepository.insert(customerName);
        throw new IllegalStateException("Simulated failure after database write");
    }
}
