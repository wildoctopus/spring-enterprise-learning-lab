package com.example.lifecycle.rest.repository;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.stereotype.Repository;

@Repository
public class RestApiDemoRepository {

    private final AtomicInteger orderSequence = new AtomicInteger(1);
    private final AtomicInteger balance = new AtomicInteger(100);
    private final Set<String> deletedUsers = ConcurrentHashMap.newKeySet();
    private final Set<String> idempotencyKeys = ConcurrentHashMap.newKeySet();

    public String nextOrderId(String prefix) {
        return prefix + "-ORDER-" + orderSequence.getAndIncrement();
    }

    public boolean claimIdempotencyKey(String key) {
        return idempotencyKeys.add(key);
    }

    public int currentBalance() {
        return balance.get();
    }

    public void setBalance(int value) {
        balance.set(value);
    }

    public boolean markDeleted(String id) {
        return deletedUsers.add(id);
    }
}
