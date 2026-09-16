package com.example.lifecycle.playground;

import java.util.List;

public final class PlaygroundCatalog {

    private PlaygroundCatalog() {
    }

    public static List<Topic> topics() {
        return List.of(
                new Topic("lifecycle", "Spring bean lifecycle",
                        "Observe construction, initialization, readiness, and shutdown.",
                        "Move work between a constructor, @PostConstruct, ApplicationRunner, and ApplicationReadyEvent.",
                        "Where should a database connection or external warmup belong?"),
                new Topic("features", "Dependency injection, transactions, and caching",
                        "See Spring choose implementations, commit a write, roll back a failure, and reuse a cache entry.",
                        "Run TransactionProxyPlayground.selfInvocationDoesNotStartTransaction and inspect the committed row.",
                        "How should retries, idempotency, and external provider calls be designed?"),
                new Topic("concurrency", "Concurrency and immutable values",
                        "Compare immutable state, Singleton publication, executors, CompletableFuture, atomic map updates, snapshots, and backpressure.",
                        "Run ConcurrencyExamplesTests against BadImmutableUserProfile or replace merge with get plus put.",
                        "What is the correct scope: request, thread, JVM, application context, or cluster?"),
                new Topic("strings", "Strings and iteration",
                        "Compare identity, equality, mutable builders, and iterator consistency.",
                        "Use == for separately-created strings or structurally mutate an ArrayList during iteration.",
                        "Which representation and collection fit this workload and ownership model?"),
                new Topic("jvm", "JVM memory and garbage collection",
                        "Inspect live memory pools, reachability, weak references, and generational mental models.",
                        "Retain an object through a long-lived collection and compare eligible versus collected objects.",
                        "What evidence would you gather before changing heap or collector settings?"),
                new Topic("jpa", "JPA and Hibernate query behavior",
                        "Measure N+1 queries and compare EntityGraph and fetch join strategies.",
                        "Access a lazy association outside the intended transaction or add a loop that loads each child.",
                        "Which fetch plan is correct for this use case and response shape?"),
                new Topic("sql", "SQL query design",
                        "Run grouping, window, latest-row, and keyset-style query examples.",
                        "Remove deterministic ordering or replace a bounded query with an unbounded result set.",
                        "How will the query behave with indexes, concurrent writes, and millions of rows?"),
                new Topic("streams", "Java Streams and collection transformations",
                        "Practice grouping, flattening, ranking, duplicate handling, Optional, and primitive aggregation.",
                        "Run StreamPlaygroundExercises.badTieBreaker, then compare explicitTieBreaker.",
                        "When is a loop clearer, safer, or faster than a stream?"),
                new Topic("design", "SOLID principles and enterprise patterns",
                        "Trace Strategy, Factory, Adapter, Decorator, Observer, Builder, and Facade in payments.",
                        "Add a payment provider by changing the service instead of the extension boundary.",
                        "Which change pressure does each abstraction isolate?"),
                new Topic("pagination", "Pagination and API error design",
                        "Compare offset and cursor pagination with validation and continuation behavior.",
                        "Replay a tenant-a cursor under tenant-b and observe INVALID_CURSOR.",
                        "How do you make pagination bounded, deterministic, tenant-safe, and observable?"));
    }

    public record Topic(String id, String title, String observe, String breakIt, String enterpriseQuestion) {
    }
}