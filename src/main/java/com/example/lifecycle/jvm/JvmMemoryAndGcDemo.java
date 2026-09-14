package com.example.lifecycle.jvm;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryType;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Executable observations for JVM memory and garbage collection.
 *
 * The JVM chooses the exact layout and collector. These examples expose stable
 * concepts and live measurements without pretending that GC timing is fixed.
 */
public final class JvmMemoryAndGcDemo {

    private JvmMemoryAndGcDemo() {
    }

    public static JvmMemorySnapshot memorySnapshot() {
        MemoryMXBean memory = ManagementFactory.getMemoryMXBean();
        List<MemoryPoolSnapshot> pools = ManagementFactory.getMemoryPoolMXBeans().stream()
                .map(pool -> new MemoryPoolSnapshot(
                        pool.getName(),
                        pool.getType(),
                        pool.getUsage().getUsed(),
                        pool.getUsage().getCommitted(),
                        pool.getUsage().getMax()))
                .toList();
        List<GarbageCollectorSnapshot> collectors = ManagementFactory
                .getGarbageCollectorMXBeans().stream()
                .map(collector -> new GarbageCollectorSnapshot(
                        collector.getName(), collector.getCollectionCount(), collector.getCollectionTime()))
                .toList();

        return new JvmMemorySnapshot(
                memory.getHeapMemoryUsage().getUsed(),
                memory.getHeapMemoryUsage().getCommitted(),
                memory.getHeapMemoryUsage().getMax(),
                memory.getNonHeapMemoryUsage().getUsed(),
                memory.getNonHeapMemoryUsage().getCommitted(),
                pools,
                collectors);
    }

    public static List<String> memoryAreaMentalModel() {
        return List.of(
                "1. Thread stack: each method call gets a stack frame containing local variables and references",
                "2. Heap: objects and arrays live here; the garbage collector finds unreachable objects",
                "3. Metaspace: class metadata lives in native memory and grows as classes are loaded",
                "4. Code cache: JIT-compiled machine code is stored outside the Java heap",
                "5. Native memory: threads, direct buffers, JNI, and the JVM itself also need memory",
                "6. A local variable is usually a reference; the referenced object is usually on the heap");
    }

        public static List<String> generationalGcMentalModel() {
                return List.of(
                                "1. New objects start in Eden, because most objects die young",
                                "2. A minor/young collection finds live objects in Eden and the active Survivor area",
                                "3. Live survivors move between S0 and S1; the two areas alternate roles each young collection",
                                "4. Objects that survive enough collections can be promoted to Old/Tenured generation",
                                "5. Old-generation collection is usually more expensive because it contains longer-lived objects",
                                "6. G1 still has young and old regions, but its implementation is region-based rather than one fixed contiguous space");
        }

        public static List<GenerationalPoolSnapshot> generationalPools() {
                return ManagementFactory.getMemoryPoolMXBeans().stream()
                                .filter(pool -> pool.getType() == MemoryType.HEAP)
                                .map(pool -> new GenerationalPoolSnapshot(
                                                pool.getName(),
                                                classifyGeneration(pool.getName()),
                                                pool.getUsage().getUsed(),
                                                pool.getUsage().getCommitted(),
                                                pool.getUsage().getMax()))
                                .toList();
        }

        private static String classifyGeneration(String poolName) {
                String normalizedName = poolName.toLowerCase();
                if (normalizedName.contains("eden")) {
                        return "EDEN";
                }
                if (normalizedName.contains("survivor")) {
                        return "SURVIVOR_S0_OR_S1";
                }
                if (normalizedName.contains("old") || normalizedName.contains("tenured")) {
                        return "OLD";
                }
                return "OTHER_HEAP";
        }

    public static GcEligibilityObservation observeGcEligibility() {
        Object object = new byte[1024];
        WeakReference<Object> weakReference = new WeakReference<>(object);
        boolean stronglyReachableBeforeDrop = weakReference.get() != null;
        object = null;

        List<String> steps = new ArrayList<>();
        steps.add("an object is created on the heap");
        steps.add("a local variable strongly references the object");
        steps.add("the local reference is cleared; the object is now eligible, not guaranteed, for GC");
        steps.add("System.gc() is only a request, so collection timing is not asserted");

        boolean collectedAfterRequest = false;
        for (int attempt = 0; attempt < 3 && !collectedAfterRequest; attempt++) {
            System.gc();
            collectedAfterRequest = weakReference.get() == null;
        }
        return new GcEligibilityObservation(
                stronglyReachableBeforeDrop,
                collectedAfterRequest,
                List.copyOf(steps));
    }

    public record JvmMemorySnapshot(
            long heapUsed,
            long heapCommitted,
            long heapMax,
            long nonHeapUsed,
            long nonHeapCommitted,
            List<MemoryPoolSnapshot> pools,
            List<GarbageCollectorSnapshot> collectors) {
    }

    public record MemoryPoolSnapshot(
            String name,
            MemoryType type,
            long used,
            long committed,
            long max) {
    }

    public record GenerationalPoolSnapshot(
            String actualPoolName,
            String conceptualGeneration,
            long used,
            long committed,
            long max) {
    }

    public record GarbageCollectorSnapshot(String name, long collectionCount, long collectionTime) {
    }

    public record GcEligibilityObservation(
            boolean stronglyReachableBeforeDrop,
            boolean collectedAfterRequest,
            List<String> steps) {
    }
}