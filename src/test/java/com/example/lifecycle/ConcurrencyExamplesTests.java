package com.example.lifecycle;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.lifecycle.concurrency.CollectionConcurrencyDemo;
import com.example.lifecycle.concurrency.DatabaseClientSingleton;
import com.example.lifecycle.concurrency.ImmutableUserProfile;
import com.example.lifecycle.concurrency.VolatileLazySingleton;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.jupiter.api.Test;

class ConcurrencyExamplesTests {

    @Test
    void holderAndVolatileSingletonsReturnOneInstanceAcrossThreads() throws Exception {
        try (ExecutorService executor = Executors.newFixedThreadPool(8)) {
            List<Future<Object>> holderResults = executor.invokeAll(
                    java.util.stream.IntStream.range(0, 32)
                            .<java.util.concurrent.Callable<Object>>mapToObj(index ->
                                () -> DatabaseClientSingleton.getInstance())
                            .toList());
            Set<Object> holderInstances = ConcurrentHashMap.newKeySet();
            for (Future<Object> result : holderResults) {
                holderInstances.add(result.get());
            }

            List<Future<Object>> lazyResults = executor.invokeAll(
                    java.util.stream.IntStream.range(0, 32)
                            .<java.util.concurrent.Callable<Object>>mapToObj(index ->
                                () -> VolatileLazySingleton.getInstance())
                            .toList());
            Set<Object> lazyInstances = ConcurrentHashMap.newKeySet();
            for (Future<Object> result : lazyResults) {
                lazyInstances.add(result.get());
            }

            assertThat(holderInstances).hasSize(1);
            assertThat(lazyInstances).hasSize(1);
        }
    }

    @Test
    void immutableProfileCopiesInputAndRejectsMutation() {
        java.util.ArrayList<String> inputRoles = new java.util.ArrayList<>(List.of("USER"));
        ImmutableUserProfile profile = new ImmutableUserProfile("u-1", "Ada", inputRoles);

        inputRoles.add("ADMIN");

        assertThat(profile.roles()).containsExactly("USER");
        assertThat(profile.roles()).isUnmodifiable();
        assertThat(profile.withDisplayName("Grace").displayName()).isEqualTo("Grace");
        assertThat(profile.displayName()).isEqualTo("Ada");
    }

    @Test
    void concurrentHashMapAtomicMergeDoesNotLoseUpdates() throws Exception {
        assertThat(CollectionConcurrencyDemo.concurrentHashMapAtomicUpdates(8, 1_000))
                .isEqualTo(8_000);
    }

    @Test
    void copyOnWriteIterationUsesSnapshot() {
        assertThat(CollectionConcurrencyDemo.copyOnWriteArrayListSnapshot())
                .containsExactly(1, 2);
    }

    @Test
    void blockingQueueCoordinatesProducerAndConsumer() throws Exception {
        assertThat(CollectionConcurrencyDemo.blockingQueueProducerConsumer()).isEqualTo(5);
    }
}
