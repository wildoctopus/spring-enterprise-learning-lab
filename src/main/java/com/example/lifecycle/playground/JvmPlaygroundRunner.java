package com.example.lifecycle.playground;

import com.example.lifecycle.jvm.JvmMemoryAndGcDemo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(35)
public class JvmPlaygroundRunner implements CommandLineRunner {

    @Override
    public void run(String... args) {
        if (!PlaygroundSelection.includes("jvm", args)) {
            return;
        }
        System.out.println("\n--- JVM memory and garbage collection playground ---");
        JvmMemoryAndGcDemo.memoryAreaMentalModel().forEach(System.out::println);
        JvmMemoryAndGcDemo.generationalGcMentalModel().forEach(System.out::println);
        JvmMemoryAndGcDemo.JvmMemorySnapshot snapshot = JvmMemoryAndGcDemo.memorySnapshot();
        System.out.println("Heap used/committed/max: " + snapshot.heapUsed() + "/"
                + snapshot.heapCommitted() + "/" + snapshot.heapMax());
        System.out.println("Generational pools: " + JvmMemoryAndGcDemo.generationalPools());
        System.out.println("GC eligibility: " + JvmMemoryAndGcDemo.observeGcEligibility());
    }
}