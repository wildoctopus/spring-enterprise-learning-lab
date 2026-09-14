package com.example.lifecycle.sql;

import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

@Component
@Order(50)
public class SqlQueryDemoRunner implements CommandLineRunner {

    private final JdbcClient jdbcClient;

    public SqlQueryDemoRunner(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public void run(String... args) {
        List<String> statusSummary = jdbcClient.sql(SqlQueryExamples.STATUS_SUMMARY)
                .query((row, rowNumber) -> row.getString("status") + "=" + row.getLong("order_count"))
                .list();
        List<String> latestOrders = jdbcClient.sql(SqlQueryExamples.LATEST_ORDER_PER_CUSTOMER)
                .query((row, rowNumber) -> row.getLong("id") + ":" + row.getString("customer_name"))
                .list();

        System.out.println("\n--- SQL query examples ---");
        System.out.println("Status summary: " + statusSummary);
        System.out.println("Latest order per customer: " + latestOrders);
        System.out.println("Running count: " + jdbcClient.sql(SqlQueryExamples.RUNNING_ORDER_COUNT)
            .query((row, rowNumber) -> row.getLong("running_count"))
                .list());
        System.out.println("SQL catalog includes joins, HAVING, windows, keyset paging, and transactions.");
    }
}
