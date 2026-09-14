package com.example.lifecycle.pagination;

public record UnexpectedProblem(String code, String message) implements ApiProblem {
}
