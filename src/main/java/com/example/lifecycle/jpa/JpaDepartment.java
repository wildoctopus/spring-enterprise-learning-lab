package com.example.lifecycle.jpa;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "departments")
public class JpaDepartment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    // LAZY avoids loading every employee when a department is read alone.
    // Cascade/orphanRemoval are choices about aggregate ownership, not a fix
    // for query count or transaction boundaries.
    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<JpaEmployee> employees = new ArrayList<>();

    protected JpaDepartment() {
        // JPA needs a no-argument constructor; protected prevents normal misuse.
    }

    public JpaDepartment(String name) {
        this.name = name;
    }

    public void addEmployee(String employeeName) {
        employees.add(new JpaEmployee(employeeName, this));
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<JpaEmployee> getEmployees() {
        return employees;
    }
}
