package com.example.lifecycle.rest.controller;

import com.example.lifecycle.rest.dto.RestApiDtos;
import com.example.lifecycle.rest.service.RestApiDemoService;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/playground/rest")
public class RestApiDemoController {

    private final RestApiDemoService service;

    public RestApiDemoController(RestApiDemoService service) {
        this.service = service;
    }

    @GetMapping("/path-vs-query")
    public ResponseEntity<RestApiDtos.PathQueryObservation> pathVsQuery() {
        return ResponseEntity.ok(service.pathVsQuery());
    }

    @GetMapping("/users/getUser")
    public ResponseEntity<RestApiDtos.UserObservation> badGetUser(@RequestParam String id) {
        return ResponseEntity.ok(service.readBadUser(id));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<RestApiDtos.UserObservation> goodGetUser(@PathVariable String id) {
        return ResponseEntity.ok(service.readGoodUser(id));
    }

    @GetMapping("/users")
    public ResponseEntity<RestApiDtos.UserCollectionObservation> filteredUsers(
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(service.filteredUsers(role, status, size));
    }

    @PostMapping("/orders/bad-create")
    public ResponseEntity<RestApiDtos.OrderObservation> badCreateOrder(
            @RequestBody RestApiDtos.CreateOrderRequest request) {
        return ResponseEntity.ok(service.createBadOrder(request));
    }

    @PostMapping("/orders/good-create")
    public ResponseEntity<RestApiDtos.OrderObservation> goodCreateOrder(
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
            @RequestBody RestApiDtos.CreateOrderRequest request) {
        return ResponseEntity.ok(service.createGoodOrder(idempotencyKey, request));
    }

    @PatchMapping("/balance/bad")
    public ResponseEntity<RestApiDtos.BalanceObservation> badPatchBalance(
            @RequestBody RestApiDtos.BadPatchRequest request) {
        return ResponseEntity.ok(service.patchBadBalance(request.delta()));
    }

    @PatchMapping("/balance/good")
    public ResponseEntity<RestApiDtos.BalanceObservation> goodPatchBalance(
            @RequestBody RestApiDtos.GoodPatchRequest request) {
        return ResponseEntity.ok(service.patchGoodBalance(request.newBalance()));
    }

    @DeleteMapping("/users/{id}/bad-delete")
    public ResponseEntity<RestApiDtos.DeleteObservation> badDelete(@PathVariable String id) {
        return ResponseEntity.ok(service.deleteBad(id));
    }

    @DeleteMapping("/users/{id}/good-delete")
    public ResponseEntity<RestApiDtos.DeleteObservation> goodDelete(@PathVariable String id) {
        return ResponseEntity.ok(service.deleteGood(id));
    }

    @GetMapping("/many-query-params")
    public ResponseEntity<RestApiDtos.QueryObservation> manyQueryParams(
            @RequestParam Map<String, String> filters) {
        return ResponseEntity.ok(service.manyQueryParams(filters));
    }

    @PostMapping("/large-payload")
    public ResponseEntity<RestApiDtos.PayloadObservation> largePayload(
            @RequestBody String payload,
            @RequestParam(defaultValue = "false") boolean allowLarge) {
        RestApiDtos.PayloadObservation observation = service.largePayload(payload);
        if (!allowLarge && "rejected".equals(observation.status())) {
            return ResponseEntity.status(413).body(observation);
        }
        return ResponseEntity.ok(observation);
    }
}
