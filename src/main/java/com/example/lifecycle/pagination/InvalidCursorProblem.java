package com.example.lifecycle.pagination;

public record InvalidCursorProblem(String code, String message) implements ApiProblem {
}
