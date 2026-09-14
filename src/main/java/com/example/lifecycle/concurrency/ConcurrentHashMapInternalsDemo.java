package com.example.lifecycle.concurrency;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A teaching model of the operations used by ConcurrentHashMap.
 *
 * The trace describes the important idea, not the private JDK implementation.
 * The real map is still used for the executable result.
 */
public final class ConcurrentHashMapInternalsDemo {

    private ConcurrentHashMapInternalsDemo() {
    }

    public static MapOperationTrace tracePutIfAbsent(String key, int value, int tableCapacity) {
        int hash = key.hashCode();
        int spreadHash = spread(hash);
        int bucket = bucketIndex(spreadHash, tableCapacity);

        return new MapOperationTrace(
                "putIfAbsent",
                List.of(
                        "1. key.hashCode() -> " + hash,
                        "2. spread hash bits -> " + spreadHash,
                        "3. choose bucket (capacity - 1) & hash -> " + bucket,
                        "4. empty bucket: publish the key/value node with an atomic operation",
                        "5. if another thread owns this key, do not overwrite its value"),
                bucket);
    }

    public static MapOperationTrace traceGet(String key, int tableCapacity) {
        int hash = key.hashCode();
        int spreadHash = spread(hash);
        int bucket = bucketIndex(spreadHash, tableCapacity);

        return new MapOperationTrace(
                "get",
                List.of(
                        "1. key.hashCode() -> " + hash,
                        "2. spread hash bits -> " + spreadHash,
                        "3. choose bucket (capacity - 1) & hash -> " + bucket,
                        "4. read the bucket without locking",
                        "5. compare hash, then equals(), while following collisions if needed"),
                bucket);
    }

    public static MergeTrace traceAtomicMerge(String key, int initialValue, int additions) {
        ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();
        map.put(key, initialValue);
        List<String> steps = new ArrayList<>();
        steps.add("get current value for " + key + " -> " + initialValue);
        steps.add("compute new value inside the map's per-key atomic update path");
        steps.add("publish the result; a competing merge retries or waits instead of losing an update");

        for (int addition = 0; addition < additions; addition++) {
            map.merge(key, 1, Integer::sum);
        }
        steps.add("final value after " + additions + " merge calls -> " + map.get(key));
        return new MergeTrace(List.copyOf(steps), map.get(key));
    }

    public static int spread(int hash) {
        return hash ^ (hash >>> 16);
    }

    public static int bucketIndex(int spreadHash, int tableCapacity) {
        if (tableCapacity < 1 || (tableCapacity & (tableCapacity - 1)) != 0) {
            throw new IllegalArgumentException("tableCapacity must be a positive power of two");
        }
        return (tableCapacity - 1) & spreadHash;
    }

    public record MapOperationTrace(String operation, List<String> steps, int bucket) {
    }

    public record MergeTrace(List<String> steps, int finalValue) {
    }
}