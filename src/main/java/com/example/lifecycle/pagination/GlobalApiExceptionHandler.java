package com.example.lifecycle.pagination;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.example.lifecycle.pagination.PaginationExceptions.InvalidCursorException;
import static com.example.lifecycle.pagination.PaginationExceptions.InvalidPaginationRequestException;

@RestControllerAdvice
public class GlobalApiExceptionHandler {

    @ExceptionHandler(InvalidPaginationRequestException.class)
    ResponseEntity<ApiErrorResponse> handleInvalidRequest(InvalidPaginationRequestException exception) {
        return response(new InvalidRequestProblem("INVALID_PAGINATION_REQUEST", exception.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidCursorException.class)
    ResponseEntity<ApiErrorResponse> handleInvalidCursor(InvalidCursorException exception) {
        return response(new InvalidCursorProblem("INVALID_CURSOR", exception.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ApiErrorResponse> handleUnexpected(Exception exception) {
        // Do not expose stack traces, SQL, or vendor details to API clients.
        return response(new UnexpectedProblem("INTERNAL_ERROR", "Unexpected server error"),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ApiErrorResponse> response(ApiProblem problem, HttpStatus status) {
        return ResponseEntity.status(status).body(ApiErrorResponse.from(problem));
    }
}
