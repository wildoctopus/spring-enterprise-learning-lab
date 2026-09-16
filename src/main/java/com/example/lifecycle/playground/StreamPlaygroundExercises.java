package com.example.lifecycle.playground;

import com.example.lifecycle.streams.StreamEmployee;
import java.util.Comparator;
import java.util.List;

public final class StreamPlaygroundExercises {

    private StreamPlaygroundExercises() {
    }

    /** Deliberately incomplete: equal salaries keep the first employee. */
    public static StreamEmployee badTieBreaker(List<StreamEmployee> employees) {
        return employees.stream().max(Comparator.comparingInt(StreamEmployee::salary)).orElse(null);
    }

    /** Corrected: salary wins, then the business name rule decides the tie. */
    public static StreamEmployee explicitTieBreaker(List<StreamEmployee> employees) {
        return employees.stream()
                .max(Comparator.comparingInt(StreamEmployee::salary)
                        .thenComparing(StreamEmployee::name))
                .orElse(null);
    }
}