package com.example.lifecycle.concurrency;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(20)
public class ConcurrencyDemoRunner implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        System.out.println("\n--- Singleton and immutable object ---");
        System.out.println("Holder singleton same instance: "
                + (DatabaseClientSingleton.getInstance() == DatabaseClientSingleton.getInstance()));
        System.out.println("Lazy singleton same instance: "
                + (VolatileLazySingleton.getInstance() == VolatileLazySingleton.getInstance()));
        System.out.println("Immutable roles: "
                + new ImmutableUserProfile("u-1", "Ada", java.util.List.of("ADMIN")).roles());

        System.out.println("\n--- Collection choices ---");
        System.out.println("HashMap: " + CollectionConcurrencyDemo.hashMap());
        System.out.println("LinkedHashMap order: " + CollectionConcurrencyDemo.linkedHashMap().keySet());
        System.out.println("ConcurrentHashMap atomic count: "
                + CollectionConcurrencyDemo.concurrentHashMapAtomicUpdates(4, 1000));
        System.out.println("CopyOnWriteArrayList snapshot: "
                + CollectionConcurrencyDemo.copyOnWriteArrayListSnapshot());
        System.out.println("BlockingQueue consumed sum: "
                + CollectionConcurrencyDemo.blockingQueueProducerConsumer());
    }
}
