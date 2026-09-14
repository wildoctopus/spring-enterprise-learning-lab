package com.example.lifecycle.pagination;

public final class PaginationExceptions {

    private PaginationExceptions() {
    }

    public static final class InvalidPaginationRequestException extends RuntimeException {
        public InvalidPaginationRequestException(String message) {
            super(message);
        }
    }

    public static final class InvalidCursorException extends RuntimeException {
        public InvalidCursorException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
