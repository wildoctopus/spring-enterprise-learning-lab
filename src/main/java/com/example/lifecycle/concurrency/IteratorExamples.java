package com.example.lifecycle.concurrency;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

public final class IteratorExamples {

    private IteratorExamples() {
    }

    public static boolean failFastArrayListWithConcurrentMutation() throws InterruptedException {
        List<Integer> values = new ArrayList<>(List.of(1, 2));
        CountDownLatch iteratorCreated = new CountDownLatch(1);
        CountDownLatch mutationFinished = new CountDownLatch(1);
        AtomicBoolean detected = new AtomicBoolean();

        Thread reader = new Thread(() -> {
            var iterator = values.iterator();
            iteratorCreated.countDown();
            await(mutationFinished);
            try {
                iterator.next();
            } catch (java.util.ConcurrentModificationException exception) {
                detected.set(true);
            }
        });
        reader.start();

        iteratorCreated.await();
        values.add(3);
        mutationFinished.countDown();
        reader.join();
        return detected.get();
    }

    public static IteratorObservation copyOnWriteWithConcurrentMutation() throws InterruptedException {
        CopyOnWriteArrayList<Integer> values = new CopyOnWriteArrayList<>(List.of(1, 2));
        CountDownLatch iteratorCreated = new CountDownLatch(1);
        CountDownLatch mutationFinished = new CountDownLatch(1);
        List<Integer> observed = new ArrayList<>();

        Thread reader = new Thread(() -> {
            var iterator = values.iterator();
            iteratorCreated.countDown();
            await(mutationFinished);
            iterator.forEachRemaining(observed::add);
        });
        reader.start();

        iteratorCreated.await();
        values.add(3);
        mutationFinished.countDown();
        reader.join();
        return new IteratorObservation(List.copyOf(observed), List.copyOf(values));
    }

    private static void await(CountDownLatch latch) {
        try {
            latch.await();
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Iterator example interrupted", exception);
        }
    }

    public record IteratorObservation(List<Integer> observed, List<Integer> finalValues) {
    }
}