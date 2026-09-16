package com.example.lifecycle.playground;

import com.example.lifecycle.sql.SqlQueryExamples;

public final class SqlPlaygroundExercises {

    private SqlPlaygroundExercises() {
    }

    public static boolean latestOrderHasDeterministicTieBreaker() {
        return SqlQueryExamples.LATEST_ORDER_PER_CUSTOMER.contains("order by id desc");
    }

    public static boolean keysetQueryIsBoundedAndOrdered() {
        return SqlQueryExamples.KEYSET_PAGE_AFTER_ID.contains("order by id")
                && SqlQueryExamples.KEYSET_PAGE_AFTER_ID.contains("fetch first :pageSize rows only");
    }
}