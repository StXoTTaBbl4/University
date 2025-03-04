package com.xoeqvdp.backend.repositories;

import com.xoeqvdp.backend.entities.Employee;
import com.xoeqvdp.backend.entities.EmployeeSkill;
import com.xoeqvdp.backend.entities.Level;
import com.xoeqvdp.backend.entities.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


import java.util.Optional;

@Repository
public interface EmployeeSkillsRepository extends JpaRepository<EmployeeSkill, Long> {
    Optional<EmployeeSkill> findByEmployeeAndTask(Employee employee, Task task);

    @Modifying
    @Transactional
    @Query("UPDATE EmployeeSkill es SET es.level = :level WHERE es.employee = :employee AND es.task = :task")
    void updateByEmployeeAndTaskAndLevel(Employee employee, Task task, Level level);
}
