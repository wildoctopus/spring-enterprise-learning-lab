package com.example.lifecycle.jpa;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class JpaDepartmentService {

    private final JpaDepartmentRepository departmentRepository;

    public JpaDepartmentService(JpaDepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    @Transactional
    public void seedIfEmpty() {
        if (departmentRepository.count() > 0) {
            return;
        }

        JpaDepartment engineering = new JpaDepartment("Engineering");
        engineering.addEmployee("Ada");
        engineering.addEmployee("Grace");

        JpaDepartment risk = new JpaDepartment("Risk");
        risk.addEmployee("Lin");
        risk.addEmployee("Morgan");

        departmentRepository.saveAll(List.of(engineering, risk));
    }

    @Transactional(readOnly = true)
    public List<String> loadWithNPlusOne() {
        // One query loads departments. Accessing each lazy collection inside
        // this loop then issues one additional query per department.
        return departmentRepository.findAll().stream()
                .map(department -> department.getName() + ": " + department.getEmployees().size())
                .toList();
    }

    @Transactional(readOnly = true)
    public List<String> loadWithEntityGraph() {
        // Same domain mapping, but a fetch plan loads departments and employees
        // together for this use case. The default association stays LAZY.
        return departmentRepository.findAllWithEmployeesBy().stream()
                .map(department -> department.getName() + ": " + department.getEmployees().size())
                .toList();
    }

    @Transactional(readOnly = true)
    public List<String> loadWithFetchJoin() {
        return departmentRepository.findAllWithEmployeesUsingFetchJoin().stream()
                .map(department -> department.getName() + ": " + department.getEmployees().size())
                .toList();
    }
}
