package com.example.lifecycle.concurrency;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public final class CollectionConcurrencyDemo {

    private CollectionConcurrencyDemo() {
    }

    public static Map<String, Integer> hashMap() {
        Map<String, Integer> map = new HashMap<>();
        map.put("first", 1);
        map.put("second", 2);
        return map;
    }

    public static Map<String, Integer> linkedHashMap() {
        Map<String, Integer> map = new LinkedHashMap<>();
        map.put("first", 1);
        map.put("second", 2);
        return map;
    }

    public static int concurrentHashMapAtomicUpdates(int workers, int updatesPerWorker)
            throws InterruptedException {
        Map<String, Integer> counts = new java.util.concurrent.ConcurrentHashMap<>();
        CountDownLatch ready = new CountDownLatch(workers);
        CountDownLatch start = new CountDownLatch(1);
        List<Thread> threads = new ArrayList<>();

        for (int worker = 0; worker < workers; worker++) {
            Thread thread = new Thread(() -> {
                ready.countDown();
                await(start);
                for (int update = 0; update < updatesPerWorker; update++) {
                    // merge is atomic for this key. get + put would lose updates.
                    counts.merge("requests", 1, Integer::sum);
                }
            });
            threads.add(thread);
            thread.start();
        }

        ready.await();
        start.countDown();
        for (Thread thread : threads) {
            thread.join();
        }
        return counts.getOrDefault("requests", 0);
    }

    public static List<Integer> copyOnWriteArrayListSnapshot() {
        CopyOnWriteArrayList<Integer> listeners = new CopyOnWriteArrayList<>();
        listeners.add(1);
        listeners.add(2);
        List<Integer> observed = new ArrayList<>();
        for (Integer listener : listeners) {
            observed.add(listener);
            listeners.add(3);
        }
        // Iteration uses a stable snapshot, so no ConcurrentModificationException.
        return List.copyOf(observed);
    }

    public static int blockingQueueProducerConsumer() throws InterruptedException {
        BlockingQueue<Integer> queue = new LinkedBlockingQueue<>(2);
        AtomicInteger consumed = new AtomicInteger();
        Thread consumer = new Thread(() -> {
            try {
                while (true) {
                    int value = queue.take();
                    if (value == -1) {
                        return;
                    }
                    consumed.addAndGet(value);
                }
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
            }
        });
        consumer.start();

        queue.put(2);
        queue.put(3);
        queue.put(-1); // poison pill: an explicit, observable shutdown protocol.
        consumer.join();
        return consumed.get();
    }

    public static List<Integer> synchronizedListWithSafeIteration() {
        List<Integer> values = Collections.synchronizedList(new ArrayList<>());
        values.add(1);
        values.add(2);
        synchronized (values) {
            return List.copyOf(values);
        }
    }

    private static void await(CountDownLatch latch) {
        try {
            latch.await();
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Worker interrupted", exception);
        }
    }
}
