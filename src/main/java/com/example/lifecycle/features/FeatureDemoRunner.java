package com.example.lifecycle.features;

import com.example.lifecycle.playground.PlaygroundSelection;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(10)
public class FeatureDemoRunner implements CommandLineRunner {

    private final OrderService orderService;
    private final OrderRepository orderRepository;
    private final ProductCatalog productCatalog;

    public FeatureDemoRunner(OrderService orderService, OrderRepository orderRepository,
            ProductCatalog productCatalog) {
        this.orderService = orderService;
        this.orderRepository = orderRepository;
        this.productCatalog = productCatalog;
    }

    @Override
    public void run(String... args) {
        if (!PlaygroundSelection.includes("features", args)) {
            return;
        }
        System.out.println("\n--- Dependency injection and conditional bean ---");
        orderService.createOrder("Ada");
        System.out.println("Orders after commit: " + orderRepository.count());

        System.out.println("\n--- Transaction rollback ---");
        try {
            orderService.createOrderThenFail("Grace");
        } catch (IllegalStateException exception) {
            System.out.println("Expected failure: " + exception.getMessage());
        }
        System.out.println("Orders after rollback: " + orderRepository.count());

        System.out.println("\n--- Cacheable ---");
        System.out.println("First lookup:  " + productCatalog.findProduct("42"));
        System.out.println("Second lookup: " + productCatalog.findProduct("42"));
        System.out.println("Underlying loads: " + productCatalog.databaseCalls());
    }
}
