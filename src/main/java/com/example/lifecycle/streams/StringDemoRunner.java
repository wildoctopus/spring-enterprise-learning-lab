package com.example.lifecycle.streams;

import com.example.lifecycle.concurrency.IteratorExamples;
import com.example.lifecycle.playground.PlaygroundSelection;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(25)
public class StringDemoRunner implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        if (!PlaygroundSelection.includes("strings", args)) {
            return;
        }

        String literal = "enterprise";
        String constructed = new String("enterprise");
        System.out.println("\n--- Strings and iterator consistency ---");
        System.out.println("== versus equals: " + StringExamples.compare(literal, constructed));
        System.out.println("StringBuilder: " + StringExamples.buildWithStringBuilder("order-", "100"));
        System.out.println("StringBuffer: " + StringExamples.buildWithStringBuffer("order-", "100"));
        System.out.println("ArrayList detects mutation: "
                + IteratorExamples.failFastArrayListWithConcurrentMutation());
        System.out.println("CopyOnWrite snapshot: "
                + IteratorExamples.copyOnWriteWithConcurrentMutation());
    }
}