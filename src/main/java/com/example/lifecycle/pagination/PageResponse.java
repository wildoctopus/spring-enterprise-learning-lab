package com.example.lifecycle.pagination;

import java.util.List;

public record PageResponse<T>(List<T> items, String nextCursor, boolean hasMore, int limit) {

    public PageResponse {
        items = List.copyOf(items);
    }
}
