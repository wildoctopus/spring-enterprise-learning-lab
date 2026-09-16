package com.example.lifecycle.concurrency;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

/** Deterministic exercises for executor ownership and CompletableFuture composition. */
public final class AsyncCoordinationPlayground {

    private AsyncCoordinationPlayground() {
    }

    public static List<Integer> executeAll(List<? extends java.util.concurrent.Callable<Integer>> tasks,
            int poolSize) throws Exception {
        if (poolSize < 1) {
            throw new IllegalArgumentException("poolSize must be positive");
        }
        try (ExecutorService executor = Executors.newFixedThreadPool(poolSize)) {
            List<Future<Integer>> futures = executor.invokeAll(tasks);
            List<Integer> results = new ArrayList<>(futures.size());
            for (Future<Integer> future : futures) {
                results.add(future.get());
            }
            return List.copyOf(results);
        }
    }

    public static String failedTaskMessage() throws Exception {
        try (ExecutorService executor = Executors.newSingleThreadExecutor()) {
            Future<String> future = executor.submit(() -> {
                throw new IllegalStateException("downstream unavailable");
            });
            try {
                future.get();
                throw new AssertionError("The task should have failed");
            } catch (ExecutionException exception) {
                return exception.getCause().getMessage();
            }
        }
    }

    public static CompletableFuture<String> withFallback(Supplier<String> primary,
            Supplier<String> fallback, ExecutorService executor) {
        return CompletableFuture.supplyAsync(primary, executor)
                .exceptionally(exception -> fallback.get());
    }

    public static CompletableFuture<Integer> fanOutAndCombine(List<Supplier<Integer>> operations,
            ExecutorService executor) {
        List<CompletableFuture<Integer>> futures = operations.stream()
                .map(operation -> CompletableFuture.supplyAsync(operation, executor))
                .toList();
        CompletableFuture<Void> all = CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
        return all.thenApply(ignored -> futures.stream().mapToInt(CompletableFuture::join).sum());
    }

    public static RejectionObservation boundedPoolRejectsWithoutUnboundedQueue() throws Exception {
        AtomicInteger running = new AtomicInteger();
        java.util.concurrent.CountDownLatch release = new java.util.concurrent.CountDownLatch(1);
        try (ThreadPoolExecutor executor = new ThreadPoolExecutor(
                1, 1, 0, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(1),
            new ThreadPoolExecutor.AbortPolicy())) {
            try {
                Runnable blockedTask = () -> await(release);
                executor.execute(blockedTask);
                executor.execute(running::incrementAndGet);
                boolean rejected;
                try {
                    executor.execute(running::incrementAndGet);
                    rejected = false;
                } catch (RejectedExecutionException exception) {
                    rejected = true;
                }
                return new RejectionObservation(rejected, executor.getQueue().remainingCapacity());
            } finally {
                release.countDown();
                executor.shutdownNow();
                executor.awaitTermination(5, TimeUnit.SECONDS);
            }
        }
    }

    public static boolean timeoutIsBounded() {
        try (ExecutorService executor = Executors.newSingleThreadExecutor()) {
            return CompletableFuture.supplyAsync(() -> {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException exception) {
                            Thread.currentThread().interrupt();
                            throw new IllegalStateException("Task interrupted", exception);
                        }
                        return "slow";
                    }, executor)
                    .orTimeout(1, TimeUnit.MILLISECONDS)
                    .handle((value, exception) -> exception != null)
                    .join();
        }
    }

    private static void await(java.util.concurrent.CountDownLatch latch) {
        try {
            latch.await();
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        }
    }

    public record RejectionObservation(boolean rejected, int queueCapacityAfterSubmission) {
    }
}