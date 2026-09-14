package com.example.lifecycle;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.lifecycle.streams.StreamCodingExamples;
import com.example.lifecycle.streams.StreamEmployee;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

class StreamCodingExamplesTests {

    @Test
    void countsWordsIgnoringCaseAndNulls() {
                assertThat(StreamCodingExamples.wordFrequency(Arrays.asList("Java", null, "java", "Spring")))
                .containsEntry("java", 2L)
                .containsEntry("spring", 1L);
    }

    @Test
    void preservesEncounterOrderForFirstNonRepeatedCharacter() {
        assertThat(StreamCodingExamples.firstNonRepeatedCharacter("swiss"))
                .isEqualTo('w');
        assertThat(StreamCodingExamples.firstNonRepeatedCharacter("aabb"))
                .isNull();
    }

    @Test
    void findsDuplicatesWithoutReturningThemMoreThanOnce() {
        assertThat(StreamCodingExamples.duplicateNumbers(List.of(4, 2, 4, 2, 2, 7)))
                .containsExactly(2, 4);
    }

    @Test
    void flattensGroupsAndPartitionsValues() {
        assertThat(StreamCodingExamples.flatten(List.of(List.of(1, 2), List.of(3))))
                .containsExactly(1, 2, 3);
        assertThat(StreamCodingExamples.partitionEvenAndOdd(List.of(1, 2, 3, 4)))
                .containsEntry(true, List.of(2, 4))
                .containsEntry(false, List.of(1, 3));
    }

    @Test
    void selectsHighestEmployeePerDepartment() {
        List<StreamEmployee> employees = List.of(
                new StreamEmployee("Ada", "Engineering", 150_000),
                new StreamEmployee("Grace", "Engineering", 170_000),
                new StreamEmployee("Lin", "Risk", 160_000));

        assertThat(StreamCodingExamples.highestPaidByDepartment(employees))
                .containsEntry("Engineering", employees.get(1))
                .containsEntry("Risk", employees.get(2));
    }

    @Test
    void ordersDistinctValuesAndRejectsNegativeLimit() {
        assertThat(StreamCodingExamples.topDistinctNumbers(List.of(4, 7, 7, 2, 9), 3))
                .containsExactly(9, 7, 4);
        assertThatThrownBy(() -> StreamCodingExamples.topDistinctNumbers(List.of(1), -1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void joinsNamesAndNormalizesDistinctValues() {
        List<StreamEmployee> employees = List.of(
                new StreamEmployee("Grace", "Engineering", 170_000),
                new StreamEmployee("Ada", "Engineering", 150_000));

        assertThat(StreamCodingExamples.joinNames(employees)).isEqualTo("Ada, Grace");
        assertThat(StreamCodingExamples.distinctUppercaseSorted(List.of("java", "JAVA", "go")))
                .containsExactly("GO", "JAVA");
    }

    @Test
    void handlesRankedValuesWithOptionalAndValidation() {
        List<Integer> numbers = List.of(9, 7, 7, 4, 2);

        assertThat(StreamCodingExamples.secondHighestDistinct(numbers)).contains(7);
        assertThat(StreamCodingExamples.nthHighestDistinct(numbers, 3)).contains(4);
        assertThat(StreamCodingExamples.secondHighestDistinct(List.of(5))).isEmpty();
        assertThatThrownBy(() -> StreamCodingExamples.nthHighestDistinct(numbers, 0))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void groupsAnagramsAndIgnoresNullLongestValue() {
        assertThat(StreamCodingExamples.groupAnagrams(List.of("eat", "tea", "tan", "ate")))
                .containsEntry("aet", List.of("eat", "tea", "ate"));
        assertThat(StreamCodingExamples.longestString(Arrays.asList("go", null, "spring")))
                .contains("spring");
    }

    @Test
    void calculatesAggregatesAndUsesExplicitTieBreaker() {
        List<StreamEmployee> employees = List.of(
                new StreamEmployee("Ada", "Engineering", 150_000),
                new StreamEmployee("Grace", "Engineering", 150_000),
                new StreamEmployee("Lin", "Risk", 160_000));

        assertThat(StreamCodingExamples.averageSalaryByDepartment(employees))
                .containsEntry("Engineering", 150_000.0);
        assertThat(StreamCodingExamples.highestPaidByDepartmentThenName(employees)
                .get("Engineering").name()).isEqualTo("Grace");
        assertThat(StreamCodingExamples.sumOfEvenSquares(List.of(1, 2, 3, 4)))
                .isEqualTo(20);
        assertThat(StreamCodingExamples.findEmployeeByName(employees, "lin"))
                .contains(employees.get(2));
    }
}
