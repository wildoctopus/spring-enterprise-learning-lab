package com.example.lifecycle.jpa;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "employees")
public class JpaEmployee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    // Many-to-one defaults to EAGER in JPA, which often surprises teams and
    // creates hidden joins. Set LAZY explicitly for predictable data access.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "department_id", nullable = false)
    private JpaDepartment department;

    protected JpaEmployee() {
    }

    JpaEmployee(String name, JpaDepartment department) {
        this.name = name;
        this.department = department;
    }

    public String getName() {
        return name;
    }
}
