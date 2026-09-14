package com.example.lifecycle.streams;

import java.util.Objects;

public final class StreamEmployee {

    private final String name;
    private final String department;
    private final int salary;

    public StreamEmployee(String name, String department, int salary) {
        this.name = Objects.requireNonNull(name, "name");
        this.department = Objects.requireNonNull(department, "department");
        this.salary = salary;
    }

    public String name() {
        return name;
    }

    public String department() {
        return department;
    }

    public int salary() {
        return salary;
    }

    @Override
    public String toString() {
        return name + "(" + department + ", " + salary + ")";
    }
}
