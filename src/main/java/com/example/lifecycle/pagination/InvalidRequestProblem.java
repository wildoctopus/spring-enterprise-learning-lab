package com.example.lifecycle.pagination;

public record InvalidRequestProblem(String code, String message) implements ApiProblem {
}
