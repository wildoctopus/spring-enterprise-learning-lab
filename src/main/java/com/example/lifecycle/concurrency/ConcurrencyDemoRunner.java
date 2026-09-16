package com.example.lifecycle.concurrency;

import com.example.lifecycle.playground.PlaygroundSelection;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(20)
public class ConcurrencyDemoRunner implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
                if (!PlaygroundSelection.includes("concurrency", args)) {
                        return;
                }
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
        System.out.println("\n--- Collection internal mental models ---");
        System.out.println(ConcurrentHashMapInternalsDemo.traceGet("requests", 16).steps());
        System.out.println(ConcurrentHashMapInternalsDemo.traceAtomicMerge("requests", 0, 3).steps());
        System.out.println(CopyOnWriteArrayListInternalsDemo.traceAddDuringIteration().steps());
        System.out.println("BlockingQueue consumed sum: "
                + CollectionConcurrencyDemo.blockingQueueProducerConsumer());
        System.out.println("Executor results: " + AsyncCoordinationPlayground.executeAll(
                java.util.List.of(() -> 2, () -> 3), 2));
        try (var executor = java.util.concurrent.Executors.newFixedThreadPool(2)) {
            System.out.println("CompletableFuture fan-in: "
                    + AsyncCoordinationPlayground.fanOutAndCombine(
                            java.util.List.of(() -> 2, () -> 3), executor).join());
            System.out.println("Fallback result: "
                    + AsyncCoordinationPlayground.withFallback(
                            () -> { throw new IllegalStateException("primary down"); },
                            () -> "cached-response", executor).join());
        }
        System.out.println("Bounded pool rejection: "
                + AsyncCoordinationPlayground.boundedPoolRejectsWithoutUnboundedQueue().rejected());
        System.out.println("Failure propagation: " + AsyncCoordinationPlayground.failedTaskMessage());
        System.out.println("Timeout handled: " + AsyncCoordinationPlayground.timeoutIsBounded());
    }
}
