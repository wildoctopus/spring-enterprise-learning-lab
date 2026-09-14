package com.example.lifecycle.pagination;

public sealed interface ApiProblem
        permits InvalidRequestProblem, InvalidCursorProblem, UnexpectedProblem {

    String code();

    String message();
}
