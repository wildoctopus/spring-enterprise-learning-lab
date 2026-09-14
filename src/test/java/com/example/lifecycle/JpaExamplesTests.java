package com.example.lifecycle;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.lifecycle.jpa.JpaDepartmentService;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class JpaExamplesTests {

    @Autowired
    private JpaDepartmentService departmentService;

    @Autowired
    private Statistics hibernateStatistics;

    @BeforeEach
    void seedAndResetStatistics() {
        departmentService.seedIfEmpty();
        hibernateStatistics.clear();
    }

    @Test
    void naiveLazyAccessCreatesNPlusOneQueries() {
        departmentService.loadWithNPlusOne();

        assertThat(hibernateStatistics.getPrepareStatementCount()).isGreaterThan(1);
    }

    @Test
    void entityGraphLoadsTheReadModelWithoutNPlusOne() {
        departmentService.loadWithEntityGraph();

        assertThat(hibernateStatistics.getPrepareStatementCount()).isEqualTo(1);
    }

    @Test
    void fetchJoinLoadsTheReadModelWithoutNPlusOne() {
        departmentService.loadWithFetchJoin();

        assertThat(hibernateStatistics.getPrepareStatementCount()).isEqualTo(1);
    }
}
