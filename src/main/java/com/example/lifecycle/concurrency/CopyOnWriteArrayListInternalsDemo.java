package com.example.lifecycle.concurrency;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * A teaching model of CopyOnWriteArrayList's snapshot behavior.
 *
 * The trace shows the read/write tradeoff without copying the JDK's private code.
 */
public final class CopyOnWriteArrayListInternalsDemo {

    private CopyOnWriteArrayListInternalsDemo() {
    }

    public static SnapshotTrace traceAddDuringIteration() {
        CopyOnWriteArrayList<String> listeners = new CopyOnWriteArrayList<>(List.of("email", "audit"));
        List<String> observedByExistingIterator = new ArrayList<>();
        var iterator = listeners.iterator();
        observedByExistingIterator.add(iterator.next());

        listeners.add("metrics");
        observedByExistingIterator.add(iterator.next());

        return new SnapshotTrace(
                List.of(
                        "1. iterator captures the current backing array -> [email, audit]",
                        "2. add(metrics) copies the array and publishes a new array -> [email, audit, metrics]",
                        "3. existing iterator keeps reading its old snapshot -> [email, audit]",
                        "4. a new iterator sees the new snapshot -> [email, audit, metrics]"),
                List.copyOf(observedByExistingIterator),
                List.copyOf(listeners));
    }

    public static SnapshotTrace traceRemoveDuringIteration() {
        CopyOnWriteArrayList<String> listeners = new CopyOnWriteArrayList<>(List.of("email", "audit"));
        List<String> observedByExistingIterator = new ArrayList<>();
        var iterator = listeners.iterator();
        listeners.remove("email");
        iterator.forEachRemaining(observedByExistingIterator::add);

        return new SnapshotTrace(
                List.of(
                        "1. iterator captures the current backing array -> [email, audit]",
                        "2. remove(email) copies the array and publishes a new array -> [audit]",
                        "3. existing iterator still sees its old snapshot -> [email, audit]"),
                List.copyOf(observedByExistingIterator),
                List.copyOf(listeners));
    }

    public record SnapshotTrace(
            List<String> steps,
            List<String> observedByExistingIterator,
            List<String> currentValues) {
    }
}