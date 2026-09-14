package com.example.lifecycle.pagination;

import java.time.Instant;

public record ApiErrorResponse(String code, String message, Instant timestamp) {

    public static ApiErrorResponse from(ApiProblem problem) {
        // Java 21 pattern matching makes the mapping exhaustive and keeps
        // HTTP error policy separate from exception classes.
        return switch (problem) {
            case InvalidRequestProblem request -> new ApiErrorResponse(
                    request.code(), request.message(), Instant.now());
            case InvalidCursorProblem cursor -> new ApiErrorResponse(
                    cursor.code(), cursor.message(), Instant.now());
            case UnexpectedProblem unexpected -> new ApiErrorResponse(
                    unexpected.code(), unexpected.message(), Instant.now());
        };
    }
}
