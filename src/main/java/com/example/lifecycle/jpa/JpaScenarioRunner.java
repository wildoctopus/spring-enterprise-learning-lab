package com.example.lifecycle.jpa;

import com.example.lifecycle.playground.PlaygroundSelection;
import org.hibernate.stat.Statistics;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(40)
public class JpaScenarioRunner implements CommandLineRunner {

    private final JpaDepartmentService departmentService;
    private final Statistics hibernateStatistics;

    public JpaScenarioRunner(JpaDepartmentService departmentService, Statistics hibernateStatistics) {
        this.departmentService = departmentService;
        this.hibernateStatistics = hibernateStatistics;
    }

    @Override
    public void run(String... args) {
        if (!PlaygroundSelection.includes("jpa", args)) {
            return;
        }
        departmentService.seedIfEmpty();

        hibernateStatistics.clear();
        departmentService.loadWithNPlusOne();
        long nPlusOneQueries = hibernateStatistics.getPrepareStatementCount();

        hibernateStatistics.clear();
        departmentService.loadWithEntityGraph();
        long entityGraphQueries = hibernateStatistics.getPrepareStatementCount();

        hibernateStatistics.clear();
        departmentService.loadWithFetchJoin();
        long fetchJoinQueries = hibernateStatistics.getPrepareStatementCount();

        System.out.println("\n--- JPA N+1 scenario ---");
        System.out.println("N+1 query count: " + nPlusOneQueries);
        System.out.println("EntityGraph query count: " + entityGraphQueries);
        System.out.println("Fetch join query count: " + fetchJoinQueries);
        System.out.println("Use SQL logs and query statistics to verify, not assumptions.");
    }
}
