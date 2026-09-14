package com.example.lifecycle.sql;

/**
 * Interview-oriented SQL examples. The text is kept beside Java so the
 * question, query shape, and explanation can be studied together.
 */
public final class SqlQueryExamples {

    private SqlQueryExamples() {
    }

    public static final String STATUS_SUMMARY = """
            select status, count(*) as order_count
            from orders
            group by status
            order by order_count desc, status
            """;

    public static final String CUSTOMERS_WITH_MULTIPLE_ORDERS = """
            select customer_name, count(*) as order_count
            from orders
            group by customer_name
            having count(*) > 1
            order by order_count desc, customer_name
            """;

    public static final String LATEST_ORDER_PER_CUSTOMER = """
            select id, customer_name, status
            from (
                select o.*, row_number() over (
                    partition by customer_name order by id desc
                ) as row_number
                from orders o
            ) ranked_orders
            where row_number = 1
            """;

    public static final String KEYSET_PAGE_AFTER_ID = """
            select id, customer_name, status
            from orders
            where id > :lastSeenId
            order by id
            fetch first :pageSize rows only
            """;

    public static final String DUPLICATE_CUSTOMERS = """
            select customer_name, count(*) as occurrences
            from orders
            group by customer_name
            having count(*) > 1
            """;

    public static final String SECOND_HIGHEST_ORDER_ID = """
            select max(id) as second_highest_id
            from orders
            where id < (select max(id) from orders)
            """;

    public static final String RUNNING_ORDER_COUNT = """
            select id, customer_name,
                   count(*) over (order by id rows between unbounded preceding and current row)
                   as running_count
            from orders
            order by id
            """;

    public static final String CUSTOMERS_WITHOUT_ORDERS = """
            -- Requires a customers table; left join preserves customers with no match.
            select c.id, c.name
            from customers c
            left join orders o on o.customer_name = c.name
            where o.id is null
            """;

    public static final String TRANSACTION_TOTALS = """
            -- Requires accounts and transactions tables in a banking schema.
            select account_id, sum(amount) as total_amount
            from transactions
            where transaction_time >= :fromTime
              and transaction_time < :toTime
            group by account_id
            having sum(amount) <> 0
            """;

    public static final String TOP_THREE_TRANSACTIONS_PER_ACCOUNT = """
            -- Window functions avoid a separate query for each account.
            select account_id, transaction_id, amount
            from (
                select t.*,
                       row_number() over (
                           partition by account_id order by amount desc, transaction_id
                       ) as row_number
                from transactions t
            ) ranked_transactions
            where row_number <= 3
            """;
}
