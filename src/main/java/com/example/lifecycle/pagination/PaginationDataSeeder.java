package com.example.lifecycle.pagination;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;
import com.example.lifecycle.playground.PlaygroundSelection;

@Component
@Order(5)
public class PaginationDataSeeder implements CommandLineRunner {

    private final JdbcClient jdbcClient;
    private final JdbcTemplate jdbcTemplate;
    private final int recordsToCreate;

    public PaginationDataSeeder(JdbcClient jdbcClient, JdbcTemplate jdbcTemplate,
            @Value("${pagination.demo-records:10000}") int recordsToCreate) {
        this.jdbcClient = jdbcClient;
        this.jdbcTemplate = jdbcTemplate;
        this.recordsToCreate = recordsToCreate;
    }

    @Override
    public void run(String... args) {
        if (!PlaygroundSelection.includes("pagination", args)) {
            return;
        }
        if (jdbcClient.sql("select count(*) from pagination_records")
                .query(Long.class).single() > 0) {
            return;
        }
        List<Object[]> batch = new ArrayList<>(recordsToCreate);
        Instant base = Instant.parse("2026-01-01T00:00:00Z");
        for (int index = 1; index <= recordsToCreate; index++) {
            batch.add(new Object[]{"tenant-a", "payload-" + index,
                    Timestamp.from(base.plusSeconds(index))});
        }
        jdbcTemplate.batchUpdate("insert into pagination_records(tenant_id, payload, created_at) "
                + "values (?, ?, ?)", batch);
        System.out.println("[Pagination] Seeded " + recordsToCreate + " records");
    }
}
