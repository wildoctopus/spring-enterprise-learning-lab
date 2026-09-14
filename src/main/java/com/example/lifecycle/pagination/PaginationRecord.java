package com.example.lifecycle.pagination;

import java.time.Instant;

public record PaginationRecord(long id, String tenantId, String payload, Instant createdAt) {
}
