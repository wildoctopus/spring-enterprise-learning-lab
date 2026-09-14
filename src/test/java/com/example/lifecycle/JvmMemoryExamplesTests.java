package com.example.lifecycle;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.lifecycle.jvm.JvmMemoryAndGcDemo;
import org.junit.jupiter.api.Test;

class JvmMemoryExamplesTests {

    @Test
    void readsLiveHeapNonHeapPoolsAndGarbageCollectors() {
        JvmMemoryAndGcDemo.JvmMemorySnapshot snapshot = JvmMemoryAndGcDemo.memorySnapshot();

        assertThat(snapshot.heapUsed()).isGreaterThan(0);
        assertThat(snapshot.heapCommitted()).isGreaterThanOrEqualTo(snapshot.heapUsed());
        assertThat(snapshot.nonHeapUsed()).isGreaterThanOrEqualTo(0);
        assertThat(snapshot.pools()).isNotEmpty();
        assertThat(snapshot.collectors()).isNotEmpty();
    }

    @Test
    void explainsMemoryAreasWithoutConfusingReferencesWithObjects() {
        assertThat(JvmMemoryAndGcDemo.memoryAreaMentalModel())
                .contains("2. Heap: objects and arrays live here; the garbage collector finds unreachable objects")
                .contains("3. Metaspace: class metadata lives in native memory and grows as classes are loaded");
    }

        @Test
        void explainsGenerationsAndReportsCollectorSpecificHeapPools() {
        assertThat(JvmMemoryAndGcDemo.generationalGcMentalModel())
            .contains("1. New objects start in Eden, because most objects die young")
            .contains("3. Live survivors move between S0 and S1; the two areas alternate roles each young collection")
            .contains("6. G1 still has young and old regions, but its implementation is region-based rather than one fixed contiguous space");
        assertThat(JvmMemoryAndGcDemo.generationalPools())
            .allSatisfy(pool -> assertThat(pool.conceptualGeneration())
                .isIn("EDEN", "SURVIVOR_S0_OR_S1", "OLD", "OTHER_HEAP"));
        }

    @Test
    void showsEligibilityForCollectionBeforeMakingTimingAClaim() {
        JvmMemoryAndGcDemo.GcEligibilityObservation observation =
                JvmMemoryAndGcDemo.observeGcEligibility();

        assertThat(observation.stronglyReachableBeforeDrop()).isTrue();
        assertThat(observation.steps()).hasSize(4);
    }
}