# Java Spring Boot Interview Preparation Lab

> Runnable Java 21 and Spring Boot interview preparation labs covering Core Java, collections, multithreading, JVM internals, Spring Boot, JPA/Hibernate, SQL, REST APIs, system design, and production tradeoffs.

This repository is designed for Java backend developers preparing for interviews, strengthening production skills, or teaching enterprise engineering. Every topic has executable code, tests, failure scenarios, and interview-level tradeoffs. It is useful for beginner, experienced, senior, and lead Java developer preparation, including Spring Boot backend roles.

[![Java](https://img.shields.io/badge/Java-21-orange)](https://adoptium.net/temurin/releases/?version=21)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5-brightgreen)](https://spring.io/projects/spring-boot)
[![Build](https://img.shields.io/badge/build-Maven-blue)](https://maven.apache.org/)

## Contents

- [Why this lab?](#why-this-lab)
- [Quick start](#quick-start)
- [Java and Spring Boot interview roadmap](#java-and-spring-boot-interview-roadmap)
- [Learning path](#learning-path)
- [Interview question index](#interview-question-index)
- [Repository map](#repository-map)
- [Interview preparation](#interview-preparation)
- [Testing strategy](#testing-strategy)
- [Contributing](#contributing)

## Java and Spring Boot interview roadmap

Use the roadmap below to prepare for Java backend interviews at service companies,
product companies, and startups. Study the explanation, run the example, break the
implementation, and use the test to verify the behavior.

### Core Java interview preparation

- OOP, abstraction, inheritance, polymorphism, and composition.
- `String` immutability, string pool, `equals()`, and `hashCode()`.
- Collections, generics, `Optional`, exceptions, and Java Streams.
- Multithreading, `synchronized`, `volatile`, executors, and `CompletableFuture`.
- JVM memory, garbage collection, class loading, and diagnostic tools.

### Spring Boot interview preparation

- Dependency injection, bean lifecycle, `@Configuration`, and `@Bean`.
- Conditional beans, profiles, configuration boundaries, and testing with doubles.
- `@Transactional`, rollback, self-invocation, caching, and proxy behavior.
- REST resources, validation, pagination, error responses, and idempotency.
- JPA/Hibernate fetch plans, N+1 queries, locking, batching, and transactions.

### Senior Java and system design preparation

- SQL indexes, query plans, isolation, deadlocks, and keyset pagination.
- Retries, timeouts, circuit breakers, backpressure, and graceful shutdown.
- Idempotency, outbox delivery, event redelivery, reconciliation, and observability.
- API scalability, multi-tenant boundaries, consistency, and failure isolation.
- How to explain complexity, tradeoffs, testing strategy, and operational risk.

The examples use generic enterprise scenarios rather than leaked or company-specific
interview questions. For India-focused preparation, combine this roadmap with the
coding, SQL, Spring Boot, and system-design rounds used for Java backend roles.

## Why this lab?

Enterprise engineering is more than making an endpoint work. It is knowing:

- Which lifecycle hook or abstraction owns a behavior.
- How a query behaves with millions of rows.
- Where transactions, caches, and proxies stop working.
- How concurrency, failure, retries, and consistency change a design.
- What really happens inside frequently discussed Java APIs, not just how to call them.
- How to explain tradeoffs clearly in a senior or lead interview.

This is an interview-focused, practical Java and Spring lab. It includes runnable code, tests, traces, and failure scenarios for:

- Java fundamentals: `String` immutability, string pooling, `==` versus `equals`, `StringBuilder`, and `StringBuffer`.
- Collections and concurrency: `ConcurrentHashMap` hashing, buckets, collision lookup, `get`, `putIfAbsent`, and atomic `merge`.
- Iterator consistency: fail-fast `ArrayList` behavior, `ConcurrentModificationException`, snapshot iteration, and fail-safe-style `CopyOnWriteArrayList` usage.
- JVM internals: stack, heap, metaspace, code cache, native memory, Eden, Survivor S0/S1, Old generation, promotion, and GC reachability.
- Enterprise Java and Spring: bean lifecycle, dependency injection, transactions, caching, JPA, SQL, streams, API design, concurrency, and design patterns.

The examples intentionally include both the good path and the trap. Every topic asks the practical interview question: when should this be used, what does it cost, and what breaks under production load?

## Quick start

Prerequisites:

- Java 21 or newer
- Maven 3.9 or newer

Run the tests:

```bash
mvn clean test
```

Start the application:

```bash
mvn spring-boot:run
```

The application starts an HTTP server on `http://localhost:8080`, seeds 10,000 pagination records in an in-memory H2 database, and prints the feature demonstrations to the console.

Try the API:

```bash
curl 'http://localhost:8080/api/pagination/records/cursor?tenantId=tenant-a&limit=5'
curl 'http://localhost:8080/api/pagination/records/offset?tenantId=tenant-a&offset=100&limit=5'
```

The cursor response contains `nextCursor`; pass it to retrieve the next page.

### Focused playground mode

The application can run one learning topic instead of printing every demo. The
default remains the complete lab:

```bash
mvn spring-boot:run
```

List the available topics:

```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--playground=help"
```

Run one topic and read its `Observe`, `Break it`, and `Enterprise question`
prompts before changing the nearby implementation:

```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--playground=concurrency"
mvn spring-boot:run -Dspring-boot.run.arguments="--playground=jpa"
mvn spring-boot:run -Dspring-boot.run.arguments="--playground=features"
mvn spring-boot:run -Dspring-boot.run.arguments="--playground=jvm"
mvn spring-boot:run -Dspring-boot.run.arguments="--playground=pagination"
mvn spring-boot:run -Dspring-boot.run.arguments="--playground=rest"
```

Available topic names are `lifecycle`, `features`, `concurrency`, `strings`,
`jvm`, `jpa`, `sql`, `streams`, `design`, `pagination`, and `rest`. The `transactions`
name is covered by the `features` runner, so use `--playground=features` for
that example.

Use this loop for each topic:

1. Read the printed `Observe` prompt and the linked implementation.
2. Run the focused test class with `mvn -Dtest=<TestClass> test`.
3. Change the line described by `Break it` and rerun the test.
4. Restore the code and answer the enterprise question in the context of a real service.

The first executable break/fix exercises are in the concurrency module:

- [ImmutableUserProfile.java](src/main/java/com/example/lifecycle/concurrency/ImmutableUserProfile.java)
  is the corrected immutable value object.
- [BadImmutableUserProfile.java](src/main/java/com/example/lifecycle/concurrency/BadImmutableUserProfile.java)
  deliberately aliases the caller's mutable list.
- [SingletonBreakageExamples.java](src/main/java/com/example/lifecycle/concurrency/SingletonBreakageExamples.java)
  demonstrates reflection bypassing a private Singleton constructor.
- [SerializableSingletonWithoutReadResolve.java](src/main/java/com/example/lifecycle/concurrency/SerializableSingletonWithoutReadResolve.java)
  demonstrates deserialization creating a second instance.
- [EnumSingleton.java](src/main/java/com/example/lifecycle/concurrency/EnumSingleton.java)
  provides the robust enum alternative.

Run those exercises with:

```bash
mvn -Dtest=ConcurrencyExamplesTests test
```

The JVM topic prints live memory-pool and reachability observations. The
pagination topic runs offset and cursor requests in-process and then you can
continue with the HTTP examples from the API section above.

Additional enterprise failure labs are executable in the normal test suite:

- `BeanLifecycleDemoApplicationTests#selfInvocationBypassesTransactionalProxy`
  shows why an internal call does not cross a Spring transaction proxy.
- `PaginationApiTests#cursorCannotBeReplayedAcrossTenants` shows tenant-bound
  cursor validation.
- `SqlQueryExamplesTests#productionQueriesHaveDeterministicBounds` checks that
  latest-row and keyset queries are ordered and bounded.
- `StreamCodingExamplesTests#streamPlaygroundMakesTieBreakerFailureVisible`
  compares an implicit stream tie with an explicit business rule.

### Team-lead concurrency checklist

The concurrency playground now covers the questions to ask before approving
asynchronous production code:

- **Executor ownership:** who creates, names, sizes, and shuts down the pool?
- **Queue policy:** is work bounded, and what happens when the queue is full?
- **Failure handling:** where does a failed `Future` or `CompletableFuture` become visible?
- **Cancellation:** does interruption stop the task, and is cleanup idempotent?
- **Composition:** are independent calls combined with `allOf` instead of blocking one by one?
- **Timeouts and fallback:** is a downstream timeout bounded, observable, and paired with a safe fallback?
- **Context:** how are tenant, trace, security, and logging contexts propagated across threads?
- **Load behavior:** what happens under overload, deploy shutdown, dependency slowness, and partial failure?

Run the focused exercises with:

```bash
mvn -Dtest=ConcurrencyExamplesTests test
```

The implementation is in
[AsyncCoordinationPlayground.java](src/main/java/com/example/lifecycle/concurrency/AsyncCoordinationPlayground.java).

## Learning path

Follow the sections in this order:

1. **How does the Spring bean lifecycle work?** See construction, dependency injection, initialization callbacks, post-processors, ready events, proxies, and destruction.
2. **How should Spring choose implementations?** Practice constructor injection, conditional beans, replacement with test doubles, and configuration boundaries.
3. **How do transactions and caches work through Spring proxies?** Observe commit, rollback, cache hits, and the self-invocation trap.
4. **What is the difference between `String`, `StringBuilder`, and `StringBuffer`?** Compare immutability, string pooling, `==`, `equals`, local mutation, and synchronized legacy builders.
5. **How do concurrent Java collections work internally?** Trace `ConcurrentHashMap` hashing, buckets, collisions, `get`, `putIfAbsent`, atomic `merge`, and `CopyOnWriteArrayList` snapshot writes.
6. **What is the difference between fail-fast and fail-safe-style iteration?** Reproduce `ConcurrentModificationException`, multithreaded structural changes, stable snapshots, and collection-selection tradeoffs.
7. **How is JVM memory divided and how does garbage collection work?** Connect stack references, heap objects, metaspace, native memory, Eden, S0/S1, Old generation, promotion, memory pools, and reachability.
8. **How do JPA and SQL behave at scale?** Measure N+1 queries, fetch plans, joins, window functions, indexes, transactions, and offset versus keyset pagination.
9. **How should Java Streams solve common coding problems?** Practice grouping, flattening, ranking, duplicate handling, ordering, `Optional`, primitive aggregation, and parallelism tradeoffs.
10. **How do SOLID principles and design patterns work in an enterprise service?** Apply Strategy, Factory, Adapter, Decorator, Observer, Builder, and Facade to payments.
11. **How should a production API handle pagination and errors?** Compare offset and cursor pagination, validate input, model sealed error categories, and test continuation and tampering.

## Interview question index

Use these questions as a practical study checklist. Each topic has executable Java or Spring code, tests that state the expected behavior, and documentation explaining the production tradeoff.

### Java fundamentals and strings

- Why is `String` immutable, and how does string pooling affect object identity?
- Why can `==` appear to work for string literals but fail for separately created strings?
- When should I use `StringBuilder` instead of repeated string concatenation?
- When is `StringBuffer` appropriate, and why does synchronized mutation not automatically make a workflow thread-safe?

### Collections and multithreading

- How does `ConcurrentHashMap` calculate a hash and select a bucket?
- How does `ConcurrentHashMap.get()` find a value and handle hash collisions?
- Why can concurrent readers proceed without one global map lock?
- Why are `putIfAbsent`, `compute`, and `merge` different from separate `get` and `put` calls?
- How does `ConcurrentHashMap.merge()` prevent lost updates for a single key under contention?
- When should I use `ConcurrentHashMap` instead of `HashMap` or synchronized access?
- How does `CopyOnWriteArrayList` let an iterator read a stable snapshot during a write?
- Why is `CopyOnWriteArrayList` useful for listener registries but expensive for write-heavy workloads?
- What does a fail-fast iterator detect, and why is `ConcurrentModificationException` not a synchronization mechanism?
- When should I choose synchronized iteration, a snapshot, a concurrent collection, or message passing?

### JVM memory and garbage collection

- What is stored in a Java thread stack, and how is a reference different from the heap object it points to?
- What lives in the heap, metaspace, code cache, and native memory?
- What happens to a new object in Eden during a young or minor collection?
- What are Survivor S0 and S1, and why do they alternate roles?
- When is an object promoted from the young generation to the Old or Tenured generation?
- What is the difference between a minor collection and an old-generation or major collection?
- How do GC roots determine whether an object is reachable or eligible for collection?
- Why does `System.gc()` not guarantee immediate garbage collection?
- How do G1 regions differ from the classic Eden/Survivor/Old generation diagram?
- How can MXBeans, GC logs, JFR, heap dumps, and allocation profiles help diagnose memory problems?

### Spring and enterprise design

- In what order does Spring construct, initialize, proxy, and destroy a bean?
- When should I use constructor injection, `@PostConstruct`, `ApplicationReadyEvent`, or a shutdown callback?
- How do `@ConditionalOnProperty`, `@Primary`, `@Qualifier`, and `@ConditionalOnMissingBean` affect implementation selection?
- What problem do `@Configuration` and `@Bean` solve, and when should I choose them over `@Component`?
- How can I register a third-party client or JDK type as a Spring bean and inject it into another bean?
- Why do `@Transactional` and `@Cacheable` fail when called through self-invocation?
- How should a service handle rollback, retries, idempotency, outbox events, and external provider failures?
- How do Strategy, Factory, Adapter, Decorator, Observer, Builder, and Facade solve different change pressures?

### JPA, SQL, streams, and APIs

- How do I prove an N+1 query problem and choose between `EntityGraph`, `JOIN FETCH`, DTO projection, and JDBC?
- How do lazy loading, persistence context state, dirty checking, optimistic locking, and batch processing interact?
- How do `GROUP BY`, `HAVING`, window functions, anti-joins, duplicate detection, and running totals work?
- When should I use offset pagination versus cursor or keyset pagination?
- How do I make a cursor deterministic, tenant-safe, bounded, and tamper-resistant?
- How do Java Streams handle grouping, flattening, duplicates, ordering, `Optional`, and primitive aggregation?
- When is a loop clearer or safer than a stream, and when is `parallelStream()` a bad performance assumption?
- How should a Java 21 API model validation errors with records, sealed types, pattern matching, and controller advice?

## Repository map

| Topic | Code | What to run or inspect |
| --- | --- | --- |
| Bean lifecycle | [lifecycle package](src/main/java/com/example/lifecycle) | `LifecycleBean`, aware callbacks, post-processors |
| DI, conditions, transactions, cache | [features package](src/main/java/com/example/lifecycle/features) | `OrderService`, `ShippingConfiguration`, `ProductCatalog` |
| Singleton and concurrency | [concurrency package](src/main/java/com/example/lifecycle/concurrency) | singleton publication, immutable values, collections |
| JVM memory and GC | [JVM package](src/main/java/com/example/lifecycle/jvm) | memory areas, live MXBean measurements, reachability |
| JPA and Hibernate | [jpa package](src/main/java/com/example/lifecycle/jpa) | N+1, `@EntityGraph`, fetch join, query counts |
| SQL interview queries | [sql package](src/main/java/com/example/lifecycle/sql) | grouping, windows, keyset query, anti-join |
| Java 8+ Streams | [streams package](src/main/java/com/example/lifecycle/streams) | coding exercises and edge cases |
| Strings and iterators | [StringExamples.java](src/main/java/com/example/lifecycle/streams/StringExamples.java), [IteratorExamples.java](src/main/java/com/example/lifecycle/concurrency/IteratorExamples.java) | identity versus equality, builders, fail-fast and snapshot iteration |
| SOLID and patterns | [design package](src/main/java/com/example/lifecycle/design) | enterprise payment authorization scenario |
| Pagination and API errors | [pagination package](src/main/java/com/example/lifecycle/pagination) | HTTP endpoints, cursors, sealed problems |
| REST API design and idempotency | [rest package](src/main/java/com/example/lifecycle/rest) | Path/query parameters, retries, idempotency, payload limits |
| Tests | [test package](src/test/java/com/example/lifecycle) | executable specifications for every module |

## 1. Spring bean lifecycle

Start with [BeanLifecycleDemoApplication.java](src/main/java/com/example/lifecycle/BeanLifecycleDemoApplication.java) and [LifecycleBean.java](src/main/java/com/example/lifecycle/LifecycleBean.java).

The demonstrated startup sequence is:

```text
Constructor
  -> dependency injection and Aware callbacks
  -> BeanPostProcessor before initialization
  -> @PostConstruct
  -> InitializingBean.afterPropertiesSet
  -> configured custom init method
  -> BeanPostProcessor after initialization
  -> SmartInitializingSingleton
  -> ApplicationRunner
  -> ApplicationReadyEvent
```

Graceful shutdown invokes `@PreDestroy`, `DisposableBean.destroy()`, and the configured destroy method. The relative order of individual `Aware` callbacks is controlled by Spring and is not an application contract.

Use constructor injection for required dependencies, `@PostConstruct` for short local setup, `@PreDestroy` for owned resources, and `ApplicationReadyEvent` for bounded application-level startup work. Avoid database calls, network calls, long tasks, or cross-bean assumptions in constructors and local initialization callbacks.

Useful breakpoints:

- `LifecycleBean()`
- `LifecycleBean.setApplicationContext(...)`
- `LifecycleBean.postConstruct()`
- `LifecycleLoggingPostProcessor`
- `StartupObserver.afterSingletonsInstantiated()`
- `ApplicationStartupPhases.run()`
- `ApplicationStartupPhases.onApplicationReady()`

## 2. Dependency injection and Spring features

The [features package](src/main/java/com/example/lifecycle/features) demonstrates programming to interfaces:

- `OrderService` receives `OrderRepository` and `ShippingProvider` through constructor injection.
- `ShippingConfiguration` selects `FastShippingProvider` with `@ConditionalOnProperty` and supplies `DefaultShippingProvider` with `@ConditionalOnMissingBean`.
- `OrderService` demonstrates `@Transactional` commit and rollback using H2.
- `ProductCatalog` demonstrates `@Cacheable` and cache hits.
- `ConfigurationBeanDemoConfiguration` demonstrates `@Configuration` as a configuration boundary and `@Bean` as an explicit factory for a JDK/infrastructure type.

### `@Configuration` and `@Bean`: what problem do they solve?

- **`@Configuration`** tells Spring that a class contains bean definitions and configuration rules. Spring discovers it during component scanning and uses it to build the application context.
- **`@Bean`** tells Spring to call a method, take the returned object, and manage it as a bean: dependency injection, lifecycle, scopes, conditions, and proxy integration can apply.
- Use `@Component` when you own a class and it is naturally a scanned application component. Use `@Bean` when you need to construct a third-party class, adapt an SDK, choose an implementation, configure constructor arguments, or express a factory decision.
- Prefer method parameters for dependencies between `@Bean` methods, as shown by `configuredGreetingService(Clock demoClock)`. This makes the dependency explicit and testable.
- `@Configuration` is a boundary for application wiring, not a replacement for business services. Keep business behavior in normal services and keep object construction and environment choices in configuration.

Important traps:

- Two candidates without `@Primary`, `@Qualifier`, or a condition cause `NoUniqueBeanDefinitionException`.
- `@Transactional` and `@Cacheable` are proxy-based; self-invocation bypasses the proxy.
- Unchecked exceptions roll back by default; checked exceptions need an explicit policy.
- A database transaction does not include an HTTP call or message broker.
- Conditional beans are startup decisions, not live feature flags.
- Local caches are per instance; distributed consistency needs a shared cache and invalidation design.

## 3. Concurrency and collections

The [concurrency package](src/main/java/com/example/lifecycle/concurrency) compares:

- Initialization-on-demand holder singleton and `volatile` double-checked locking.
- Immutable values with defensive copies.
- `HashMap` for local state.
- `LinkedHashMap` for deterministic encounter order.
- `ConcurrentHashMap` with atomic `merge` updates.
- `CopyOnWriteArrayList` for many reads and rare writes.
- Bounded `BlockingQueue` for producer-consumer backpressure.

The enterprise rule is to decide the scope first: request, thread, JVM, application context, or cluster. Prefer immutable data and message passing before locks. A JVM singleton is not a cluster singleton, and a thread-safe collection does not make a multi-step business workflow atomic.

### Collection internals as a mental model

The runnable traces in [ConcurrentHashMapInternalsDemo.java](src/main/java/com/example/lifecycle/concurrency/ConcurrentHashMapInternalsDemo.java) and [CopyOnWriteArrayListInternalsDemo.java](src/main/java/com/example/lifecycle/concurrency/CopyOnWriteArrayListInternalsDemo.java) explain the important path without reproducing private JDK source:

- **`ConcurrentHashMap.get(key)`**: calculate `hashCode()`, spread high bits, choose a bucket with `(capacity - 1) & hash`, read without locking, then compare hash and `equals()` while checking collisions.
- **`ConcurrentHashMap.putIfAbsent`**: use the same hash and bucket path; an empty bucket can be published atomically, while a competing update for the same key is coordinated rather than silently overwriting data. Different keys can make progress independently.
- **`ConcurrentHashMap.merge`**: the read, value computation, and publication form one atomic update for that key. This is why `merge` works for concurrent counters while separate `get` plus `put` can lose updates. The mapping function should be short and side-effect free.
- **`CopyOnWriteArrayList` reads**: an iterator captures the current array reference, so iteration needs no lock and remains stable while another thread writes.
- **`CopyOnWriteArrayList` writes**: `add` or `remove` copies the array, changes the copy, and publishes it as the new current array. Existing iterators keep the old snapshot; new iterators see the new array.

The traces are deliberately a mental model, not a promise about private implementation details such as the exact bin-lock or treeification code in a particular JDK version. Use `ConcurrentHashMap` for shared key/value state with atomic per-key operations; use `CopyOnWriteArrayList` for many reads and rare writes such as listener lists. Neither collection makes an arbitrary multi-step business workflow atomic.

## 4. JVM memory and garbage collection

The [JVM package](src/main/java/com/example/lifecycle/jvm) provides a practical memory model without pretending that every JVM or garbage collector has the same internal layout.

The mental model is:

- **Thread stack**: method frames, parameters, local primitives, and references. A reference is not the object itself.
- **Heap**: objects and arrays. The garbage collector reclaims objects that are no longer reachable from GC roots such as live thread stacks, static fields, and JNI references.
- **Metaspace**: class metadata in native memory. Loading many classes can grow it even when the Java heap looks healthy.
- **Code cache**: JIT-compiled machine code, also outside the Java heap.
- **Other native memory**: thread stacks, direct byte buffers, JNI allocations, and JVM bookkeeping.

Run `JvmMemoryAndGcDemo.memorySnapshot()` to inspect live heap/non-heap usage, memory pools, and collector counts through standard MXBeans. Values are measurements at one moment, not fixed limits; `max` can be unavailable and is represented by `-1`.

The [generational GC trace](src/main/java/com/example/lifecycle/jvm/JvmMemoryAndGcDemo.java) covers the classic interview model: new objects begin in **Eden**; a young/minor collection copies surviving objects through alternating **S0** and **S1 Survivor** areas; objects that survive enough cycles may be promoted to **Old/Tenured** generation. `generationalPools()` maps live pool names to these concepts when the active collector exposes them. The label `SURVIVOR_S0_OR_S1` is intentional: the same logical survivor pool may alternate roles, and the MXBean name does not always identify which side is currently active.

Do not apply the classic picture too literally to every collector. G1 divides the heap into regions and assigns regions to young or old roles; ZGC and Shenandoah use different designs. The code therefore keeps both the actual pool name and a conceptual classification, and reports `OTHER_HEAP` when a collector's names do not match the classic labels.

The GC example follows the important lifecycle: create an object, hold a strong reference, clear that reference, and observe that the object becomes **eligible** for collection. `System.gc()` is only a request. Collection timing, collector choice, generations, pauses, compaction, and promotion depend on the JVM, flags, allocation pressure, and runtime workload. The test therefore proves reachability, not a particular collection time.

For production diagnosis, use allocation profiles, GC logs, JFR, heap dumps, and container-aware memory limits. Do not use `System.gc()` as an application memory-management strategy.

## 5. JPA and Hibernate

The [JPA module](src/main/java/com/example/lifecycle/jpa) uses `Department` and `Employee` entities to make N+1 measurable:

```text
Naive lazy access: 3 SQL statements for 2 departments
@EntityGraph:      1 SQL statement
JOIN FETCH:        1 SQL statement
```

The tests prove the counts with Hibernate statistics. The README and code cover:

- Lazy versus eager associations.
- `@EntityGraph` versus `JOIN FETCH` versus DTO projection.
- `LazyInitializationException`.
- Pagination with collection fetch joins.
- `MultipleBagFetchException`.
- Dirty checking, flush, merge, and persistence context states.
- Optimistic and pessimistic locking.
- `@Version`, batch processing, and outbox design.
- When JDBC or jOOQ is more appropriate than JPA.

The main lesson: reducing query count is not enough. Check returned rows, execution plans, memory, transaction duration, and connection-pool pressure.

## 6. SQL interview practice

[SqlQueryExamples.java](src/main/java/com/example/lifecycle/sql/SqlQueryExamples.java) contains runnable queries for the existing H2 schema and templates for larger banking-style schemas:

- `GROUP BY` and `HAVING`.
- Latest row per customer with `ROW_NUMBER`.
- Top N per account.
- Keyset pagination.
- Duplicate detection.
- Running totals.
- Customers without orders using an anti-join.
- Transaction totals.

Lead-level SQL topics include null semantics, join multiplication, `WHERE` versus `HAVING`, `UNION` versus `UNION ALL`, composite indexes, execution plans, isolation, deadlocks, lost updates, SQL injection, and outbox consistency. The project favors bind parameters and deterministic ordering.

## 7. Java 8+ Streams

The [Streams module](src/main/java/com/example/lifecycle/streams) uses Java 8-compatible APIs and covers:

- Word frequency and first non-repeated character.
- Duplicate detection and `flatMap`.
- Grouping, partitioning, downstream collectors, and joining.
- Top N, second/Nth highest, anagrams, and tie-breaking.
- `Optional` for absent results.
- Primitive aggregation with `mapToInt`.
- Null handling, duplicate keys, ordering, side effects, and exceptions.
- Why `parallelStream()` is not a free performance switch.

For each stream problem, state empty-input behavior, duplicate semantics, ordering, complexity, memory cost, and whether parallel execution is safe. A clear loop is better than an opaque stream pipeline.

## 8. Strings and concurrent iteration

[StringExamples.java](src/main/java/com/example/lifecycle/streams/StringExamples.java) makes two common interview distinctions executable:

- `==` compares whether two references point to the same object; `equals` compares string content. String interning can make `==` appear to work for literals, so production code should use `equals` for values.
- `String` is immutable. `StringBuilder` is mutable and preferred for local, single-threaded assembly; `StringBuffer` provides synchronized methods for legacy shared mutable use, but a lock does not automatically make a multi-step workflow correct.

[IteratorExamples.java](src/main/java/com/example/lifecycle/concurrency/IteratorExamples.java) contrasts collection consistency models:

- A normal `ArrayList` iterator is fail-fast on a best-effort basis. An unsynchronized structural change may result in `ConcurrentModificationException`; this is a bug signal, not a thread-safety mechanism or a guaranteed outcome.
- `CopyOnWriteArrayList` iterators are snapshot-based. Readers see a stable snapshot without that exception, while writes copy the backing array, making it suitable for many reads and rare writes such as listener registries. It is not a universal replacement for synchronized coordination.

For lead-level designs, first decide whether the requirement is fail-fast detection, a stable snapshot, synchronized live iteration, or a concurrent algorithm with explicit atomic operations. Then select the collection and synchronization boundary to match that requirement.

## 9. SOLID and enterprise design patterns

The [design package](src/main/java/com/example/lifecycle/design) models payment authorization:

```text
PaymentCommand -> EnterprisePaymentService
                 |-> FraudCheck port -> legacy API adapter
                 |-> PaymentGatewayFactory -> payment strategies
                 |-> audited gateway decorator
                 \-> payment-completed observers
```

SOLID examples:

- **Single Responsibility**: orchestration, fraud translation, payment, and audit have separate change reasons.
- **Open/Closed**: add a provider strategy without rewriting orchestration.
- **Liskov Substitution**: gateway implementations honor a truthful contract.
- **Interface Segregation**: small `FraudCheck`, `PaymentGateway`, and listener ports.
- **Dependency Inversion**: business policy depends on ports, not vendor SDKs.

Patterns demonstrated:

- Strategy for payment methods.
- Factory for validated provider selection.
- Adapter for a legacy fraud API.
- Decorator for audit behavior.
- Observer for completion reactions.
- Builder plus record for an immutable command.
- Facade/application service for one business operation.

Do not add patterns to increase class count. Add an abstraction when it localizes a likely change, isolates a failure boundary, improves testing, or clarifies ownership. For payments, discuss idempotency, provider timeout after acceptance, reconciliation, durable events, and sensitive-data logging.

## 10. Pagination API and Java 21 errors

The [pagination package](src/main/java/com/example/lifecycle/pagination) seeds 10,000 rows and exposes:

```text
GET /api/pagination/records/offset?tenantId=tenant-a&offset=100&limit=20
GET /api/pagination/records/cursor?tenantId=tenant-a&limit=20
GET /api/pagination/records/cursor?tenantId=tenant-a&cursor=<nextCursor>&limit=20
```

### Offset or cursor?

| Requirement | Better default | Reason |
| --- | --- | --- |
| Small dataset or page-number UI | Offset | Simple and supports direct page jumps |
| Very large sequential feed | Cursor/keyset | Seeks from an indexed position instead of walking deep offsets |
| Infinite scroll/mobile API | Cursor/keyset | Stable forward traversal and bounded work |
| Billion-row export | Async job plus bounded chunks | Do not hold an HTTP request or memory for the whole export |

Cursor pagination still needs a deterministic unique ordering, such as `(created_at, id)`, tenant/filter binding, cursor expiry, and signed or encrypted opaque state in production. It does not automatically provide a snapshot under concurrent writes.

### Java 21 error model

- Records model immutable page items, responses, and error values.
- A sealed `ApiProblem` hierarchy constrains known error categories.
- Pattern matching in `ApiErrorResponse` provides an exhaustive mapping.
- `@RestControllerAdvice` keeps exception policy out of controllers.
- Unexpected errors return a safe generic message while details remain in server logs.

The API validates tenant, cursor, and page size. It caps `limit` at 100 and uses `(tenant_id, id)` for the access path. The tests cover cursor continuation, offset pages, invalid cursors, and invalid limits.

## 11. REST API design and idempotency

Run the REST playground to print the failure scenarios and lead-level design questions:

```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--playground=rest"
```

The same topic exposes runnable Spring MVC examples under `/playground/rest`:

```bash
curl 'http://localhost:8080/playground/rest/users/getUser?id=101'
curl 'http://localhost:8080/playground/rest/users/101'
curl 'http://localhost:8080/playground/rest/users?role=admin&status=active&size=20'

curl -X POST 'http://localhost:8080/playground/rest/orders/bad-create' \
  -H 'Content-Type: application/json' \
  -d '{"customerId":42,"items":[101,102]}'

curl -X POST 'http://localhost:8080/playground/rest/orders/good-create' \
  -H 'Content-Type: application/json' \
  -H 'Idempotency-Key: checkout-42' \
  -d '{"customerId":42,"items":[101,102]}'
```

Call the good create command twice with the same `Idempotency-Key` and compare it with the bad create endpoint. Other endpoints demonstrate PATCH retry behavior, DELETE consistency, large query filters, and payload-size rejection. The implementation is organized under the [rest package](src/main/java/com/example/lifecycle/rest) with separate controller, service, DTO, and repository layers.

### REST question-to-code map

| Lead question | Problem to observe | Bad code | Good code |
| --- | --- | --- | --- |
| Where does resource identity belong? | Action routes hide resource intent. | [`badGetUser`](src/main/java/com/example/lifecycle/rest/controller/RestApiDemoController.java) -> [`readBadUser`](src/main/java/com/example/lifecycle/rest/service/RestApiDemoService.java) | [`goodGetUser`](src/main/java/com/example/lifecycle/rest/controller/RestApiDemoController.java) -> [`readGoodUser`](src/main/java/com/example/lifecycle/rest/service/RestApiDemoService.java) |
| What happens when POST is retried? | A create can produce duplicate orders. | [`badCreateOrder`](src/main/java/com/example/lifecycle/rest/controller/RestApiDemoController.java) -> [`createBadOrder`](src/main/java/com/example/lifecycle/rest/service/RestApiDemoService.java) | [`goodCreateOrder`](src/main/java/com/example/lifecycle/rest/controller/RestApiDemoController.java) -> [`createGoodOrder`](src/main/java/com/example/lifecycle/rest/service/RestApiDemoService.java) |
| Is PATCH safe to retry? | Delta updates apply the same side effect repeatedly. | [`patchBadBalance`](src/main/java/com/example/lifecycle/rest/service/RestApiDemoService.java) | [`patchGoodBalance`](src/main/java/com/example/lifecycle/rest/service/RestApiDemoService.java) |
| What should repeated DELETE do? | Retries can repeat cleanup or return inconsistent results. | [`deleteBad`](src/main/java/com/example/lifecycle/rest/service/RestApiDemoService.java) | [`deleteGood`](src/main/java/com/example/lifecycle/rest/service/RestApiDemoService.java) |
| Where should request limits be enforced? | Arbitrary filters and large bodies pressure infrastructure and the database. | [`manyQueryParams`](src/main/java/com/example/lifecycle/rest/controller/RestApiDemoController.java) and unrestricted request input | [`largePayload`](src/main/java/com/example/lifecycle/rest/controller/RestApiDemoController.java) with service-side size observation |

The [REST study guide](src/main/java/com/example/lifecycle/rest/RestApiStudyGuide.java) prints this same map when running `--playground=rest`, including the endpoint to call for each bad and good implementation.

## Interview preparation

The repository includes senior/lead themes commonly discussed in large enterprise and financial-services engineering interviews. They are not claimed to be leaked company-specific question lists.

Prepare to explain:

- Why `String` is immutable, when `StringBuilder` is preferable to `StringBuffer`, and why `equals` is correct for content comparison while `==` checks identity.
- How `ConcurrentHashMap` calculates a bucket, reads without a global lock, handles collisions, and makes `merge` or `compute` atomic for one key.
- Why `get` followed by `put` is not the same as an atomic map operation under contention.
- Why `CopyOnWriteArrayList` is effective for many reads and rare writes, and why copying on every write is expensive for write-heavy workloads.
- What fail-fast iterators detect, why `ConcurrentModificationException` is best-effort rather than a synchronization mechanism, and when snapshot iteration is appropriate.
- How stack references, heap objects, GC roots, Eden, Survivor S0/S1, promotion, Old generation, metaspace, and native memory fit together.
- Why `System.gc()` is not a production memory-management strategy and how to investigate memory with GC logs, JFR, heap dumps, and allocation profiles.
- How you prove an N+1 fix with query counts and plans.
- When cursor pagination beats offset and how to secure a cursor.
- How to prevent duplicate payments with idempotency and reconciliation.
- How isolation, optimistic locking, constraints, and retries work together.
- Why `@Transactional` and `@Cacheable` fail on self-invocation.
- How an outbox makes database state and event publication reliable.
- How to process millions of rows without one huge transaction or persistence context.
- When to choose JDBC/jOOQ over JPA.
- How to test concurrency, provider failures, cursor tampering, and event redelivery.
- How to trade off simplicity, extensibility, observability, and operational risk.

## Testing strategy

The tests are executable learning notes:

```bash
mvn -q -Dtest=BeanLifecycleDemoApplicationTests test
mvn -q -Dtest=ConcurrencyExamplesTests test
mvn -q -Dtest=JvmMemoryExamplesTests test
mvn -q -Dtest=JpaExamplesTests test
mvn -q -Dtest=PaginationApiTests test
mvn -q -Dtest=StreamCodingExamplesTests test
mvn -q -Dtest=SqlQueryExamplesTests test
mvn -q -Dtest=DesignPrinciplesTests test
```

Run all tests before opening a pull request:

```bash
mvn clean test
```

## Project structure

```text
src/main/java/com/example/lifecycle/
├── concurrency/   Singleton, immutable values, concurrent collections
├── design/        SOLID and enterprise design patterns
├── features/      DI, conditions, transactions, caching
├── jpa/           Entities, N+1, entity graphs, fetch joins
├── pagination/    HTTP pagination and global API errors
├── sql/           SQL query catalog and JDBC runner
└── streams/       Java 8+ Stream coding exercises
```

## Contributing

This project is intentionally easy to fork and extend. A useful contribution should include:

1. A small runnable example.
2. Comments explaining the decision and its failure mode.
3. A focused test for the behavior, not only context loading.
4. A README section with use case, tradeoffs, and when not to use it.
5. No secrets, real customer data, or private interview questions.

Good next modules include resilience and retries, Testcontainers, messaging and outbox delivery, observability, security boundaries, rate limiting, and contract testing.

Please read [CONTRIBUTING.md](CONTRIBUTING.md) before opening a pull request. Use
[GitHub Discussions](../../discussions) for preparation questions and study ideas,
and use an [issue template](.github/ISSUE_TEMPLATE/) for a focused content request
or bug report.

## Project name

**Java Spring Boot Interview Preparation Lab** describes the repository's primary
purpose while leaving room for more enterprise Java, backend, and system-design
topics.

If this lab helps your preparation, fork it, adapt the examples, and share improvements through a pull request or issue. Keep examples runnable and explain the tradeoff behind every pattern.
