package com.example.lifecycle.features;

import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class ProductCatalog {

    private final AtomicInteger databaseCalls = new AtomicInteger();

    @Cacheable("products")
    public String findProduct(String productId) {
        // The method body runs only on a cache miss. The second call with the
        // same key returns from the cache and skips this simulated database call.
        int callNumber = databaseCalls.incrementAndGet();
        return "product-" + productId + "-load-" + callNumber;
    }

    public int databaseCalls() {
        return databaseCalls.get();
    }
}
