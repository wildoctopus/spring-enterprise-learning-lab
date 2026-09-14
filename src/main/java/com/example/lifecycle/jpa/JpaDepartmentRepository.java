package com.example.lifecycle.jpa;

import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface JpaDepartmentRepository extends JpaRepository<JpaDepartment, Long> {

    // EntityGraph is a repository-level fetch plan. It keeps the default
    // mapping LAZY while asking for employees for this specific read case.
    @EntityGraph(attributePaths = "employees")
    List<JpaDepartment> findAllWithEmployeesBy();

    // Fetch join is explicit JPQL. DISTINCT prevents duplicate department
    // objects when one department has many employee rows.
    @Query("select distinct d from JpaDepartment d left join fetch d.employees")
    List<JpaDepartment> findAllWithEmployeesUsingFetchJoin();
}
