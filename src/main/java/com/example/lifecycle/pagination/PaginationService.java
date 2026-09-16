package com.example.lifecycle.pagination;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.lifecycle.pagination.PaginationExceptions.InvalidCursorException;
import static com.example.lifecycle.pagination.PaginationExceptions.InvalidPaginationRequestException;

@Service
public class PaginationService {

    private static final int MAX_LIMIT = 100;
    private final JdbcClient jdbcClient;

    public PaginationService(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Transactional(readOnly = true)
    public PageResponse<PaginationRecord> offsetPage(String tenantId, int offset, int limit) {
        validate(tenantId, offset, limit);
        List<PaginationRecord> items = jdbcClient.sql("""
                select id, tenant_id, payload, created_at
                from pagination_records
                where tenant_id = :tenantId
                order by id
                offset :offset rows fetch first :limit rows only
                """)
                .param("tenantId", tenantId)
                .param("offset", offset)
                .param("limit", limit)
                .query((row, rowNumber) -> map(row))
                .list();
        boolean hasMore = items.size() == limit && hasRowAfterOffset(tenantId, offset + limit);
        return new PageResponse<>(items, null, hasMore, limit);
    }

    @Transactional(readOnly = true)
    public PageResponse<PaginationRecord> cursorPage(String tenantId, String cursor, int limit) {
        validate(tenantId, 0, limit);
        CursorPosition position = decodeCursor(cursor);
        if (position.tenantId() != null && !position.tenantId().equals(tenantId)) {
            throw new InvalidCursorException("cursor belongs to another tenant", null);
        }
        long lastSeenId = position.lastSeenId();
        List<PaginationRecord> items = jdbcClient.sql("""
                select id, tenant_id, payload, created_at
                from pagination_records
                where tenant_id = :tenantId and id > :lastSeenId
                order by id
                fetch first :limit rows only
                """)
                .param("tenantId", tenantId)
                .param("lastSeenId", lastSeenId)
                .param("limit", limit + 1)
                .query((row, rowNumber) -> map(row))
                .list();
        boolean hasMore = items.size() > limit;
        if (hasMore) {
            items = items.subList(0, limit);
        }
        String nextCursor = hasMore ? encodeCursor(tenantId, items.get(items.size() - 1).id()) : null;
        return new PageResponse<>(items, nextCursor, hasMore, limit);
    }

    public String firstCursor() {
        return encodeCursor(null, 0);
    }

    private boolean hasRowAfterOffset(String tenantId, int nextOffset) {
        return jdbcClient.sql("""
                select id
                from pagination_records
                where tenant_id = :tenantId
                order by id
                offset :nextOffset rows fetch first 1 rows only
                """)
                .param("tenantId", tenantId)
                .param("nextOffset", nextOffset)
                .query(Long.class)
                .optional()
                .isPresent();
    }

    private PaginationRecord map(java.sql.ResultSet row) throws java.sql.SQLException {
        return new PaginationRecord(row.getLong("id"), row.getString("tenant_id"),
                row.getString("payload"), row.getTimestamp("created_at").toInstant());
    }

    private void validate(String tenantId, int offset, int limit) {
        if (tenantId == null || tenantId.isBlank()) {
            throw new InvalidPaginationRequestException("tenantId is required");
        }
        if (offset < 0) {
            throw new InvalidPaginationRequestException("offset cannot be negative");
        }
        if (limit < 1 || limit > MAX_LIMIT) {
            throw new InvalidPaginationRequestException("limit must be between 1 and " + MAX_LIMIT);
        }
    }

    private CursorPosition decodeCursor(String cursor) {
        if (cursor == null || cursor.isBlank()) {
            return new CursorPosition(null, 0);
        }
        try {
            String decoded = new String(Base64.getUrlDecoder().decode(cursor), StandardCharsets.UTF_8);
            String[] parts = decoded.split(":", -1);
            long value = Long.parseLong(parts.length == 2 ? parts[1] : decoded);
            if (value < 0 || parts.length > 2) {
                throw new NumberFormatException("invalid cursor");
            }
            return new CursorPosition(parts.length == 2 && !parts[0].isBlank() ? parts[0] : null, value);
        } catch (IllegalArgumentException exception) {
            throw new InvalidCursorException("cursor is not a valid opaque position", exception);
        }
    }

    private String encodeCursor(String tenantId, long id) {
        String value = tenantId == null ? Long.toString(id) : tenantId + ":" + id;
        return Base64.getUrlEncoder().withoutPadding().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    private record CursorPosition(String tenantId, long lastSeenId) {
    }
}
