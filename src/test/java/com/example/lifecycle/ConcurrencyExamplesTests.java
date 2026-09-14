package com.example.lifecycle;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.lifecycle.concurrency.CollectionConcurrencyDemo;
import com.example.lifecycle.concurrency.ConcurrentHashMapInternalsDemo;
import com.example.lifecycle.concurrency.CopyOnWriteArrayListInternalsDemo;
import com.example.lifecycle.concurrency.DatabaseClientSingleton;
import com.example.lifecycle.concurrency.ImmutableUserProfile;
import com.example.lifecycle.concurrency.IteratorExamples;
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
    void explainsConcurrentHashMapHashingAndAtomicMerge() {
    ConcurrentHashMapInternalsDemo.MapOperationTrace putTrace =
        ConcurrentHashMapInternalsDemo.tracePutIfAbsent("requests", 1, 16);
    ConcurrentHashMapInternalsDemo.MapOperationTrace getTrace =
        ConcurrentHashMapInternalsDemo.traceGet("requests", 16);

    assertThat(putTrace.steps()).contains(
        "1. key.hashCode() -> " + "requests".hashCode(),
        "4. empty bucket: publish the key/value node with an atomic operation");
    assertThat(getTrace.steps()).contains("4. read the bucket without locking");
    assertThat(putTrace.bucket()).isEqualTo(getTrace.bucket());
    assertThat(ConcurrentHashMapInternalsDemo.traceAtomicMerge("requests", 1, 3).finalValue())
        .isEqualTo(4);
    }

    @Test
    void explainsCopyOnWriteArrayListSnapshotsAndWriteCost() {
    CopyOnWriteArrayListInternalsDemo.SnapshotTrace addTrace =
        CopyOnWriteArrayListInternalsDemo.traceAddDuringIteration();
    CopyOnWriteArrayListInternalsDemo.SnapshotTrace removeTrace =
        CopyOnWriteArrayListInternalsDemo.traceRemoveDuringIteration();

    assertThat(addTrace.observedByExistingIterator()).containsExactly("email", "audit");
    assertThat(addTrace.currentValues()).containsExactly("email", "audit", "metrics");
    assertThat(removeTrace.observedByExistingIterator()).containsExactly("email", "audit");
    assertThat(removeTrace.currentValues()).containsExactly("audit");
    }

    @Test
    void arrayListIteratorDetectsConcurrentStructuralMutation() throws Exception {
        assertThat(IteratorExamples.failFastArrayListWithConcurrentMutation()).isTrue();
    }

    @Test
    void copyOnWriteIteratorReadsSnapshotDuringConcurrentMutation() throws Exception {
        IteratorExamples.IteratorObservation observation =
                IteratorExamples.copyOnWriteWithConcurrentMutation();

        assertThat(observation.observed()).containsExactly(1, 2);
        assertThat(observation.finalValues()).containsExactly(1, 2, 3);
    }

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
