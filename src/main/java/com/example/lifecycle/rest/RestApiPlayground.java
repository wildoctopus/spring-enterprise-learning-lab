package com.example.lifecycle.rest;

import java.util.List;

public final class RestApiPlayground {

    private RestApiPlayground() {
    }

    public static List<ApiScenario> scenarios() {
        return List.of(
                new ApiScenario(
                        "Path parameter vs query parameter",
                        "GET /users/101\nGET /users?role=admin&status=active",
                        "We use a path parameter when the value identifies a single resource and a query parameter when the value filters a collection.",
                        "A path parameter is easier to read, more semantic, and better matches resource identity. Query parameters let us narrow a broad result set without changing the resource meaning.",
                        "A mixed design like /users/101?status=active makes identity and selection blur together. The API becomes harder to reason about and harder to authorize consistently.",
                        "Use /users/101 for one user and /users?role=admin&status=active for filtered collection results.",
                        "Path param = which resource. Query param = what subset of resources."),
                new ApiScenario(
                        "Many query parameters can create real system pressure",
                        "GET /users?role=admin&status=active&country=in&city=bengaluru&department=eng&page=2&size=100",
                        "We are filtering a collection with several optional values.",
                        "This is flexible and works well for dashboards and admin screens, but too many filters can generate very large queries and poor execution plans.",
                        "A large filter set may lead to slow SQL, poor DB performance, long URLs, and gateway limits. One badly shaped query can drag down the system.",
                        "Limit allowed filters, validate values, enforce page-size caps, and prefer a POST /search payload for complex filters.",
                        "Flexibility is powerful, but unbounded queries turn into performance and security risk."),
                new ApiScenario(
                        "Deep nested path params can become brittle",
                        "GET /companies/10/users/22/orders/5",
                        "We are modeling a real hierarchy where an order belongs to a user and a company.",
                        "Nested paths reflect ownership and hierarchy, which is useful when the relationship is truly part of the domain.",
                        "If the nesting becomes too deep, the API becomes hard to read, harder to evolve, and more tightly coupled to path structure than business meaning.",
                        "Keep hierarchy shallow and intentional. Prefer /orders/5 or /users/22/orders instead of endlessly nested routes.",
                        "Hierarchy is useful, but deep nesting creates brittle contracts."),
                new ApiScenario(
                        "Lists in path params are usually a design smell",
                        "GET /users/1,2,3",
                        "A developer may try to pass multiple ids in one URL for bulk fetch or delete.",
                        "It looks compact, but this is not a clean REST contract and parsing rules differ across technologies.",
                        "The URL becomes hard to validate, hard to log, and difficult to support. Bulk operations are better represented as a collection endpoint or a request body.",
                        "Prefer GET /users?ids=1,2,3, POST /users/search, or POST /users/bulk-delete.",
                        "Lists in paths are easy to write but expensive to maintain and reason about."),
                new ApiScenario(
                        "Large payloads can starve the service",
                        "POST /orders\n{\n  \"items\": [ ... hundreds of entries ... ],\n  \"customer\": { ... large nested data ... }\n}",
                        "The server accepts complex create/update payloads.",
                        "Complex payloads are common in real business flows, but large request bodies consume network, memory, CPU, and DB effort.",
                        "A payload that is acceptable in a local test can cause timeouts, memory spikes, and gateway rejection under real traffic.",
                        "Set payload-size limits, validate early, batch large operations, and move very large files to storage-backed upload flows.",
                        "A working payload in dev is not automatically a production-safe API contract."),
                new ApiScenario(
                        "Long query strings can fail at the network boundary",
                        "GET /products?category=books&brand=a&brand=b&brand=c&...&veryLongFilter=...",
                        "We are passing a big set of optional filters in the URL.",
                        "This is convenient for straightforward searches, but long URLs hit browser, proxy, cache, and log limits.",
                        "The request may be truncated, rejected, or logged in ways that expose sensitive data. Performance can degrade when the request becomes huge and arbitrary.",
                        "Keep GET endpoints small and predictable; use POST /search for large or complex filter sets.",
                        "GET is simple, but not always the right choice for large filter payloads."),
                new ApiScenario(
                        "POST retry without idempotency creates duplicates",
                        "POST /orders\nBody: {\"customerId\": 42, \"items\": [101, 202]}",
                        "The client creates an order with POST, then a timeout occurs after the request is processed but before the response arrives.",
                        "POST is not idempotent by default. A retry may create another order or charge a second payment.",
                        "Duplicate records, duplicate charges, and reconciliation issues appear. Support teams must clean up the mess manually.",
                        "Use an Idempotency-Key header, enforce uniqueness at the database, and deduplicate business requests by a stable key.",
                        "Create flows are only safe when retries are explicitly designed to be safe."),
                new ApiScenario(
                        "PUT is idempotent; PATCH is only idempotent when the semantics are safe",
                        "PUT /users/101\n{\"name\": \"Alice\", \"role\": \"ADMIN\"}\n\nPATCH /users/101\n{\"balance\": 50}",
                        "We are replacing the full user state with PUT and partially modifying it with PATCH.",
                        "PUT is naturally idempotent because repeating the same payload ends in the same final state. PATCH can be safe or unsafe depending on what it does.",
                        "If PATCH increments counters or appends values, repeated calls cause repeated side effects. A client retry can create a completely different final state.",
                        "Use PUT for full-state replacement or idempotent set operations. Only use PATCH when the semantics are clearly repeat-safe or deduplicated.",
                        "The business meaning matters more than the HTTP verb alone."),
                new ApiScenario(
                        "DELETE should be consistent on retries",
                        "DELETE /users/101\nDELETE /users/101",
                        "A client deletes a resource and retries after a timeout.",
                        "DELETE is usually idempotent, but only if the system returns a consistent result on repeat calls.",
                        "If the second delete creates a new audit entry, fails unpredictably, or triggers extra side effects, the API is not truly retry-safe.",
                        "Return a consistent outcome: 204 on success or 404 if already deleted, while avoiding duplicate side effects.",
                        "A delete contract is reliable only when repeated calls are harmless."),
                new ApiScenario(
                        "Action-style APIs hide intent and break standard semantics",
                        "GET /getUser?id=123\nPOST /saveOrder",
                        "The API is exposing actions instead of resources.",
                        "This is easy to write, but it breaks standard HTTP semantics and makes the contract inconsistent across services.",
                        "Caching, security, client understanding, and documentation all become harder. Teams also drift into custom conventions that become impossible to govern.",
                        "Use resource-driven endpoints, then rely on HTTP methods to communicate intent:\nGET /users/123\nPOST /orders",
                        "Action names make the API procedural; resource names make it predictable."),
                new ApiScenario(
                        "Mixing identity and filtering in one URL creates ambiguity",
                        "GET /users/123?status=active",
                        "A developer wants to combine object identity and view selection in one URL.",
                        "It looks convenient, but it mixes identity and filter concerns, which makes the contract harder to validate and harder to reason about.",
                        "Authorization rules become muddled, and it is unclear whether the filter is business logic or identity semantics. Different consumers may interpret the request differently.",
                        "Keep identity in path parameters and filtering in query parameters. If filtering changes the resource itself, model a separate state or resource.",
                        "Ambiguity is more expensive than a slightly longer URL."),
                new ApiScenario(
                        "Idempotency is not optional in business-critical flows",
                        "POST /payments\nPOST /payments\nPOST /payments",
                        "A payment flow is retried after a timeout or duplicate submission occurs from the client side.",
                        "Without idempotency, a single user experience becomes multiple charges, multiple ledger entries, and reconciliation debt.",
                        "The system can create duplicate financial events, customer trust can be damaged, and support teams inherit expensive cleanup work.",
                        "Use a stable business key, a deduplication store, and unique constraints. Design for retries before the system goes live.",
                        "In production, idempotency is not a theoretical rule; it is a reliability contract."));
    }

    public record ApiScenario(String question, String code, String what, String why, String breakIt,
                             String bestApproach, String leadInsight) {
    }
}
