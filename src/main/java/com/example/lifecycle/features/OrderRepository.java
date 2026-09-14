package com.example.lifecycle.features;

import java.util.List;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class OrderRepository {

    private final JdbcClient jdbcClient;

    // Constructor injection makes the required database dependency explicit,
    // immutable, and straightforward to replace with a test double.
    public OrderRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public void insert(String customerName) {
        jdbcClient.sql("insert into orders(customer_name, status) values (:customerName, 'NEW')")
                .param("customerName", customerName)
                .update();
    }

    public long count() {
        return jdbcClient.sql("select count(*) from orders")
                .query(Long.class)
                .single();
    }

    public List<String> findCustomerNames() {
        return jdbcClient.sql("select customer_name from orders order by id")
                .query(String.class)
                .list();
    }
}
