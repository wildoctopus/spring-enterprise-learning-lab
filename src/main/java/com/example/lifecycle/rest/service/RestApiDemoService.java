package com.example.lifecycle.rest.service;

import com.example.lifecycle.rest.dto.RestApiDtos;
import com.example.lifecycle.rest.repository.RestApiDemoRepository;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class RestApiDemoService {

    private static final int MAX_PAYLOAD_BYTES = 20_000;

    private final RestApiDemoRepository repository;

    public RestApiDemoService(RestApiDemoRepository repository) {
        this.repository = repository;
    }

    public RestApiDtos.PathQueryObservation pathVsQuery() {
        return new RestApiDtos.PathQueryObservation(
                "/playground/rest/users/getUser?id=123", "/playground/rest/users/123",
                "/playground/rest/users?role=admin&status=active",
                "Action-based routes hide resource intent and mix identity and filters into one contract.",
                "Use path params for identity and query params for filtering.",
                "Controller route design and API contract documentation.");
    }

    public RestApiDtos.UserObservation readBadUser(String id) {
        return user("/playground/rest/users/getUser?id=" + id, id, "bad",
                "This action-style route hides resource intent behind an operation name.",
                "Use GET /playground/rest/users/{id} and keep identity in the path.",
                "Controller mapping and REST contract design.");
    }

    public RestApiDtos.UserObservation readGoodUser(String id) {
        return user("/playground/rest/users/" + id, id, "good",
                "This is a clean resource lookup with predictable semantics.",
                "Path param is the correct place for resource identity.",
                "Route design and resource modeling.");
    }

    private RestApiDtos.UserObservation user(String route, String id, String status, String problem,
                                             String fix, String whereToFix) {
        return new RestApiDtos.UserObservation(route, "resource-lookup", id, status, problem, fix, whereToFix);
    }

    public RestApiDtos.UserCollectionObservation filteredUsers(String role, String status, int size) {
        return new RestApiDtos.UserCollectionObservation(
                "/playground/rest/users?role=" + role + "&status=" + status + "&size=" + size,
                List.of("u-101", "u-102", "u-103"), role, status, size,
                "This is fine because the params narrow a collection, not identify one object.",
                "Keep filters in query params and cap page size.",
                "Controller validation and pagination limits.");
    }

    public RestApiDtos.OrderObservation createBadOrder(RestApiDtos.CreateOrderRequest request) {
        return order(repository.nextOrderId("BAD"), request, "created",
                "The client may retry after a timeout and create a second order with the same payload.",
                "Use an Idempotency-Key and dedupe outstanding create requests.",
                "Create service, dedupe store, and database unique index.");
    }

    public RestApiDtos.OrderObservation createGoodOrder(String key, RestApiDtos.CreateOrderRequest request) {
        if (key == null || key.isBlank()) {
            return order(null, request, "rejected",
                    "The endpoint requires a stable Idempotency-Key for create semantics.",
                    "Add Idempotency-Key to create requests and reject duplicates.",
                    "API contract and create endpoint validation.");
        }
        if (!repository.claimIdempotencyKey(key)) {
            return order(null, request, "duplicate-suppressed",
                    "The retry was detected and the original result was not created again.",
                    "Return the original stored response for the same key in production.",
                    "Create service, idempotency repository, and database uniqueness.");
        }
        return order(repository.nextOrderId("GOOD"), request, "created",
                "The request was accepted once and will not create a second order with the same key.",
                "Use a stable key and persist the complete original response for retries.",
                "Create service and idempotency repository.");
    }

    private RestApiDtos.OrderObservation order(String id, RestApiDtos.CreateOrderRequest request, String status,
                                               String problem, String fix, String whereToFix) {
        return new RestApiDtos.OrderObservation(id, request, status, problem, fix, whereToFix);
    }

    public RestApiDtos.BalanceObservation patchBadBalance(Integer delta) {
        int oldValue = repository.currentBalance();
        int newValue = oldValue + delta;
        repository.setBalance(newValue);
        return new RestApiDtos.BalanceObservation(oldValue, newValue, "patched",
                "Repeated PATCH calls re-apply the same delta and drift the final state.",
                "Set the target final value explicitly or guard the update by version.",
                "Update service logic and optimistic locking.");
    }

    public RestApiDtos.BalanceObservation patchGoodBalance(Integer newBalance) {
        int oldValue = repository.currentBalance();
        repository.setBalance(newBalance);
        return new RestApiDtos.BalanceObservation(oldValue, newBalance, "final-state-set",
                "Retries are safe because the final state is set explicitly, not incremented again.",
                "Use final-state semantics for idempotent patch/update flows.",
                "Update semantics and version-based conflict handling.");
    }

    public RestApiDtos.DeleteObservation deleteBad(String id) {
        boolean firstDelete = repository.markDeleted(id);
        return new RestApiDtos.DeleteObservation(id, firstDelete ? "deleted" : "still-not-deterministic",
                firstDelete ? "The first delete succeeded, but a retry without a consistent contract may trigger duplicate cleanup or inconsistent responses."
                        : "The second delete is not consistently handled and can hide the real retry problem.",
                "Treat repeated delete calls as a safe retry with a stable response.",
                "Delete implementation and business-side cleanup rules.");
    }

    public RestApiDtos.DeleteObservation deleteGood(String id) {
        boolean firstDelete = repository.markDeleted(id);
        return new RestApiDtos.DeleteObservation(id, firstDelete ? "deleted" : "already-deleted",
                "Retry-safe behavior: repeated delete calls return a stable result and do not trigger new side effects.",
                "Keep delete semantics idempotent and deterministic.",
                "Delete idempotency contract and cleanup logic.");
    }

    public RestApiDtos.QueryObservation manyQueryParams(Map<String, String> filters) {
        return new RestApiDtos.QueryObservation(filters,
                "A large and dynamic filter map can create huge SQL WHERE clauses and poor query plans.",
                "Limit allowed fields, cap page size, and move complex filters into a search payload.",
                "Controller validation, query builder, and DB index strategy.");
    }

    public RestApiDtos.PayloadObservation largePayload(String payload) {
        int size = payload.getBytes(StandardCharsets.UTF_8).length;
        String status = size > MAX_PAYLOAD_BYTES ? "rejected" : "accepted";
        return new RestApiDtos.PayloadObservation(size, MAX_PAYLOAD_BYTES, status,
                size > MAX_PAYLOAD_BYTES ? "The payload is too large and the request was rejected before processing."
                        : "The payload is within the configured policy limit.",
                "Validate request size and reject early or split payloads into chunks.",
                "Request validation and gateway payload limits.");
    }
}
