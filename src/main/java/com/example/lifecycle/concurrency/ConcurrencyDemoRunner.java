package com.example.lifecycle.concurrency;

import com.example.lifecycle.jvm.JvmMemoryAndGcDemo;
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
        System.out.println("\n--- Collection internal mental models ---");
        System.out.println(ConcurrentHashMapInternalsDemo.traceGet("requests", 16).steps());
        System.out.println(ConcurrentHashMapInternalsDemo.traceAtomicMerge("requests", 0, 3).steps());
        System.out.println(CopyOnWriteArrayListInternalsDemo.traceAddDuringIteration().steps());
        System.out.println("BlockingQueue consumed sum: "
                + CollectionConcurrencyDemo.blockingQueueProducerConsumer());
        System.out.println("\n--- JVM memory snapshot ---");
        JvmMemoryAndGcDemo.JvmMemorySnapshot memory = JvmMemoryAndGcDemo.memorySnapshot();
        System.out.println("Heap used/committed/max: " + memory.heapUsed() + "/"
                + memory.heapCommitted() + "/" + memory.heapMax());
        System.out.println("Non-heap used/committed: " + memory.nonHeapUsed() + "/"
                + memory.nonHeapCommitted());
        System.out.println("Generational pools: " + JvmMemoryAndGcDemo.generationalPools());
        System.out.println("GC collectors: " + memory.collectors());
    }
}
