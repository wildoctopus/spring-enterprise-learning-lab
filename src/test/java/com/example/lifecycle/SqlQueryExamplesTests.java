package com.example.lifecycle;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.lifecycle.sql.SqlQueryExamples;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;

@SpringBootTest
class SqlQueryExamplesTests {

    @Autowired
    private JdbcClient jdbcClient;

    @Test
    void aggregateAndWindowQueriesRunAgainstTheDemoSchema() {
        List<String> statuses = jdbcClient.sql(SqlQueryExamples.STATUS_SUMMARY)
                .query((row, rowNumber) -> row.getString("status"))
                .list();
        List<Long> runningCounts = jdbcClient.sql(SqlQueryExamples.RUNNING_ORDER_COUNT)
            .query((row, rowNumber) -> row.getLong("running_count"))
                .list();

        assertThat(statuses).contains("NEW");
        assertThat(runningCounts).isNotEmpty();
    }
}
