package com.example.lifecycle.features;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Demonstrates why transactional methods must be called through a Spring proxy. */
@Service
public class TransactionProxyPlayground {

    private final OrderRepository orderRepository;

    public TransactionProxyPlayground(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public void selfInvocationDoesNotStartTransaction(String customerName) {
        insertThenFail(customerName);
    }

    @Transactional
    public void insertThenFail(String customerName) {
        orderRepository.insert(customerName);
        throw new IllegalStateException("Self-invoked transaction failure");
    }
}