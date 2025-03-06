package com.xoeqvdp.backend.repositories;

import com.xoeqvdp.backend.entities.TaskAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskAssignmentRepository extends JpaRepository<TaskAssignment, Long> {

    @Query(value = "CALL assign_task_to_employee(:p_employee_id, :p_task_id, :p_required_level_id, :p_task_assign_id)", nativeQuery = true)
    void assignTaskToEmployees(@Param("p_employee_id") Long employeeId, @Param("p_task_id") Long  p_task_id, @Param("p_required_level_id") Long p_required_level_id, @Param("p_task_assign_id") Long p_task_assign_id);

}