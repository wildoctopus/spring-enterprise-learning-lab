package com.example.lifecycle.rest.dto;

import java.util.List;
import java.util.Map;

public final class RestApiDtos {

    private RestApiDtos() {
    }

    public record PathQueryObservation(String badRoute, String goodRoute, String filteredCollection,
                                       String problem, String fix, String whereToFix) {
    }

    public record UserObservation(String route, String resourceType, String resourceId, String status,
                                  String problem, String fix, String whereToFix) {
    }

    public record UserCollectionObservation(String route, List<String> users, String role, String status,
                                            int size, String problem, String fix, String whereToFix) {
    }

    public record CreateOrderRequest(Long customerId, List<Integer> items) {
    }

    public record OrderObservation(String orderId, CreateOrderRequest request, String status,
                                   String problem, String fix, String whereToFix) {
    }

    public record BalanceObservation(int before, int after, String status, String problem,
                                     String fix, String whereToFix) {
    }

    public record DeleteObservation(String resourceId, String status, String problem, String fix,
                                    String whereToFix) {
    }

    public record QueryObservation(Map<String, String> filters, String problem, String fix,
                                   String whereToFix) {
    }

    public record PayloadObservation(int payloadSize, int maxAllowedBytes, String status,
                                     String problem, String fix, String whereToFix) {
    }

    public record BadPatchRequest(Integer delta) {
    }

    public record GoodPatchRequest(Integer newBalance) {
    }
}
