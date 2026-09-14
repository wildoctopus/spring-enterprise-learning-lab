package com.example.lifecycle.streams;

import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(30)
public class StreamDemoRunner implements CommandLineRunner {

    @Override
    public void run(String... args) {
        List<StreamEmployee> employees = List.of(
                new StreamEmployee("Ada", "Engineering", 150_000),
                new StreamEmployee("Grace", "Engineering", 170_000),
                new StreamEmployee("Lin", "Risk", 160_000));

        System.out.println("\n--- Java 8+ Streams coding examples ---");
        System.out.println("Word frequency: " + StreamCodingExamples.wordFrequency(
                List.of("Java", "Spring", "java")));
        System.out.println("First non-repeated character: "
                + StreamCodingExamples.firstNonRepeatedCharacter("swiss"));
        System.out.println("Duplicate numbers: "
                + StreamCodingExamples.duplicateNumbers(List.of(1, 2, 2, 3, 3, 3)));
        System.out.println("Flattened numbers: "
                + StreamCodingExamples.flatten(List.of(List.of(1, 2), List.of(3, 4))));
        System.out.println("Highest paid by department: "
                + StreamCodingExamples.highestPaidByDepartment(employees));
        System.out.println("Top 2 distinct numbers: "
                + StreamCodingExamples.topDistinctNumbers(List.of(4, 7, 7, 2, 9), 2));
        System.out.println("Even/odd partition: "
                + StreamCodingExamples.partitionEvenAndOdd(List.of(1, 2, 3, 4)));
        System.out.println("Joined names: " + StreamCodingExamples.joinNames(employees));
        System.out.println("Second highest distinct: "
                + StreamCodingExamples.secondHighestDistinct(List.of(9, 7, 7, 4)));
        System.out.println("Grouped anagrams: "
                + StreamCodingExamples.groupAnagrams(List.of("eat", "tea", "tan", "ate")));
        System.out.println("Average salary by department: "
                + StreamCodingExamples.averageSalaryByDepartment(employees));
        System.out.println("Even squares sum: "
                + StreamCodingExamples.sumOfEvenSquares(List.of(1, 2, 3, 4)));
    }
}
