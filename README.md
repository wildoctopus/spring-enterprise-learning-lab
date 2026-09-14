# Spring Enterprise Learning Lab

> A runnable Java 21 + Spring Boot workshop for understanding the decisions behind production-grade enterprise applications.

This repository is designed to be read, run, debugged, and extended. Every topic has executable code, tests, failure scenarios, and interview-level tradeoffs.

[![Java](https://img.shields.io/badge/Java-21-orange)](https://adoptium.net/temurin/releases/?version=21)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5-brightgreen)](https://spring.io/projects/spring-boot)
[![Build](https://img.shields.io/badge/build-Maven-blue)](https://maven.apache.org/)

## Why this lab?

Enterprise engineering is more than making an endpoint work. It is knowing:

- Which lifecycle hook or abstraction owns a behavior.
- How a query behaves with millions of rows.
- Where transactions, caches, and proxies stop working.
- How concurrency, failure, retries, and consistency change a design.
- How to explain tradeoffs clearly in a senior or lead interview.

The examples intentionally include both the good path and the trap.

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

## Learning path

Follow the sections in this order:

1. **Bean lifecycle**: understand how Spring creates, initializes, proxies, and destroys beans.
2. **Dependency injection and conditions**: learn how implementations are selected and replaced.
3. **Transactions and caching**: see proxy boundaries, rollback, cache hits, and self-invocation traps.
4. **Concurrency**: compare singleton publication, immutable state, concurrent collections, and queues.
5. **JPA and SQL**: measure N+1 queries, fetch plans, joins, windows, indexes, and pagination.
6. **Java Streams**: solve common coding problems while reasoning about ordering, complexity, and parallelism.
7. **Java fundamentals and collection iteration**: compare string identity, mutability, and iterator consistency under concurrency.
8. **SOLID and design patterns**: apply Strategy, Factory, Adapter, Decorator, Observer, Builder, and Facade to payments.
9. **API design**: compare offset and cursor pagination and model errors with Java 21.

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

## Interview preparation

The repository includes senior/lead themes commonly discussed in large enterprise and financial-services engineering interviews. They are not claimed to be leaked company-specific question lists.

Prepare to explain:

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

## Contributing and extending

This project is intentionally easy to fork and extend. A useful contribution should include:

1. A small runnable example.
2. Comments explaining the decision and its failure mode.
3. A focused test for the behavior, not only context loading.
4. A README section with use case, tradeoffs, and when not to use it.
5. No secrets, real customer data, or private interview questions.

Good next modules include resilience and retries, Testcontainers, messaging and outbox delivery, observability, security boundaries, rate limiting, and contract testing.

## Project name

**Spring Enterprise Learning Lab** is the working project name. It is broad enough to grow beyond bean lifecycle while making the purpose clear to someone discovering the repository.

If this lab helps your preparation, fork it, adapt the examples, and share improvements through a pull request or issue. Keep examples runnable and explain the tradeoff behind every pattern.
