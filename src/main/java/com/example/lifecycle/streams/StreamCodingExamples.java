package com.example.lifecycle.streams;

import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class StreamCodingExamples {

    private StreamCodingExamples() {
    }

    public static Map<String, Long> wordFrequency(List<String> words) {
        // groupingBy creates the buckets; counting is a downstream collector.
        return words.stream()
                .filter(Objects::nonNull)
                .map(String::toLowerCase)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
    }

    public static Character firstNonRepeatedCharacter(String value) {
        // LinkedHashMap is intentional: HashMap would lose encounter order.
        Map<Character, Long> counts = value.chars()
                .mapToObj(character -> (char) character)
                .collect(Collectors.groupingBy(Function.identity(), LinkedHashMap::new,
                        Collectors.counting()));
        return counts.entrySet().stream()
                .filter(entry -> entry.getValue() == 1)
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }

    public static List<Integer> duplicateNumbers(List<Integer> numbers) {
        return numbers.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet().stream()
                .filter(entry -> entry.getValue() > 1)
                .map(Map.Entry::getKey)
                .sorted()
                .collect(Collectors.toList());
    }

    public static List<Integer> flatten(List<List<Integer>> nestedNumbers) {
        return nestedNumbers.stream()
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    public static Map<String, List<StreamEmployee>> groupByDepartment(List<StreamEmployee> employees) {
        return employees.stream()
                .collect(Collectors.groupingBy(StreamEmployee::department));
    }

    public static Map<String, StreamEmployee> highestPaidByDepartment(List<StreamEmployee> employees) {
        return employees.stream()
                .collect(Collectors.toMap(StreamEmployee::department, Function.identity(),
                        BinaryOperators.maxBySalary()));
    }

    public static List<Integer> topDistinctNumbers(List<Integer> numbers, int limit) {
        if (limit < 0) {
            throw new IllegalArgumentException("limit cannot be negative");
        }
        return numbers.stream()
                .distinct()
                .sorted(Comparator.reverseOrder())
                .limit(limit)
                .collect(Collectors.toList());
    }

    public static Map<Boolean, List<Integer>> partitionEvenAndOdd(List<Integer> numbers) {
        return numbers.stream()
                .collect(Collectors.partitioningBy(number -> number % 2 == 0));
    }

    public static String joinNames(List<StreamEmployee> employees) {
        return employees.stream()
                .map(StreamEmployee::name)
                .sorted()
                .collect(Collectors.joining(", "));
    }

    public static List<String> distinctUppercaseSorted(List<String> values) {
        return values.stream()
                .filter(Objects::nonNull)
                .map(String::toUpperCase)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    public static Set<Integer> distinctWithExplicitSet(List<Integer> numbers) {
        // Useful when explaining stateful distinct logic; prefer distinct()
        // unless the set itself is part of the required result.
        return numbers.stream().collect(Collectors.toCollection(HashSet::new));
    }

        public static Optional<Integer> secondHighestDistinct(List<Integer> numbers) {
        // Optional is honest about inputs with fewer than two distinct values;
        // using sorted().skip(1).findFirst().get() would fail at runtime.
        return numbers.stream()
            .distinct()
            .sorted(Comparator.reverseOrder())
            .skip(1)
            .findFirst();
        }

        public static Optional<Integer> nthHighestDistinct(List<Integer> numbers, int rank) {
        if (rank < 1) {
            throw new IllegalArgumentException("rank must be positive");
        }
        return numbers.stream()
            .distinct()
            .sorted(Comparator.reverseOrder())
            .skip(rank - 1L)
            .findFirst();
        }

        public static Optional<String> longestString(List<String> values) {
        return values.stream()
            .filter(Objects::nonNull)
            // Tie-breaking is explicit: retain the first longest value.
                .max(Comparator.comparingInt(String::length));
        }

        public static Map<String, List<String>> groupAnagrams(List<String> words) {
        // Sorting letters creates a canonical key: "eat", "tea", and "ate"
        // all land in the same group.
        return words.stream()
            .filter(Objects::nonNull)
            .collect(Collectors.groupingBy(StreamCodingExamples::anagramKey));
        }

        public static Map<String, Double> averageSalaryByDepartment(List<StreamEmployee> employees) {
        return employees.stream()
            .collect(Collectors.groupingBy(StreamEmployee::department,
                Collectors.averagingInt(StreamEmployee::salary)));
        }

        public static Map<String, StreamEmployee> highestPaidByDepartmentThenName(
            List<StreamEmployee> employees) {
        // A comparator chain makes the business tie-break rule visible.
        Comparator<StreamEmployee> salaryThenName = Comparator
            .comparingInt(StreamEmployee::salary)
            .thenComparing(StreamEmployee::name);
        return employees.stream()
            .collect(Collectors.toMap(StreamEmployee::department, Function.identity(),
                (left, right) -> salaryThenName.compare(left, right) >= 0 ? left : right));
        }

        public static int sumOfEvenSquares(List<Integer> numbers) {
        // mapToInt avoids Integer boxing during numeric aggregation.
        return numbers.stream()
            .filter(number -> number % 2 == 0)
            .mapToInt(number -> number * number)
            .sum();
        }

        public static Optional<StreamEmployee> findEmployeeByName(
            List<StreamEmployee> employees, String name) {
        return employees.stream()
            .filter(employee -> employee.name().equalsIgnoreCase(name))
            .findFirst();
        }

        private static String anagramKey(String word) {
        return word.toLowerCase().chars()
            .sorted()
            .mapToObj(character -> String.valueOf((char) character))
            .collect(Collectors.joining());
        }

    private static final class BinaryOperators {

        private BinaryOperators() {
        }

        static java.util.function.BinaryOperator<StreamEmployee> maxBySalary() {
            return (left, right) -> left.salary() >= right.salary() ? left : right;
        }
    }
}
