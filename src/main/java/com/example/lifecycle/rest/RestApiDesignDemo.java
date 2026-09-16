package com.example.lifecycle.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class RestApiDesignDemo {

    private RestApiDesignDemo() {
    }

    public static void printAll() {
        System.out.println("\n=== REST API design demo: bad patterns and the fixes ===");

        System.out.println("\n--- 1. Path param vs query param ---");
        printPathVsQuery();

        System.out.println("\n--- 2. Many query params become a performance trap ---");
        printManyQueryParams();

        System.out.println("\n--- 3. Large payload rejects early ---");
        printLargePayload();

        System.out.println("\n--- 4. Duplicate create after retry ---");
        printDuplicateCreateFailure();

        System.out.println("\n--- 5. PATCH retry re-applies side effects ---");
        printPatchRetry();

        System.out.println("\n--- 6. DELETE retry must be consistent ---");
        printDeleteRetry();

        System.out.println("\n--- 7. List in path is ambiguous ---");
        printListInPathProblem();

        System.out.println("\n--- 8. Action route hides contract semantics ---");
        printActionVsResource();
    }

    private static void printPathVsQuery() {
        System.out.println("Bad implementation:");
        System.out.println("  String userId = request.getParameter(\"id\");");
        System.out.println("  return \"/users/getUser?id=\" + userId;");
        System.out.println();
        System.out.println("Good implementation:");
        System.out.println("  GET /users/101");
        System.out.println("  GET /users?role=admin&status=active");
        System.out.println();
        System.out.println("Why the fix is better:");
        System.out.println("- one resource identity is in the path");
        System.out.println("- filtering stays in the query string");
        System.out.println("- the contract becomes obvious and easier to secure");
        System.out.println("Where to fix: route design, controller mapping, and API documentation.");
    }

    private static void printManyQueryParams() {
        List<String> badFilters = List.of(
                "role=admin", "status=active", "country=in", "city=bengaluru",
                "department=eng", "page=2", "size=100", "sort=name,asc",
                "include=profile", "include=payments", "include=audit", "include=meta");

        System.out.println("Bad code:");
        System.out.println("  Map<String, String> filters = request.getParameterMap();");
        System.out.println("  // allow any filter without validation");
        System.out.println("  // SQL becomes large, dynamic, and hard to optimize");
        System.out.println();
        System.out.println("Filters observed: " + badFilters);
        System.out.println("Actual breakage: query planner chooses a poor plan and the database slows down.");
        System.out.println();
        System.out.println("Good pattern:");
        System.out.println("  validate allowed fields: role, status, page, size");
        System.out.println("  cap page size at 50");
        System.out.println("  if filters are complex, use POST /search with a JSON body");
        System.out.println("Where to fix: request validation, controller boundary, query builder, DB indexes.");
    }

    private static void printLargePayload() {
        String payload = "x".repeat(25000);
        int maxBytes = 20000;

        System.out.println("Bad code:");
        System.out.println("  byte[] body = request.getInputStream().readAllBytes();");
        System.out.println("  // no size guard");
        System.out.println();
        System.out.println("Payload size: " + payload.getBytes().length + " bytes");
        System.out.println("Max limit: " + maxBytes + " bytes");
        if (payload.getBytes().length > maxBytes) {
            System.out.println("Observed result: 413 Request Entity Too Large");
        }
        System.out.println();
        System.out.println("Good pattern:");
        System.out.println("  if (body.length > MAX_ALLOWED_BYTES) { return 413; }");
        System.out.println("  validate before parsing or storing");
        System.out.println("Where to fix: request validation, controller advice, API gateway size policy.");
    }

    private static void printDuplicateCreateFailure() {
        Map<String, Integer> createdByRequestId = new HashMap<>();
        Set<String> seenRequestIds = new HashSet<>();
        List<String> clientRetryIds = List.of("req-1", "req-1", "req-2");

        System.out.println("Bad code:");
        System.out.println("  if (!seen.contains(requestId)) { createOrder(); }");
        System.out.println("  // but the set is only in memory and resets on restart");
        System.out.println();
        System.out.println("Retry ids: " + clientRetryIds);

        for (String id : clientRetryIds) {
            if (!seenRequestIds.contains(id)) {
                seenRequestIds.add(id);
                createdByRequestId.put(id, createdByRequestId.getOrDefault(id, 0) + 1);
            } else {
                System.out.println("Bad behavior: duplicate order created for request id " + id);
            }
        }

        System.out.println("Result: " + createdByRequestId.size() + " distinct logical creates, but the caller thought it was one request.");
        System.out.println();
        System.out.println("Good pattern:");
        System.out.println("  store requestId in a deduplication table with TTL");
        System.out.println("  return the same response for the same requestId");
        System.out.println("  enforce unique constraint on business key or order token");
        System.out.println("Where to fix: create service, idempotency store, and database unique index.");
    }

    private static void printPatchRetry() {
        int balance = 100;
        int delta = 50;

        System.out.println("Bad code:");
        System.out.println("  balance += 50; // every retry adds again");
        System.out.println();
        System.out.println("Initial balance: " + balance);
        for (int i = 0; i < 2; i++) {
            balance += delta;
        }
        System.out.println("After retrying PATCH twice: " + balance);
        System.out.println("Why it breaks: repeated calls re-apply the same side effect.");
        System.out.println();
        System.out.println("Good pattern:");
        System.out.println("  apply patch only if previous version matches expected version");
        System.out.println("  or set the final value explicitly, not increment again on every retry");
        System.out.println("Where to fix: update service method and optimistic locking/version field.");
    }

    private static void printDeleteRetry() {
        Set<Integer> deletedUsers = new HashSet<>();
        List<Integer> requests = List.of(42, 42, 42);

        System.out.println("Bad behavior:");
        for (Integer userId : requests) {
            if (!deletedUsers.contains(userId)) {
                deletedUsers.add(userId);
                System.out.println("Delete accepted for user " + userId);
            } else {
                System.out.println("Second delete still does side effects unexpectedly");
            }
        }

        System.out.println();
        System.out.println("Good pattern:");
        System.out.println("  if already deleted -> return 204 or 404 consistently");
        System.out.println("  do not trigger extra side effects on repeat delete calls");
        System.out.println("Where to fix: delete service and API contract documentation.");
    }

    private static void printListInPathProblem() {
        System.out.println("Bad code:");
        System.out.println("  String ids = \"1,2,3\";");
        System.out.println("  return \"GET /users/\" + ids;");
        System.out.println();
        System.out.println("Why it breaks:");
        System.out.println("- URL parsing differs across frameworks");
        System.out.println("- escaping becomes messy");
        System.out.println("- the contract is unclear");
        System.out.println();
        System.out.println("Good pattern:");
        System.out.println("  GET /users?ids=1,2,3");
        System.out.println("  or POST /users/bulk-delete with a JSON body");
        System.out.println("Where to fix: bulk-operation API design, controller parameter parsing.");
    }

    private static void printActionVsResource() {
        System.out.println("Bad code:");
        System.out.println("  @GetMapping(\"/getUser\")");
        System.out.println("  public User getUser(@RequestParam String id) { ... }");
        System.out.println();
        System.out.println("Good code:");
        System.out.println("  @GetMapping(\"/users/{id}\")");
        System.out.println("  public User getUser(@PathVariable Long id) { ... }");
        System.out.println();
        System.out.println("Why the fix is better:");
        System.out.println("- the route matches the resource");
        System.out.println("- HTTP semantics are obvious");
        System.out.println("- security and caching become easier to standardize");
        System.out.println("Where to fix: route design, controller layer, and service boundaries.");
    }
}
